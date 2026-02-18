import React from 'react';
import { AlertTriangle, RefreshCw } from 'lucide-react';

const ErrorState = ({ message, onRetry }) => {
    return (
        <div className="flex flex-col items-center justify-center py-20 bg-white rounded-2xl border border-red-100 p-8 text-center">
            <div className="w-12 h-12 bg-red-50 text-red-500 rounded-full flex items-center justify-center mb-4">
                <AlertTriangle className="w-6 h-6" />
            </div>
            <h3 className="text-lg font-semibold text-slate-900 mb-2">Failed to load analytics</h3>
            <p className="text-slate-500 mb-6 max-w-sm">{message || "Something went wrong while fetching data. Please check your connection and try again."}</p>
            <button
                onClick={onRetry}
                className="flex items-center gap-2 px-4 py-2 bg-white border border-slate-300 rounded-lg text-slate-700 font-medium hover:bg-slate-50 transition-colors"
            >
                <RefreshCw className="w-4 h-4" />
                Retry
            </button>
        </div>
    );
};

export default ErrorState;
