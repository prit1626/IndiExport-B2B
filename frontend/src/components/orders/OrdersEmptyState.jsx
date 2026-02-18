import React from 'react';
import { Package, Search } from 'lucide-react';
import { useNavigate } from 'react-router-dom';

const OrdersEmptyState = ({ isFilterActive, resetFilters }) => {
    const navigate = useNavigate();

    return (
        <div className="flex flex-col items-center justify-center py-20 bg-white rounded-xl border border-slate-200 border-dashed">
            <div className="w-16 h-16 bg-slate-50 rounded-full flex items-center justify-center mb-4">
                {isFilterActive ? <Search size={32} className="text-slate-400" /> : <Package size={32} className="text-slate-400" />}
            </div>

            <h3 className="text-lg font-semibold text-slate-900 mb-1">
                {isFilterActive ? 'No orders found' : 'No orders yet'}
            </h3>

            <p className="text-slate-500 text-center max-w-xs mb-6">
                {isFilterActive
                    ? "Try adjusting your filters to see more results."
                    : "Looks like you haven't placed any orders yet. Start shopping now!"}
            </p>

            {isFilterActive ? (
                <button
                    onClick={resetFilters}
                    className="text-brand-600 font-medium hover:text-brand-700"
                >
                    Clear all filters
                </button>
            ) : (
                <button
                    onClick={() => navigate('/products')}
                    className="bg-brand-600 text-white px-6 py-2 rounded-lg font-medium hover:bg-brand-700 transition-colors"
                >
                    Browse Products
                </button>
            )}
        </div>
    );
};

export default OrdersEmptyState;
