import React from 'react';
import { useNavigate } from 'react-router-dom';
import { motion } from 'framer-motion';
import { Home, ArrowLeft } from 'lucide-react';

const NotFoundPage = () => {
    const navigate = useNavigate();

    return (
        <div className="min-h-screen flex items-center justify-center bg-slate-50 dark:bg-slate-950 px-4">
            <div className="max-w-md w-full text-center">
                <motion.div
                    initial={{ opacity: 0, y: 20 }}
                    animate={{ opacity: 1, y: 0 }}
                    transition={{ duration: 0.5 }}
                >
                    <div className="text-9xl font-black text-slate-200 dark:text-slate-800 mb-4">404</div>
                    <h1 className="text-3xl font-bold text-slate-900 dark:text-white mb-4">Page Not Found</h1>
                    <p className="text-slate-500 dark:text-slate-400 mb-8 leading-relaxed">
                        Oops! The page you're looking for doesn't exist or has been moved.
                    </p>

                    <div className="flex flex-col sm:flex-row gap-4 justify-center">
                        <button
                            onClick={() => navigate(-1)}
                            className="flex items-center justify-center gap-2 px-6 py-3 bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 rounded-xl font-bold text-slate-700 dark:text-slate-300 hover:bg-slate-50 dark:hover:bg-slate-800 transition-all shadow-sm"
                        >
                            <ArrowLeft size={18} />
                            Go Back
                        </button>
                        <button
                            onClick={() => navigate('/')}
                            className="flex items-center justify-center gap-2 px-6 py-3 bg-brand-600 rounded-xl font-bold text-white hover:bg-brand-700 transition-all shadow-lg shadow-brand-500/20"
                        >
                            <Home size={18} />
                            Return Home
                        </button>
                    </div>
                </motion.div>
            </div>
        </div>
    );
};

export default NotFoundPage;
