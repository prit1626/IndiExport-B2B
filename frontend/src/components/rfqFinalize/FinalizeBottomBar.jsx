import React from 'react';
import { ArrowRight } from 'lucide-react';
import { motion, AnimatePresence } from 'framer-motion';

const FinalizeBottomBar = ({ isVisible, onFinalizeClick }) => {
    return (
        <AnimatePresence>
            {isVisible && (
                <motion.div
                    initial={{ y: 100 }}
                    animate={{ y: 0 }}
                    exit={{ y: 100 }}
                    className="fixed bottom-0 left-0 right-0 bg-white border-t border-slate-200 p-4 shadow-2xl z-40"
                >
                    <div className="container mx-auto max-w-7xl flex items-center justify-between">
                        <div>
                            <p className="font-semibold text-slate-900">Quote Selected</p>
                            <p className="text-xs text-slate-500">Proceed to create order and initiate payment.</p>
                        </div>
                        <button
                            onClick={onFinalizeClick}
                            className="bg-brand-600 hover:bg-brand-700 text-white px-8 py-3 rounded-lg font-bold shadow-lg hover:shadow-xl transition-all flex items-center gap-2"
                        >
                            Finalize Selected Quote <ArrowRight size={20} />
                        </button>
                    </div>
                </motion.div>
            )}
        </AnimatePresence>
    );
};

export default FinalizeBottomBar;
