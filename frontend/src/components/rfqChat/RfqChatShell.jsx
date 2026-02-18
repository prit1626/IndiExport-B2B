import React from 'react';
import { AnimatePresence, motion } from 'framer-motion';

const RfqChatShell = ({ sidebar, content, isMobileChatOpen }) => {
    return (
        <div className="flex h-[calc(100vh-64px)] bg-slate-50 overflow-hidden">
            {/* Sidebar (Chat List) */}
            <div className={`
                w-full md:w-80 lg:w-96 bg-white border-r border-slate-200 flex flex-col z-20
                ${isMobileChatOpen ? 'hidden md:flex' : 'flex'}
            `}>
                {sidebar}
            </div>

            {/* Main Content (Chat Area) */}
            <div className={`
                flex-1 flex flex-col relative bg-slate-50 z-10
                ${isMobileChatOpen ? 'flex' : 'hidden md:flex'}
            `}>
                {content}
            </div>
        </div>
    );
};

export default RfqChatShell;
