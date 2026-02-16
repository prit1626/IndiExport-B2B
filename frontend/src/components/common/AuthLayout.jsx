import React from 'react';
import { motion } from 'framer-motion';
import { Link } from 'react-router-dom';
import { Layers } from 'lucide-react';

const AuthLayout = ({ children, title, subtitle }) => {
    return (
        <div className="min-h-screen flex flex-col justify-center items-center bg-slate-50 relative overflow-hidden">
            {/* Background Decor */}
            <div className="absolute top-0 left-0 w-full h-full overflow-hidden z-0">
                <div className="absolute -top-[20%] -left-[10%] w-[70%] h-[70%] rounded-full bg-brand-400/20 blur-3xl" />
                <div className="absolute top-[40%] -right-[10%] w-[60%] h-[60%] rounded-full bg-indigo-400/20 blur-3xl" />
            </div>

            <div className="z-10 w-full max-w-md px-4">
                {/* Logo / Header */}
                <div className="text-center mb-8">
                    <Link to="/" className="inline-flex items-center gap-2 mb-2">
                        <div className="p-2 bg-white rounded-xl shadow-sm">
                            <Layers className="w-8 h-8 text-brand-600" />
                        </div>
                        <span className="text-2xl font-bold text-slate-900 tracking-tight">IndiExport</span>
                    </Link>
                    <motion.h2
                        initial={{ opacity: 0, y: 10 }}
                        animate={{ opacity: 1, y: 0 }}
                        className="text-2xl font-bold text-slate-900"
                    >
                        {title}
                    </motion.h2>
                    {subtitle && (
                        <motion.p
                            initial={{ opacity: 0, y: 10 }}
                            animate={{ opacity: 1, y: 0 }}
                            transition={{ delay: 0.1 }}
                            className="mt-2 text-sm text-slate-600"
                        >
                            {subtitle}
                        </motion.p>
                    )}
                </div>

                {/* Card Content */}
                <motion.div
                    initial={{ opacity: 0, y: 20 }}
                    animate={{ opacity: 1, y: 0 }}
                    transition={{ delay: 0.2 }}
                    className="bg-white/80 backdrop-blur-xl rounded-2xl shadow-xl border border-white/50 p-8"
                >
                    {children}
                </motion.div>
            </div>
        </div>
    );
};

export default AuthLayout;
