import React from 'react';
import { formatTime } from '../../utils/chatUtils';
import { CheckCheck, FileText, Download } from 'lucide-react';
import PriceProposalMessage from './PriceProposalMessage';
import SystemEventMessage from './SystemEventMessage';

const RfqMessageBubble = ({ message, isOwnMessage }) => {

    if (message.messageType === 'SYSTEM') {
        return <SystemEventMessage message={message} />;
    }

    if (message.messageType === 'PRICE_PROPOSAL') {
        return (
            <div className={`flex flex-col mb-4 ${isOwnMessage ? 'items-end' : 'items-start'}`}>
                <PriceProposalMessage
                    proposal={message.payload}
                    isOwnMessage={isOwnMessage}
                    senderRole={message.senderRole}
                />
                <span className={`text-[10px] text-slate-400 mt-1 px-1`}>
                    {formatTime(message.createdAt)}
                </span>
            </div>
        );
    }

    return (
        <div className={`flex flex-col mb-2 ${isOwnMessage ? 'items-end' : 'items-start'}`}>
            <div
                className={`
                    relative max-w-[85%] sm:max-w-[70%] px-4 py-2 rounded-2xl shadow-sm
                    ${isOwnMessage
                        ? 'bg-brand-600 text-white rounded-br-none'
                        : 'bg-white border border-slate-100 text-slate-800 rounded-bl-none'}
                `}
            >
                {/* File Attachment */}
                {message.messageType === 'FILE' && (
                    <div className={`mb-2 p-3 rounded-lg flex items-center gap-3 ${isOwnMessage ? 'bg-brand-700/50' : 'bg-slate-50'}`}>
                        <div className={`p-2 rounded-full ${isOwnMessage ? 'bg-white/20' : 'bg-slate-200'}`}>
                            <FileText size={20} className={isOwnMessage ? 'text-white' : 'text-slate-600'} />
                        </div>
                        <div className="flex-1 min-w-0">
                            <p className={`text-sm font-medium truncate ${isOwnMessage ? 'text-white' : 'text-slate-800'}`}>
                                {message.payload?.fileName || 'Attachment'}
                            </p>
                            <p className={`text-xs ${isOwnMessage ? 'text-brand-100' : 'text-slate-500'}`}>
                                {message.payload?.fileType || 'FILE'}
                            </p>
                        </div>
                        <a
                            href={message.payload?.attachmentUrl}
                            target="_blank"
                            rel="noopener noreferrer"
                            className={`p-1.5 rounded-full transition-colors ${isOwnMessage ? 'hover:bg-white/20 text-white' : 'hover:bg-slate-200 text-slate-600'}`}
                        >
                            <Download size={16} />
                        </a>
                    </div>
                )}

                {/* Text Content */}
                {message.messageType === 'TEXT' && (
                    <p className={`text-sm whitespace-pre-wrap ${isOwnMessage ? 'text-white' : 'text-slate-800'}`}>
                        {message.payload?.text}
                    </p>
                )}

                {/* Metadata */}
                <div className={`text-[10px] mt-1 flex items-center justify-end gap-1 ${isOwnMessage ? 'text-brand-100' : 'text-slate-400'}`}>
                    <span>{formatTime(message.createdAt)}</span>
                    {isOwnMessage && <CheckCheck size={12} />}
                </div>
            </div>
        </div>
    );
};

export default RfqMessageBubble;
