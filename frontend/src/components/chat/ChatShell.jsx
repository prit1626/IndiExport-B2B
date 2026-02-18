import React from 'react';
import { motion } from 'framer-motion';

const ChatShell = ({ sidebar, content, isMobileChatOpen }) => {
    return (
        <div className="flex h-[calc(100vh-64px)] bg-white border-t border-slate-200 overflow-hidden">
            {/* Sidebar (List) */}
            <div className={`
                w-full md:w-80 lg:w-96 bg-slate-50 border-r border-slate-200 flex flex-col
                ${isMobileChatOpen ? 'hidden md:flex' : 'flex'}
            `}>
                {sidebar}
            </div>

            {/* Main Content (Chat) */}
            <div className={`
                flex-1 flex flex-col bg-slate-100 relative
                ${!isMobileChatOpen ? 'hidden md:flex' : 'flex'}
            `}>
                {content}
            </div>
        </div>
    );
};

export default ChatShell;
