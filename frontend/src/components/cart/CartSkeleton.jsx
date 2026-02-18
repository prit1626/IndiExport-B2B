import React from 'react';

const CartSkeleton = () => {
    return (
        <div className="container mx-auto px-4 py-8 animate-pulse">
            <div className="h-8 bg-slate-200 rounded w-48 mb-8" />

            <div className="flex flex-col lg:flex-row gap-8">
                {/* Items List */}
                <div className="flex-1 space-y-4">
                    {[1, 2, 3].map(i => (
                        <div key={i} className="bg-white rounded-xl border border-slate-100 p-6 flex gap-6">
                            <div className="w-24 h-24 bg-slate-200 rounded-lg" />
                            <div className="flex-1 space-y-3">
                                <div className="h-6 bg-slate-200 rounded w-1/2" />
                                <div className="h-4 bg-slate-200 rounded w-1/4" />
                                <div className="flex gap-4 pt-2">
                                    <div className="h-10 bg-slate-200 rounded w-24" />
                                    <div className="h-10 bg-slate-200 rounded w-32" />
                                </div>
                            </div>
                        </div>
                    ))}
                </div>

                {/* Summary */}
                <div className="w-full lg:w-96">
                    <div className="bg-white rounded-xl border border-slate-100 p-6 space-y-6">
                        <div className="h-6 bg-slate-200 rounded w-1/2" />
                        <div className="space-y-3">
                            <div className="h-4 bg-slate-200 rounded w-full" />
                            <div className="h-4 bg-slate-200 rounded w-full" />
                            <div className="h-10 bg-slate-200 rounded w-full mt-4" />
                        </div>
                    </div>
                </div>
            </div>
        </div>
    );
};

export default CartSkeleton;
