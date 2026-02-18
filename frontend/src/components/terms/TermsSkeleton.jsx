
import React from 'react';

const TermsSkeleton = () => {
    return (
        <div className="max-w-4xl mx-auto py-10 px-4 space-y-8 animate-pulse">
            <div className="h-10 bg-slate-200 rounded w-1/3 mx-auto"></div>
            <div className="space-y-4">
                <div className="h-4 bg-slate-200 rounded w-full"></div>
                <div className="h-4 bg-slate-200 rounded w-5/6"></div>
                <div className="h-4 bg-slate-200 rounded w-full"></div>
                <div className="h-4 bg-slate-200 rounded w-4/5"></div>
            </div>
            <div className="h-60 bg-slate-100 rounded-xl w-full"></div>
        </div>
    );
};

export default TermsSkeleton;
