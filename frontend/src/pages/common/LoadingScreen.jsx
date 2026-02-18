import React from 'react';
import { motion } from 'framer-motion';
import Loader from '../../components/common/Loader';

const LoadingScreen = () => {
    return (
        <div className="min-h-screen flex flex-col items-center justify-center bg-white dark:bg-slate-950">
            <motion.div
                initial={{ opacity: 0, scale: 0.9 }}
                animate={{ opacity: 1, scale: 1 }}
                className="flex flex-col items-center"
            >
                {/* Brand Logo Placeholder */}
                <div className="w-16 h-16 bg-brand-600 rounded-2xl mb-8 flex items-center justify-center text-white font-black text-2xl shadow-xl shadow-brand-500/20">
                    IE
                </div>

                <Loader size="lg" />

                <p className="mt-6 text-slate-500 dark:text-slate-400 font-medium animate-pulse">
                    Initializing IndiExport...
                </p>
            </motion.div>
        </div>
    );
};

export default LoadingScreen;
