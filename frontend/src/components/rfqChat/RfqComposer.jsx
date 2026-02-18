import React, { useState } from 'react';
import { Send, Paperclip, FileText, X } from 'lucide-react';
import toast from 'react-hot-toast';

const RfqComposer = ({ onSend, onUploadClick, onProposalClick }) => {
    const [text, setText] = useState('');
    const [sending, setSending] = useState(false);

    const handleSend = async () => {
        if (!text.trim()) return;

        setSending(true);
        try {
            await onSend({
                messageType: 'TEXT',
                text: text.trim()
            });
            setText('');
        } catch (error) {
            console.error(error);
            toast.error('Failed to send');
        } finally {
            setSending(false);
        }
    };

    const handleKeyDown = (e) => {
        if (e.key === 'Enter' && !e.shiftKey) {
            e.preventDefault();
            handleSend();
        }
    };

    return (
        <div className="p-4 bg-white border-t border-slate-200">
            <div className="flex items-end gap-2 max-w-5xl mx-auto">
                {/* Attachments */}
                <div className="flex gap-1 pb-1">
                    <button
                        onClick={onUploadClick}
                        className="p-2 text-slate-500 hover:bg-slate-100 rounded-full transition-colors tooltip"
                        title="Attach File"
                        disabled={!onUploadClick}
                    >
                        <Paperclip size={20} />
                    </button>
                    <button
                        onClick={onProposalClick}
                        className="p-2 text-slate-500 hover:bg-slate-100 rounded-full transition-colors tooltip"
                        title="Send Price Proposal"
                        disabled={!onProposalClick}
                    >
                        <FileText size={20} />
                    </button>
                </div>

                {/* Input */}
                <div className="flex-1 bg-slate-100 rounded-2xl p-2 px-4 focus-within:ring-2 focus-within:ring-brand-500/20 focus-within:bg-white transition-all border border-transparent focus-within:border-brand-200">
                    <textarea
                        value={text}
                        onChange={(e) => setText(e.target.value)}
                        onKeyDown={handleKeyDown}
                        placeholder="Type a message..."
                        className="w-full bg-transparent outline-none text-sm text-slate-800 resize-none max-h-32 min-h-[24px]"
                        rows={1}
                        style={{ height: 'auto', minHeight: '24px' }}
                        onInput={(e) => {
                            e.target.style.height = 'auto';
                            e.target.style.height = e.target.scrollHeight + 'px';
                        }}
                    />
                </div>

                {/* Send Button */}
                <button
                    onClick={handleSend}
                    disabled={!text.trim() || sending}
                    className="p-3 bg-brand-600 hover:bg-brand-700 disabled:bg-slate-300 text-white rounded-full shadow-sm transition-all active:scale-95 disabled:scale-100"
                >
                    <Send size={18} />
                </button>
            </div>
        </div>
    );
};

export default RfqComposer;
