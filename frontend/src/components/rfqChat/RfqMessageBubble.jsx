import React from 'react';
import { formatTime } from '../../utils/chatUtils';
import { CheckCheck, FileText, Download } from 'lucide-react';
import PriceProposalMessage from './PriceProposalMessage';
import SystemEventMessage from './SystemEventMessage';
import AttachmentMessage from './AttachmentMessage';

const RfqMessageBubble = ({ message, isOwnMessage, isBuyer, onProposalAccepted }) => {

    if (message.messageType === 'SYSTEM') {
        return <SystemEventMessage message={message} />;
    }

    if (message.messageType === 'PRICE_PROPOSAL') {
        return (
            <div className={`flex flex-col mb-4 ${isOwnMessage ? 'items-end' : 'items-start'}`}>
                <PriceProposalMessage
                    message={message}
                    isOwnMessage={isOwnMessage}
                    isBuyer={isBuyer}
                    onAccepted={onProposalAccepted}
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
                {message.messageType === 'ATTACHMENT' && (
                    <AttachmentMessage message={message} isOwnMessage={isOwnMessage} />
                )}

                {/* Text Content */}
                {message.messageType === 'TEXT' && (
                    <p className={`text-sm whitespace-pre-wrap leading-relaxed ${isOwnMessage ? 'text-white' : 'text-slate-800'}`}>
                        {message.messageText}
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
