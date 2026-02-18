
import React, { useRef, useState } from 'react';
import { UploadCloud, File, X, Check } from 'lucide-react';
import { toast } from 'react-hot-toast';

const SellerUploadDropzone = ({ label, accept = "image/*,.pdf", onUpload, isLoading, existingFileUrl, existingFileName }) => {
    const fileInputRef = useRef(null);
    const [dragActive, setDragActive] = useState(false);
    const [preview, setPreview] = useState(existingFileUrl);

    const handleDrag = (e) => {
        e.preventDefault();
        e.stopPropagation();
        if (e.type === "dragenter" || e.type === "dragover") {
            setDragActive(true);
        } else if (e.type === "dragleave") {
            setDragActive(false);
        }
    };

    const handleDrop = (e) => {
        e.preventDefault();
        e.stopPropagation();
        setDragActive(false);
        if (e.dataTransfer.files && e.dataTransfer.files[0]) {
            handleFile(e.dataTransfer.files[0]);
        }
    };

    const handleChange = (e) => {
        e.preventDefault();
        if (e.target.files && e.target.files[0]) {
            handleFile(e.target.files[0]);
        }
    };

    const handleFile = (file) => {
        // Simple client side validation
        if (file.size > 5 * 1024 * 1024) {
            toast.error('File size too large (Max 5MB)');
            return;
        }

        // Show local preview immediately for images
        if (file.type.startsWith('image/')) {
            const reader = new FileReader();
            reader.onload = (e) => setPreview(e.target.result);
            reader.readAsDataURL(file);
        } else {
            setPreview(null); // No preview for PDF yet locally
        }

        onUpload(file);
    };

    return (
        <div className="mb-4">
            <label className="block text-sm font-medium text-slate-700 mb-2">{label}</label>

            <div
                className={`relative border-2 border-dashed rounded-lg p-6 flex flex-col items-center justify-center transition-colors cursor-pointer
                    ${dragActive ? 'border-brand-500 bg-brand-50' : 'border-slate-300 hover:border-brand-400 bg-slate-50 hover:bg-slate-100'}
                    ${isLoading ? 'opacity-50 pointer-events-none' : ''}
                `}
                onDragEnter={handleDrag}
                onDragLeave={handleDrag}
                onDragOver={handleDrag}
                onDrop={handleDrop}
                onClick={() => fileInputRef.current?.click()}
            >
                <input
                    ref={fileInputRef}
                    type="file"
                    className="hidden"
                    accept={accept}
                    onChange={handleChange}
                />

                {isLoading ? (
                    <div className="flex flex-col items-center animate-pulse">
                        <UploadCloud className="text-brand-500 mb-2" size={32} />
                        <p className="text-sm text-brand-600 font-medium">Uploading...</p>
                    </div>
                ) : existingFileUrl || preview ? (
                    <div className="w-full flex items-center justify-between bg-white p-3 rounded border border-slate-200 shadow-sm relative z-10" onClick={(e) => e.stopPropagation()}>
                        <div className="flex items-center gap-3 overflow-hidden">
                            <div className="w-10 h-10 bg-slate-100 rounded flex items-center justify-center shrink-0">
                                {preview ? (
                                    <img src={preview} alt="Preview" className="w-full h-full object-cover rounded" />
                                ) : (
                                    <File className="text-slate-500" size={20} />
                                )}
                            </div>
                            <div className="truncate">
                                <p className="text-sm font-medium text-slate-900 truncate">{existingFileName || "Uploaded File"}</p>
                                <p className="text-xs text-green-600 flex items-center gap-1"><Check size={12} /> Uploaded</p>
                            </div>
                        </div>
                        <button
                            onClick={(e) => {
                                e.stopPropagation();
                                fileInputRef.current?.click();
                            }}
                            className="text-xs text-brand-600 font-medium hover:underline ml-2"
                        >
                            Replace
                        </button>
                    </div>
                ) : (
                    <div className="text-center">
                        <UploadCloud className="text-slate-400 w-10 h-10 mx-auto mb-2" />
                        <p className="text-sm text-slate-600 font-medium">Click to upload or drag and drop</p>
                        <p className="text-xs text-slate-400 mt-1">SVG, PNG, JPG or PDF (MAX 5MB)</p>
                    </div>
                )}
            </div>
        </div>
    );
};

export default SellerUploadDropzone;
