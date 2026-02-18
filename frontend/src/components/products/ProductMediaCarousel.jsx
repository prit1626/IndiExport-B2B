import React, { useState } from 'react';
import { motion, AnimatePresence } from 'framer-motion';
import { ChevronLeft, ChevronRight, Maximize2, Play } from 'lucide-react';
import Modal from '../common/Modal';

const ProductMediaCarousel = ({ images = [], videoUrl = null }) => {
    const [currentIndex, setCurrentIndex] = useState(0);
    const [isModalOpen, setIsModalOpen] = useState(false);

    // Combine images and optional video into a single media array
    const media = [
        ...(videoUrl ? [{ type: 'video', url: videoUrl }] : []),
        ...images.map(img => ({ type: 'image', url: img }))
    ];

    if (media.length === 0) return <div className="bg-slate-100 rounded-2xl aspect-square flex items-center justify-center text-slate-400">No Media</div>;

    const handleNext = (e) => {
        e.stopPropagation();
        setCurrentIndex((prev) => (prev + 1) % media.length);
    };

    const handlePrev = (e) => {
        e.stopPropagation();
        setCurrentIndex((prev) => (prev - 1 + media.length) % media.length);
    };

    const currentMedia = media[currentIndex];

    return (
        <div className="space-y-4">
            {/* Main Display */}
            <div
                className="relative aspect-square bg-slate-100 rounded-2xl overflow-hidden group cursor-pointer"
                onClick={() => setIsModalOpen(true)}
            >
                <AnimatePresence mode="wait">
                    <motion.div
                        key={currentIndex}
                        initial={{ opacity: 0 }}
                        animate={{ opacity: 1 }}
                        exit={{ opacity: 0 }}
                        transition={{ duration: 0.3 }}
                        className="w-full h-full"
                    >
                        {currentMedia.type === 'video' ? (
                            <div className="relative w-full h-full flex items-center justify-center bg-black">
                                <video src={currentMedia.url} className="w-full h-full object-contain" controls />
                            </div>
                        ) : (
                            <img src={currentMedia.url} alt={`Product view ${currentIndex + 1}`} className="w-full h-full object-cover" />
                        )}
                    </motion.div>
                </AnimatePresence>

                {/* Navigation Arrows */}
                {media.length > 1 && (
                    <>
                        <button
                            onClick={handlePrev}
                            className="absolute left-4 top-1/2 -translate-y-1/2 p-2 bg-white/80 backdrop-blur-sm rounded-full shadow-lg opacity-0 group-hover:opacity-100 transition-opacity hover:bg-white"
                        >
                            <ChevronLeft size={20} />
                        </button>
                        <button
                            onClick={handleNext}
                            className="absolute right-4 top-1/2 -translate-y-1/2 p-2 bg-white/80 backdrop-blur-sm rounded-full shadow-lg opacity-0 group-hover:opacity-100 transition-opacity hover:bg-white"
                        >
                            <ChevronRight size={20} />
                        </button>
                    </>
                )}

                {/* Fullscreen Icon */}
                <button
                    className="absolute top-4 right-4 p-2 bg-black/50 text-white rounded-full opacity-0 group-hover:opacity-100 transition-opacity hover:bg-black/70"
                >
                    <Maximize2 size={18} />
                </button>
            </div>

            {/* Thumbnail Strip */}
            {media.length > 1 && (
                <div className="flex gap-4 overflow-x-auto pb-2 scrollbar-hide">
                    {media.map((item, idx) => (
                        <button
                            key={idx}
                            onClick={() => setCurrentIndex(idx)}
                            className={`relative flex-shrink-0 w-20 h-20 rounded-lg overflow-hidden border-2 transition-all ${idx === currentIndex ? 'border-brand-600 ring-2 ring-brand-100' : 'border-transparent opacity-70 hover:opacity-100'
                                }`}
                        >
                            {item.type === 'video' ? (
                                <div className="w-full h-full bg-slate-900 flex items-center justify-center">
                                    <Play size={20} className="text-white" />
                                </div>
                            ) : (
                                <img src={item.url} alt={`Thumbnail ${idx}`} className="w-full h-full object-cover" />
                            )}
                        </button>
                    ))}
                </div>
            )}

            {/* Fullscreen Modal */}
            <Modal isOpen={isModalOpen} onClose={() => setIsModalOpen(false)} size="full" title="Media Viewer">
                <div className="flex h-[80vh] items-center justify-center bg-black/5 rounded-xl">
                    {currentMedia.type === 'video' ? (
                        <video src={currentMedia.url} className="max-w-full max-h-full" controls autoPlay />
                    ) : (
                        <img src={currentMedia.url} alt="Fullscreen" className="max-w-full max-h-full object-contain" />
                    )}
                </div>
            </Modal>
        </div>
    );
};

export default ProductMediaCarousel;
