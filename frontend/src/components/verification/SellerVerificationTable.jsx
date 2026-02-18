
import React from 'react';
import { Eye, CheckCircle2, AlertCircle, ShieldAlert } from 'lucide-react';
import { useNavigate } from 'react-router-dom';
import { formatDate } from '../../utils/formatDate';

const SellerVerificationTable = ({ sellers }) => {
    const navigate = useNavigate();

    const getStatusBadge = (status) => {
        switch (status) {
            case 'VERIFIED': return <span className="inline-flex items-center gap-1 px-2.5 py-0.5 rounded-full text-xs font-medium bg-emerald-100 text-emerald-800"><CheckCircle2 size={12} /> Verified</span>;
            case 'REJECTED': return <span className="inline-flex items-center gap-1 px-2.5 py-0.5 rounded-full text-xs font-medium bg-red-100 text-red-800"><ShieldAlert size={12} /> Rejected</span>;
            case 'NEED_MORE_INFO': return <span className="inline-flex items-center gap-1 px-2.5 py-0.5 rounded-full text-xs font-medium bg-amber-100 text-amber-800"><AlertCircle size={12} /> Needs Info</span>;
            default: return <span className="inline-flex items-center gap-1 px-2.5 py-0.5 rounded-full text-xs font-medium bg-slate-100 text-slate-800">Pending</span>;
        }
    };

    return (
        <div className="bg-white border border-slate-200 rounded-xl shadow-sm overflow-hidden">
            <table className="w-full text-left border-collapse">
                <thead>
                    <tr className="bg-slate-50 border-b border-slate-200">
                        <th className="px-6 py-4 text-xs font-semibold text-slate-500 uppercase tracking-wider">Company</th>
                        <th className="px-6 py-4 text-xs font-semibold text-slate-500 uppercase tracking-wider">IEC Number</th>
                        <th className="px-6 py-4 text-xs font-semibold text-slate-500 uppercase tracking-wider">Submitted</th>
                        <th className="px-6 py-4 text-xs font-semibold text-slate-500 uppercase tracking-wider">Status</th>
                        <th className="px-6 py-4 text-xs font-semibold text-slate-500 uppercase tracking-wider text-right">Action</th>
                    </tr>
                </thead>
                <tbody className="divide-y divide-slate-100">
                    {sellers.length === 0 ? (
                        <tr>
                            <td colSpan="5" className="px-6 py-10 text-center text-slate-400">
                                No pending verifications found.
                            </td>
                        </tr>
                    ) : (
                        sellers.map((seller) => (
                            <tr key={seller.sellerId} className="hover:bg-slate-50 transition-colors">
                                <td className="px-6 py-4">
                                    <div className="font-medium text-slate-900">{seller.companyName || 'N/A'}</div>
                                    <div className="text-xs text-slate-500">ID: {seller.sellerId}</div>
                                </td>
                                <td className="px-6 py-4 font-mono text-sm text-slate-600">
                                    {seller.iecNumber || '-'}
                                </td>
                                <td className="px-6 py-4 text-sm text-slate-600">
                                    {formatDate(seller.submittedAt)}
                                </td>
                                <td className="px-6 py-4">
                                    {getStatusBadge(seller.verificationStatus)}
                                </td>
                                <td className="px-6 py-4 text-right">
                                    <button
                                        onClick={() => navigate(`/admin/sellers/${seller.sellerId}`)}
                                        className="text-brand-600 hover:text-brand-800 font-medium text-sm flex items-center justify-end gap-1 ml-auto"
                                    >
                                        <Eye size={16} /> Review
                                    </button>
                                </td>
                            </tr>
                        ))
                    )}
                </tbody>
            </table>
        </div>
    );
};

export default SellerVerificationTable;
