import React from 'react';

const ProductDetailsSkeleton = () => {
    return (
        <div className="container mx-auto px-4 py-8 animate-pulse">
            <div className="grid grid-cols-1 md:grid-cols-2 gap-12">
                {/* Left: Media */}
                <div className="bg-slate-200 rounded-2xl aspect-square w-full" />

                {/* Right: Info */}
                <div className="space-y-6">
                    <div className="h-8 bg-slate-200 rounded w-3/4" />
                    <div className="h-6 bg-slate-200 rounded w-1/4" />

                    <div className="h-24 bg-slate-200 rounded-2xl w-full" />

                    <div className="grid grid-cols-2 gap-4">
                        {[1, 2, 3, 4].map(i => (
                            <div key={i} className="h-12 bg-slate-200 rounded-lg" />
                        ))}
                    </div>

                    <div className="h-16 bg-slate-200 rounded-xl w-full" />

                    <div className="flex gap-4 pt-4">
                        <div className="h-12 bg-slate-200 rounded-xl flex-1" />
                        <div className="h-12 bg-slate-200 rounded-xl flex-1" />
                    </div>
                </div>
            </div>
        </div>
    );
};

export default ProductDetailsSkeleton;
