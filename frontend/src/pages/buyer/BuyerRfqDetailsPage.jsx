import React, { useState, useEffect, useCallback } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { motion, AnimatePresence } from 'framer-motion';
import {
    ArrowLeft, CheckCircle, Star, Shield, Clock, Package, Globe,
    TrendingDown, Loader2, AlertTriangle, ChevronDown, ChevronUp, XCircle, MessageSquare
} from 'lucide-react';
import rfqApi from '../../api/rfqApi';
import toast from 'react-hot-toast';

const STATUS_CONFIG = {
    OPEN: { label: 'Open', className: 'bg-emerald-100 text-emerald-700' },
    UNDER_NEGOTIATION: { label: 'Negotiating', className: 'bg-blue-100 text-blue-700' },
    FINALIZED: { label: 'Finalized', className: 'bg-violet-100 text-violet-700' },
    CONVERTED_TO_ORDER: { label: 'Converted to Order', className: 'bg-cyan-100 text-cyan-700' },
    CANCELLED: { label: 'Cancelled', className: 'bg-red-100 text-red-700' },
    EXPIRED: { label: 'Expired', className: 'bg-slate-100 text-slate-500' },
};

const QUOTE_STATUS_CONFIG = {
    ACTIVE: { label: 'Active', className: 'bg-emerald-100 text-emerald-700' },
    ACCEPTED: { label: 'Accepted ✓', className: 'bg-brand-100 text-brand-700' },
    WITHDRAWN: { label: 'Withdrawn', className: 'bg-slate-100 text-slate-500' },
};

const fmtINR = (paise) => {
    if (!paise) return 'N/A';
    return new Intl.NumberFormat('en-IN', { style: 'currency', currency: 'INR', maximumFractionDigits: 0 }).format(paise / 100);
};

