
import React from 'react';
import { FileText, Calendar, ArrowRight } from 'lucide-react';
import { formatDate } from '../../utils/formatDate';
import DisputeStatusBadge from './DisputeStatusBadge';

const DisputeCard = ({ dispute, onClick }) => {
    return (
        <div
            onClick={onClick}
            className="bg-white border border-slate-200 rounded-lg p-4 hover:shadow-md transition-shadow cursor-pointer group"
        >
            <div className="flex justify-between items-start mb-3">
                <div className="flex items-center gap-2">
                    <div className="bg-slate-100 p-2 rounded-full text-slate-600">
                        <FileText size={18} />
                    </div>
                    <div>
                        <p className="text-sm font-semibold text-slate-800">Order #{dispute.orderId?.substring(0, 8)}</p>
                        <p className="text-xs text-slate-500">{formatDate(dispute.createdAt)}</p>
                    </div>
                </div>
                <DisputeStatusBadge status={dispute.status} />
            </div>

            <div className="mb-3">
                <p className="text-sm font-medium text-slate-700">{dispute.reason?.replace(/_/g, ' ')}</p>
                <p className="text-sm text-slate-500 line-clamp-2">{dispute.description}</p>
            </div>

            <div className="flex items-center justify-between mt-4 pt-3 border-t border-slate-100">
                <span className="text-xs font-medium text-slate-500 bg-slate-50 px-2 py-1 rounded">
                    {dispute.raisedByRole === 'BUYER' ? 'Raised by Customer' : 'System Generated'}
                </span>
                <span className="text-brand-600 text-sm font-medium flex items-center gap-1 group-hover:underline">
                    View Details <ArrowRight size={14} />
                </span>
            </div>
        </div>
    );
};

export default DisputeCard;
