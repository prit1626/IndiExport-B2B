import React, { useState, useEffect, useCallback } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { motion, AnimatePresence } from 'framer-motion';
import { Plus, FileText, Clock, CheckCircle, XCircle, AlertCircle, ChevronRight, Loader2, Package } from 'lucide-react';
import rfqApi from '../../api/rfqApi';
import toast from 'react-hot-toast';

const STATUS_CONFIG = {
    OPEN: { label: 'Open', color: 'bg-emerald-100 text-emerald-700 border-emerald-200', icon: <AlertCircle size={12} /> },
    UNDER_NEGOTIATION: { label: 'Negotiating', color: 'bg-blue-100 text-blue-700 border-blue-200', icon: <Clock size={12} /> },
    FINALIZED: { label: 'Finalized', color: 'bg-violet-100 text-violet-700 border-violet-200', icon: <CheckCircle size={12} /> },
    CONVERTED_TO_ORDER: { label: 'Ordered', color: 'bg-cyan-100 text-cyan-700 border-cyan-200', icon: <Package size={12} /> },
    CANCELLED: { label: 'Cancelled', color: 'bg-red-100 text-red-700 border-red-200', icon: <XCircle size={12} /> },
    EXPIRED: { label: 'Expired', color: 'bg-slate-100 text-slate-500 border-slate-200', icon: <Clock size={12} /> },
};

const RFQStatusBadge = ({ status }) => {
    const cfg = STATUS_CONFIG[status] || { label: status, color: 'bg-slate-100 text-slate-600 border-slate-200' };
    return (
        <span className={`inline-flex items-center gap-1 px-2.5 py-0.5 rounded-full text-xs font-semibold border ${cfg.color}`}>
            {cfg.icon}{cfg.label}
        </span>
    );
};

export default function BuyerRfqListPage() {
    const [rfqs, setRfqs] = useState([]);
    const [loading, setLoading] = useState(true);
    const [page, setPage] = useState(0);
    const [totalPages, setTotalPages] = useState(1);
    const navigate = useNavigate();

    const fetchRfqs = useCallback(async () => {
        setLoading(true);
        try {
            const res = await rfqApi.listMyRfqs({ page, size: 10 });
            const data = res.data;
            setRfqs(data.content || []);
            setTotalPages(data.totalPages || 1);
        } catch {
            toast.error('Failed to load RFQs');
        } finally {
            setLoading(false);
        }
    }, [page]);

    useEffect(() => { fetchRfqs(); }, [fetchRfqs]);

    return (
        <div className="min-h-screen bg-gradient-to-br from-slate-50 to-slate-100 p-6">
            <div className="max-w-5xl mx-auto space-y-6">
                {/* Header */}
                <div className="flex items-center justify-between">
                    <div>
                        <h1 className="text-2xl font-bold text-slate-900">My RFQs</h1>
                        <p className="text-slate-500 text-sm mt-1">Request for Quotation — find the best seller for your needs</p>
                    </div>
                    <motion.button
                        whileHover={{ scale: 1.03 }}
                        whileTap={{ scale: 0.97 }}
                        onClick={() => navigate('/buyer/rfq/create')}
                        className="flex items-center gap-2 bg-gradient-to-r from-brand-600 to-brand-700 text-white px-5 py-2.5 rounded-xl font-semibold shadow-md shadow-brand-200 hover:shadow-lg transition-all"
                    >
                        <Plus size={18} /> Create RFQ
                    </motion.button>
                </div>

                {/* List */}
                {loading ? (
                    <div className="flex justify-center items-center py-24">
                        <Loader2 className="animate-spin text-brand-600" size={32} />
                    </div>
                ) : rfqs.length === 0 ? (
                    <motion.div
                        initial={{ opacity: 0, y: 20 }}
                        animate={{ opacity: 1, y: 0 }}
                        className="bg-white rounded-2xl border border-dashed border-slate-300 p-16 text-center"
                    >
                        <FileText size={48} className="mx-auto text-slate-300 mb-4" />
                        <h3 className="text-xl font-semibold text-slate-700">No RFQs yet</h3>
                        <p className="text-slate-500 mt-2 mb-6">Create your first Request for Quotation to get competitive quotes from sellers.</p>
                        <button
                            onClick={() => navigate('/buyer/rfq/create')}
                            className="bg-brand-600 text-white px-6 py-2 rounded-lg font-medium hover:bg-brand-700 transition-colors"
                        >
                            Create RFQ
                        </button>
                    </motion.div>
                ) : (
                    <AnimatePresence>
                        <div className="space-y-3">
                            {rfqs.map((rfq, i) => (
                                <motion.div
                                    key={rfq.id}
                                    initial={{ opacity: 0, y: 16 }}
                                    animate={{ opacity: 1, y: 0 }}
                                    transition={{ delay: i * 0.05 }}
                                    onClick={() => navigate(`/buyer/rfq/${rfq.id}`)}
                                    className="bg-white rounded-2xl border border-slate-200 p-5 cursor-pointer hover:border-brand-300 hover:shadow-md transition-all group"
                                >
                                    <div className="flex items-start justify-between gap-4">
                                        <div className="flex-1 min-w-0">
                                            <div className="flex items-center gap-3 mb-2">
                                                <RFQStatusBadge status={rfq.status} />
                                                <span className="text-xs text-slate-400">
                                                    {new Date(rfq.createdAt).toLocaleDateString('en-IN', { day: 'numeric', month: 'short', year: 'numeric' })}
                                                </span>
                                            </div>
                                            <h3 className="text-base font-semibold text-slate-900 truncate group-hover:text-brand-700 transition-colors">{rfq.title}</h3>
                                            <div className="flex items-center gap-4 mt-2 text-sm text-slate-500">
                                                <span>{rfq.quantity} {rfq.unit}</span>
                                                <span>•</span>
                                                <span>🌍 {rfq.destinationCountry}</span>
                                                {rfq.incoterm && <><span>•</span><span>{rfq.incoterm}</span></>}
                                            </div>
                                        </div>
                                        <div className="flex items-center gap-4 shrink-0">
                                            <div className="text-center">
                                                <div className="text-xl font-bold text-brand-600">{rfq.quoteCount || 0}</div>
                                                <div className="text-xs text-slate-400">Quotes</div>
                                            </div>
                                            <ChevronRight size={20} className="text-slate-300 group-hover:text-brand-500 transition-colors" />
                                        </div>
                                    </div>
                                </motion.div>
                            ))}
                        </div>
                    </AnimatePresence>
                )}

                {/* Pagination */}
                {totalPages > 1 && (
                    <div className="flex justify-center gap-2">
                        {Array.from({ length: totalPages }, (_, i) => (
                            <button
                                key={i}
                                onClick={() => setPage(i)}
                                className={`w-9 h-9 rounded-lg text-sm font-medium transition-colors ${page === i ? 'bg-brand-600 text-white' : 'bg-white text-slate-600 hover:bg-slate-100 border border-slate-200'
                                    }`}
                            >
                                {i + 1}
                            </button>
                        ))}
                    </div>
                )}
            </div>
        </div>
    );
}
