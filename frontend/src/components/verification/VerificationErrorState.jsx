
import React from 'react';
import { AlertTriangle, RefreshCcw } from 'lucide-react';

const VerificationErrorState = ({ message = "Failed to load verification details", onRetry }) => {
    return (
        <div className="flex flex-col items-center justify-center py-20 bg-white rounded-xl border border-dashed border-slate-300">
            <AlertTriangle className="text-red-500 w-12 h-12 mb-4" />
            <h3 className="text-lg font-medium text-slate-900">Something went wrong</h3>
            <p className="text-slate-500 mb-6">{message}</p>
            {onRetry && (
                <button
                    onClick={onRetry}
                    className="flex items-center gap-2 px-4 py-2 bg-slate-100 hover:bg-slate-200 text-slate-700 rounded-lg font-medium transition-colors"
                >
                    <RefreshCcw size={18} /> Try Again
                </button>
            )}
        </div>
    );
};

export default VerificationErrorState;
