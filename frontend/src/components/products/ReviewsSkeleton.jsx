import React from 'react';

const ReviewsSkeleton = () => {
    return (
        <div className="space-y-6">
            {[1, 2, 3].map((i) => (
                <div key={i} className="flex gap-4 p-6 bg-white rounded-xl border border-slate-100 animate-pulse">
                    <div className="w-12 h-12 bg-slate-200 rounded-full flex-shrink-0" />
                    <div className="flex-1 space-y-3">
                        <div className="flex justify-between">
                            <div className="h-4 bg-slate-200 rounded w-32" />
                            <div className="h-4 bg-slate-200 rounded w-20" />
                        </div>
                        <div className="flex gap-2">
                            {[1, 2, 3, 4, 5].map(j => <div key={j} className="w-4 h-4 bg-slate-200 rounded" />)}
                        </div>
                        <div className="space-y-2">
                            <div className="h-4 bg-slate-200 rounded w-full" />
                            <div className="h-4 bg-slate-200 rounded w-3/4" />
                        </div>
                    </div>
                </div>
            ))}
        </div>
    );
};

export default ReviewsSkeleton;
