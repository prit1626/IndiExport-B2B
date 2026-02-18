import React from 'react';

const OrderDetailsSkeleton = () => {
    return (
        <div className="space-y-8 animate-pulse">
            {/* Header */}
            <div className="flex justify-between items-center">
                <div className="space-y-2">
                    <div className="h-8 w-48 bg-slate-200 rounded"></div>
                    <div className="h-4 w-32 bg-slate-100 rounded"></div>
                </div>
                <div className="h-10 w-32 bg-slate-200 rounded"></div>
            </div>

            {/* Timeline */}
            <div className="h-20 bg-slate-100 rounded-lg w-full"></div>

            <div className="flex flex-col lg:flex-row gap-8">
                {/* Left Col */}
                <div className="flex-1 space-y-6">
                    {/* Items */}
                    <div className="bg-white p-6 rounded-xl border border-slate-200 space-y-4">
                        <div className="h-6 w-32 bg-slate-200 rounded"></div>
                        <div className="h-24 bg-slate-50 rounded"></div>
                        <div className="h-24 bg-slate-50 rounded"></div>
                    </div>
                </div>

                {/* Right Col */}
                <div className="w-full lg:w-80 space-y-6">
                    <div className="bg-white p-6 rounded-xl border border-slate-200 h-64"></div>
                    <div className="bg-white p-6 rounded-xl border border-slate-200 h-40"></div>
                </div>
            </div>
        </div>
    );
};

export default OrderDetailsSkeleton;
