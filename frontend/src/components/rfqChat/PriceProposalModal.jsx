import React, { useState } from 'react';
import { motion, AnimatePresence } from 'framer-motion';
import { X, DollarSign, Clock, Loader2 } from 'lucide-react';
import toast from 'react-hot-toast';
import rfqChatApi from '../../api/rfqChatApi';

const PriceProposalModal = ({ isOpen, onClose, chatId, onProposalSent }) => {
    const [price, setPrice] = useState('');
    const [currency, setCurrency] = useState('INR'); // Default to INR to match order prep logic
    const [leadTime, setLeadTime] = useState('');
    const [submitting, setSubmitting] = useState(false);

    if (!isOpen) return null;

    const handleSubmit = async (e) => {
        e.preventDefault();
        if (!price || !leadTime) return;

        setSubmitting(true);
        try {
            // Price must be converted to minor units (paise/cents)
            const priceMinor = Math.round(parseFloat(price) * 100);

            const payload = {
                proposedPriceMinor: priceMinor,
                currency,
                leadTimeDays: parseInt(leadTime, 10)
            };

            await rfqChatApi.sendPriceProposal(chatId, payload);
            toast.success('Price proposal sent!');
            if (onProposalSent) onProposalSent();
            onClose();
        } catch (error) {
            console.error(error);
            toast.error(error.response?.data?.message || 'Failed to send proposal');
        } finally {
            setSubmitting(false);
        }
    };

    return (
        <AnimatePresence>
            <div className="fixed inset-0 z-50 flex items-center justify-center p-4 bg-slate-900/50 backdrop-blur-sm">
                <motion.div
                    initial={{ opacity: 0, scale: 0.95 }}
                    animate={{ opacity: 1, scale: 1 }}
                    exit={{ opacity: 0, scale: 0.95 }}
                    className="bg-white rounded-2xl shadow-xl w-full max-w-md overflow-hidden flex flex-col"
                >
                    <div className="flex items-center justify-between p-4 border-b border-slate-100 bg-slate-50">
                        <h3 className="font-bold text-slate-800">Send Price Proposal</h3>
                        <button onClick={onClose} className="p-1.5 text-slate-400 hover:bg-slate-200 rounded-lg transition-colors">
                            <X size={18} />
                        </button>
                    </div>

                    <form onSubmit={handleSubmit} className="p-5 space-y-4">
                        <div className="space-y-1.5">
                            <label className="text-sm font-medium text-slate-700">Proposed Price</label>
                            <div className="flex gap-2">
                                <select
                                    value={currency}
                                    onChange={(e) => setCurrency(e.target.value)}
                                    className="bg-slate-50 border border-slate-200 text-slate-800 text-sm rounded-lg focus:ring-brand-500 focus:border-brand-500 block p-2.5 outline-none"
                                >
                                    <option value="INR">INR</option>
                                    <option value="USD">USD</option>
                                    <option value="EUR">EUR</option>
                                </select>
                                <div className="relative flex-1">
                                    <div className="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none">
                                        <DollarSign size={14} className="text-slate-400" />
                                    </div>
                                    <input
                                        type="number"
                                        step="0.01"
                                        min="0"
                                        required
                                        value={price}
                                        onChange={(e) => setPrice(e.target.value)}
                                        className="bg-slate-50 border border-slate-200 text-slate-900 text-sm rounded-lg focus:ring-brand-500 focus:border-brand-500 block w-full pl-9 p-2.5 outline-none"
                                        placeholder="0.00"
                                    />
                                </div>
                            </div>
                            <p className="text-xs text-slate-500">Enter price per unit.</p>
                        </div>

                        <div className="space-y-1.5">
                            <label className="text-sm font-medium text-slate-700">Lead Time (Days)</label>
                            <div className="relative">
                                <div className="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none">
                                    <Clock size={14} className="text-slate-400" />
                                </div>
                                <input
                                    type="number"
                                    min="1"
                                    required
                                    value={leadTime}
                                    onChange={(e) => setLeadTime(e.target.value)}
                                    className="bg-slate-50 border border-slate-200 text-slate-900 text-sm rounded-lg focus:ring-brand-500 focus:border-brand-500 block w-full pl-9 p-2.5 outline-none"
                                    placeholder="e.g. 15"
                                />
                            </div>
                        </div>

                        <div className="pt-4 flex gap-3">
                            <button
                                type="button"
                                onClick={onClose}
                                className="flex-1 px-4 py-2.5 border border-slate-300 hover:bg-slate-50 text-slate-700 rounded-xl font-medium transition-colors"
                            >
                                Cancel
                            </button>
                            <button
                                type="submit"
                                disabled={submitting || !price || !leadTime}
                                className="flex-1 px-4 py-2.5 bg-brand-600 hover:bg-brand-700 disabled:bg-brand-400 text-white rounded-xl font-medium transition-colors flex items-center justify-center gap-2"
                            >
                                {submitting && <Loader2 size={16} className="animate-spin" />}
                                Send Proposal
                            </button>
                        </div>
                    </form>
                </motion.div>
            </div>
        </AnimatePresence>
    );
};

export default PriceProposalModal;
