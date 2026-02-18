import React, { useEffect, useRef } from 'react';
import MessageBubble from './MessageBubble';
import { groupMessages } from '../../utils/chatUtils';
import { Loader2 } from 'lucide-react';

const MessageList = ({ messages, currentUserId, loading, hasMore, onLoadMore, loadingMore }) => {
    const bottomRef = useRef(null);
    const scrollRef = useRef(null);
    const prevHeightRef = useRef(0);

    // Group messages by date
    // ensure messages are sorted asc by date first
    const sortedMessages = [...messages].sort((a, b) => new Date(a.createdAt) - new Date(b.createdAt));
    const groupedGroups = groupMessages(sortedMessages);

    // Auto scroll to bottom on initial load or new message
    useEffect(() => {
        if (!loadingMore && bottomRef.current) {
            bottomRef.current.scrollIntoView({ behavior: 'smooth' });
        }
    }, [messages.length, loadingMore]);

    // Handle preserving scroll position when loading older messages
    useEffect(() => {
        if (loadingMore && scrollRef.current) {
            const newHeight = scrollRef.current.scrollHeight;
            const diff = newHeight - prevHeightRef.current;
            if (diff > 0) {
                scrollRef.current.scrollTop = diff;
            }
        }
    }, [messages.length, loadingMore]);

    const handleScroll = (e) => {
        if (e.target.scrollTop === 0 && hasMore && !loadingMore) {
            prevHeightRef.current = e.target.scrollHeight;
            onLoadMore();
        }
    };

    if (loading && messages.length === 0) {
        return (
            <div className="flex-1 flex items-center justify-center">
                <Loader2 className="animate-spin text-slate-400" />
            </div>
        );
    }

    return (
        <div
            ref={scrollRef}
            className="flex-1 overflow-y-auto p-4 content-visibility-auto"
            onScroll={handleScroll}
        >
            {loadingMore && (
                <div className="py-2 text-center text-xs text-slate-400">
                    <Loader2 className="animate-spin inline mr-2 w-3 h-3" />
                    Loading older messages...
                </div>
            )}

            {groupedGroups.map((group) => (
                <div key={group.date}>
                    <div className="sticky top-0 z-10 flex justify-center my-4">
                        <span className="bg-slate-200 text-slate-600 px-3 py-1 rounded-full text-xs font-medium shadow-sm opacity-90">
                            {group.date}
                        </span>
                    </div>
                    {group.messages.map(msg => (
                        <MessageBubble
                            key={msg.id}
                            message={msg}
                            isByType={msg.senderUserId === currentUserId}
                        />
                    ))}
                </div>
            ))}
            <div ref={bottomRef} />
        </div>
    );
};

export default MessageList;
