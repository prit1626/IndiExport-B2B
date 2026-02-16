import React from 'react';

const ProductCardSkeleton = () => (
    <div className="bg-white rounded-2xl border border-slate-200 overflow-hidden animate-pulse">
        {/* Image Skeleton */}
        <div className="aspect-[4/3] bg-slate-200" />

        <div className="p-4 space-y-3">
            {/* Seller Info */}
            <div className="flex items-center justify-between">
                <div className="h-3 bg-slate-200 rounded w-1/3" />
                <div className="h-3 bg-slate-200 rounded w-10" />
            </div>

            {/* Title */}
            <div className="space-y-1 pt-1">
                <div className="h-4 bg-slate-200 rounded w-full" />
                <div className="h-4 bg-slate-200 rounded w-2/3" />
            </div>

            {/* Chips */}
            <div className="flex gap-2 pt-1">
                <div className="h-6 bg-slate-200 rounded w-16" />
                <div className="h-6 bg-slate-200 rounded w-16" />
            </div>

            {/* Price */}
            <div className="pt-2">
                <div className="h-6 bg-slate-200 rounded w-1/2" />
                <div className="h-3 bg-slate-200 rounded w-1/3 mt-1" />
            </div>
        </div>
    </div>
);

const ProductGridSkeleton = () => {
    return (
        <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 xl:grid-cols-3 gap-6">
            {[...Array(6)].map((_, i) => (
                <ProductCardSkeleton key={i} />
            ))}
        </div>
    );
};

export default ProductGridSkeleton;
