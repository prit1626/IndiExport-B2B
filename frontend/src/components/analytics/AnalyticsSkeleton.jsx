import React from 'react';

const AnalyticsSkeleton = () => {
    return (
        <div className="animate-pulse space-y-8">
            {/* Header */}
            <div className="flex justify-between items-center">
                <div className="h-8 w-48 bg-slate-200 rounded-lg"></div>
                <div className="h-10 w-64 bg-slate-200 rounded-xl"></div>
            </div>

            {/* KPI Grid */}
            <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6">
                {[1, 2, 3, 4].map((i) => (
                    <div key={i} className="h-32 bg-slate-200 rounded-2xl"></div>
                ))}
            </div>

            {/* Charts */}
            <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
                <div className="h-80 bg-slate-200 rounded-2xl"></div>
                <div className="h-80 bg-slate-200 rounded-2xl"></div>
            </div>
        </div>
    );
};

export default AnalyticsSkeleton;
