import axiosClient from './axiosClient';
import toast from 'react-hot-toast';

const invoiceApi = {

    /**
     * Stream the invoice PDF directly from backend (no Cloudinary, no CORS, no auth issues).
     * Uses JWT-authenticated request via axiosClient, returns a blob and opens it in a new tab.
     */
    downloadInvoiceByOrder: async (orderId) => {
        const toastId = toast.loading('Generating invoice...');
        try {
            const response = await axiosClient.get(`invoices/order/${orderId}/pdf`, {
                responseType: 'blob',
                headers: { Accept: 'application/pdf' },
            });

            // Create a local blob URL — forces application/pdf MIME type
            const blob = new Blob([response.data], { type: 'application/pdf' });
            const blobUrl = URL.createObjectURL(blob);

            const newTab = window.open(blobUrl, '_blank', 'noopener,noreferrer');
            if (!newTab) {
                // Popup blocked — trigger download instead
                const a = document.createElement('a');
                a.href = blobUrl;
                a.download = `invoice-${orderId}.pdf`;
                document.body.appendChild(a);
                a.click();
                document.body.removeChild(a);
                toast.success('Invoice downloaded', { id: toastId });
            } else {
                toast.success('Invoice opened', { id: toastId });
            }

            // Clean up blob URL after 60 seconds
            setTimeout(() => URL.revokeObjectURL(blobUrl), 60000);

        } catch (error) {
            console.error('Invoice download failed:', error);
            if (error.response?.status === 403) {
                toast.error('Access denied to this invoice', { id: toastId });
            } else if (error.response?.status === 404) {
                toast.error('Invoice not found for this order', { id: toastId });
            } else if (error.response?.status === 400) {
                toast.error('Invoice not yet available. Order must be paid first.', { id: toastId });
            } else {
                toast.error('Failed to generate invoice. Please try again.', { id: toastId });
            }
        }
    },

    // Legacy methods kept for backward compatibility
    getInvoiceByOrderId: async (orderId) => {
        const response = await axiosClient.get(`invoices/order/${orderId}`);
        return response.data || response;
    },

    openInvoiceInNewTab: async (invoiceId) => {
        // Delegates to the order-based download — call downloadInvoiceByOrder when you have orderId
        const toastId = toast.loading('Fetching invoice...');
        try {
            const response = await axiosClient.get(`invoices/${invoiceId}/pdf-stream`, {
                responseType: 'blob',
                headers: { Accept: 'application/pdf' },
            });
            const blob = new Blob([response.data], { type: 'application/pdf' });
            const blobUrl = URL.createObjectURL(blob);
            window.open(blobUrl, '_blank', 'noopener,noreferrer');
            toast.success('Invoice opened', { id: toastId });
            setTimeout(() => URL.revokeObjectURL(blobUrl), 60000);
        } catch (error) {
            toast.error('Failed to fetch invoice.', { id: toastId });
        }
    },

    getInvoiceDownloadUrl: (invoiceId) => `/invoices/${invoiceId}/download`,
};

export default invoiceApi;
