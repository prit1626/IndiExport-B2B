import React from 'react';
import { formatDistanceToNow } from 'date-fns';
import { Package, MapPin } from 'lucide-react';

const RfqChatListItem = ({ chat, isActive, onClick }) => {
    return (
        <div
            onClick={onClick}
            className={`
                p-4 border-b border-slate-100 cursor-pointer transition-colors hover:bg-slate-50
                ${isActive ? 'bg-brand-50/50 border-l-4 border-l-brand-600' : 'border-l-4 border-l-transparent'}
            `}
        >
            <div className="flex justify-between items-start mb-1">
                <h4 className={`font-semibold text-sm truncate pr-2 ${isActive ? 'text-brand-900' : 'text-slate-800'}`}>
                    {chat.rfqTitle}
                </h4>
                {chat.updatedAt && (
                    <span className="text-[10px] text-slate-400 whitespace-nowrap">
                        {formatDistanceToNow(new Date(chat.updatedAt), { addSuffix: true })}
                    </span>
                )}
            </div>

            <div className="flex items-center gap-3 text-xs text-slate-500 mb-2">
                <span className="flex items-center gap-1">
                    <Package size={12} /> {chat.qty} {chat.unit}
                </span>
                <span className="flex items-center gap-1">
                    <MapPin size={12} /> {chat.destinationCountry}
                </span>
            </div>

            <div className="flex justify-between items-center">
                <p className={`text-sm truncate max-w-[80%] ${chat.unreadCount > 0 ? 'font-medium text-slate-800' : 'text-slate-500'}`}>
                    {chat.lastMessagePreview || 'No messages yet'}
                </p>
                {chat.unreadCount > 0 && (
                    <span className="bg-brand-600 text-white text-[10px] font-bold px-1.5 py-0.5 rounded-full min-w-[20px] text-center">
                        {chat.unreadCount}
                    </span>
                )}
            </div>
        </div>
    );
};

export default RfqChatListItem;
