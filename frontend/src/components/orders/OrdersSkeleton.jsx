import React from 'react';

const OrdersSkeleton = () => {
    return (
        <div className="space-y-4">
            {[1, 2, 3].map((i) => (
                <div key={i} className="bg-white rounded-xl border border-slate-200 p-5 animate-pulse">
                    <div className="flex justify-between items-start mb-4">
                        <div className="space-y-2">
                            <div className="h-6 w-32 bg-slate-200 rounded"></div>
                            <div className="h-4 w-48 bg-slate-100 rounded"></div>
                        </div>
                        <div className="space-y-2 text-right">
                            <div className="h-4 w-20 bg-slate-100 rounded ml-auto"></div>
                            <div className="h-7 w-24 bg-slate-200 rounded ml-auto"></div>
                        </div>
                    </div>
                    <div className="h-px bg-slate-100 my-4"></div>
                    <div className="flex gap-6">
                        <div className="h-5 w-20 bg-slate-100 rounded"></div>
                        <div className="h-5 w-24 bg-slate-100 rounded"></div>
                    </div>
                </div>
            ))}
        </div>
    );
};

export default OrdersSkeleton;
