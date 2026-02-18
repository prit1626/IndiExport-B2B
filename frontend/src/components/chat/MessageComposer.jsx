import React, { useState, useRef } from 'react';
import { Send, Paperclip, X, Loader2 } from 'lucide-react';
import chatApi from '../../api/chatApi';
import toast from 'react-hot-toast';

const MessageComposer = ({ chatId, onSend }) => {
    const [text, setText] = useState('');
    const [file, setFile] = useState(null);
    const [uploading, setUploading] = useState(false);
    const [sending, setSending] = useState(false);
    const fileInputRef = useRef(null);

    const [uploadedAttachment, setUploadedAttachment] = useState(null); // Store uploaded file data if send fails

    const handleFileSelect = (e) => {
        if (e.target.files?.[0]) {
            setFile(e.target.files[0]);
            setUploadedAttachment(null); // Reset prev upload if file changes
        }
    };

    const handleSend = async () => {
        if ((!text.trim() && !file) || uploading || sending) return;

        setSending(true);
        try {
            let attachmentData = uploadedAttachment;

            // Upload file if exists and not yet uploaded
            if (file && !attachmentData) {
                setUploading(true);
                const response = await chatApi.uploadChatAttachment(chatId, file, (progressEvent) => {
                    // Optional: Update progress
                });
                // { fileUrl, fileName, fileMimeType }
                attachmentData = {
                    fileUrl: response.data.fileUrl || response.data.attachmentUrl,
                    fileName: response.data.fileName,
                    fileMimeType: response.data.fileType || response.data.fileMimeType
                };
                setUploadedAttachment(attachmentData);
                setUploading(false);
            }

            // Send actual message
            await onSend({
                messageType: file ? 'FILE' : 'TEXT',
                messageText: text.trim(), // Can be empty if file sent
                ...attachmentData
            });

            // Reset UI
            setText('');
            setFile(null);
            setUploadedAttachment(null);
            if (fileInputRef.current) fileInputRef.current.value = '';

        } catch (error) {
            console.error(error);
            toast.error('Failed to send message');
            // Do NOT clear file/text so user can retry.
            // setUploadedAttachment is preserved, so retry won't re-upload.
            setUploading(false);
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
        <div className="bg-white border-t border-slate-200 p-3 md:p-4">
            {/* File Preview */}
            {file && (
                <div className="flex items-center gap-2 mb-2 bg-slate-50 p-2 rounded-lg border border-slate-200 w-fit">
                    <span className="text-sm truncate max-w-[200px] text-slate-700">{file.name}</span>
                    <button onClick={() => { setFile(null); setUploadedAttachment(null); fileInputRef.current.value = ''; }} className="text-slate-400 hover:text-red-500">
                        <X size={16} />
                    </button>
                    {uploading && <Loader2 size={14} className="animate-spin text-brand-600" />}
                </div>
            )}

            <div className="flex gap-2 items-end">
                <button
                    onClick={() => fileInputRef.current?.click()}
                    className="p-3 text-slate-400 hover:text-brand-600 hover:bg-slate-50 rounded-full transition-colors"
                >
                    <Paperclip size={20} />
                </button>
                <input
                    type="file"
                    ref={fileInputRef}
                    className="hidden"
                    onChange={handleFileSelect}
                />

                <div className="flex-1 bg-slate-100 rounded-2xl flex items-center px-4 py-2 border border-transparent focus-within:border-brand-300 transition-colors">
                    <textarea
                        value={text}
                        onChange={(e) => setText(e.target.value)}
                        onKeyDown={handleKeyDown}
                        placeholder="Type a message..."
                        rows={1}
                        className="w-full bg-transparent border-none outline-none resize-none text-slate-900 placeholder:text-slate-400 max-h-32 py-1"
                        style={{ minHeight: '24px' }}
                    // Auto-expand could be added here
                    />
                </div>

                <button
                    onClick={handleSend}
                    disabled={(!text.trim() && !file) || uploading || sending}
                    className={`
                        p-3 rounded-full transition-colors flex items-center justify-center
                        ${(!text.trim() && !file) || uploading || sending
                            ? 'bg-slate-100 text-slate-300 cursor-not-allowed'
                            : 'bg-brand-600 text-white hover:bg-brand-700 shadow-sm'}
                    `}
                >
                    {sending ? <Loader2 size={20} className="animate-spin" /> : <Send size={20} />}
                </button>
            </div>
        </div>
    );
};

export default MessageComposer;
