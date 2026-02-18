import React from 'react';
import { CreditCard, Zap, CheckCircle2, ArrowUpRight } from 'lucide-react';

const PlanInfoCard = ({ profile }) => {
    const isAdvanced = profile?.currentPlan === 'ADVANCED_SELLER';

    return (
        <div className="bg-white dark:bg-slate-900 rounded-2xl p-6 border border-slate-200 dark:border-slate-800 shadow-sm h-full flex flex-col">
            <div className="flex items-center gap-2 mb-6">
                <div className="p-2 bg-amber-50 dark:bg-amber-900/30 rounded-lg text-amber-600 dark:text-amber-400">
                    <Zap size={20} />
                </div>
                <h3 className="text-lg font-bold text-slate-900 dark:text-white">Active Plan</h3>
            </div>

            <div className="flex-1">
                <div className={`p-5 rounded-2xl border-2 transition-all duration-300 ${isAdvanced
                        ? 'bg-gradient-to-br from-indigo-500 to-purple-600 border-indigo-400/30 text-white shadow-lg shadow-indigo-500/20'
                        : 'bg-slate-50 dark:bg-slate-800/50 border-slate-100 dark:border-slate-800'
                    }`}>
                    <div className="flex justify-between items-start mb-4">
                        <div>
                            <p className={`text-[10px] font-bold uppercase tracking-[0.2em] mb-1 ${isAdvanced ? 'text-white/80' : 'text-slate-400'}`}>Current Tier</p>
                            <h4 className="text-xl font-black">{isAdvanced ? 'Advanced' : 'Basic'}</h4>
                        </div>
                        <div className={`p-2 rounded-xl ${isAdvanced ? 'bg-white/20' : 'bg-white dark:bg-slate-700 shadow-sm'}`}>
                            <Zap size={24} className={isAdvanced ? 'text-white' : 'text-amber-500'} />
                        </div>
                    </div>

                    <div className="space-y-3">
                        <div className="flex items-center gap-2">
                            <CheckCircle2 size={16} className={isAdvanced ? 'text-white/80' : 'text-emerald-500'} />
                            <span className="text-sm font-medium">{isAdvanced ? 'Unlimited' : '5'} Active Products</span>
                        </div>
                        <div className="flex items-center gap-2">
                            <CheckCircle2 size={16} className={isAdvanced ? 'text-white/80' : 'text-emerald-500'} />
                            <span className="text-sm font-medium">{isAdvanced ? '0.5%' : '2%'} Platform Fee</span>
                        </div>
                        {isAdvanced && (
                            <div className="flex items-center gap-2">
                                <CheckCircle2 size={16} className="text-white/80" />
                                <span className="text-sm font-medium">Priority Support & Analytics</span>
                            </div>
                        )}
                    </div>
                </div>

                {!isAdvanced && (
                    <button className="w-full mt-6 py-3 px-4 bg-indigo-600 hover:bg-indigo-700 text-white rounded-xl font-bold text-sm flex items-center justify-center gap-2 shadow-lg shadow-indigo-500/25 transition-all active:scale-95 group">
                        Upgrade to Advanced
                        <ArrowUpRight size={18} className="group-hover:translate-x-0.5 group-hover:-translate-y-0.5 transition-transform" />
                    </button>
                )}
            </div>

            <p className="mt-6 text-[10px] text-slate-400 text-center font-medium">
                Next billing date: {new Date(new Date().setMonth(new Date().getMonth() + 1)).toLocaleDateString()}
            </p>
        </div>
    );
};

export default PlanInfoCard;
