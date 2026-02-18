import React, { useState } from 'react';
import { AnimatePresence, motion } from 'framer-motion';
import { X, CheckCircle, AlertTriangle, Loader2 } from 'lucide-react';

const FinalizeConfirmModal = ({ isOpen, onClose, onConfirm, quote, submitting }) => {
    if (!isOpen || !quote) return null;

    return (
        <AnimatePresence>
            <div className="fixed inset-0 z-50 flex items-center justify-center p-4 bg-black/50 backdrop-blur-sm">
                <motion.div
                    initial={{ opacity: 0, scale: 0.95 }}
                    animate={{ opacity: 1, scale: 1 }}
                    exit={{ opacity: 0, scale: 0.95 }}
                    className="bg-white rounded-xl shadow-2xl w-full max-w-md overflow-hidden"
                >
                    <div className="p-6">
                        <div className="flex items-center justify-between mb-4">
                            <h2 className="text-xl font-bold text-slate-800">Confirm Finalization</h2>
                            <button onClick={onClose} disabled={submitting} className="text-slate-400 hover:text-slate-600">
                                <X size={24} />
                            </button>
                        </div>

                        <div className="bg-yellow-50 border border-yellow-100 rounded-lg p-3 flex gap-3 mb-6">
                            <AlertTriangle className="text-yellow-600 flex-shrink-0" size={20} />
                            <p className="text-sm text-yellow-700">
                                Proceeding will accept this quote and generate an order. You will be redirected to payment.
                            </p>
                        </div>

                        <div className="bg-slate-50 rounded-lg p-4 border border-slate-100 mb-6">
                            <h3 className="font-semibold text-slate-900 mb-2">Quote Summary</h3>
                            <div className="flex justify-between text-sm mb-1">
                                <span className="text-slate-500">Seller</span>
                                <span className="font-medium text-slate-900">{quote.sellerName}</span>
                            </div>
                            <div className="flex justify-between text-sm mb-1">
                                <span className="text-slate-500">Price</span>
                                <span className="font-medium text-slate-900">₹{(quote.priceINRPaise / 100).toLocaleString()}</span>
                            </div>
                            <div className="flex justify-between text-sm">
                                <span className="text-slate-500">Terms</span>
                                <span className="font-medium text-slate-900">{quote.incoterm}</span>
                            </div>
                        </div>

                        <div className="flex gap-3">
                            <button
                                onClick={onClose}
                                disabled={submitting}
                                className="flex-1 py-3 text-slate-600 font-medium hover:bg-slate-50 rounded-lg border border-slate-200 transition-colors"
                            >
                                Cancel
                            </button>
                            <button
                                onClick={onConfirm}
                                disabled={submitting}
                                className="flex-1 py-3 bg-brand-600 hover:bg-brand-700 text-white font-semibold rounded-lg shadow-lg flex items-center justify-center gap-2 transition-all"
                            >
                                {submitting ? <Loader2 className="animate-spin" size={20} /> : <CheckCircle size={20} />}
                                Confirm & Pay
                            </button>
                        </div>
                    </div>
                </motion.div>
            </div>
        </AnimatePresence>
    );
};

export default FinalizeConfirmModal;
