
import React from 'react';

const UploadProgressBar = ({ progress, label }) => {
    return (
        <div className="w-full">
            <div className="flex justify-between text-xs font-medium text-slate-500 mb-1">
                <span>{label || 'Uploading...'}</span>
                <span>{progress}%</span>
            </div>
            <div className="w-full bg-slate-200 rounded-full h-2 overflow-hidden">
                <div
                    className="bg-brand-600 h-2 rounded-full transition-all duration-300"
                    style={{ width: `${progress}%` }}
                ></div>
            </div>
        </div>
    );
};

export default UploadProgressBar;
