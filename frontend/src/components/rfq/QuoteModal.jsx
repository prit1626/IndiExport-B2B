import React, { useState, useMemo } from 'react';
import { useForm, useWatch } from 'react-hook-form';
import { motion, AnimatePresence } from 'framer-motion';
import { X, Send, Loader2, AlertCircle, Calendar } from 'lucide-react';
import rfqApi from '../../api/rfqApi';
import toast from 'react-hot-toast';

const QuoteModal = ({ isOpen, onClose, rfq, onSuccess }) => {
    const { register, handleSubmit, formState: { errors }, reset, control } = useForm({
        defaultValues: {
            leadTimeDays: '',
            notes: '',
            quotedPriceInrPaise: '',
            shippingEstimateInrPaise: '',
            validityUntil: '',
        }
    });

    const watchedPrice = useWatch({ control, name: 'quotedPriceInrPaise' });
    const watchedShipping = useWatch({ control, name: 'shippingEstimateInrPaise' });

    const totals = useMemo(() => {
        const price = parseFloat(watchedPrice) || 0;
        const shipping = parseFloat(watchedShipping) || 0;
        const subtotal = price * (rfq?.quantity || 0);
        const grandTotal = subtotal + shipping;
        return { subtotal, grandTotal };
    }, [watchedPrice, watchedShipping, rfq?.quantity]);

    const [submitting, setSubmitting] = useState(false);

    const onSubmit = async (data) => {
        setSubmitting(true);
        try {
            const payload = {
                // Price entered in INR (decimal) → convert to paise
                quotedPriceInrPaise: Math.round(parseFloat(data.quotedPriceInrPaise) * 100),
                shippingEstimateInrPaise: data.shippingEstimateInrPaise
                    ? Math.round(parseFloat(data.shippingEstimateInrPaise) * 100)
                    : null,
                leadTimeDays: parseInt(data.leadTimeDays),
                notes: data.notes || null,
                // Convert local date string to ISO-8601 Instant (end of day UTC)
                validityUntil: new Date(data.validityUntil + 'T23:59:59Z').toISOString(),
            };

            await rfqApi.sellerSubmitQuote(rfq.id, payload);
            toast.success('Quote submitted successfully!');
            reset();
            onSuccess();
            onClose();
        } catch (error) {
            console.error(error);
            const msg = error.response?.data?.message || error.response?.data?.errors?.join(', ') || 'Failed to submit quote';
            toast.error(msg);
        } finally {
            setSubmitting(false);
        }
    };

    // Minimum validity date = tomorrow
    const minDate = new Date();
    minDate.setDate(minDate.getDate() + 1);
    const minDateStr = minDate.toISOString().split('T')[0];

    if (!isOpen) return null;

    const fieldCls = (hasError) =>
        `w-full px-3 py-2 border rounded-lg outline-none focus:ring-2 focus:ring-brand-500/20 transition-all ${hasError ? 'border-red-400 bg-red-50' : 'border-slate-300'}`;

    return (
        <AnimatePresence>
            <div className="fixed inset-0 z-50 flex items-center justify-center p-4 bg-black/50 backdrop-blur-sm">
                <motion.div
                    initial={{ opacity: 0, scale: 0.95 }}
                    animate={{ opacity: 1, scale: 1 }}
                    exit={{ opacity: 0, scale: 0.95 }}
                    className="bg-white rounded-2xl shadow-2xl w-full max-w-lg overflow-hidden"
                >
                    {/* Header */}
                    <div className="px-6 py-4 border-b border-slate-100 flex justify-between items-center bg-gradient-to-r from-brand-50 to-slate-50">
                        <div>
                            <h2 className="text-xl font-bold text-slate-800">Submit Quote</h2>
                            <p className="text-xs text-slate-500 mt-0.5">For: <span className="font-medium text-slate-700">{rfq.title}</span></p>
                        </div>
                        <button onClick={onClose} className="p-1.5 rounded-full hover:bg-slate-200 text-slate-500 transition-colors">
                            <X size={18} />
                        </button>
                    </div>

                    {/* Form */}
                    <form onSubmit={handleSubmit(onSubmit)} className="p-6 space-y-4 max-h-[70vh] overflow-y-auto">

                        {/* Context Banner */}
                        {rfq.targetPriceINRPaise && (
                            <div className="bg-emerald-50 border border-emerald-100 rounded-lg p-3 text-sm text-emerald-700 flex gap-2 items-start">
                                <AlertCircle size={15} className="mt-0.5 shrink-0" />
                                <div>
                                    <p className="font-medium text-emerald-800">Buyer's target:</p>
                                    <p className="text-lg font-bold">
                                        {new Intl.NumberFormat('en-US', { style: 'currency', currency: rfq.targetCurrency || 'USD' }).format(rfq.targetPriceMinor / 100)}
                                        <span className="text-xs font-normal ml-2 opacity-80">(≈ ₹{(rfq.targetPriceINRPaise / 100).toLocaleString()})</span>
                                    </p>
                                    <p className="text-[10px] opacity-70">per {rfq.unit}</p>
                                </div>
                            </div>
                        )}

                        {/* Price per unit */}
                        <div>
                            <label className="block text-sm font-medium text-slate-700 mb-1">
                                Your Price per {rfq.unit} (₹ INR) <span className="text-red-500">*</span>
                            </label>
                            <input
                                type="number"
                                step="0.01"
                                min="0.01"
                                className={fieldCls(errors.quotedPriceInrPaise)}
                                placeholder="e.g. 250.00"
                                {...register('quotedPriceInrPaise', {
                                    required: 'Price is required',
                                    min: { value: 0.01, message: 'Price must be greater than 0' }
                                })}
                            />
                            {errors.quotedPriceInrPaise && <p className="text-red-500 text-xs mt-1">{errors.quotedPriceInrPaise.message}</p>}
                        </div>

                        <div className="grid grid-cols-2 gap-4">
                            {/* Shipping Estimate */}
                            <div>
                                <label className="block text-sm font-medium text-slate-700 mb-1">
                                    Shipping Cost (₹ INR)
                                </label>
                                <input
                                    type="number"
                                    step="0.01"
                                    min="0"
                                    className={fieldCls(errors.shippingEstimateInrPaise)}
                                    placeholder="Optional"
                                    {...register('shippingEstimateInrPaise', {
                                        min: { value: 0, message: 'Cannot be negative' }
                                    })}
                                />
                                {errors.shippingEstimateInrPaise && <p className="text-red-500 text-xs mt-1">{errors.shippingEstimateInrPaise.message}</p>}
                            </div>

                            {/* Lead Time */}
                            <div>
                                <label className="block text-sm font-medium text-slate-700 mb-1">
                                    Lead Time (Days) <span className="text-red-500">*</span>
                                </label>
                                <input
                                    type="number"
                                    min="1"
                                    className={fieldCls(errors.leadTimeDays)}
                                    placeholder="e.g. 14"
                                    {...register('leadTimeDays', {
                                        required: 'Lead time is required',
                                        min: { value: 1, message: 'At least 1 day' }
                                    })}
                                />
                                {errors.leadTimeDays && <p className="text-red-500 text-xs mt-1">{errors.leadTimeDays.message}</p>}
                            </div>
                        </div>

                        {/* Quote Validity Until */}
                        <div>
                            <label className="block text-sm font-medium text-slate-700 mb-1">
                                <span className="flex items-center gap-1.5">
                                    <Calendar size={14} /> Quote Valid Until <span className="text-red-500">*</span>
                                </span>
                            </label>
                            <input
                                type="date"
                                min={minDateStr}
                                className={fieldCls(errors.validityUntil)}
                                {...register('validityUntil', {
                                    required: 'Please set how long this quote is valid'
                                })}
                            />
                            {errors.validityUntil && <p className="text-red-500 text-xs mt-1">{errors.validityUntil.message}</p>}
                            <p className="text-xs text-slate-400 mt-1">Your quote will automatically expire after this date</p>
                        </div>

                        {/* Calculations Summary */}
                        {(totals.subtotal > 0 || totals.grandTotal > 0) && (
                            <div className="bg-slate-50 border border-slate-200 rounded-xl p-4 space-y-2">
                                <div className="flex justify-between text-sm text-slate-600">
                                    <span>Subtotal ({rfq.quantity} {rfq.unit} × ₹{parseFloat(watchedPrice || 0).toLocaleString()})</span>
                                    <span className="font-semibold text-slate-800">₹{totals.subtotal.toLocaleString(undefined, { minimumFractionDigits: 2, maximumFractionDigits: 2 })}</span>
                                </div>
                                {parseFloat(watchedShipping) > 0 && (
                                    <div className="flex justify-between text-sm text-slate-600">
                                        <span>Shipping Cost</span>
                                        <span className="font-semibold text-slate-800">₹{parseFloat(watchedShipping).toLocaleString(undefined, { minimumFractionDigits: 2, maximumFractionDigits: 2 })}</span>
                                    </div>
                                )}
                                <div className="pt-2 border-t border-slate-200 flex justify-between items-center">
                                    <span className="text-base font-bold text-slate-900">Grand Total</span>
                                    <span className="text-xl font-black text-brand-600">₹{totals.grandTotal.toLocaleString(undefined, { minimumFractionDigits: 2, maximumFractionDigits: 2 })}</span>
                                </div>
                            </div>
                        )}

                        {/* Notes */}
                        <div>
                            <label className="block text-sm font-medium text-slate-700 mb-1">Notes</label>
                            <textarea
                                rows={3}
                                className="w-full px-3 py-2 border border-slate-300 rounded-lg outline-none focus:ring-2 focus:ring-brand-500/20 resize-none transition-all focus:border-brand-300"
                                placeholder="Shipping port, packaging specs, payment terms, etc."
                                {...register('notes')}
                            />
                        </div>

                        {/* Actions */}
                        <div className="flex justify-end gap-3 pt-3 border-t border-slate-100">
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
                                className="px-6 py-2 bg-gradient-to-r from-brand-600 to-brand-700 hover:from-brand-700 hover:to-brand-800 text-white rounded-lg font-semibold shadow-md shadow-brand-200 flex items-center gap-2 disabled:opacity-60 disabled:cursor-not-allowed transition-all"
                            >
                                {submitting ? <Loader2 size={16} className="animate-spin" /> : <Send size={16} />}
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
