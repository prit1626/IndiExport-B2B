import React from 'react';

const RfqBrowseSkeleton = () => {
    return (
        <div className="space-y-4">
            {[1, 2, 3, 4].map((i) => (
                <div key={i} className="bg-white rounded-xl border border-slate-200 p-4 animate-pulse flex gap-4">
                    <div className="w-20 h-20 bg-slate-200 rounded-lg flex-shrink-0"></div>
                    <div className="flex-1 space-y-3 py-1">
                        <div className="h-4 bg-slate-200 rounded w-3/4"></div>
                        <div className="h-3 bg-slate-200 rounded w-full"></div>
                        <div className="flex gap-2">
                            <div className="h-6 w-20 bg-slate-200 rounded"></div>
                            <div className="h-6 w-20 bg-slate-200 rounded"></div>
                        </div>
                    </div>
                </div>
            ))}
        </div>
    );
};

export default RfqBrowseSkeleton;
