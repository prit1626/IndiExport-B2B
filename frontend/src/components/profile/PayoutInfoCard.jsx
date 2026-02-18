import React from 'react';
import { CreditCard, Landmark, Banknote, ShieldCheck } from 'lucide-react';

const PayoutInfoCard = ({ profile }) => {
    return (
        <div className="bg-white dark:bg-slate-900 rounded-2xl p-6 border border-slate-200 dark:border-slate-800 shadow-sm h-full">
            <div className="flex items-center gap-2 mb-6">
                <div className="p-2 bg-rose-50 dark:bg-rose-900/30 rounded-lg text-rose-600 dark:text-rose-400">
                    <Banknote size={20} />
                </div>
                <h3 className="text-lg font-bold text-slate-900 dark:text-white">Payout Settings</h3>
            </div>

            <div className="space-y-6">
                <div className="flex items-center justify-between p-4 rounded-xl bg-slate-50 dark:bg-slate-800/50 border border-slate-100 dark:border-slate-800">
                    <div className="flex items-center gap-3">
                        <div className="p-2 bg-indigo-100 dark:bg-indigo-900/40 rounded-lg">
                            <Landmark size={20} className="text-indigo-600 dark:text-indigo-400" />
                        </div>
                        <div>
                            <p className="text-[10px] font-bold text-slate-400 uppercase tracking-wider">Preferred Method</p>
                            <p className="text-sm font-bold text-slate-700 dark:text-slate-200">{profile?.payoutMethod || 'Bank Transfer'}</p>
                        </div>
                    </div>
                </div>

                <div className="space-y-4">
                    <div className="flex justify-between items-center text-sm border-b border-slate-100 dark:border-slate-800 pb-3">
                        <span className="text-slate-500 font-medium">Account Holder</span>
                        <span className="text-slate-900 dark:text-white font-bold">{profile?.accountHolderName || '••••••••'}</span>
                    </div>
                    <div className="flex justify-between items-center text-sm border-b border-slate-100 dark:border-slate-800 pb-3">
                        <span className="text-slate-500 font-medium">Account Number</span>
                        <span className="text-slate-900 dark:text-white font-bold tracking-widest">{profile?.accountNumberMasked || '••••••••'}</span>
                    </div>
                    <div className="flex justify-between items-center text-sm border-b border-slate-100 dark:border-slate-800 pb-3">
                        <span className="text-slate-500 font-medium">IFSC Code</span>
                        <span className="text-slate-900 dark:text-white font-bold">{profile?.ifscMasked || '••••••••'}</span>
                    </div>
                </div>

                <div className="p-4 bg-emerald-50 dark:bg-emerald-900/20 rounded-xl border border-emerald-100 dark:border-emerald-900/30 flex items-start gap-3">
                    <ShieldCheck size={18} className="text-emerald-600 dark:text-emerald-400 shrink-0 mt-0.5" />
                    <p className="text-[10px] text-emerald-700 dark:text-emerald-300 font-medium leading-relaxed">
                        Payout information is encrypted and securely managed by our gateway provider. Only masked data is displayed here.
                    </p>
                </div>
            </div>
        </div>
    );
};

export default PayoutInfoCard;
