import React from 'react';
import { motion } from 'framer-motion';
import { ArrowUpRight, ArrowDownRight, Minus } from 'lucide-react';

const StatCard = ({ title, value, icon: Icon, trend, trendLabel, color = "brand", onClick }) => {
    const isPositive = trend > 0;
    const isNeutral = trend === 0;

    const colorClasses = {
        brand: "bg-brand-50 text-brand-600",
        blue: "bg-blue-50 text-blue-600",
        green: "bg-green-50 text-green-600",
        orange: "bg-orange-50 text-orange-600",
        purple: "bg-purple-50 text-purple-600",
    };

    return (
        <motion.div
            whileHover={{ y: -2 }}
            onClick={onClick}
            className={`bg-white p-6 rounded-2xl border border-slate-200 shadow-sm ${onClick ? 'cursor-pointer hover:border-brand-500/30 transition-all' : ''}`}
        >
            <div className="flex items-center justify-between mb-4">
                <div className={`p-3 rounded-xl ${colorClasses[color] || colorClasses.brand}`}>
                    <Icon className="w-6 h-6" />
                </div>
                {trend !== undefined && (
                    <div className={`flex items-center gap-1 text-sm font-medium ${isPositive ? 'text-green-600' : isNeutral ? 'text-slate-500' : 'text-red-600'}`}>
                        {isPositive ? <ArrowUpRight className="w-4 h-4" /> : isNeutral ? <Minus className="w-4 h-4" /> : <ArrowDownRight className="w-4 h-4" />}
                        <span>{Math.abs(trend)}%</span>
                    </div>
                )}
            </div>
            <div>
                <p className="text-slate-500 text-sm font-medium mb-1">{title}</p>
                <h3 className="text-2xl font-bold text-slate-900">{value}</h3>
                {trendLabel && <p className="text-xs text-slate-400 mt-1">{trendLabel}</p>}
            </div>
        </motion.div>
    );
};

export default StatCard;
