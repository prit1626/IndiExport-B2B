import React from 'react';
import { motion } from 'framer-motion';

const ProfileTabs = ({ tabs, activeTab, onTabChange }) => {
    return (
        <div className="flex border-b border-slate-200 dark:border-slate-800 mb-8 relative">
            {tabs.map((tab) => {
                const isActive = activeTab === tab.id;
                return (
                    <button
                        key={tab.id}
                        onClick={() => onTabChange(tab.id)}
                        className={`relative px-6 py-4 text-sm font-semibold transition-colors duration-200 focus:outline-none flex items-center gap-2 ${isActive
                                ? 'text-indigo-600 dark:text-indigo-400'
                                : 'text-slate-500 dark:text-slate-400 hover:text-slate-700 dark:hover:text-slate-200'
                            }`}
                    >
                        {tab.icon && <tab.icon size={18} />}
                        {tab.label}

                        {isActive && (
                            <motion.div
                                layoutId="activeTabUnderline"
                                className="absolute bottom-0 left-0 right-0 h-0.5 bg-indigo-600 dark:bg-indigo-400 z-10"
                                transition={{ type: 'spring', stiffness: 300, damping: 30 }}
                            />
                        )}
                    </button>
                );
            })}
        </div>
    );
};

export default ProfileTabs;
