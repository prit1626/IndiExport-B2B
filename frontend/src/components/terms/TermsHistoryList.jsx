
import React from 'react';
import { History, CheckCircle2 } from 'lucide-react';
import { formatDate } from '../../utils/formatDate';

const TermsHistoryList = ({ history, activeVersionId }) => {
    if (!history || history.length === 0) return null;

    return (
        <div className="bg-white rounded-lg border border-slate-200 overflow-hidden">
            <div className="bg-slate-50 px-4 py-3 border-b border-slate-200">
                <h3 className="text-sm font-semibold text-slate-700 flex items-center gap-2">
                    <History size={16} /> Version History
                </h3>
            </div>
            <div className="divide-y divide-slate-100 max-h-60 overflow-y-auto">
                {history.map((ver) => (
                    <div key={ver.id} className="px-4 py-3 flex items-center justify-between hover:bg-slate-50 transition-colors">
                        <div>
                            <div className="flex items-center gap-2">
                                <span className="font-medium text-slate-900">{ver.versionLabel || ver.version}</span>
                                {ver.id === activeVersionId && (
                                    <span className="bg-emerald-100 text-emerald-700 text-xs px-2 py-0.5 rounded-full flex items-center gap-1 font-medium">
                                        <CheckCircle2 size={10} /> Active
                                    </span>
                                )}
                            </div>
                            <p className="text-xs text-slate-500 mt-0.5">
                                Published {formatDate(ver.publishedAt)} by {ver.publishedBy?.name || 'Admin'}
                            </p>
                        </div>
                    </div>
                ))}
            </div>
        </div>
    );
};

export default TermsHistoryList;
