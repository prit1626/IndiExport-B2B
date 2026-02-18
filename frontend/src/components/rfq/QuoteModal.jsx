import React, { useState } from 'react';
import { useForm } from 'react-hook-form';
import { motion, AnimatePresence } from 'framer-motion';
import { X, Send, Loader2, AlertCircle } from 'lucide-react';
import rfqApi from '../../api/rfqApi';
import toast from 'react-hot-toast';

const QuoteModal = ({ isOpen, onClose, rfq, onSuccess }) => {
    const { register, handleSubmit, formState: { errors }, reset } = useForm({
        defaultValues: {
            minQty: rfq?.qty,
            incoterm: rfq?.incoterm
        }
    });

    const [submitting, setSubmitting] = useState(false);

    const onSubmit = async (data) => {
        setSubmitting(true);
        try {
            // Convert price to paise
            const payload = {
                ...data,
                priceINRPaise: Math.round(parseFloat(data.priceINRPaise) * 100),
                minQty: parseInt(data.minQty),
                leadTimeDays: parseInt(data.leadTimeDays)
            };

            await rfqApi.sellerSubmitQuote(rfq.id, payload);
            toast.success('Quote submitted successfully!');
            reset();
            onSuccess();
            onClose();
        } catch (error) {
            console.error(error);
            toast.error(error.response?.data?.message || 'Failed to submit quote');
        } finally {
            setSubmitting(false);
        }
    };

    if (!isOpen) return null;

    return (
        <AnimatePresence>
            <div className="fixed inset-0 z-50 flex items-center justify-center p-4 bg-black/50 backdrop-blur-sm">
                <motion.div
                    initial={{ opacity: 0, scale: 0.95 }}
                    animate={{ opacity: 1, scale: 1 }}
                    exit={{ opacity: 0, scale: 0.95 }}
                    className="bg-white rounded-xl shadow-2xl w-full max-w-lg overflow-hidden"
                >
                    {/* Header */}
                    <div className="px-6 py-4 border-b border-slate-100 flex justify-between items-center bg-slate-50">
                        <h2 className="text-xl font-bold text-slate-800">Submit Quote</h2>
                        <button onClick={onClose} className="p-1 rounded-full hover:bg-slate-200 text-slate-500 transition-colors">
                            <X size={20} />
                        </button>
                    </div>

                    {/* Form */}
                    <form onSubmit={handleSubmit(onSubmit)} className="p-6 space-y-4">

                        {/* Validation Notice */}
                        <div className="bg-blue-50 border border-blue-100 rounded-lg p-3 text-sm text-blue-700 flex gap-2 items-start">
                            <AlertCircle size={16} className="mt-0.5" />
                            <p>You are quoting for <strong>{rfq.title}</strong>. Target price: {rfq.targetPriceINRPaise ? `₹${(rfq.targetPriceINRPaise / 100).toLocaleString()}` : 'Not specified'}.</p>
                        </div>

                        {/* Price & Unit */}
                        <div>
                            <label className="block text-sm font-medium text-slate-700 mb-1">
                                Price per {rfq.unit} (INR) <span className="text-red-500">*</span>
                            </label>
                            <input
                                type="number"
                                step="0.01"
                                className={`w-full px-3 py-2 border rounded-lg outline-none focus:ring-2 focus:ring-brand-500/20 transition-all ${errors.priceINRPaise ? 'border-red-500' : 'border-slate-300'}`}
                                placeholder="e.g. 250.00"
                                {...register('priceINRPaise', { required: 'Price is required', min: { value: 1, message: 'Invalid price' } })}
                            />
                            {errors.priceINRPaise && <p className="text-red-500 text-xs mt-1">{errors.priceINRPaise.message}</p>}
                        </div>

                        <div className="grid grid-cols-2 gap-4">
                            {/* Min Qty */}
                            <div>
                                <label className="block text-sm font-medium text-slate-700 mb-1">
                                    Min Qty ({rfq.unit}) <span className="text-red-500">*</span>
                                </label>
                                <input
                                    type="number"
                                    className={`w-full px-3 py-2 border rounded-lg outline-none focus:ring-2 focus:ring-brand-500/20 transition-all ${errors.minQty ? 'border-red-500' : 'border-slate-300'}`}
                                    {...register('minQty', { required: 'Required', min: 1 })}
                                />
                                {errors.minQty && <p className="text-red-500 text-xs mt-1">{errors.minQty.message}</p>}
                            </div>

                            {/* Lead Time */}
                            <div>
                                <label className="block text-sm font-medium text-slate-700 mb-1">
                                    Lead Time (Days) <span className="text-red-500">*</span>
                                </label>
                                <input
                                    type="number"
                                    className={`w-full px-3 py-2 border rounded-lg outline-none focus:ring-2 focus:ring-brand-500/20 transition-all ${errors.leadTimeDays ? 'border-red-500' : 'border-slate-300'}`}
                                    {...register('leadTimeDays', { required: 'Required', min: 0 })}
                                />
                                {errors.leadTimeDays && <p className="text-red-500 text-xs mt-1">{errors.leadTimeDays.message}</p>}
                            </div>
                        </div>

                        {/* Incoterm */}
                        <div>
                            <label className="block text-sm font-medium text-slate-700 mb-1">
                                Incoterm <span className="text-red-500">*</span>
                            </label>
                            <select
                                className="w-full px-3 py-2 border border-slate-300 rounded-lg outline-none focus:ring-2 focus:ring-brand-500/20 bg-white"
                                {...register('incoterm', { required: 'Incoterm is required' })}
                            >
                                <option value="EXW">EXW - Ex Works</option>
                                <option value="FOB">FOB - Free on Board</option>
                                <option value="CIF">CIF - Cost, Insurance & Freight</option>
                                <option value="DDP">DDP - Delivered Duty Paid</option>
                            </select>
                        </div>

                        {/* Notes */}
                        <div>
                            <label className="block text-sm font-medium text-slate-700 mb-1">
                                Notes
                            </label>
                            <textarea
                                rows="3"
                                className="w-full px-3 py-2 border border-slate-300 rounded-lg outline-none focus:ring-2 focus:ring-brand-500/20 resize-none"
                                placeholder="Additional details like shipping port, packaging, etc."
                                {...register('notes')}
                            />
                        </div>

                        {/* Actions */}
                        <div className="flex justify-end gap-3 pt-4 border-t border-slate-100">
                            <button
                                type="button"
                                onClick={onClose}
                                className="px-4 py-2 text-slate-600 hover:bg-slate-100 rounded-lg font-medium transition-colors"
                            >
                                Cancel
                            </button>
                            <button
                                type="submit"
                                disabled={submitting}
                                className="px-6 py-2 bg-brand-600 hover:bg-brand-700 text-white rounded-lg font-medium shadow-sm flex items-center gap-2 disabled:opacity-70 disabled:cursor-not-allowed transition-all"
                            >
                                {submitting ? <Loader2 size={18} className="animate-spin" /> : <Send size={18} />}
                                Submit Quote
                            </button>
                        </div>

                    </form>
                </motion.div>
            </div>
        </AnimatePresence>
    );
};

export default QuoteModal;
