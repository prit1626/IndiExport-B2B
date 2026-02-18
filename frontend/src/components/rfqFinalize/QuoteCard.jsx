import React from 'react';
import { CheckCircle2, Factory, Clock, Box, ShieldCheck, Star } from 'lucide-react';
import { motion } from 'framer-motion';

const QuoteCard = ({ quote, isSelected, onSelect }) => {
    return (
        <motion.div
            layout
            onClick={() => onSelect(quote.id)}
            className={`
                relative cursor-pointer rounded-xl border-2 p-5 transition-all
                ${isSelected
                    ? 'border-brand-500 bg-brand-50/50 shadow-md ring-1 ring-brand-500/20'
                    : 'border-slate-200 bg-white hover:border-brand-200 hover:shadow-sm'}
            `}
        >
            {/* Selection Checkmark */}
            {isSelected && (
                <div className="absolute top-4 right-4 text-brand-600">
                    <CheckCircle2 size={24} fill="currentColor" className="text-white" />
                </div>
            )}

            <div className="flex items-center gap-3 mb-4">
                <div className="w-10 h-10 bg-slate-100 rounded-full flex items-center justify-center text-slate-500">
                    <Factory size={20} />
                </div>
                <div>
                    <h3 className="font-semibold text-slate-900">{quote.sellerName || 'Seller'}</h3>
                    <div className="flex items-center gap-1 text-xs text-slate-500">
                        {quote.sellerRating > 0 && (
                            <span className="flex items-center text-yellow-500 font-bold">
                                <Star size={10} fill="currentColor" /> {quote.sellerRating}
                            </span>
                        )}
                        {quote.isIecVerified && (
                            <span className="text-green-600 bg-green-50 px-1.5 py-0.5 rounded text-[10px] font-bold border border-green-100">IEC Verified</span>
                        )}
                    </div>
                </div>
            </div>

            <div className="mb-4">
                <p className="text-xs text-slate-500 uppercase font-bold tracking-wider mb-1">Quoted Price</p>
                <div className="flex items-baseline gap-1">
                    <span className="text-2xl font-bold text-slate-900">₹{(quote.priceINRPaise / 100).toLocaleString()}</span>
                    <span className="text-sm text-slate-500">/ unit</span>
                </div>
            </div>

            <div className="grid grid-cols-2 gap-3 text-sm text-slate-600 bg-slate-50/50 p-3 rounded-lg border border-slate-100">
                <div className="flex items-center gap-2">
                    <Box size={14} className="text-slate-400" />
                    <span>Min: {quote.minQty}</span>
                </div>
                <div className="flex items-center gap-2">
                    <Clock size={14} className="text-slate-400" />
                    <span>{quote.leadTimeDays} Days</span>
                </div>
                <div className="flex items-center gap-2 col-span-2">
                    <ShieldCheck size={14} className="text-slate-400" />
                    <span>Incoterm: <span className="font-medium text-slate-800">{quote.incoterm}</span></span>
                </div>
            </div>

            {quote.notes && (
                <div className="mt-3 text-sm text-slate-500 italic border-l-2 border-slate-200 pl-2">
                    "{quote.notes}"
                </div>
            )}
        </motion.div>
    );
};

export default QuoteCard;
