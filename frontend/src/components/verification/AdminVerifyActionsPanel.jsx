
import React, { useState } from 'react';
import { CheckCircle2, XCircle, AlertTriangle } from 'lucide-react';

const AdminVerifyActionsPanel = ({ status, onVerify, onReject, isProcessing }) => {
    const [rejectReason, setRejectReason] = useState('');
    const [showRejectInput, setShowRejectInput] = useState(false);

    if (status === 'VERIFIED') {
        return (
            <div className="bg-emerald-50 border border-emerald-200 rounded-xl p-6 flex flex-col items-center text-center">
                <CheckCircle2 className="text-emerald-500 w-12 h-12 mb-2" />
                <h3 className="text-lg font-bold text-emerald-800">Verified Seller</h3>
                <p className="text-emerald-600">This seller has been approved and can start selling.</p>
            </div>
        );
    }

    return (
        <div className="bg-white border border-slate-200 rounded-xl shadow-sm p-6 sticky top-24">
            <h3 className="font-bold text-slate-900 mb-4">Verification Actions</h3>

            <div className="space-y-3">
                <button
                    onClick={onVerify}
                    disabled={isProcessing}
                    className="w-full flex items-center justify-center gap-2 px-4 py-3 bg-emerald-600 hover:bg-emerald-700 text-white rounded-lg font-bold shadow-sm transition-all active:scale-95 disabled:opacity-70"
                >
                    <CheckCircle2 size={20} /> Approve Seller
                </button>

                {!showRejectInput ? (
                    <button
                        onClick={() => setShowRejectInput(true)}
                        disabled={isProcessing}
                        className="w-full flex items-center justify-center gap-2 px-4 py-3 bg-white border border-red-200 text-red-600 hover:bg-red-50 rounded-lg font-bold transition-all"
                    >
                        <XCircle size={20} /> Reject / Request Info
                    </button>
                ) : (
                    <div className="p-4 bg-red-50 rounded-lg border border-red-100 animate-in fade-in zoom-in-95 duration-200">
                        <label className="block text-xs font-bold text-red-700 uppercase mb-1">Reason for Rejection</label>
                        <textarea
                            value={rejectReason}
                            onChange={(e) => setRejectReason(e.target.value)}
                            placeholder="e.g. IEC Document unclear..."
                            className="w-full p-2 text-sm border border-red-200 rounded bg-white text-slate-800 focus:ring-red-500 focus:border-red-500 mb-2"
                            rows={3}
                        />
                        <div className="flex gap-2">
                            <button
                                onClick={() => setShowRejectInput(false)}
                                className="flex-1 px-3 py-1.5 bg-white text-slate-600 border border-slate-200 rounded text-sm hover:bg-slate-50"
                            >
                                Cancel
                            </button>
                            <button
                                onClick={() => onReject(rejectReason)}
                                disabled={!rejectReason.trim() || isProcessing}
                                className="flex-1 px-3 py-1.5 bg-red-600 text-white rounded text-sm font-bold hover:bg-red-700 disabled:opacity-50"
                            >
                                Confirm Reject
                            </button>
                        </div>
                    </div>
                )}
            </div>
        </div>
    );
};

export default AdminVerifyActionsPanel;
