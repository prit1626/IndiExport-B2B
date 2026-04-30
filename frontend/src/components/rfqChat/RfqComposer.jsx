import React, { useState, useRef } from 'react';
import { Send, Paperclip, FileText, Loader2 } from 'lucide-react';
import toast from 'react-hot-toast';
import PriceProposalModal from './PriceProposalModal';
import rfqChatApi from '../../api/rfqChatApi';

const RfqComposer = ({ chatId, isBuyer, onSend, onProposalSent }) => {
    const [text, setText] = useState('');
    const [sending, setSending] = useState(false);
    const [uploading, setUploading] = useState(false);
    const fileInputRef = useRef(null);

    const handleSend = async () => {
        if (!text.trim()) return;

        setSending(true);
        try {
            await onSend({
                messageType: 'TEXT',
                messageText: text.trim()
            });
            setText('');
        } catch (error) {
            console.error(error);
            toast.error('Failed to send');
        } finally {
            setSending(false);
        }
    };

    const handleFileUpload = async (e) => {
        const file = e.target.files?.[0];
        if (!file) return;

        // Basic validation
        if (file.size > 10 * 1024 * 1024) {
            toast.error('File must be less than 10MB');
            return;
        }

        const formData = new FormData();
        formData.append('file', file);

        setUploading(true);
        // Clear input to allow uploading same file again
        if (fileInputRef.current) fileInputRef.current.value = '';

        try {
            // First get Cloudinary URL
            const { data: uploadRes } = await rfqChatApi.uploadAttachment(chatId, formData);

            // Then send as ATTACHMENT message
            await onSend({
                messageType: 'ATTACHMENT',
                attachmentUrl: uploadRes.url,
                attachmentFileName: uploadRes.fileName
            });

        } catch (error) {
            console.error('Upload failed:', error);
            toast.error(error.response?.data?.message || 'Failed to upload attachment');
        } finally {
            setUploading(false);
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
                    <input
                        type="file"
                        ref={fileInputRef}
                        onChange={handleFileUpload}
                        className="hidden"
                        accept="image/*,.pdf,.doc,.docx,.xls,.xlsx"
                    />
                    <button
                        onClick={() => fileInputRef.current?.click()}
                        disabled={uploading}
                        className="p-2 text-slate-500 hover:bg-slate-100 rounded-full transition-colors tooltip disabled:opacity-50"
                        title="Attach File"
                    >
                        {uploading ? <Loader2 size={20} className="animate-spin" /> : <Paperclip size={20} />}
                    </button>

                    {!isBuyer && (
                        <button
                            onClick={() => {
                                if (onProposalSent) {
                                    // Just a trigger for parent to open modal instead of showing internal modal
                                    onProposalSent('OPEN_MODAL');
                                }
                            }}
                            className="p-2 text-slate-500 hover:bg-slate-100 rounded-full transition-colors tooltip"
                            title="Send Price Proposal"
                        >
                            <FileText size={20} />
                        </button>
                    )}
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
