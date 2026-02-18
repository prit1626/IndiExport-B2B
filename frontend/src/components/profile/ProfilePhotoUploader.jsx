import React, { useState, useRef } from 'react';
import { Camera, Upload, Check, X, Loader2 } from 'lucide-react';
import { motion, AnimatePresence } from 'framer-motion';
import toast from 'react-hot-toast';

const ProfilePhotoUploader = ({ currentPhoto, onUpload, type = 'user' }) => {
    const [preview, setPreview] = useState(null);
    const [uploading, setUploading] = useState(false);
    const fileInputRef = useRef(null);

    const handleFileChange = (e) => {
        const file = e.target.files[0];
        if (!file) return;

        if (file.size > 2 * 1024 * 1024) {
            toast.error("File size must be less than 2MB");
            return;
        }

        if (!file.type.match('image.*')) {
            toast.error("Only images are allowed");
            return;
        }

        const reader = new FileReader();
        reader.onloadend = () => setPreview(reader.result);
        reader.readAsDataURL(file);
    };

    const handleUpload = async () => {
        if (!fileInputRef.current.files[0]) return;

        setUploading(true);
        const formData = new FormData();
        formData.append('file', fileInputRef.current.files[0]);

        try {
            await onUpload(formData);
            toast.success("Photo updated successfully");
            setPreview(null);
        } catch (error) {
            console.error("Upload error:", error);
            toast.error("Failed to upload photo");
        } finally {
            setUploading(false);
        }
    };

    const cancelPreview = () => {
        setPreview(null);
        fileInputRef.current.value = '';
    };

    return (
        <div className="flex flex-col items-center">
            <div className="relative group">
                <div className="w-32 h-32 rounded-3xl overflow-hidden border-4 border-slate-100 dark:border-slate-800 shadow-lg bg-slate-50 dark:bg-slate-900 group-hover:shadow-2xl transition-all duration-300">
                    <img
                        src={preview || currentPhoto || (type === 'user' ? 'https://via.placeholder.com/150?text=User' : 'https://via.placeholder.com/150?text=Company')}
                        alt="Profile"
                        className="w-full h-full object-cover"
                    />

                    {!preview && (
                        <button
                            onClick={() => fileInputRef.current?.click()}
                            className="absolute inset-0 bg-black/40 text-white flex flex-col items-center justify-center opacity-0 group-hover:opacity-100 transition-opacity duration-300"
                        >
                            <Camera size={24} className="mb-1" />
                            <span className="text-[10px] font-bold uppercase tracking-wider">Change Photo</span>
                        </button>
                    )}
                </div>

                <AnimatePresence>
                    {preview && (
                        <motion.div
                            initial={{ scale: 0.8, opacity: 0 }}
                            animate={{ scale: 1, opacity: 1 }}
                            exit={{ scale: 0.8, opacity: 0 }}
                            className="absolute -bottom-2 -right-2 flex gap-1"
                        >
                            <button
                                onClick={handleUpload}
                                disabled={uploading}
                                className="bg-emerald-500 text-white p-2 rounded-xl shadow-lg hover:bg-emerald-600 transition-colors"
                            >
                                {uploading ? <Loader2 size={18} className="animate-spin" /> : <Check size={18} />}
                            </button>
                            <button
                                onClick={cancelPreview}
                                disabled={uploading}
                                className="bg-rose-500 text-white p-2 rounded-xl shadow-lg hover:bg-rose-600 transition-colors"
                            >
                                <X size={18} />
                            </button>
                        </motion.div>
                    )}
                </AnimatePresence>
            </div>

            <input
                type="file"
                ref={fileInputRef}
                onChange={handleFileChange}
                accept="image/*"
                className="hidden"
            />

            <p className="mt-4 text-[11px] font-bold text-slate-400 uppercase tracking-[0.2em]">
                {type === 'user' ? 'Profile Picture' : 'Company Logo'}
            </p>
            <p className="text-[10px] text-slate-400 mt-1">PNG, JPG up to 2MB</p>
        </div>
    );
};

export default ProfilePhotoUploader;
