
import React from 'react';
import { ShieldCheck, ShieldAlert, Clock, AlertCircle, AlertTriangle } from 'lucide-react';

const STATUS_CONFIG = {
    NOT_VERIFIED: {
        icon: AlertCircle,
        color: 'text-slate-500',
        bg: 'bg-slate-100',
        border: 'border-slate-200',
        label: 'Not Verified',
        desc: 'Please submit your documents to start selling.'
    },
    NEED_MORE_INFO: {
        icon: AlertTriangle,
        color: 'text-amber-600',
        bg: 'bg-amber-50',
        border: 'border-amber-200',
        label: 'Action Required',
        desc: 'The admin has requested changes to your application.'
    },
    REJECTED: {
        icon: ShieldAlert,
        color: 'text-red-600',
        bg: 'bg-red-50',
        border: 'border-red-200',
        label: 'Verification Rejected',
        desc: 'Your application was rejected. Please review the reasons.'
    },
    VERIFIED: {
        icon: ShieldCheck,
        color: 'text-emerald-600',
        bg: 'bg-emerald-50',
        border: 'border-emerald-200',
        label: 'Verified Seller',
        desc: 'Congratulations! Your account is fully verified.'
    },
    SUBMITTED: { // Frontend derived state if needed, or mapped from backend status
        icon: Clock,
        color: 'text-blue-600',
        bg: 'bg-blue-50',
        border: 'border-blue-200',
        label: 'Under Review',
        desc: 'Your documents are being reviewed by our team.'
    }
};

const SellerVerificationStatusCard = ({ status, rejectionReason }) => {
    // If backend uses specific enum, map appropriately. Assuming backend enums match keys roughly.
    // If status is NOT_VERIFIED but docs are uploaded, backend might still say NOT_VERIFIED until admin sees it, 
    // or we might track a SUBMITTED state if supported. 
    // For now, mapping directly.
    const config = STATUS_CONFIG[status] || STATUS_CONFIG.NOT_VERIFIED;
    const Icon = config.icon;

    return (
        <div className={`rounded-xl border ${config.border} ${config.bg} p-6 mb-6`}>
            <div className="flex items-start gap-4">
                <div className={`p-3 rounded-full bg-white ${config.color} shadow-sm`}>
                    <Icon size={32} />
                </div>
                <div>
                    <h2 className={`text-lg font-bold ${config.color} mb-1`}>{config.label}</h2>
                    <p className="text-slate-600">{config.desc}</p>

                    {/* Show reason for Rejection or Need Info */}
                    {(status === 'REJECTED' || status === 'NEED_MORE_INFO') && rejectionReason && (
                        <div className="mt-4 p-4 bg-white rounded-lg border border-slate-200 shadow-sm">
                            <h4 className="text-sm font-semibold text-slate-800 mb-1">Admin Note:</h4>
                            <p className="text-sm text-slate-600 italic">"{rejectionReason}"</p>
                        </div>
                    )}
                </div>
            </div>
        </div>
    );
};

export default SellerVerificationStatusCard;
