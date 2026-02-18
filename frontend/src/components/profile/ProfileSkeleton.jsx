import React from 'react';

const ProfileSkeleton = () => {
    return (
        <div className="animate-pulse space-y-8">
            <div className="h-48 w-full bg-slate-200 dark:bg-slate-800 rounded-3xl" />
            <div className="container mx-auto px-6 -mt-16 h-40 bg-white dark:bg-slate-900 rounded-2xl border border-slate-100 dark:border-slate-800" />

            <div className="flex gap-4 border-b border-slate-100 dark:border-slate-800">
                <div className="h-10 w-24 bg-slate-200 dark:bg-slate-800 rounded-lg" />
                <div className="h-10 w-24 bg-slate-200 dark:bg-slate-800 rounded-lg" />
            </div>

            <div className="grid grid-cols-1 md:grid-cols-2 gap-8">
                <div className="h-64 bg-white dark:bg-slate-900 rounded-2xl border border-slate-100 dark:border-slate-800" />
                <div className="h-64 bg-white dark:bg-slate-900 rounded-2xl border border-slate-100 dark:border-slate-800" />
            </div>
        </div>
    );
};

export default ProfileSkeleton;
