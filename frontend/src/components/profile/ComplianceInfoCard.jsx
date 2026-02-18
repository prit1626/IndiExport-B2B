import React from 'react';
import { ShieldCheck, FileText, AlertCircle, CheckCircle2, XCircle } from 'lucide-react';

const ComplianceInfoCard = ({ profile }) => {
    const status = profile?.iecStatus || 'NOT_VERIFIED';

    const StatusBadge = ({ status }) => {
        const styles = {
            VERIFIED: 'bg-emerald-50 text-emerald-700 border-emerald-100',
            PENDING: 'bg-amber-50 text-amber-700 border-amber-100',
            REJECTED: 'bg-rose-50 text-rose-700 border-rose-100',
            NOT_VERIFIED: 'bg-slate-50 text-slate-700 border-slate-100'
        };

        const icons = {
            VERIFIED: <CheckCircle2 size={14} />,
            PENDING: <AlertCircle size={14} />,
            REJECTED: <XCircle size={14} />,
            NOT_VERIFIED: <AlertCircle size={14} />
        };

        return (
            <span className={`px-2.5 py-1 rounded-full text-xs font-bold flex items-center gap-1.5 border ${styles[status]}`}>
                {icons[status]}
                {status.replace('_', ' ')}
            </span>
        );
    };

    return (
        <div className="bg-white dark:bg-slate-900 rounded-2xl p-6 border border-slate-200 dark:border-slate-800 shadow-sm h-full">
            <div className="flex items-center justify-between mb-6">
                <div className="flex items-center gap-2">
                    <div className="p-2 bg-indigo-50 dark:bg-indigo-900/30 rounded-lg text-indigo-600 dark:text-indigo-400">
                        <ShieldCheck size={20} />
                    </div>
                    <h3 className="text-lg font-bold text-slate-900 dark:text-white">Export Compliance</h3>
                </div>
                <StatusBadge status={status} />
            </div>

            <div className="space-y-4">
                <div className="p-4 bg-slate-50 dark:bg-slate-800/50 rounded-xl border border-slate-100 dark:border-slate-800 transition-all hover:border-indigo-100 dark:hover:border-indigo-900/30">
                    <div className="flex items-center justify-between">
                        <div className="flex items-center gap-3">
                            <div className="p-2 bg-white dark:bg-slate-800 rounded-lg shadow-sm">
                                <FileText size={16} className="text-slate-500" />
                            </div>
                            <div>
                                <p className="text-[10px] font-bold text-slate-400 uppercase tracking-wider">IEC Number</p>
                                <p className="text-sm font-bold text-slate-700 dark:text-slate-200">{profile?.iecNumber || 'Not provided'}</p>
                            </div>
                        </div>
                    </div>
                </div>

                <div className="p-4 bg-slate-50 dark:bg-slate-800/50 rounded-xl border border-slate-100 dark:border-slate-800 transition-all hover:border-indigo-100 dark:hover:border-indigo-900/30">
                    <div className="flex items-center justify-between">
                        <div className="flex items-center gap-3">
                            <div className="p-2 bg-white dark:bg-slate-800 rounded-lg shadow-sm">
                                <FileText size={16} className="text-slate-500" />
                            </div>
                            <div>
                                <p className="text-[10px] font-bold text-slate-400 uppercase tracking-wider">GSTIN</p>
                                <p className="text-sm font-bold text-slate-700 dark:text-slate-200">{profile?.gstin || 'Not provided'}</p>
                            </div>
                        </div>
                    </div>
                </div>

                <div className="p-4 bg-slate-50 dark:bg-slate-800/50 rounded-xl border border-slate-100 dark:border-slate-800 transition-all hover:border-indigo-100 dark:hover:border-indigo-900/30">
                    <div className="flex items-center justify-between">
                        <div className="flex items-center gap-3">
                            <div className="p-2 bg-white dark:bg-slate-800 rounded-lg shadow-sm">
                                <FileText size={16} className="text-slate-500" />
                            </div>
                            <div>
                                <p className="text-[10px] font-bold text-slate-400 uppercase tracking-wider">PAN Number</p>
                                <p className="text-sm font-bold text-slate-700 dark:text-slate-200">{profile?.panNumber || 'Not provided'}</p>
                            </div>
                        </div>
                    </div>
                </div>
            </div>

            {status !== 'VERIFIED' && (
                <div className="mt-6 p-4 bg-indigo-50 dark:bg-indigo-900/20 rounded-xl border border-indigo-100 dark:border-indigo-900/30">
                    <p className="text-xs text-indigo-700 dark:text-indigo-300 font-medium leading-relaxed">
                        To update these highly sensitive fields, please contact our support team or visit the verification center.
                    </p>
                </div>
            )}
        </div>
    );
};

export default ComplianceInfoCard;
