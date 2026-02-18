import React from 'react';
import ChatListItem from './ChatListItem';
import { Search } from 'lucide-react';

const ChatList = ({ threads, activeChatId, onSelect, loading, emptyMessage = "No inquiries yet" }) => {

    if (loading && threads.length === 0) {
        return (
            <div className="p-4 space-y-4">
                {[1, 2, 3].map(i => (
                    <div key={i} className="flex gap-3 animate-pulse">
                        <div className="w-12 h-12 bg-slate-200 rounded-lg"></div>
                        <div className="flex-1 space-y-2">
                            <div className="h-4 bg-slate-200 rounded w-3/4"></div>
                            <div className="h-3 bg-slate-200 rounded w-1/2"></div>
                        </div>
                    </div>
                ))}
            </div>
        );
    }

    if (threads.length === 0) {
        return (
            <div className="flex-1 flex items-center justify-center text-slate-400 p-6 text-center">
                <p>{emptyMessage}</p>
            </div>
        );
    }

    return (
        <div className="flex-1 overflow-y-auto">
            {threads.map(thread => (
                <ChatListItem
                    key={thread.chatId}
                    thread={thread}
                    active={activeChatId === thread.chatId}
                    onClick={() => onSelect(thread.chatId)}
                />
            ))}
        </div>
    );
};

export default ChatList;
