import React from 'react';

const TrackingSkeleton = () => {
    return (
        <div className="bg-white rounded-xl shadow-sm border border-slate-200 p-6 animate-pulse">
            <div className="h-6 w-48 bg-slate-200 rounded mb-6"></div>

            <div className="space-y-8 pl-4 border-l-2 border-slate-100">
                {[1, 2, 3].map(i => (
                    <div key={i} className="relative pl-6">
                        <div className="absolute -left-[9px] top-1 w-4 h-4 rounded-full bg-slate-200 ring-4 ring-white"></div>
                        <div className="h-4 w-32 bg-slate-200 rounded mb-2"></div>
                        <div className="h-3 w-48 bg-slate-100 rounded mb-1"></div>
                        <div className="h-3 w-24 bg-slate-100 rounded"></div>
                    </div>
                ))}
            </div>
        </div>
    );
};

export default TrackingSkeleton;
