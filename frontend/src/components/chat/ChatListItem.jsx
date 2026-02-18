import React from 'react';
import { format } from 'date-fns';
import { motion } from 'framer-motion';

const ChatListItem = ({ thread, active, onClick }) => {
    const lastMsgTime = thread.lastMessage ? format(new Date(thread.lastMessage.createdAt), 'MMM d') : '';

    return (
        <motion.div
            whileHover={{ backgroundColor: active ? '' : 'rgba(241, 245, 249, 0.8)' }}
            onClick={onClick}
            className={`
                p-4 border-b border-slate-200 cursor-pointer transition-colors
                ${active ? 'bg-white border-l-4 border-l-brand-600 shadow-sm z-10' : 'bg-transparent border-l-4 border-l-transparent'}
            `}
        >
            <div className="flex gap-3">
                {/* Product Thumbnail */}
                <img
                    src={thread.topicImageUrl || '/placeholder-product.png'}
                    alt={thread.topicTitle}
                    className="w-12 h-12 rounded-lg object-cover bg-slate-200"
                />

                <div className="flex-1 min-w-0">
                    <div className="flex justify-between items-start">
                        <h4 className="font-semibold text-slate-900 truncate pr-2">
                            {thread.otherParticipantName || 'Unknown User'}
                        </h4>
                        <span className="text-xs text-slate-500 whitespace-nowrap">{lastMsgTime}</span>
                    </div>

                    <p className="text-xs text-brand-600 font-medium truncate mb-1">
                        {thread.topicTitle}
                    </p>

                    <div className="flex justify-between items-center">
                        <p className={`text-sm truncate ${thread.unreadCount > 0 ? 'text-slate-900 font-medium' : 'text-slate-500'}`}>
                            {thread.lastMessage?.messageText || (thread.lastMessage?.messageType === 'FILE' ? 'Attachment' : '')}
                        </p>

                        {thread.unreadCount > 0 && (
                            <span className="bg-brand-600 text-white text-xs font-bold px-2 py-0.5 rounded-full min-w-[20px] text-center">
                                {thread.unreadCount}
                            </span>
                        )}
                    </div>
                </div>
            </div>
        </motion.div>
    );
};

export default ChatListItem;
