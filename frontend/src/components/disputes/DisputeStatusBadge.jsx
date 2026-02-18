
import React from 'react';

const STATUS_CONFIG = {
    RAISED: { label: 'Raised', color: 'bg-amber-100 text-amber-700 border-amber-200' },
    UNDER_REVIEW: { label: 'Under Review', color: 'bg-blue-100 text-blue-700 border-blue-200' },
    RESOLVED: { label: 'Resolved', color: 'bg-emerald-100 text-emerald-700 border-emerald-200' },
    REJECTED: { label: 'Rejected', color: 'bg-red-100 text-red-700 border-red-200' },
};

const DisputeStatusBadge = ({ status }) => {
    const config = STATUS_CONFIG[status] || { label: status, color: 'bg-slate-100 text-slate-700 border-slate-200' };

    return (
        <span className={`px-2.5 py-0.5 rounded-full text-xs font-medium border ${config.color}`}>
            {config.label}
        </span>
    );
};

export default DisputeStatusBadge;
