import React from 'react';
import { AlertCircle, RefreshCw } from 'lucide-react';

const OrdersErrorState = ({ message, onRetry }) => {
    return (
        <div className="bg-red-50 border border-red-100 rounded-xl p-8 flex flex-col items-center justify-center text-center">
            <AlertCircle size={32} className="text-red-500 mb-3" />
            <h3 className="text-lg font-semibold text-red-700 mb-1">Failed to load orders</h3>
            <p className="text-red-600/80 mb-6 max-w-sm">{message || "Something went wrong while fetching your orders. Please try again."}</p>
            <button
                onClick={onRetry}
                className="flex items-center gap-2 bg-white text-red-600 px-4 py-2 rounded-lg border border-red-200 font-medium hover:bg-red-50 hover:border-red-300 transition-colors"
            >
                <RefreshCw size={18} />
                Try Again
            </button>
        </div>
    );
};

export default OrdersErrorState;
