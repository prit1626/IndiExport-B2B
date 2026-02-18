import React from 'react';
import { ArrowLeft, Package, MapPin, ExternalLink } from 'lucide-react';
import { useNavigate } from 'react-router-dom';

const RfqChatHeader = ({ chat, onBack }) => {
    if (!chat) return (
        <div className="h-16 bg-white border-b border-slate-200 flex items-center px-4">
            <div className="h-6 w-1/3 bg-slate-200 rounded animate-pulse"></div>
        </div>
    );

    return (
        <div className="h-16 bg-white border-b border-slate-200 flex items-center justify-between px-4 sticky top-0 z-30 shadow-sm">
            <div className="flex items-center gap-3">
                <button
                    onClick={onBack}
                    className="md:hidden p-2 -ml-2 text-slate-600 hover:bg-slate-100 rounded-full"
                >
                    <ArrowLeft size={20} />
                </button>

                <div>
                    <h2 className="font-bold text-slate-800 text-sm md:text-base flex items-center gap-2">
                        {chat.rfqTitle}
                        <span className="text-[10px] font-normal px-2 py-0.5 bg-slate-100 border border-slate-200 rounded text-slate-600">RFQ</span>
                    </h2>
                    <div className="flex items-center gap-3 text-xs text-slate-500">
                        <span className="flex items-center gap-1">
                            <Package size={12} /> {chat.qty} {chat.unit}
                        </span>
                        <span>•</span>
                        <span className="flex items-center gap-1">
                            <MapPin size={12} /> {chat.destinationCountry}
                        </span>
                    </div>
                </div>
            </div>

            <button
                // Navigate to public RFQ details or seller details based on role (not implemented here, keeping generic)
                className="text-brand-600 hover:bg-brand-50 px-3 py-1.5 rounded-lg text-xs font-medium flex items-center gap-1 transition-colors"
                title="View RFQ Details"
            >
                View RFQ <ExternalLink size={14} />
            </button>
        </div>
    );
};

export default RfqChatHeader;
