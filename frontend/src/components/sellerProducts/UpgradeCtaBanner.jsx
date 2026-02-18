import React from 'react';
import { Crown, ArrowRight } from 'lucide-react';
import { Link } from 'react-router-dom';

const UpgradeCtaBanner = ({ isVisible }) => {
    if (!isVisible) return null;

    return (
        <div className="bg-gradient-to-r from-yellow-50 to-amber-50 border border-yellow-200 rounded-xl p-4 flex items-center justify-between shadow-sm mb-6">
            <div className="flex items-center gap-3">
                <div className="bg-yellow-100 p-2 rounded-full">
                    <Crown size={24} className="text-yellow-600" />
                </div>
                <div>
                    <h3 className="font-bold text-slate-800 text-sm">Unlock Unlimited Products</h3>
                    <p className="text-slate-600 text-xs">You've reached the 5 active products limit for Basic plan.</p>
                </div>
            </div>
            <Link
                to="/seller/subscription"
                className="bg-yellow-500 hover:bg-yellow-600 text-white text-xs font-bold px-4 py-2 rounded-lg flex items-center gap-1 transition-colors shadow-sm"
            >
                Upgrade Now <ArrowRight size={14} />
            </Link>
        </div>
    );
};

export default UpgradeCtaBanner;
