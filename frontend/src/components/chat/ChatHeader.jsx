import React from 'react';
import { ArrowLeft, MoreVertical, Phone } from 'lucide-react';
import { useNavigate } from 'react-router-dom';

const ChatHeader = ({ thread, onBack }) => {
    if (!thread) return null;

    return (
        <div className="bg-white border-b border-slate-200 px-4 py-3 flex items-center justify-between shadow-sm z-10">
            <div className="flex items-center gap-3">
                <button onClick={onBack} className="md:hidden p-2 -ml-2 text-slate-600 hover:bg-slate-100 rounded-full">
                    <ArrowLeft size={20} />
                </button>

                <div className="relative">
                    <div className="w-10 h-10 bg-brand-100 rounded-full flex items-center justify-center text-brand-700 font-semibold text-lg">
                        {thread.otherParticipantName?.charAt(0) || 'U'}
                    </div>
                </div>

                <div>
                    <h3 className="font-semibold text-slate-900 leading-tight">
                        {thread.otherParticipantName || 'Unknown User'}
                    </h3>
                    <p className="text-xs text-brand-600 truncate max-w-[200px]">
                        Inquiry: {thread.topicTitle}
                    </p>
                </div>
            </div>

            <div className="flex items-center gap-1">
                {/* Actions like Call or Profile could go here */}
                {/* <button className="p-2 text-slate-500 hover:bg-slate-50 rounded-full">
                    <MoreVertical size={20} />
                </button> */}
            </div>
        </div>
    );
};

export default ChatHeader;
