import React from 'react';
import { FileText, Clock, Box, ShieldCheck, AlertCircle } from 'lucide-react';
import toast from 'react-hot-toast';

const PriceProposalMessage = ({ proposal, isOwnMessage, senderRole }) => {

    // Viewer is BUYER if sender is SELLER (and vice versa for isOwnMessage check logic)
    // Actually, simply:
    // If I sent it (isOwnMessage=true), I am the proposer.
    // If I received it (isOwnMessage=false), I am the viewer (Counterparty).

    const handleAction = (action) => {
        toast('Integration pending: ' + action, { icon: '🚧' });
    };

    return (
        <div className={`rounded-lg overflow-hidden border ${isOwnMessage ? 'bg-brand-50 border-brand-100' : 'bg-white border-slate-200 shadow-sm'} max-w-sm`}>
            {/* Header */}
            <div className={`px-4 py-2 flex items-center justify-between ${isOwnMessage ? 'bg-brand-100/50' : 'bg-slate-50 border-b border-slate-100'}`}>
                <div className="flex items-center gap-2">
                    <FileText size={16} className={isOwnMessage ? 'text-brand-600' : 'text-slate-500'} />
                    <span className={`font-semibold text-sm ${isOwnMessage ? 'text-brand-700' : 'text-slate-700'}`}>Price Proposal</span>
                </div>
                {/* Status Badge (Mock logic) */}
                <span className="text-[10px] font-bold px-2 py-0.5 rounded-full bg-yellow-100 text-yellow-700 uppercase tracking-wide">
                    Pending
                </span>
            </div>

            {/* Content */}
            <div className="p-4 space-y-3">
                <div className="flex justify-between items-baseline">
                    <span className="text-sm text-slate-500">Price</span>
                    <span className="text-xl font-bold text-slate-900">₹{(proposal.priceINRPaise / 100).toLocaleString()}</span>
                </div>

                <div className="grid grid-cols-2 gap-3 text-xs">
                    <div className="bg-white/50 p-2 rounded border border-slate-100/50">
                        <span className="block text-slate-400 mb-0.5">Min Qty</span>
                        <span className="font-medium text-slate-700 flex items-center gap-1">
                            <Box size={12} /> {proposal.minQty}
                        </span>
                    </div>
                    <div className="bg-white/50 p-2 rounded border border-slate-100/50">
                        <span className="block text-slate-400 mb-0.5">Lead Time</span>
                        <span className="font-medium text-slate-700 flex items-center gap-1">
                            <Clock size={12} /> {proposal.leadTimeDays} Days
                        </span>
                    </div>
                </div>

                <div className="flex items-center gap-2 text-xs text-slate-600 bg-slate-50 p-2 rounded">
                    <ShieldCheck size={14} className="text-green-600" />
                    <span>Incoterm: <span className="font-semibold">{proposal.incoterm}</span></span>
                </div>

                {proposal.notes && (
                    <div className="text-xs text-slate-500 italic border-l-2 border-slate-200 pl-2">
                        "{proposal.notes}"
                    </div>
                )}
            </div>

            {/* Actions for Receiver Only */}
            {!isOwnMessage && (
                <div className="px-4 py-3 bg-slate-50 border-t border-slate-100 flex gap-2">
                    <button
                        onClick={() => handleAction('Accept')}
                        className="flex-1 bg-green-600 hover:bg-green-700 text-white text-xs font-semibold py-2 rounded transition-colors"
                    >
                        Accept
                    </button>
                    <button
                        onClick={() => handleAction('Counter')}
                        className="flex-1 bg-white hover:bg-slate-50 text-slate-700 border border-slate-300 text-xs font-semibold py-2 rounded transition-colors"
                    >
                        Counter Offer
                    </button>
                </div>
            )}

            {/* Simple Status for Sender */}
            {isOwnMessage && (
                <div className="px-4 py-2 bg-brand-100/30 border-t border-brand-100 text-center">
                    <span className="text-xs text-brand-600 font-medium select-none">You sent this proposal</span>
                </div>
            )}
        </div>
    );
};

export default PriceProposalMessage;
