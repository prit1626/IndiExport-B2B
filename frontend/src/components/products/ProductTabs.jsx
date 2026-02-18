import React, { useState } from 'react';
import { motion } from 'framer-motion';

const tabs = [
    { id: 'desc', label: 'Description' },
    { id: 'specs', label: 'Specifications' },
    { id: 'reviews', label: 'Reviews' }
];

const ProductTabs = ({ activeTab, onChange }) => {
    return (
        <div className="border-b border-slate-200 mb-6">
            <div className="flex gap-8">
                {tabs.map((tab) => (
                    <button
                        key={tab.id}
                        onClick={() => onChange(tab.id)}
                        className={`relative pb-4 text-sm font-medium transition-colors ${activeTab === tab.id ? 'text-brand-600' : 'text-slate-500 hover:text-slate-700'
                            }`}
                    >
                        {tab.label}
                        {activeTab === tab.id && (
                            <motion.div
                                layoutId="activeTab"
                                className="absolute bottom-0 left-0 right-0 h-0.5 bg-brand-600 rounded-full"
                            />
                        )}
                    </button>
                ))}
            </div>
        </div>
    );
};

export default ProductTabs;
