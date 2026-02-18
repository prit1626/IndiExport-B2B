import React from 'react';
import { Search, Filter, X } from 'lucide-react';
import { debounce } from 'lodash';

// Basic Filter Component
const RfqFilters = ({ filters, onFilterChange }) => {

    // Debounce search input
    const handleSearch = debounce((val) => {
        onFilterChange({ keyword: val, page: 0 }); // reset page on search
    }, 500);

    return (
        <div className="space-y-4">
            {/* Search */}
            <div className="relative">
                <Search className="absolute left-3 top-1/2 -translate-y-1/2 text-slate-400" size={18} />
                <input
                    type="text"
                    placeholder="Search requests..."
                    className="w-full pl-10 pr-4 py-2 border border-slate-200 rounded-lg focus:outline-none focus:ring-2 focus:ring-brand-500/20 focus:border-brand-500 transition-all text-sm"
                    onChange={(e) => handleSearch(e.target.value)}
                />
            </div>

            {/* Filters */}
            <div className="bg-white rounded-lg border border-slate-200 p-4 space-y-4">
                <div className="flex items-center gap-2 text-slate-900 font-semibold text-sm">
                    <Filter size={16} /> Filters
                </div>

                {/* Min Qty */}
                <div>
                    <label className="text-xs font-medium text-slate-600 mb-1 block">Min Quantity</label>
                    <input
                        type="number"
                        min="0"
                        placeholder="e.g. 100"
                        className="w-full px-3 py-2 border border-slate-200 rounded text-sm focus:border-brand-500 outline-none"
                        value={filters.minQty || ''}
                        onChange={(e) => onFilterChange({ minQty: e.target.value || '', page: 0 })}
                    />
                </div>

                {/* Sort */}
                <div>
                    <label className="text-xs font-medium text-slate-600 mb-1 block">Sort By</label>
                    <select
                        className="w-full px-3 py-2 border border-slate-200 rounded text-sm focus:border-brand-500 outline-none bg-white"
                        value={filters.sort || ''}
                        onChange={(e) => onFilterChange({ sort: e.target.value, page: 0 })}
                    >
                        <option value="createdAt,desc">Newest First</option>
                        <option value="createdAt,asc">Oldest First</option>
                        <option value="qty,desc">Highest Qty</option>
                        <option value="qty,asc">Lowest Qty</option>
                    </select>
                </div>

                {/* Country */}
                <div>
                    <label className="text-xs font-medium text-slate-600 mb-1 block">Destination</label>
                    <input
                        type="text"
                        placeholder="Country code (e.g. US)"
                        className="w-full px-3 py-2 border border-slate-200 rounded text-sm focus:border-brand-500 outline-none"
                        value={filters.destinationCountry || ''}
                        onChange={(e) => onFilterChange({ destinationCountry: e.target.value.toUpperCase(), page: 0 })}
                    />
                </div>

                {/* Clear Filters */}
                {(filters.minQty || filters.destinationCountry || filters.keyword) && (
                    <button
                        onClick={() => onFilterChange({ minQty: '', destinationCountry: '', keyword: '', page: 0 })}
                        className="w-full flex items-center justify-center gap-2 text-xs text-red-500 hover:bg-red-50 py-2 rounded transition-colors"
                    >
                        <X size={14} /> Clear All
                    </button>
                )}
            </div>
        </div>
    );
};

export default RfqFilters;
