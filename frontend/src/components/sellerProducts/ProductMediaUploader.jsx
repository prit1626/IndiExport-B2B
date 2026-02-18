import React, { useState, useCallback } from 'react';
import { useDropzone } from 'react-dropzone';
import { Upload, X, Image as ImageIcon, Video, Loader2 } from 'lucide-react';
import toast from 'react-hot-toast';
import productApi from '../../api/productApi';

const ProductMediaUploader = ({ productId, onUploadSuccess }) => {
    const [uploading, setUploading] = useState(false);
    const [files, setFiles] = useState([]);

    const onDrop = useCallback((acceptedFiles) => {
        setFiles(prev => [...prev, ...acceptedFiles]);
    }, []);

    const { getRootProps, getInputProps, isDragActive } = useDropzone({
        onDrop,
        accept: {
            'image/*': ['.jpeg', '.jpg', '.png', '.webp'],
            'video/mp4': ['.mp4']
        },
        maxSize: 10 * 1024 * 1024 // 10MB
    });

    const removeFile = (index) => {
        setFiles(prev => prev.filter((_, i) => i !== index));
    };

    const handleUpload = async () => {
        if (files.length === 0) return;

        setUploading(true);
        const formData = new FormData();

        // Backend expects 'files' for images and 'video' for video
        // We separate them
        files.forEach(file => {
            if (file.type.startsWith('image/')) {
                formData.append('files', file);
            } else if (file.type.startsWith('video/')) {
                formData.append('video', file);
            }
        });

        try {
            await productApi.sellerUploadProductMedia(productId, formData);
            toast.success('Media uploaded successfully');
            setFiles([]);
            onUploadSuccess(); // Refresh parent
        } catch (error) {
            console.error('Upload failed', error);
            toast.error('Failed to upload media');
        } finally {
            setUploading(false);
        }
    };

    return (
        <div className="space-y-4">
            <h3 className="text-sm font-semibold text-slate-800">Media Upload</h3>

            {/* Dropzone */}
            <div
                {...getRootProps()}
                className={`
                    border-2 border-dashed rounded-xl p-8 text-center cursor-pointer transition-colors
                    ${isDragActive ? 'border-brand-500 bg-brand-50' : 'border-slate-300 hover:border-brand-400 hover:bg-slate-50'}
                `}
            >
                <input {...getInputProps()} />
                <Upload className="mx-auto h-8 w-8 text-slate-400 mb-2" />
                <p className="text-sm text-slate-600 font-medium">
                    {isDragActive ? 'Drop files here...' : 'Drag & drop images/video, or click to select'}
                </p>
                <p className="text-xs text-slate-400 mt-1">
                    Images up to 5MB, Video up to 10MB
                </p>
            </div>

            {/* Preview List */}
            {files.length > 0 && (
                <div className="grid grid-cols-2 md:grid-cols-4 gap-4">
                    {files.map((file, index) => (
                        <div key={index} className="relative group rounded-lg overflow-hidden border border-slate-200 aspect-square bg-slate-100 flex items-center justify-center">
                            {file.type.startsWith('image/') ? (
                                <img src={URL.createObjectURL(file)} alt="preview" className="w-full h-full object-cover" />
                            ) : (
                                <Video className="text-slate-400" />
                            )}
                            <button
                                onClick={() => removeFile(index)}
                                className="absolute top-1 right-1 bg-black/50 text-white p-1 rounded-full hover:bg-red-500 transition-colors"
                            >
                                <X size={14} />
                            </button>
                            <div className="absolute bottom-0 left-0 right-0 bg-black/60 text-white text-[10px] px-2 py-1 truncate">
                                {file.name}
                            </div>
                        </div>
                    ))}
                </div>
            )}

            {/* Upload Button */}
            {files.length > 0 && (
                <div className="flex justify-end">
                    <button
                        onClick={handleUpload}
                        disabled={uploading}
                        className="bg-brand-600 hover:bg-brand-700 text-white px-4 py-2 rounded-lg text-sm font-medium flex items-center gap-2 shadow-sm disabled:opacity-50"
                    >
                        {uploading ? <Loader2 className="animate-spin" size={16} /> : <Upload size={16} />}
                        {uploading ? 'Uploading...' : 'Upload Selected Files'}
                    </button>
                </div>
            )}
        </div>
    );
};

export default ProductMediaUploader;
