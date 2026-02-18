import React, { useState } from 'react';
import { FileText, Loader2, Download } from 'lucide-react';
import { motion } from 'framer-motion';
import invoiceApi from '../../api/invoiceApi';

const InvoiceButton = ({ invoiceId, label, filename }) => {
    const [loading, setLoading] = useState(false);

    const handleClick = async () => {
        if (!invoiceId || loading) return;

        setLoading(true);
        try {
            await invoiceApi.openInvoiceInNewTab(invoiceId, filename);
        } finally {
            setLoading(false);
        }
    };

    if (!invoiceId) {
        return (
            <button
                disabled
                className="flex items-center gap-2 bg-slate-100 text-slate-400 px-4 py-2 rounded-lg border border-slate-200 font-medium cursor-not-allowed"
                title={`${label} not yet generated`}
            >
                <FileText size={18} />
                {label} (Pending)
            </button>
        );
    }

    return (
        <motion.button
            whileHover={{ scale: 1.02 }}
            whileTap={{ scale: 0.98 }}
            onClick={handleClick}
            disabled={loading}
            className="flex items-center gap-2 bg-white text-slate-700 px-4 py-2 rounded-lg border border-slate-300 font-medium hover:bg-slate-50 transition-colors shadow-sm"
        >
            {loading ? (
                <Loader2 size={18} className="animate-spin text-brand-600" />
            ) : (
                <FileText size={18} className="text-brand-600" />
            )}
            {label}
        </motion.button>
    );
};

const InvoiceButtons = ({ order }) => {
    if (!order) return null;

    const invoices = order.invoices || {};

    return (
        <div className="flex flex-wrap gap-2">
            {/* Proforma Invoice */}
            {invoices.proformaInvoiceId && (
                <InvoiceButton
                    invoiceId={invoices.proformaInvoiceId}
                    label="Proforma Invoice"
                    filename={`Proforma-${order.orderNumber}.pdf`}
                />
            )}

            {/* Final Invoice */}
            {invoices.finalInvoiceId && (
                <InvoiceButton
                    invoiceId={invoices.finalInvoiceId}
                    label="Final Invoice"
                    filename={`Invoice-${order.orderNumber}.pdf`}
                />
            )}

            {/* Fallback if no invoices yet */}
            {(!invoices.proformaInvoiceId && !invoices.finalInvoiceId) && (
                <span className="text-xs text-slate-400 italic px-2 py-2">
                    Invoices will appear here
                </span>
            )}
        </div>
    );
};

export default InvoiceButtons;
