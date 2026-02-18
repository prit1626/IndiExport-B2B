import React from 'react';
import { useNavigate } from 'react-router-dom';
import { motion } from 'framer-motion';
import { ShieldAlert, Home, LogIn } from 'lucide-react';
import useAuthStore from '../../store/authStore';

const AccessDeniedPage = () => {
    const navigate = useNavigate();
    const { isAuthenticated } = useAuthStore();

    return (
        <div className="min-h-screen flex items-center justify-center bg-slate-50 dark:bg-slate-950 px-4">
            <div className="max-w-md w-full text-center">
                <motion.div
                    initial={{ opacity: 0, scale: 0.95 }}
                    animate={{ opacity: 1, scale: 1 }}
                    transition={{ duration: 0.5 }}
                >
                    <div className="w-24 h-24 bg-red-100 dark:bg-red-900/30 text-red-600 dark:text-red-400 rounded-full flex items-center justify-center mx-auto mb-8 shadow-inner">
                        <ShieldAlert size={48} />
                    </div>

                    <h1 className="text-3xl font-bold text-slate-900 dark:text-white mb-4">Access Denied</h1>
                    <p className="text-slate-500 dark:text-slate-400 mb-8 leading-relaxed">
                        You don't have the required permissions to view this page. If you believe this is an error, please contact support.
                    </p>

                    <div className="flex flex-col sm:flex-row gap-4 justify-center">
                        {!isAuthenticated ? (
                            <button
                                onClick={() => navigate('/auth/login')}
                                className="flex items-center justify-center gap-2 px-6 py-3 bg-brand-600 rounded-xl font-bold text-white hover:bg-brand-700 transition-all shadow-lg shadow-brand-500/20"
                            >
                                <LogIn size={18} />
                                Sign In
                            </button>
                        ) : (
                            <button
                                onClick={() => navigate('/')}
                                className="flex items-center justify-center gap-2 px-6 py-3 bg-brand-600 rounded-xl font-bold text-white hover:bg-brand-700 transition-all shadow-lg shadow-brand-500/20"
                            >
                                <Home size={18} />
                                Back to Dashboard
                            </button>
                        )}
                    </div>
                </motion.div>
            </div>
        </div>
    );
};

export default AccessDeniedPage;
