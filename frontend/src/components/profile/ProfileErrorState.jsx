import React from 'react';
import { AlertCircle, RefreshCw } from 'lucide-react';

const ProfileErrorState = ({ error, onRetry }) => {
    return (
        <div className="flex flex-col items-center justify-center py-20 px-6 text-center">
            <div className="p-4 bg-rose-50 dark:bg-rose-900/20 rounded-2xl text-rose-500 mb-6">
                <AlertCircle size={48} />
            </div>
            <h2 className="text-2xl font-black text-slate-900 dark:text-white mb-2">Oops! Something went wrong</h2>
            <p className="text-slate-500 dark:text-slate-400 max-w-md mb-8">
                {error?.message || "We couldn't load your profile information. Please check your connection and try again."}
            </p>
            <button
                onClick={onRetry}
                className="flex items-center gap-2 px-8 py-3 bg-indigo-600 hover:bg-indigo-700 text-white rounded-xl font-bold shadow-lg shadow-indigo-500/25 transition-all active:scale-95"
            >
                <RefreshCw size={20} />
                Try Again
            </button>
        </div>
    );
};

export default ProfileErrorState;
