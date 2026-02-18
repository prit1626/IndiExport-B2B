
import React from 'react';

const VerificationSkeleton = () => {
    return (
        <div className="space-y-6 animate-pulse p-4 max-w-5xl mx-auto">
            <div className="h-32 bg-slate-200 rounded-xl w-full"></div>
            <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
                <div className="h-64 bg-slate-200 rounded-xl w-full"></div>
                <div className="h-64 bg-slate-200 rounded-xl w-full"></div>
            </div>
            <div className="h-12 bg-slate-200 rounded-lg w-1/3 mx-auto"></div>
        </div>
    );
};

export default VerificationSkeleton;
