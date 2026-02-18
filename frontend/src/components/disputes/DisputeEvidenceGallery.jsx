
import React, { useState } from 'react';
import { X, Image as ImageIcon } from 'lucide-react';
import { motion, AnimatePresence } from 'framer-motion';

const DisputeEvidenceGallery = ({ evidence = [] }) => {
    const [selectedImage, setSelectedImage] = useState(null);

    if (!evidence || evidence.length === 0) return null;

    return (
        <div className="mt-4">
            <h4 className="text-sm font-medium text-slate-700 mb-2 flex items-center gap-2">
                <ImageIcon size={16} /> Evidence ({evidence.length})
            </h4>
            <div className="grid grid-cols-3 sm:grid-cols-4 md:grid-cols-5 gap-2">
                {evidence.map((item, index) => (
                    <div
                        key={index}
                        onClick={() => setSelectedImage(item.url)}
                        className="aspect-square rounded-lg overflow-hidden border border-slate-200 cursor-pointer hover:opacity-80 transition-opacity bg-slate-50"
                    >
                        <img
                            src={item.url}
                            alt={`Evidence ${index + 1}`}
                            className="w-full h-full object-cover"
                        />
                    </div>
                ))}
            </div>

            {/* Lightbox Modal */}
            <AnimatePresence>
                {selectedImage && (
                    <motion.div
                        initial={{ opacity: 0 }}
                        animate={{ opacity: 1 }}
                        exit={{ opacity: 0 }}
                        className="fixed inset-0 z-50 bg-black/90 flex items-center justify-center p-4"
                        onClick={() => setSelectedImage(null)}
                    >
                        <button
                            onClick={() => setSelectedImage(null)}
                            className="absolute top-4 right-4 text-white hover:text-slate-300 p-2"
                        >
                            <X size={32} />
                        </button>
                        <motion.img
                            initial={{ scale: 0.9 }}
                            animate={{ scale: 1 }}
                            exit={{ scale: 0.9 }}
                            src={selectedImage}
                            alt="Evidence Full View"
                            className="max-w-full max-h-[90vh] object-contain rounded-md"
                            onClick={(e) => e.stopPropagation()}
                        />
                    </motion.div>
                )}
            </AnimatePresence>
        </div>
    );
};

export default DisputeEvidenceGallery;
