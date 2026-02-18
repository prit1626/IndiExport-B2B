import React from 'react';
import { motion } from 'framer-motion';

const Toggle = ({ checked, onChange, label }) => {
    return (
        <div className="flex items-center cursor-pointer" onClick={() => onChange(!checked)}>
            <div className={`relative w-11 h-6 rounded-full transition-colors duration-200 ease-in-out ${checked ? 'bg-brand-600' : 'bg-slate-200'}`}>
                <motion.div
                    className="absolute top-0.5 left-0.5 w-5 h-5 bg-white rounded-full shadow-sm"
                    animate={{ x: checked ? 20 : 0 }}
                    transition={{ type: "spring", stiffness: 500, damping: 30 }}
                />
            </div>
            {label && <span className="ml-3 text-sm font-medium text-slate-700 select-none">{label}</span>}
        </div>
    );
};

export default Toggle;
