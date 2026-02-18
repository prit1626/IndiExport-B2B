import React from 'react';
import { motion } from 'framer-motion';
import { MapPin, Clock, Package, DollarSign, FileText } from 'lucide-react';
import { formatTime } from '../../utils/formatDate'; // Assuming reuse or chatUtils

const RfqCard = ({ rfq, onClick }) => {
    return (
        <motion.div
            layout
            initial={{ opacity: 0, y: 10 }}
            animate={{ opacity: 1, y: 0 }}
            whileHover={{ y: -2, boxShadow: '0 4px 6px -1px rgb(0 0 0 / 0.1), 0 2px 4px -2px rgb(0 0 0 / 0.1)' }}
            onClick={onClick}
            className="bg-white rounded-xl border border-slate-200 p-4 cursor-pointer transition-shadow"
        >
            <div className="flex gap-4">
                {/* Thumbnail */}
                <div className="w-20 h-20 bg-slate-100 rounded-lg flex-shrink-0 overflow-hidden">
                    {rfq.media?.[0] ? (
                        <img src={rfq.media[0].url} alt={rfq.title} className="w-full h-full object-cover" />
                    ) : (
                        <div className="w-full h-full flex items-center justify-center text-slate-300">
                            <Package size={24} />
                        </div>
                    )}
                </div>

                {/* Content */}
                <div className="flex-1 min-w-0">
                    <div className="flex justify-between items-start mb-1">
                        <h3 className="font-semibold text-slate-900 truncate pr-2">{rfq.title}</h3>
                        <span className="text-xs text-slate-500 whitespace-nowrap flex items-center gap-1">
                            <Clock size={12} /> {formatTime(rfq.createdAt)}
                        </span>
                    </div>

                    <div className="text-sm text-slate-600 mb-3 line-clamp-2">
                        {rfq.details}
                    </div>

                    <div className="flex flex-wrap gap-y-2 gap-x-4 text-xs font-medium text-slate-500">
                        <span className="flex items-center gap-1 bg-slate-50 px-2 py-1 rounded">
                            <Package size={14} className="text-brand-600" />
                            {rfq.qty} {rfq.unit}
                        </span>

                        <span className="flex items-center gap-1 bg-slate-50 px-2 py-1 rounded">
                            <MapPin size={14} className="text-brand-600" />
                            {rfq.destinationCountry} ({rfq.incoterm})
                        </span>

                        {rfq.targetPriceINRPaise && (
                            <span className="flex items-center gap-1 bg-green-50 text-green-700 px-2 py-1 rounded">
                                <DollarSign size={14} />
                                Target: ₹{(rfq.targetPriceINRPaise / 100).toLocaleString()}
                            </span>
                        )}
                    </div>
                </div>
            </div>
        </motion.div>
    );
};

export default RfqCard;