export default function BuyerRfqDetailsPage() {
    const { rfqId } = useParams();
    const navigate = useNavigate();
    const [rfq, setRfq] = useState(null);
    const [loading, setLoading] = useState(true);
    const [finalizing, setFinalizing] = useState(false);
    const [selectedQuoteId, setSelectedQuoteId] = useState(null);
    const [confirmOpen, setConfirmOpen] = useState(false);
    const [sortBy, setSortBy] = useState('price'); // price | lead | rating
    const [cancelling, setCancelling] = useState(false);

    const fetchRfq = useCallback(async () => {
        setLoading(true);
        try {
            const res = await rfqApi.getMyRfq(rfqId);
            setRfq(res.data);
        } catch (err) {
            toast.error(err.response?.data?.message || 'Failed to load RFQ');
            navigate('/buyer/rfq');
        } finally {
            setLoading(false);
        }
    }, [rfqId, navigate]);

    useEffect(() => { fetchRfq(); }, [fetchRfq]);

    const handleFinalize = async () => {
        if (!selectedQuoteId) { toast.error('Select a quote to finalize'); return; }
        setFinalizing(true);
        try {
            const res = await rfqApi.finalizeRfq(rfqId, selectedQuoteId);
            const { orderId, invoiceId } = res.data;
            toast.success('RFQ finalized! Redirecting to payment...');
            navigate(`/buyer/orders/${orderId}/pay`);
        } catch (err) {
            toast.error(err.response?.data?.message || 'Finalization failed');
        } finally {
            setFinalizing(false);
            setConfirmOpen(false);
        }
    };

    const handleCancel = async () => {
        if (!window.confirm('Cancel this RFQ? This cannot be undone.')) return;
        setCancelling(true);
        try {
            await rfqApi.cancelRfq(rfqId);
            toast.success('RFQ cancelled');
            navigate('/buyer/rfq');
        } catch (err) {
            toast.error(err.response?.data?.message || 'Failed to cancel RFQ');
        } finally {
            setCancelling(false);
        }
    };

    if (loading) return (
        <div className="min-h-screen flex items-center justify-center">
            <Loader2 className="animate-spin text-brand-600" size={36} />
        </div>
    );

    if (!rfq) return null;

    const canFinalize = rfq.status === 'OPEN' || rfq.status === 'UNDER_NEGOTIATION';
    const activeQuotes = (rfq.quotes || []).filter(q => q.status !== 'WITHDRAWN');
    const sortedQuotes = [...activeQuotes].sort((a, b) => {
        if (sortBy === 'price') return a.quotedPriceInrPaise - b.quotedPriceInrPaise;
        if (sortBy === 'lead') return (a.leadTimeDays || 999) - (b.leadTimeDays || 999);
        return 0;
    });
    const lowestPrice = activeQuotes.length > 0 ? Math.min(...activeQuotes.map(q => q.quotedPriceInrPaise)) : null;
    const statusCfg = STATUS_CONFIG[rfq.status] || { label: rfq.status, className: 'bg-slate-100 text-slate-600' };

    return (
        <div className="min-h-screen bg-gradient-to-br from-slate-50 to-slate-100 p-6">
            <div className="max-w-5xl mx-auto space-y-6">
                {/* Header */}
                <div className="flex items-start justify-between gap-4">
                    <div className="flex items-start gap-3">
                        <button onClick={() => navigate('/buyer/rfq')} className="mt-1 p-2 rounded-lg hover:bg-slate-200 transition-colors">
                            <ArrowLeft size={18} className="text-slate-600" />
                        </button>
                        <div>
                            <div className="flex items-center gap-2 mb-1">
                                <span className={`text-xs font-semibold px-2.5 py-1 rounded-full ${statusCfg.className}`}>{statusCfg.label}</span>
                                <span className="text-xs text-slate-400">{new Date(rfq.createdAt).toLocaleDateString()}</span>
                            </div>
                            <h1 className="text-2xl font-bold text-slate-900">{rfq.title}</h1>
                        </div>
                    </div>
                    <div className="flex flex-col items-end gap-2">
                        <button
                            onClick={() => navigate('/buyer/rfq-chats')}
                            className="text-sm bg-white border border-slate-200 hover:border-slate-300 text-slate-700 px-4 py-2 rounded-xl flex items-center gap-2 shadow-sm transition-all hover:shadow-md"
                        >
                            <MessageSquare size={16} className="text-brand-600" />
                            View Negotiations
                        </button>
                        {canFinalize && (
                            <button onClick={handleCancel} disabled={cancelling} className="text-sm text-red-500 hover:text-red-700 flex items-center gap-1 transition-colors">
                                <XCircle size={14} /> {cancelling ? 'Cancelling...' : 'Cancel RFQ'}
                            </button>
                        )}
                    </div>
                </div>

                {/* RFQ Summary Card */}
                <div className="bg-white rounded-2xl border border-slate-200 p-5">
                    <h2 className="font-semibold text-slate-800 mb-3">RFQ Details</h2>
                    <div className="grid grid-cols-2 md:grid-cols-4 gap-4">
                        {[
                            { label: 'Quantity', value: `${rfq.quantity} ${rfq.unit}`, icon: <Package size={16} /> },
                            { label: 'Destination', value: rfq.destinationCountry, icon: <Globe size={16} /> },
                            { label: 'Incoterm', value: rfq.incoterm, icon: <Anchor size={16} /> },
                            { label: 'Quotes Received', value: activeQuotes.length, icon: <CheckCircle size={16} /> },
                        ].map(({ label, value, icon }) => (
                            <div key={label} className="bg-slate-50 rounded-xl p-3">
                                <div className="flex items-center gap-1.5 text-slate-400 text-xs mb-1">{icon} {label}</div>
                                <div className="font-bold text-slate-800">{value}</div>
                            </div>
                        ))}
                    </div>
                    {rfq.details && (
                        <div className="mt-4 pt-4 border-t border-slate-100">
                            <p className="text-sm text-slate-600 leading-relaxed">{rfq.details}</p>
                        </div>
                    )}
                    {rfq.targetPriceMinor && (
                        <div className="mt-3 flex items-center gap-2">
                            <TrendingDown size={15} className="text-brand-500" />
                            <span className="text-sm text-slate-600">Target price: <span className="font-semibold text-brand-700">{fmtINR(rfq.targetPriceMinor)} per unit</span></span>
                        </div>
                    )}
                </div>

                {/* Quotes Section */}
                <div className="space-y-4">
                    <div className="flex items-center justify-between">
                        <h2 className="text-lg font-bold text-slate-900">
                            Seller Quotes <span className="text-slate-400 font-normal text-base">({activeQuotes.length})</span>
                        </h2>
                        {activeQuotes.length > 1 && (
                            <div className="flex items-center gap-2">
                                <span className="text-sm text-slate-500">Sort by:</span>
                                {['price', 'lead'].map(s => (
                                    <button key={s}
                                        onClick={() => setSortBy(s)}
                                        className={`text-sm px-3 py-1 rounded-lg capitalize transition-colors ${sortBy === s ? 'bg-brand-100 text-brand-700 font-semibold' : 'text-slate-500 hover:bg-slate-100'}`}
                                    >
                                        {s === 'price' ? 'Price' : 'Lead Time'}
                                    </button>
                                ))}
                            </div>
                        )}
                    </div>

                    {activeQuotes.length === 0 ? (
                        <div className="bg-white rounded-2xl border border-dashed border-slate-300 p-12 text-center">
                            <Clock size={40} className="mx-auto text-slate-300 mb-3" />
                            <h3 className="font-semibold text-slate-700">Waiting for quotes</h3>
                            <p className="text-sm text-slate-400 mt-1">Sellers will submit their best offers soon.</p>
                        </div>
                    ) : (
                        <div className="space-y-3">
                            {sortedQuotes.map((quote, i) => {
                                const isLowest = quote.quotedPriceInrPaise === lowestPrice;
                                const isSelected = selectedQuoteId === quote.id;
                                const isAccepted = rfq.acceptedQuoteId === quote.id;
                                return (
                                    <motion.div
                                        key={quote.id}
                                        initial={{ opacity: 0, y: 12 }}
                                        animate={{ opacity: 1, y: 0 }}
                                        transition={{ delay: i * 0.05 }}
                                        onClick={() => canFinalize && quote.status === 'ACTIVE' && setSelectedQuoteId(quote.id)}
                                        className={`bg-white rounded-2xl border-2 p-5 transition-all ${isAccepted ? 'border-emerald-400 bg-emerald-50/30' :
                                            isSelected ? 'border-brand-500 shadow-md shadow-brand-100' :
                                                canFinalize && quote.status === 'ACTIVE' ? 'border-slate-200 hover:border-brand-300 cursor-pointer' :
                                                    'border-slate-200'
                                            }`}
                                    >
                                        <div className="flex items-start justify-between gap-4">
                                            <div className="flex-1">
                                                <div className="flex items-center gap-2 mb-2">
                                                    <span className="font-bold text-slate-900">{quote.sellerName || 'Seller'}</span>
                                                    {quote.verifiedSeller && (
                                                        <span className="text-xs bg-blue-100 text-blue-700 px-2 py-0.5 rounded-full flex items-center gap-1">
                                                            <Shield size={11} /> Verified
                                                        </span>
                                                    )}
                                                    {isLowest && (
                                                        <span className="text-xs bg-emerald-100 text-emerald-700 px-2 py-0.5 rounded-full font-semibold">
                                                            Lowest Price
                                                        </span>
                                                    )}
                                                    {isAccepted && (
                                                        <span className="text-xs bg-emerald-600 text-white px-2 py-0.5 rounded-full font-semibold">
                                                            ✓ Accepted
                                                        </span>
                                                    )}
                                                    <span className={`text-xs px-2 py-0.5 rounded-full ml-auto ${(QUOTE_STATUS_CONFIG[quote.status] || {}).className || 'bg-slate-100 text-slate-500'}`}>
                                                        {(QUOTE_STATUS_CONFIG[quote.status] || {}).label || quote.status}
                                                    </span>
                                                </div>
                                                <div className="grid grid-cols-3 gap-3 mt-3">
                                                    <div>
                                                        <div className="text-xs text-slate-400 mb-0.5">Unit Price</div>
                                                        <div className="text-lg font-bold text-slate-900">{fmtINR(quote.quotedPriceInrPaise)}</div>
                                                    </div>
                                                    <div>
                                                        <div className="text-xs text-slate-400 mb-0.5">Total ({rfq.quantity} {rfq.unit})</div>
                                                        <div className="font-semibold text-brand-600">{fmtINR(quote.quotedPriceInrPaise * rfq.quantity)}</div>
                                                    </div>
                                                    <div>
                                                        <div className="text-xs text-slate-400 mb-0.5">Lead Time</div>
                                                        <div className="font-semibold text-slate-700">{quote.leadTimeDays ? `${quote.leadTimeDays} days` : 'N/A'}</div>
                                                    </div>
                                                </div>
                                                {quote.shippingEstimateInrPaise && (
                                                    <div className="mt-2 text-sm text-slate-500">
                                                        + Shipping: {fmtINR(quote.shippingEstimateInrPaise)}
                                                    </div>
                                                )}
                                                {quote.notes && (
                                                    <p className="mt-3 text-sm text-slate-600 bg-slate-50 rounded-lg p-3 italic">"{quote.notes}"</p>
                                                )}
                                            </div>
                                            {canFinalize && quote.status === 'ACTIVE' && (
                                                <div className={`w-6 h-6 rounded-full border-2 flex items-center justify-center shrink-0 transition-all ${isSelected ? 'border-brand-600 bg-brand-600' : 'border-slate-300'
                                                    }`}>
                                                    {isSelected && <Check size={14} className="text-white" />}
                                                </div>
                                            )}
                                        </div>
                                    </motion.div>
                                );
                            })}
                        </div>
                    )}
                </div>

                {/* Finalize Bar */}
                <AnimatePresence>
                    {canFinalize && selectedQuoteId && (
                        <motion.div
                            initial={{ opacity: 0, y: 20 }}
                            animate={{ opacity: 1, y: 0 }}
                            exit={{ opacity: 0, y: 20 }}
                            className="sticky bottom-6 bg-white border border-brand-200 rounded-2xl shadow-xl shadow-brand-100 p-4 flex items-center justify-between gap-4"
                        >
                            <div className="flex items-center gap-2 text-slate-600 text-sm">
                                <CheckCircle size={18} className="text-brand-600" />
                                <span>Quote selected. Ready to finalize and create order?</span>
                            </div>
                            <div className="flex gap-3">
                                <button onClick={() => setSelectedQuoteId(null)} className="px-4 py-2 rounded-lg border border-slate-300 text-slate-600 text-sm font-medium hover:bg-slate-50 transition-colors">
                                    Clear
                                </button>
                                <motion.button
                                    whileHover={{ scale: 1.03 }}
                                    whileTap={{ scale: 0.97 }}
                                    onClick={() => setConfirmOpen(true)}
                                    disabled={finalizing}
                                    className="px-6 py-2 bg-gradient-to-r from-brand-600 to-brand-700 text-white rounded-lg font-semibold shadow-md shadow-brand-200 text-sm flex items-center gap-2 disabled:opacity-60"
                                >
                                    {finalizing ? <Loader2 size={16} className="animate-spin" /> : <CheckCircle size={16} />}
                                    Finalize & Order
                                </motion.button>
                            </div>
                        </motion.div>
                    )}
                </AnimatePresence>

                {/* Confirm Modal */}
                <AnimatePresence>
                    {confirmOpen && (
                        <motion.div
                            initial={{ opacity: 0 }}
                            animate={{ opacity: 1 }}
                            exit={{ opacity: 0 }}
                            className="fixed inset-0 bg-black/50 backdrop-blur-sm flex items-center justify-center z-50 p-4"
                        >
                            <motion.div
                                initial={{ scale: 0.9, opacity: 0 }}
                                animate={{ scale: 1, opacity: 1 }}
                                exit={{ scale: 0.9, opacity: 0 }}
                                className="bg-white rounded-2xl max-w-sm w-full p-6 shadow-2xl"
                            >
                                <div className="text-center">
                                    <AlertTriangle size={40} className="mx-auto text-amber-500 mb-4" />
                                    <h3 className="text-lg font-bold text-slate-900 mb-2">Confirm Finalization</h3>
                                    <p className="text-sm text-slate-500 mb-6">
                                        This will accept the selected quote, reject all others, and create an order. You'll be redirected to payment.
                                    </p>
                                </div>
                                <div className="flex gap-3">
                                    <button onClick={() => setConfirmOpen(false)} className="flex-1 py-2.5 rounded-xl border border-slate-300 text-slate-600 font-medium hover:bg-slate-50 transition-colors">
                                        Go Back
                                    </button>
                                    <button onClick={handleFinalize} disabled={finalizing} className="flex-1 py-2.5 rounded-xl bg-brand-600 text-white font-semibold hover:bg-brand-700 transition-colors flex items-center justify-center gap-2">
                                        {finalizing ? <Loader2 size={16} className="animate-spin" /> : null}
                                        Confirm
                                    </button>
                                </div>
                            </motion.div>
                        </motion.div>
                    )}
                </AnimatePresence>
            </div>
        </div>
    );
}

// Missing import fix
function Anchor({ size, className }) {
    return <span className={className} style={{ fontSize: size }}>⚓</span>;
}
function Check({ size, className }) {
    return <CheckCircle size={size} className={className} />;
}
