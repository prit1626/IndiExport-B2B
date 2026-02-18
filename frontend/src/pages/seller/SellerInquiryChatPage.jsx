import React, { useState, useEffect, useRef } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import chatApi from '../../api/chatApi';
import ChatShell from '../../components/chat/ChatShell';
import ChatList from '../../components/chat/ChatList';
import ChatHeader from '../../components/chat/ChatHeader';
import MessageList from '../../components/chat/MessageList';
import MessageComposer from '../../components/chat/MessageComposer';
import useAuthStore from '../../store/authStore';
import toast from 'react-hot-toast';

const SellerInquiryChatPage = () => {
    const { chatId } = useParams();
    const navigate = useNavigate();
    const { user } = useAuthStore();

    // Threads State
    const [threads, setThreads] = useState([]);
    const [loadingThreads, setLoadingThreads] = useState(true);

    // Messages State
    const [messages, setMessages] = useState([]);
    const [loadingMessages, setLoadingMessages] = useState(true);
    const [page, setPage] = useState(0);
    const [hasMore, setHasMore] = useState(true);
    const [loadingMore, setLoadingMore] = useState(false);

    const pollingInterval = useRef(null);

    // 1. Fetch Threads (Sidebar)
    useEffect(() => {
        fetchThreads();
    }, []);

    const fetchThreads = async () => {
        try {
            const { data } = await chatApi.getSellerInquiryThreads();
            setThreads(data.content || []);
            setLoadingThreads(false);
        } catch (error) {
            console.error('Threads error', error);
        }
    };

    // 2. Fetch Messages (Content)
    useEffect(() => {
        if (!chatId) return;

        // Reset state on chat change
        setMessages([]);
        setPage(0);
        setHasMore(true);
        setLoadingMessages(true);

        fetchMessages(0, true);
        startPolling();

        return () => stopPolling();
    }, [chatId]);

    const startPolling = () => {
        stopPolling();
        pollingInterval.current = setInterval(async () => {
            if (document.hidden) return; // Skip if tab hidden
            try {
                // Poll simple newest page
                const { data } = await chatApi.getChatMessages(chatId, { page: 0, size: 20 });
                if (data.content) {
                    setMessages(prev => {
                        // Merge new messages (avoid duplicates)
                        const existingIds = new Set(prev.map(m => m.id));
                        const newMsgs = data.content.filter(m => !existingIds.has(m.id));
                        if (newMsgs.length === 0) return prev;
                        return [...prev, ...newMsgs];
                    });

                    // Mark read if new messages arrive
                    await chatApi.markChatRead(chatId);
                }
            } catch (e) {
                // Silent fail on poll
            }
        }, 5000); // 5 sec
    };

    const stopPolling = () => {
        if (pollingInterval.current) clearInterval(pollingInterval.current);
    };

    const fetchMessages = async (pageNum, isInitial = false) => {
        try {
            if (!isInitial) setLoadingMore(true);

            const { data } = await chatApi.getChatMessages(chatId, { page: pageNum, size: 50 });

            setMessages(prev => {
                const newMsgs = data.content || [];
                const existingIds = new Set(prev.map(m => m.id));
                const uniqueNew = newMsgs.filter(m => !existingIds.has(m.id));
                return [...prev, ...uniqueNew];
            });

            setHasMore(!data.last);
            setPage(pageNum);

            if (isInitial) {
                await chatApi.markChatRead(chatId);
                // Also update thread unread count locally
                setThreads(prev => prev.map(t =>
                    t.chatId === chatId ? { ...t, unreadCount: 0 } : t
                ));
            }

        } catch (error) {
            console.error('Msg error', error);
            toast.error('Failed to load messages');
        } finally {
            setLoadingMessages(false);
            setLoadingMore(false);
        }
    };

    const handleSendMessage = async (payload) => {
        try {
            const { data } = await chatApi.sendChatMessage(chatId, payload);
            setMessages(prev => [...prev, data]);
            // Refresh threads to update last message
            fetchThreads();
        } catch (error) {
            throw error; // Composer handles toast
        }
    };

    const currentThread = threads.find(t => t.chatId === chatId);

    return (
        <ChatShell
            isMobileChatOpen={true}
            sidebar={
                <>
                    <div className="p-4 border-b border-slate-200 bg-white">
                        <h1 className="text-xl font-bold text-slate-800">Inquiries</h1>
                    </div>
                    <ChatList
                        threads={threads}
                        loading={loadingThreads}
                        activeChatId={chatId}
                        onSelect={(id) => navigate(`/seller/inquiries/${id}`)}
                    />
                </>
            }
            content={
                <>
                    <ChatHeader
                        thread={currentThread}
                        onBack={() => navigate('/seller/inquiries')}
                    />
                    <MessageList
                        messages={messages}
                        currentUserId={user?.id}
                        loading={loadingMessages}
                        hasMore={hasMore}
                        loadingMore={loadingMore}
                        onLoadMore={() => fetchMessages(page + 1)}
                    />
                    <MessageComposer
                        chatId={chatId}
                        onSend={handleSendMessage}
                    />
                </>
            }
        />
    );
};

export default SellerInquiryChatPage;
