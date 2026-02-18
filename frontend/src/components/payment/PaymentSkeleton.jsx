import React from 'react';

const PaymentSkeleton = () => {
    return (
        <div className="container mx-auto px-4 py-8 animate-pulse">
            <div className="h-8 bg-slate-200 rounded w-48 mb-8" />
            <div className="grid grid-cols-1 md:grid-cols-2 gap-8 lg:gap-12">
                <div className="space-y-6">
                    <div className="h-64 bg-slate-200 rounded-2xl" />
                    <div className="h-12 bg-slate-200 rounded-xl" />
                </div>
                <div className="bg-white rounded-2xl border border-slate-100 p-6 space-y-4">
                    <div className="h-6 bg-slate-200 rounded w-1/3" />
                    <div className="h-24 bg-slate-200 rounded" />
                    <div className="h-32 bg-slate-200 rounded" />
                </div>
            </div>
        </div>
    );
};

export default PaymentSkeleton;
