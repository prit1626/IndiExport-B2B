import React from 'react';
import { FileText, Image as ImageIcon, Download } from 'lucide-react';

/**
 * Rendered when messageType === 'ATTACHMENT'.
 */
const AttachmentMessage = ({ message, isOwnMessage }) => {
    const url = message.attachmentUrl;
    const fileName = message.attachmentFileName || 'Attachment';
    const isImage = url && /\.(jpg|jpeg|png|gif|webp)$/i.test(url);

    return (
        <div className={`rounded-2xl overflow-hidden border max-w-xs ${isOwnMessage
                ? 'bg-brand-600 border-brand-700 text-white'
                : 'bg-white border-slate-200 text-slate-800'
            }`}>
            {isImage ? (
                <a href={url} target="_blank" rel="noopener noreferrer">
                    <img
                        src={url}
                        alt={fileName}
                        className="w-full max-h-48 object-cover block"
                        loading="lazy"
                    />
                </a>
            ) : (
                <div className={`flex items-center gap-3 p-3 ${isOwnMessage ? 'bg-brand-700/40' : 'bg-slate-50'
                    }`}>
                    <div className={`p-2 rounded-xl ${isOwnMessage ? 'bg-white/20' : 'bg-slate-200'}`}>
                        <FileText size={20} className={isOwnMessage ? 'text-white' : 'text-slate-600'} />
                    </div>
                    <div className="flex-1 min-w-0">
                        <p className={`text-sm font-medium truncate ${isOwnMessage ? 'text-white' : 'text-slate-800'}`}>
                            {fileName}
                        </p>
                        <p className={`text-xs ${isOwnMessage ? 'text-brand-200' : 'text-slate-500'}`}>
                            Document
                        </p>
                    </div>
                    <a
                        href={url}
                        target="_blank"
                        rel="noopener noreferrer"
                        className={`p-1.5 rounded-full transition-colors ${isOwnMessage ? 'hover:bg-white/20 text-white' : 'hover:bg-slate-200 text-slate-600'
                            }`}
                    >
                        <Download size={16} />
                    </a>
                </div>
            )}
        </div>
    );
};

export default AttachmentMessage;
