import React from 'react';
import { formatTime } from '../../utils/chatUtils';
import { FileText, Download } from 'lucide-react';

const MessageBubble = ({ message, isByType }) => {
    // isByType = true means current user sent it (Right side)

    const isFile = message.messageType === 'FILE';

    return (
        <div className={`flex w-full mb-2 ${isByType ? 'justify-end' : 'justify-start'}`}>
            <div className={`
                max-w-[75%] md:max-w-[60%] rounded-2xl px-4 py-2 shadow-sm relative group
                ${isByType
                    ? 'bg-brand-600 text-white rounded-tr-none'
                    : 'bg-white text-slate-800 rounded-tl-none border border-slate-100'}
            `}>
                {isFile ? (
                    <div className="flex items-center gap-3">
                        <div className={`p-2 rounded-lg ${isByType ? 'bg-white/20' : 'bg-slate-100'}`}>
                            <FileText size={24} />
                        </div>
                        <div className="flex-1 min-w-0">
                            <p className="font-medium truncate text-sm">{message.fileName || 'Attachment'}</p>
                            <a
                                href={message.fileUrl}
                                target="_blank"
                                rel="noreferrer"
                                className={`text-xs underline flex items-center gap-1 mt-1 ${isByType ? 'text-brand-100 hover:text-white' : 'text-brand-600 hover:text-brand-700'}`}
                            >
                                <Download size={12} /> Download
                            </a>
                        </div>
                    </div>
                ) : (
                    <p className="text-sm cursor-text whitespace-pre-wrap">{message.messageText}</p>
                )}

                <p className={`
                    text-[10px] text-right mt-1 opacity-70
                    ${isByType ? 'text-brand-100' : 'text-slate-400'}
                `}>
                    {formatTime(message.createdAt)}
                </p>
            </div>
        </div>
    );
};

export default MessageBubble;
