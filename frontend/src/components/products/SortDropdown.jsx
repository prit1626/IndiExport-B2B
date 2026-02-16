import React from 'react';
import { ArrowUpDown } from 'lucide-react';

const SortDropdown = ({ value, onChange }) => {
    return (
        <div className="flex items-center gap-2">
            <span className="text-sm text-slate-500 hidden sm:inline">Sort by:</span>
            <div className="relative">
                <ArrowUpDown className="absolute left-2.5 top-1/2 -translate-y-1/2 w-4 h-4 text-slate-400" />
                <select
                    value={value || 'latest'}
                    onChange={(e) => onChange(e.target.value)}
                    className="pl-8 pr-8 py-2 bg-white border border-slate-200 rounded-xl text-sm font-medium text-slate-700 outline-none focus:ring-2 focus:ring-brand-500/20 appearance-none cursor-pointer hover:border-slate-300 transition-colors"
                >
                    <option value="latest">Newest Arrivals</option>
                    <option value="price_low_to_high">Price: Low to High</option>
                    <option value="price_high_to_low">Price: High to Low</option>
                    <option value="rating_high_to_low">Top Rated</option>
                </select>
            </div>
        </div>
    );
};

export default SortDropdown;
