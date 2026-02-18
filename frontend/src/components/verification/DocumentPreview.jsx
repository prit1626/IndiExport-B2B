
import React from 'react';
import { X } from 'lucide-react';
import { motion, AnimatePresence } from 'framer-motion';

const DocumentPreview = ({ isOpen, onClose, fileUrl, title }) => {
    if (!isOpen) return null;

    const isPdf = fileUrl?.toLowerCase().endsWith('.pdf');

    return (
        <AnimatePresence>
            <div className="fixed inset-0 z-50 flex items-center justify-center p-4 sm:p-6">
                <motion.div
                    initial={{ opacity: 0 }}
                    animate={{ opacity: 1 }}
                    exit={{ opacity: 0 }}
                    onClick={onClose}
                    className="absolute inset-0 bg-black/80 backdrop-blur-sm"
                ></motion.div>

                <motion.div
                    initial={{ scale: 0.95, opacity: 0 }}
                    animate={{ scale: 1, opacity: 1 }}
                    exit={{ scale: 0.95, opacity: 0 }}
                    className="relative bg-white rounded-xl shadow-2xl w-full max-w-5xl h-[85vh] flex flex-col overflow-hidden z-10"
                >
                    <div className="flex items-center justify-between px-6 py-4 border-b border-slate-200 bg-white">
                        <h3 className="font-semibold text-slate-900">{title || 'Document Preview'}</h3>
                        <button onClick={onClose} className="p-2 hover:bg-slate-100 rounded-full text-slate-500 transition-colors">
                            <X size={20} />
                        </button>
                    </div>

                    <div className="flex-1 bg-slate-50 overflow-auto flex items-center justify-center p-4">
                        {fileUrl ? (
                            isPdf ? (
                                <iframe src={fileUrl} className="w-full h-full rounded-lg border border-slate-200" title="PDF Preview"></iframe>
                            ) : (
                                <img src={fileUrl} alt="Preview" className="max-w-full max-h-full object-contain shadow-lg rounded" />
                            )
                        ) : (
                            <p className="text-slate-400">No document to preview</p>
                        )}
                    </div>
                </motion.div>
            </div>
        </AnimatePresence>
    );
};

export default DocumentPreview;
