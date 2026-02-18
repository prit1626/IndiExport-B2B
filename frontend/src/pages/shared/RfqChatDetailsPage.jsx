import React, { useState, useEffect, useRef } from 'react';
import { useParams, useNavigate, useLocation } from 'react-router-dom';
import rfqChatApi from '../../api/rfqChatApi';
import useAuthStore from '../../store/authStore';
import toast from 'react-hot-toast';

// Components
import RfqChatShell from '../../components/rfqChat/RfqChatShell';
import RfqChatListItem from '../../components/rfqChat/RfqChatListItem';
import RfqChatHeader from '../../components/rfqChat/RfqChatHeader';
import RfqMessageList from '../../components/rfqChat/RfqMessageList';
import RfqComposer from '../../components/rfqChat/RfqComposer';
import { Loader2, MessageSquareOff } from 'lucide-react';

const RfqChatDetailsPage = () => {
    const { chatId } = useParams();
    const navigate = useNavigate();
    const location = useLocation();
    const { user } = useAuthStore();

    // Determine Role based on URL (safe assumption due to RoleGuard)
    const isSeller = location.pathname.startsWith('/seller');
    const basePath = isSeller ? '/seller/rfq-chats' : '/buyer/rfq-chats';

    // State
    const [chats, setChats] = useState([]);
    const [loadingChats, setLoadingChats] = useState(true);
    const [messages, setMessages] = useState([]);
    const [loadingMessages, setLoadingMessages] = useState(true);
    const [page, setPage] = useState(0);
    const [hasMore, setHasMore] = useState(true);
    const [loadingMore, setLoadingMore] = useState(false);

    const pollingInterval = useRef(null);

    // 1. Fetch Chat List
    useEffect(() => {
        const fetchChats = async () => {
            try {
                const apiCall = isSeller ? rfqChatApi.getSellerRfqChats : rfqChatApi.getBuyerRfqChats;
                const { data } = await apiCall({ page: 0, size: 20 });
                setChats(data.content || []);
            } catch (error) {
                console.error('Failed to load chats', error);
                toast.error('Failed to load conversations');
            } finally {
                setLoadingChats(false);
            }
        };
        fetchChats();
    }, [isSeller]);

    // 2. Fetch Messages & Polling
    useEffect(() => {
        if (!chatId) return;

        setMessages([]);
        setPage(0);
        setLoadingMessages(true);
        setHasMore(true);

        fetchMessages(0, true);
        startPolling();

        return () => stopPolling();
    }, [chatId]);

    const startPolling = () => {
        stopPolling();
        pollingInterval.current = setInterval(async () => {
            if (document.hidden) return;
            try {
                const { data } = await rfqChatApi.getRfqChatMessages(chatId, { page: 0, size: 20 });
                if (data.content) {
                    setMessages(prev => {
                        const existingIds = new Set(prev.map(m => m.id));
                        const newMsgs = data.content.filter(m => !existingIds.has(m.id));
                        if (newMsgs.length === 0) return prev;
                        // Assuming new messages come from top (newest), we append them? 
                        // No. MessageList renders [Oldest -> Newest]. 
                        // Backend returns Page 0 (Newest). 
                        // If we verify timestamps, we should just merge and re-sort or append unique if we know they are newer.
                        // Simplified: Append unique messages that are *newer* than latest.
                        // Actually, simplified merge:
                        const combined = [...prev, ...newMsgs];
                        // Sort by createdAt ASC just in case
                        return combined.sort((a, b) => new Date(a.createdAt) - new Date(b.createdAt));
                    });
                }
            } catch (e) {
                // silent
            }
        }, 5000);
    };

    const stopPolling = () => {
        if (pollingInterval.current) clearInterval(pollingInterval.current);
    };

    const fetchMessages = async (pageNum, isInitial = false) => {
        try {
            if (!isInitial) setLoadingMore(true);
            const { data } = await rfqChatApi.getRfqChatMessages(chatId, { page: pageNum, size: 50 });

            setMessages(prev => {
                const newMsgs = data.content || [];
                // Sort NEW chunk by date ASC (Backend returns DESC usually)
                const sortedChunk = [...newMsgs].sort((a, b) => new Date(a.createdAt) - new Date(b.createdAt));

                if (isInitial) return sortedChunk;

                // If loading older (pageNum > 0), prepend them
                // Filter duplicates
                const existingIds = new Set(prev.map(m => m.id));
                const uniqueOld = sortedChunk.filter(m => !existingIds.has(m.id));
                return [...uniqueOld, ...prev];
            });

            setHasMore(!data.last);
            setPage(pageNum);

        } catch (error) {
            console.error(error);
            toast.error('Failed to load messages');
        } finally {
            setLoadingMessages(false);
            setLoadingMore(false);
        }
    };

    const handleSendMessage = async (payload) => {
        try {
            const { data } = await rfqChatApi.sendRfqChatMessage(chatId, payload);
            setMessages(prev => [...prev, data]);
            // Refresh chat list to update preview
            // (Optional optimization: manually update list state)
        } catch (error) {
            throw error;
        }
    };

    const activeChat = chats.find(c => c.chatId === chatId);

    // Sidebar Content
    const Sidebar = (
        <div className="flex flex-col h-full">
            <div className="p-4 border-b border-slate-200 bg-white">
                <h1 className="text-lg font-bold text-slate-800">Negotiations</h1>
            </div>
            <div className="flex-1 overflow-y-auto">
                {loadingChats ? (
                    <div className="flex justify-center p-8"><Loader2 className="animate-spin text-brand-600" /></div>
                ) : chats.length === 0 ? (
                    <div className="p-8 text-center text-slate-500 flex flex-col items-center">
                        <MessageSquareOff size={32} className="mb-2 opacity-50" />
                        <p className="text-sm">No active negotiations</p>
                    </div>
                ) : (
                    chats.map(chat => (
                        <RfqChatListItem
                            key={chat.chatId}
                            chat={chat}
                            isActive={chat.chatId === chatId}
                            onClick={() => navigate(`${basePath}/${chat.chatId}`)}
                        />
                    ))
                )}
            </div>
        </div>
    );

    return (
        <RfqChatShell
            isMobileChatOpen={!!chatId} // On mobile, if chatId exists, show content. Else show sidebar.
            sidebar={Sidebar}
            content={
                activeChat ? (
                    <>
                        <RfqChatHeader chat={activeChat} onBack={() => navigate(basePath)} />
                        <RfqMessageList
                            messages={messages}
                            currentUserId={user?.id}
                            loading={loadingMessages}
                            hasMore={hasMore}
                            loadingMore={loadingMore}
                            onLoadMore={() => fetchMessages(page + 1)}
                        />
                        <RfqComposer
                            onSend={handleSendMessage}
                            onUploadClick={() => toast('Attachment upload pending backend support', { icon: 'ℹ️' })}
                            onProposalClick={() => toast('Price Proposal UI pending integration', { icon: '🚧' })}
                        />
                    </>
                ) : (
                    <div className="flex-1 flex items-center justify-center bg-slate-50 text-slate-400">
                        {loadingChats ? 'Loading...' : 'Select a conversation to start negotiating'}
                    </div>
                )
            }
        />
    );
};

export default RfqChatDetailsPage;
