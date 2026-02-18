import React, { useRef, useEffect } from 'react';
import RfqMessageBubble from './RfqMessageBubble';
import { Loader2, ArrowUp } from 'lucide-react';
import { groupMessages } from '../../utils/chatUtils'; // Reuse existing logic

const RfqMessageList = ({ messages, currentUserId, loading, hasMore, loadingMore, onLoadMore }) => {
    const bottomRef = useRef(null);
    const containerRef = useRef(null);
    const prevHeightRef = useRef(0);

    // Auto-scroll to bottom on new message (if near bottom)
    useEffect(() => {
        if (!loadingMore && bottomRef.current) {
            bottomRef.current.scrollIntoView({ behavior: 'smooth' });
        }
    }, [messages.length, loadingMore]);

    // Maintain scroll position when loading older messages
    useEffect(() => {
        if (!loadingMore && containerRef.current && prevHeightRef.current > 0) {
            const newHeight = containerRef.current.scrollHeight;
            containerRef.current.scrollTop = newHeight - prevHeightRef.current;
        }
    }, [messages.length, loadingMore]);

    const handleScroll = (e) => {
        if (e.target.scrollTop === 0 && hasMore && !loadingMore) {
            prevHeightRef.current = e.target.scrollHeight;
            onLoadMore();
        }
    };

    const groupedMessages = groupMessages(messages);

    if (loading) {
        return (
            <div className="flex-1 flex items-center justify-center bg-slate-50">
                <Loader2 className="animate-spin text-brand-600" size={32} />
            </div>
        );
    }

    return (
        <div
            ref={containerRef}
            onScroll={handleScroll}
            className="flex-1 overflow-y-auto p-4 bg-slate-50 space-y-6"
        >
            {loadingMore && (
                <div className="flex justify-center py-2">
                    <Loader2 className="animate-spin text-brand-600" size={20} />
                </div>
            )}

            {!hasMore && messages.length > 20 && (
                <div className="text-center text-xs text-slate-400 py-4">
                    Start of conversation
                </div>
            )}

            {Object.entries(groupedMessages).map(([date, msgs]) => (
                <div key={date}>
                    <div className="flex justify-center mb-4 sticky top-0 z-10">
                        <span className="bg-slate-200/80 backdrop-blur-sm text-slate-600 text-[10px] font-bold px-3 py-1 rounded-full shadow-sm uppercase tracking-wide">
                            {date}
                        </span>
                    </div>
                    {msgs.map((msg) => (
                        <RfqMessageBubble
                            key={msg.id}
                            message={msg}
                            isOwnMessage={msg.senderId === currentUserId}
                        />
                    ))}
                </div>
            ))}

            <div ref={bottomRef} />
        </div>
    );
};

export default RfqMessageList;
