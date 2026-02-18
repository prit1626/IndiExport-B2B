
import React from 'react';

const SettingsSkeleton = () => {
    return (
        <div className="max-w-4xl mx-auto space-y-6 animate-pulse">
            {[1, 2, 3, 4].map((i) => (
                <div key={i} className="bg-white p-6 rounded-lg border border-slate-200">
                    <div className="h-6 bg-slate-200 rounded w-1/3 mb-4"></div>
                    <div className="space-y-3">
                        <div className="h-4 bg-slate-100 rounded w-full"></div>
                        <div className="h-10 bg-slate-100 rounded w-full"></div>
                    </div>
                </div>
            ))}
        </div>
    );
};

export default SettingsSkeleton;
