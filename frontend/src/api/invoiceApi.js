import axiosClient from './axiosClient';
import toast from 'react-hot-toast';

const invoiceApi = {
    /**
     * Download Invoice PDF as a Blob
     * @param {string} invoiceId - UUID of the invoice
     * @returns {Promise<Blob>} - PDF Blob
     */
    downloadInvoicePdfBlob: async (invoiceId) => {
        try {
            const response = await axiosClient.get(`/invoices/${invoiceId}/download`, {
                responseType: 'blob',
                headers: {
                    'Accept': 'application/pdf'
                }
            });
            return response.data;
        } catch (error) {
            throw error;
        }
    },

    /**
     * Open Invoice in a new tab
     * @param {string} invoiceId - UUID of the invoice
     * @param {string} [filename] - Optional filename for download attribute
     */
    openInvoiceInNewTab: async (invoiceId, filename = 'invoice.pdf') => {
        const toastId = toast.loading('Fetching invoice...');

        try {
            const blob = await invoiceApi.downloadInvoicePdfBlob(invoiceId);

            // Create a Blob URL
            const url = window.URL.createObjectURL(new Blob([blob], { type: 'application/pdf' }));

            // Open in new tab
            const newTab = window.open(url, '_blank');

            if (!newTab) {
                toast.error('Popup blocked! Please allow popups for this site.', { id: toastId });
                return;
            }

            // Revoke the object URL after a delay to allow the new tab to load
            // 1 minute delay to be safe, though modern browsers might handle it better
            setTimeout(() => {
                window.URL.revokeObjectURL(url);
            }, 60000);

            toast.success('Invoice opened in new tab', { id: toastId });
        } catch (error) {
            console.error('Invoice download failed:', error);

            if (error.response) {
                if (error.response.status === 403) {
                    toast.error('Access denied to this invoice', { id: toastId });
                } else if (error.response.status === 404) {
                    toast.error('Invoice file not found', { id: toastId });
                } else {
                    toast.error('Failed to download invoice', { id: toastId });
                }
            } else {
                toast.error('Network error. Please try again.', { id: toastId });
            }
        }
    },

    /**
     * Get Invoice Download URL (Helper)
     * Note directly returning URL won't work with JWT auth in headers usually,
     * unless the token is in a cookie or query param. 
     * @param {string} invoiceId 
     */
    getInvoiceDownloadUrl: (invoiceId) => {
        return `/api/v1/invoices/${invoiceId}/download`;
    }
};

export default invoiceApi;
