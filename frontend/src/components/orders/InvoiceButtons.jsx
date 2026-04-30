import React, { useState } from 'react';
import { FileText, Loader2 } from 'lucide-react';
import { motion } from 'framer-motion';
import invoiceApi from '../../api/invoiceApi';

const InvoiceButtons = ({ order }) => {
    const [loading, setLoading] = useState(false);

    if (!order) return null;

    const handleDownload = async () => {
        setLoading(true);
        try {
            // Uses the new on-demand streaming endpoint — no Cloudinary, no 401
            await invoiceApi.downloadInvoiceByOrder(order.id);
        } finally {
            setLoading(false);
        }
    };

    return (
        <div className="flex flex-wrap gap-2">
            <motion.button
                whileHover={{ scale: 1.02 }}
                whileTap={{ scale: 0.98 }}
                onClick={handleDownload}
                disabled={loading}
                className="flex items-center gap-2 bg-white text-slate-700 px-4 py-2 rounded-lg border border-slate-300 font-medium hover:bg-slate-50 transition-colors shadow-sm"
            >
                {loading ? (
                    <Loader2 size={18} className="animate-spin text-brand-600" />
                ) : (
                    <FileText size={18} className="text-brand-600" />
                )}
                Download Invoice
            </motion.button>
        </div>
    );
};

export default InvoiceButtons;
