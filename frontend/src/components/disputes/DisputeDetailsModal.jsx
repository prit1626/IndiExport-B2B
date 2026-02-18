
import React, { useState } from 'react';
import { X, User, Package, AlertTriangle, CheckCircle2 } from 'lucide-react';
import { Dialog } from '@headlessui/react';
import { motion } from 'framer-motion';
import { formatDate } from '../../utils/formatDate';
import { formatMoney } from '../../utils/formatMoney';
import DisputeStatusBadge from './DisputeStatusBadge';
import DisputeEvidenceGallery from './DisputeEvidenceGallery';
import ResolveDisputeForm from './ResolveDisputeForm';
import useAuthStore from '../../store/authStore';

const DisputeDetailsModal = ({ isOpen, onClose, dispute, onResolve }) => {
    const { user } = useAuthStore();
    const isAdmin = user?.role === 'ADMIN';

    if (!dispute) return null;

    return (
        <Dialog open={isOpen} onClose={onClose} className="relative z-50">
            <div className="fixed inset-0 bg-black/30 backdrop-blur-sm" aria-hidden="true" />

            <div className="fixed inset-0 flex items-center justify-center p-4">
                <Dialog.Panel
                    as={motion.div}
                    initial={{ opacity: 0, scale: 0.95 }}
                    animate={{ opacity: 1, scale: 1 }}
                    className="mx-auto max-w-2xl w-full bg-white rounded-xl shadow-2xl overflow-hidden flex flex-col max-h-[90vh]"
                >
                    {/* Header */}
                    <div className="px-6 py-4 border-b border-slate-200 flex justify-between items-center bg-slate-50">
                        <div>
                            <Dialog.Title className="text-lg font-bold text-slate-900">
                                Dispute Details
                            </Dialog.Title>
                            <p className="text-sm text-slate-500">Order #{dispute.orderId}</p>
                        </div>
                        <button onClick={onClose} className="text-slate-400 hover:text-slate-600 transition-colors">
                            <X size={24} />
                        </button>
                    </div>

                    {/* Content - Scrollable */}
                    <div className="p-6 overflow-y-auto flex-1">
                        {/* Status & Reason */}
                        <div className="flex flex-wrap gap-4 mb-6">
                            <div>
                                <p className="text-xs text-slate-400 uppercase tracking-wider font-semibold mb-1">Status</p>
                                <DisputeStatusBadge status={dispute.status} />
                            </div>
                            <div>
                                <p className="text-xs text-slate-400 uppercase tracking-wider font-semibold mb-1">Reason</p>
                                <p className="text-sm font-medium text-slate-800 bg-slate-100 px-3 py-1 rounded-md inline-block">
                                    {dispute.reason?.replace(/_/g, ' ')}
                                </p>
                            </div>
                            <div>
                                <p className="text-xs text-slate-400 uppercase tracking-wider font-semibold mb-1">Date</p>
                                <p className="text-sm text-slate-700">{formatDate(dispute.createdAt)}</p>
                            </div>
                        </div>

                        {/* Description */}
                        <div className="mb-6 bg-slate-50 p-4 rounded-lg border border-slate-200">
                            <h4 className="text-sm font-semibold text-slate-800 mb-2 flex items-center gap-2">
                                <User size={16} /> Description of Issue
                            </h4>
                            <p className="text-sm text-slate-600 leading-relaxed whitespace-pre-wrap">
                                {dispute.description}
                            </p>
                        </div>

                        {/* Order Snapshot (Mocked if missing in API) */}
                        <div className="mb-6">
                            <h4 className="text-sm font-semibold text-slate-800 mb-2 flex items-center gap-2">
                                <Package size={16} /> Order Summary
                            </h4>
                            <div className="bg-white border border-slate-200 rounded-lg p-3 text-sm text-slate-600 grid grid-cols-2 gap-y-2">
                                <p><span className="font-medium text-slate-800">Tracking:</span> {dispute.orderSummary?.trackingNumber || 'N/A'}</p>
                                <p><span className="font-medium text-slate-800">Courier:</span> {dispute.orderSummary?.courier || 'N/A'}</p>
                                <p><span className="font-medium text-slate-800">Status:</span> {dispute.orderSummary?.status || 'Unknown'}</p>
                                <p><span className="font-medium text-slate-800">Mode:</span> {dispute.orderSummary?.shippingMode || 'Standard'}</p>
                            </div>
                        </div>

                        {/* Evidence */}
                        <DisputeEvidenceGallery evidence={dispute.evidence} />

                        {/* Resolution (Show if resolved OR if Admin needs to resolve) */}
                        {dispute.resolution && (
                            <div className="mt-8 border-t border-slate-200 pt-6">
                                <h4 className="text-base font-bold text-slate-900 mb-3 flex items-center gap-2">
                                    <CheckCircle2 className="text-emerald-600" size={20} /> Resolution
                                </h4>
                                <div className="bg-emerald-50 border border-emerald-100 rounded-lg p-4">
                                    <div className="flex justify-between items-start mb-2">
                                        <p className="font-semibold text-emerald-800">{dispute.resolution.action.replace(/_/g, ' ')}</p>
                                        {dispute.resolution.amountINRPaise > 0 && (
                                            <p className="font-bold text-emerald-700">
                                                Refund: {formatMoney(dispute.resolution.amountINRPaise)}
                                            </p>
                                        )}
                                    </div>
                                    <p className="text-sm text-emerald-700">{dispute.resolution.notes}</p>
                                </div>
                            </div>
                        )}

                        {/* Admin Action Area */}
                        {isAdmin && dispute.status !== 'RESOLVED' && (
                            <div className="mt-8 border-t border-slate-200 pt-6">
                                <h4 className="text-base font-bold text-slate-900 mb-4 flex items-center gap-2">
                                    <AlertTriangle className="text-amber-500" size={20} /> Admin Action
                                </h4>
                                <ResolveDisputeForm disputeId={dispute.id} onResolve={onResolve} />
                            </div>
                        )}
                    </div>

                    <div className="p-4 border-t border-slate-200 bg-slate-50 flex justify-end">
                        <button
                            onClick={onClose}
                            className="px-4 py-2 bg-white text-slate-700 border border-slate-300 rounded-lg hover:bg-slate-50 font-medium text-sm"
                        >
                            Close
                        </button>
                    </div>
                </Dialog.Panel>
            </div>
        </Dialog>
    );
};

export default DisputeDetailsModal;
