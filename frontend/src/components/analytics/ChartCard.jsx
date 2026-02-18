import React from 'react';
import { motion } from 'framer-motion';

const ChartCard = ({ title, children, actions }) => {
    return (
        <motion.div
            initial={{ opacity: 0, y: 10 }}
            animate={{ opacity: 1, y: 0 }}
            transition={{ duration: 0.4 }}
            className="bg-white p-6 rounded-2xl border border-slate-200 shadow-sm h-full flex flex-col"
        >
            <div className="flex items-center justify-between mb-6">
                <h3 className="text-lg font-bold text-slate-800">{title}</h3>
                {actions && <div>{actions}</div>}
            </div>
            <div className="flex-1 w-full min-h-[300px]">
                {children}
            </div>
        </motion.div>
    );
};

export default ChartCard;
