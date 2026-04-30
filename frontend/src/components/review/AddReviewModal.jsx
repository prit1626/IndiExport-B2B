import React, { useState } from 'react';
import { Dialog } from '@headlessui/react';
import { motion } from 'framer-motion';
import { useForm } from 'react-hook-form';
import { X, Star, UploadCloud, FileText } from 'lucide-react';
import { toast } from 'react-hot-toast';
import reviewApi from '../../api/reviewApi';
import uploadApi from '../../api/uploadApi';

const AddReviewModal = ({ isOpen, onClose, order, onSuccess }) => {
    const { register, handleSubmit, formState: { errors, isSubmitting }, reset, watch } = useForm({
        defaultValues: {
            rating: 5,
            productId: order?.items?.[0]?.product?.id || order?.items?.[0]?.productId
        }
    });

    const [selectedFiles, setSelectedFiles] = useState([]);
    const [uploading, setUploading] = useState(false);
    
    const rating = watch('rating');

    const handleFileChange = (e) => {
        if (e.target.files) {
            const files = Array.from(e.target.files);
            if (files.length + selectedFiles.length > 5) {
                toast.error('Maximum 5 photos allowed');
                return;
            }
            setSelectedFiles([...selectedFiles, ...files]);
        }
    };

    const removeFile = (index) => {
        setSelectedFiles(selectedFiles.filter((_, i) => i !== index));
    };

    const onSubmit = async (data) => {
        try {
            setUploading(true);
            let photoUrls = [];

            // 1. Upload files if any
            if (selectedFiles.length > 0) {
                for (const file of selectedFiles) {
                    const url = await uploadApi.uploadFile(file);
                    if (url) photoUrls.push(url);
                }
            }

            // 2. Create Review
            const reviewData = {
                orderId: order.id,
                productId: data.productId,
                rating: parseInt(data.rating),
                reviewText: data.reviewText,
                photoUrls
            };

            await reviewApi.createReview(reviewData);
            toast.success('Review submitted successfully!');
            reset();
            setSelectedFiles([]);
            onSuccess();
            onClose();
        } catch (error) {
            console.error(error);
            toast.error(error.response?.data?.message || 'Failed to submit review');
        } finally {
            setUploading(false);
        }
    };

    if (!order) return null;

    return (
        <Dialog open={isOpen} onClose={onClose} className="relative z-50">
            <div className="fixed inset-0 bg-black/30 backdrop-blur-sm" aria-hidden="true" />

            <div className="fixed inset-0 flex items-center justify-center p-4">
                <Dialog.Panel
                    as={motion.div}
                    initial={{ opacity: 0, scale: 0.95 }}
                    animate={{ opacity: 1, scale: 1 }}
                    className="mx-auto max-w-lg w-full bg-white rounded-xl shadow-2xl overflow-hidden"
                >
                    <div className="px-6 py-4 border-b border-slate-200 flex justify-between items-center bg-slate-50">
                        <Dialog.Title className="text-lg font-bold text-slate-900">Write a Review</Dialog.Title>
                        <button onClick={onClose} className="text-slate-400 hover:text-slate-600 transition-colors">
                            <X size={24} />
                        </button>
                    </div>

                    <form onSubmit={handleSubmit(onSubmit)} className="p-6 space-y-5">
                        {/* Product Selection if multiple items */}
                        {order.items && order.items.length > 1 ? (
                            <div>
                                <label className="block text-sm font-medium text-slate-700 mb-1.5">Select Product to Review</label>
                                <select
                                    {...register('productId', { required: 'Please select a product' })}
                                    className="w-full px-3 py-2 border border-slate-300 rounded-lg text-sm focus:ring-brand-500 focus:border-brand-500"
                                >
                                    {order.items.map(item => (
                                        <option key={item.product?.id || item.productId} value={item.product?.id || item.productId}>
                                            {item.title}
                                        </option>
                                    ))}
                                </select>
                            </div>
                        ) : (
                            <input type="hidden" {...register('productId')} />
                        )}

                        {/* Star Rating */}
                        <div>
                            <label className="block text-sm font-medium text-slate-700 mb-2">Rating</label>
                            <div className="flex gap-2">
                                {[1, 2, 3, 4, 5].map((num) => (
                                    <label key={num} className="cursor-pointer group">
                                        <input
                                            type="radio"
                                            value={num}
                                            {...register('rating')}
                                            className="hidden"
                                        />
                                        <Star
                                            size={32}
                                            className={`transition-colors ${
                                                num <= rating 
                                                ? 'fill-yellow-400 text-yellow-400' 
                                                : 'text-slate-300 group-hover:text-yellow-200'
                                            }`}
                                        />
                                    </label>
                                ))}
                            </div>
                        </div>

                        {/* Review Text */}
                        <div>
                            <label className="block text-sm font-medium text-slate-700 mb-1.5">Your Review</label>
                            <textarea
                                {...register('reviewText', { 
                                    required: 'Review text is required',
                                    minLength: { value: 10, message: 'Review must be at least 10 characters' }
                                })}
                                rows={4}
                                className="w-full px-3 py-2 border border-slate-300 rounded-lg text-sm focus:ring-brand-500 focus:border-brand-500"
                                placeholder="What did you like or dislike? Help other buyers!"
                            />
                            {errors.reviewText && <p className="text-red-500 text-xs mt-1">{errors.reviewText.message}</p>}
                        </div>

                        {/* Photo Upload */}
                        <div>
                            <label className="block text-sm font-medium text-slate-700 mb-1.5">Add Photos (Optional)</label>
                            <div className="flex flex-wrap gap-2 mb-2">
                                {selectedFiles.map((file, idx) => (
                                    <div key={idx} className="relative w-16 h-16 rounded bg-slate-100 border border-slate-200 overflow-hidden group">
                                        <img src={URL.createObjectURL(file)} className="w-full h-full object-cover" alt="preview" />
                                        <button 
                                            type="button"
                                            onClick={() => removeFile(idx)}
                                            className="absolute top-0 right-0 bg-red-500 text-white p-0.5 rounded-bl opacity-0 group-hover:opacity-100 transition-opacity"
                                        >
                                            <X size={12} />
                                        </button>
                                    </div>
                                ))}
                                {selectedFiles.length < 5 && (
                                    <label className="w-16 h-16 rounded border-2 border-dashed border-slate-300 flex flex-col items-center justify-center cursor-pointer hover:border-brand-500 hover:bg-brand-50 transition-colors">
                                        <UploadCloud size={20} className="text-slate-400" />
                                        <input type="file" accept="image/*" multiple onChange={handleFileChange} className="hidden" />
                                    </label>
                                )}
                            </div>
                            <p className="text-[10px] text-slate-400">Up to 5 images. Max 5MB each.</p>
                        </div>

                        {/* Actions */}
                        <div className="pt-2 flex justify-end gap-3">
                            <button
                                type="button"
                                onClick={onClose}
                                className="px-4 py-2 text-slate-600 hover:bg-slate-100 rounded-lg font-medium text-sm transition-colors"
                            >
                                Cancel
                            </button>
                            <button
                                type="submit"
                                disabled={isSubmitting || uploading}
                                className="px-6 py-2 bg-brand-600 text-white rounded-lg hover:bg-brand-700 font-medium text-sm shadow-md transition-all active:scale-95 disabled:opacity-50"
                            >
                                {isSubmitting || uploading ? 'Submitting...' : 'Submit Review'}
                            </button>
                        </div>
                    </form>
                </Dialog.Panel>
            </div>
        </Dialog>
    );
};

export default AddReviewModal;
