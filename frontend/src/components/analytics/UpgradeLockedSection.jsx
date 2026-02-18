import React from 'react';
import { Lock, Sparkles } from 'lucide-react';

const UpgradeLockedSection = () => {
    return (
        <div className="relative overflow-hidden rounded-2xl border border-slate-200 bg-slate-50 min-h-[300px] flex items-center justify-center">
            {/* Blurred Background Pattern */}
            <div className="absolute inset-0 opacity-10 bg-[radial-gradient(#4f46e5_1px,transparent_1px)] [background-size:16px_16px]"></div>

            <div className="relative z-10 text-center p-8 max-w-md">
                <div className="w-14 h-14 bg-gradient-to-br from-indigo-500 to-purple-600 rounded-2xl flex items-center justify-center mx-auto mb-6 shadow-lg shadow-indigo-200">
                    <Lock className="w-7 h-7 text-white" />
                </div>

                <h3 className="text-2xl font-bold text-slate-900 mb-2">Unlock Advanced Analytics</h3>
                <p className="text-slate-500 mb-8">
                    Gain deep insights into your sales performance, customer demographics, and product trends with our Advanced Seller plan.
                </p>

                <button className="inline-flex items-center gap-2 px-6 py-3 bg-slate-900 hover:bg-slate-800 text-white rounded-xl font-semibold transition-all shadow-xl shadow-slate-200 hover:shadow-2xl hover:-translate-y-1">
                    <Sparkles className="w-4 h-4 text-amber-300" />
                    Upgrade to Advanced
                </button>
            </div>
        </div>
    );
};

export default UpgradeLockedSection;
