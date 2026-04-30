import React, { useState } from 'react';
import { motion } from 'framer-motion';
import { FileText, Clock, CheckCircle2, Loader2, DollarSign } from 'lucide-react';
import rfqChatApi from '../../api/rfqChatApi';
import toast from 'react-hot-toast';

/**
 * Rendered when messageType === 'PRICE_PROPOSAL'.
 *
 * Props from RfqMessageBubble:
 *   message     – the full message object from backend
 *   isOwnMessage – true if current user sent it
 *   isBuyer     – true if current user is BUYER (can accept)
 *   onAccepted  – callback(orderId) after successful accept
 */
const PriceProposalMessage = ({ message, isOwnMessage, isBuyer, onAccepted }) => {
    const [accepting, setAccepting] = useState(false);
    const [accepted, setAccepted] = useState(message.accepted || false);

    const price = message.proposedPriceMinor;
    const currency = message.currency || 'USD';
    const leadTime = message.leadTimeDays;

    // Format price as decimal
    const formattedPrice = price
        ? new Intl.NumberFormat('en-US', { style: 'decimal', maximumFractionDigits: 2 })
            .format(price / 100)
        : '—';

    const handleAccept = async () => {
        if (accepting || accepted) return;
        setAccepting(true);
        try {
            const { data } = await rfqChatApi.acceptProposal(message.chatId, message.id);
            setAccepted(true);
            toast.success('Proposal accepted! Redirecting to payment…');
            if (onAccepted) onAccepted(data.orderId);
        } catch (err) {
            const msg = err.response?.data?.message || 'Failed to accept proposal';
            toast.error(msg);
        } finally {
            setAccepting(false);
        }
    };

    return (
        <motion.div
            initial={{ opacity: 0, y: 8 }}
            animate={{ opacity: 1, y: 0 }}
            className={`rounded-2xl overflow-hidden border shadow-sm max-w-xs w-full ${accepted
                    ? 'bg-emerald-50 border-emerald-200'
                    : isOwnMessage
                        ? 'bg-brand-50 border-brand-200'
                        : 'bg-white border-slate-200'
                }`}
        >
            {/* Header */}
            <div className={`px-4 py-2.5 flex items-center justify-between ${accepted ? 'bg-emerald-100/60' : isOwnMessage ? 'bg-brand-100/50' : 'bg-amber-50'
                }`}>
                <div className="flex items-center gap-2">
                    <DollarSign size={15} className={accepted ? 'text-emerald-600' : 'text-amber-600'} />
                    <span className="text-sm font-semibold text-slate-800">Price Proposal</span>
                </div>
                {accepted ? (
                    <span className="flex items-center gap-1 text-[10px] font-bold px-2 py-0.5 rounded-full bg-emerald-500 text-white uppercase tracking-wide">
                        <CheckCircle2 size={10} /> Accepted
                    </span>
                ) : (
                    <span className="text-[10px] font-bold px-2 py-0.5 rounded-full bg-amber-100 text-amber-700 uppercase tracking-wide">
                        Pending
                    </span>
                )}
            </div>

            {/* Body */}
            <div className="p-4 space-y-3">
                <div className="flex justify-between items-baseline">
                    <span className="text-xs text-slate-500">Quoted Price</span>
                    <span className="text-2xl font-bold text-slate-900">
                        {currency} {formattedPrice}
                    </span>
                </div>

                {leadTime && (
                    <div className="flex items-center gap-2 text-xs text-slate-600 bg-slate-50 px-3 py-2 rounded-lg border border-slate-100">
                        <Clock size={13} className="text-slate-400" />
                        <span>Lead time: <span className="font-semibold">{leadTime} days</span></span>
                    </div>
                )}
            </div>

            {/* Actions */}
            {!isOwnMessage && isBuyer && !accepted && (
                <div className="px-4 pb-4">
                    <motion.button
                        whileHover={{ scale: 1.02 }}
                        whileTap={{ scale: 0.97 }}
                        onClick={handleAccept}
                        disabled={accepting}
                        className="w-full bg-emerald-600 hover:bg-emerald-700 disabled:opacity-60 text-white text-sm font-semibold py-2.5 rounded-xl transition-colors flex items-center justify-center gap-2"
                    >
                        {accepting ? <Loader2 size={15} className="animate-spin" /> : <CheckCircle2 size={15} />}
                        {accepting ? 'Processing…' : 'Accept & Create Order'}
                    </motion.button>
                </div>
            )}

            {isOwnMessage && !accepted && (
                <div className="px-4 pb-3">
                    <p className="text-xs text-brand-600 font-medium text-center">Waiting for buyer to accept</p>
                </div>
            )}
        </motion.div>
    );
};

export default PriceProposalMessage;
