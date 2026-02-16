import React, { useState } from 'react';
import { Filter, X, ChevronDown, ChevronUp } from 'lucide-react';

const FilterSection = ({ title, isOpenDefault = true, children }) => {
    const [isOpen, setIsOpen] = useState(isOpenDefault);
    return (
        <div className="border-b border-slate-200 py-4 last:border-0">
            <button
                onClick={() => setIsOpen(!isOpen)}
                className="flex items-center justify-between w-full text-sm font-semibold text-slate-900 mb-2"
            >
                {title}
                {isOpen ? <ChevronUp className="w-4 h-4 text-slate-400" /> : <ChevronDown className="w-4 h-4 text-slate-400" />}
            </button>
            {isOpen && <div className="mt-2 space-y-2">{children}</div>}
        </div>
    );
};

const ProductFiltersSidebar = ({ filters, onFilterChange, onReset, categories = [], className = '' }) => {

    const handleChange = (key, value) => {
        onFilterChange({ [key]: value, page: 0 }); // reset page when filter changes
    };

    return (
        <aside className={`w-64 flex-shrink-0 hidden lg:block bg-white p-6 rounded-2xl border border-slate-200 h-fit sticky top-24 ${className}`}>
            <div className="flex items-center justify-between mb-6">
                <h3 className="font-bold text-slate-900 flex items-center gap-2">
                    <Filter className="w-4 h-4" /> Filters
                </h3>
                <button
                    onClick={onReset}
                    className="text-xs text-brand-600 font-medium hover:text-brand-700"
                >
                    Reset All
                </button>
            </div>

            {/* Category */}
            <FilterSection title="Category">
                <select
                    value={filters.category || ''}
                    onChange={(e) => handleChange('category', e.target.value)}
                    className="w-full p-2 text-sm border border-slate-200 rounded-lg focus:ring-2 focus:ring-brand-500/20 outline-none"
                >
                    <option value="">All Categories</option>
                    {['FOOD', 'TEXTILES', 'HANDICRAFTS', 'SPICES', 'JEWELRY'].map(cat => (
                        <option key={cat} value={cat}>{cat}</option>
                    ))}
                </select>
            </FilterSection>

            {/* Price Range */}
            <FilterSection title="Price Range (USD)">
                <div className="flex gap-2">
                    <input
                        type="number"
                        placeholder="Min"
                        value={filters.minPrice || ''}
                        onChange={(e) => handleChange('minPrice', e.target.value)}
                        className="w-full p-2 text-sm border border-slate-200 rounded-lg outline-none focus:border-brand-500"
                    />
                    <input
                        type="number"
                        placeholder="Max"
                        value={filters.maxPrice || ''}
                        onChange={(e) => handleChange('maxPrice', e.target.value)}
                        className="w-full p-2 text-sm border border-slate-200 rounded-lg outline-none focus:border-brand-500"
                    />
                </div>
            </FilterSection>

            {/* Rating */}
            <FilterSection title="Rating">
                {[4, 3, 2].map((rating) => (
                    <label key={rating} className="flex items-center gap-2 text-sm text-slate-600 cursor-pointer hover:text-slate-900">
                        <input
                            type="radio"
                            name="rating"
                            checked={parseInt(filters.rating) === rating}
                            onChange={() => handleChange('rating', rating)}
                            className="text-brand-600 focus:ring-brand-500"
                        />
                        <span>{rating}+ Stars</span>
                    </label>
                ))}
                <label className="flex items-center gap-2 text-sm text-slate-600 cursor-pointer hover:text-slate-900">
                    <input
                        type="radio"
                        name="rating"
                        checked={!filters.rating}
                        onChange={() => handleChange('rating', '')}
                        className="text-brand-600 focus:ring-brand-500"
                    />
                    <span>Any Rating</span>
                </label>
            </FilterSection>

            {/* Verified Seller */}
            <FilterSection title="Seller Status">
                <label className="flex items-center gap-2 text-sm text-slate-600 cursor-pointer">
                    <input
                        type="checkbox"
                        checked={filters.verifiedSeller === 'true'}
                        onChange={(e) => handleChange('verifiedSeller', e.target.checked ? 'true' : '')}
                        className="rounded text-brand-600 focus:ring-brand-500"
                    />
                    <span className="flex items-center gap-1">Verified Seller <ShieldCheckIcon className="w-3 h-3 text-emerald-500" /></span>
                </label>
            </FilterSection>

            {/* Incoterm */}
            <FilterSection title="Incoterm" isOpenDefault={false}>
                <select
                    value={filters.incoterm || ''}
                    onChange={(e) => handleChange('incoterm', e.target.value)}
                    className="w-full p-2 text-sm border border-slate-200 rounded-lg outline-none"
                >
                    <option value="">Any</option>
                    {['FOB', 'CIF', 'EXW', 'DDP'].map(term => (
                        <option key={term} value={term}>{term}</option>
                    ))}
                </select>
            </FilterSection>

            {/* Lead Time */}
            <FilterSection title="Max Lead Time (Days)" isOpenDefault={false}>
                <input
                    type="range"
                    min="1"
                    max="60"
                    value={filters.leadTime || 60}
                    onChange={(e) => handleChange('leadTime', e.target.value)}
                    className="w-full accent-brand-600"
                />
                <div className="text-right text-xs text-slate-500 mt-1">{filters.leadTime || 60} days</div>
            </FilterSection>

        </aside>
    );
};

// Helper icon
const ShieldCheckIcon = ({ className }) => (
    <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round" className={className}><path d="M12 22s8-4 8-10V5l-8-3-8 3v7c0 6 8 10 8 10" /><path d="m9 12 2 2 4-4" /></svg>
);

export default ProductFiltersSidebar;
