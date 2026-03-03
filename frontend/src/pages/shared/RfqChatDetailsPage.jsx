import React, { useState, useEffect, useRef } from 'react';
import { useParams, useNavigate, useLocation } from 'react-router-dom';
import rfqChatApi from '../../api/rfqChatApi';
import useAuthStore from '../../store/authStore';
import toast from 'react-hot-toast';
import { Client } from '@stomp/stompjs';
import SockJS from 'sockjs-client';

// Components
import RfqChatShell from '../../components/rfqChat/RfqChatShell';
import RfqChatListItem from '../../components/rfqChat/RfqChatListItem';
import RfqChatHeader from '../../components/rfqChat/RfqChatHeader';
import RfqMessageList from '../../components/rfqChat/RfqMessageList';
import RfqComposer from '../../components/rfqChat/RfqComposer';
import PriceProposalModal from '../../components/rfqChat/PriceProposalModal';
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
    const [showProposalModal, setShowProposalModal] = useState(false);

    const stompClient = useRef(null);

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
        connectWebSocket();

        return () => {
            if (stompClient.current) {
                stompClient.current.deactivate();
            }
        };
    }, [chatId]);

    const connectWebSocket = () => {
        if (stompClient.current) {
            stompClient.current.deactivate();
        }

        const token = useAuthStore.getState().token;
        if (!token) return;

        const client = new Client({
            webSocketFactory: () => new SockJS(`${import.meta.env.VITE_API_BASE_URL || 'http://localhost:8081'}/ws`),
            connectHeaders: { Authorization: `Bearer ${token}` },
            reconnectDelay: 5000,
            onConnect: () => {
                client.subscribe(`/topic/rfq-chat/${chatId}`, (message) => {
                    const parsed = JSON.parse(message.body);

                    if (parsed.event === 'NEW_MESSAGE' || parsed.event === 'PRICE_PROPOSAL' || parsed.event === 'SYSTEM_EVENT') {
                        setMessages(prev => {
                            // Filter dedup in case of concurrent REST fetch
                            if (prev.some(m => m.id === parsed.data.id)) return prev;
                            return [...prev, parsed.data].sort((a, b) => new Date(a.createdAt) - new Date(b.createdAt));
                        });
                    } else if (parsed.event === 'PROPOSAL_ACCEPTED') {
                        // Wait briefly for toast to show, then redirect
                        setTimeout(() => navigate(`/buyer/orders/${parsed.orderId}/pay`), 1500);
                    }
                });

                // Mark chat as read upon connecting
                rfqChatApi.markRfqChatRead(chatId).catch(() => { });
            },
            onStompError: (frame) => console.error('Broker reported error:', frame.headers['message'])
        });

        client.activate();
        stompClient.current = client;
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
            await rfqChatApi.sendRfqChatMessage(chatId, payload);
            // Don't modify `setMessages` here — wait for the WebSocket broadcast instead!
            // This prevents duplicate messages (optimistic vs broadcast).
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
        <>
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
                                isBuyer={!isSeller}
                                onProposalAccepted={(orderId) => {
                                    // The STOMP event will also fire, this is backup
                                    setTimeout(() => navigate(`/buyer/orders/${orderId}/pay`), 1500);
                                }}
                                loading={loadingMessages}
                                hasMore={hasMore}
                                loadingMore={loadingMore}
                                onLoadMore={() => fetchMessages(page + 1)}
                            />
                            <RfqComposer
                                chatId={chatId}
                                isBuyer={!isSeller}
                                onSend={handleSendMessage}
                                onProposalSent={(action) => {
                                    if (action === 'OPEN_MODAL') {
                                        setShowProposalModal(true);
                                    }
                                }}
                            />
                        </>
                    ) : (
                        <div className="flex-1 flex items-center justify-center bg-slate-50 text-slate-400">
                            {loadingChats ? 'Loading...' : 'Select a conversation to start negotiating'}
                        </div>
                    )
                }
            />

            {!isSeller === false && (
                <PriceProposalModal
                    isOpen={showProposalModal}
                    onClose={() => setShowProposalModal(false)}
                    chatId={chatId}
                    onProposalSent={() => {
                        // Chat websockets will handle the rest
                    }}
                />
            )}
        </>
    );
};

export default RfqChatDetailsPage;
