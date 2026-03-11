import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { Search } from 'lucide-react';

const ProductSearchBar = ({ className = "" }) => {
    const [keyword, setKeyword] = useState('');
    const navigate = useNavigate();

    const handleSearch = (e) => {
        e.preventDefault();
        if (keyword.trim()) {
            navigate(`/search?q=${encodeURIComponent(keyword.trim())}`);
        }
    };

    return (
        <form
            onSubmit={handleSearch}
            className={`relative flex items-center w-full max-w-xl ${className}`}
        >
            <div className="relative w-full">
                <div className="absolute inset-y-0 left-0 flex items-center pl-3 pointer-events-none">
                    <Search className="w-5 h-5 text-slate-400" />
                </div>
                <input
                    type="text"
                    value={keyword}
                    onChange={(e) => setKeyword(e.target.value)}
                    className="block w-full p-3 pl-10 text-sm text-slate-900 border border-slate-200 rounded-xl bg-slate-50 focus:ring-brand-500 focus:border-brand-500 outline-none transition-all"
                    placeholder="Search products, materials, or suppliers..."
                    required
                />
            </div>
            <button
                type="submit"
                className="ml-2 text-white bg-brand-600 hover:bg-brand-700 focus:ring-4 focus:outline-none focus:ring-brand-300 font-medium rounded-xl text-sm px-5 py-3 transition-colors"
            >
                Search
            </button>
        </form>
    );
};

export default ProductSearchBar;
