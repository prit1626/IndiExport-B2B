import React from 'react';

const RfqDetailsSkeleton = () => {
    return (
        <div className="container mx-auto px-4 py-8 animate-pulse">
            <div className="h-8 bg-slate-200 rounded w-1/3 mb-6"></div>
            <div className="grid grid-cols-1 md:grid-cols-3 gap-8">
                <div className="md:col-span-2 space-y-6">
                    <div className="h-64 bg-slate-200 rounded-xl"></div>
                    <div className="space-y-2">
                        <div className="h-4 bg-slate-200 rounded w-full"></div>
                        <div className="h-4 bg-slate-200 rounded w-5/6"></div>
                    </div>
                </div>
                <div className="h-96 bg-slate-200 rounded-xl"></div>
            </div>
        </div>
    );
};

export default RfqDetailsSkeleton;
