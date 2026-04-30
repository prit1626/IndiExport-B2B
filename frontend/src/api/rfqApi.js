import axiosClient from './axiosClient';

const rfqApi = {

    // ========== BUYER APIS ==========

    /** Create a new RFQ */
    createRfq: (data) => axiosClient.post('buyer/rfq', data),

    /** List buyer's own RFQs (paginated) */
    listMyRfqs: (params) => axiosClient.get('buyer/rfq', { params }),

    /** Get buyer's RFQ detail with all quotes */
    getMyRfq: (rfqId) => axiosClient.get(`buyer/rfq/${rfqId}`),

    /** Cancel an RFQ */
    cancelRfq: (rfqId) => axiosClient.put(`buyer/rfq/${rfqId}/cancel`),

    /** Finalize RFQ by accepting a specific quote */
    finalizeRfq: (rfqId, quoteId) =>
        axiosClient.post(`rfq/${rfqId}/finalize`, { quoteId }),

    // ========== SELLER APIS ==========

    /** Browse all public RFQs (with filters) */
    browseRfqs: (params) => axiosClient.get('seller/rfq', { params }),
    sellerGetRfqs: (params) => axiosClient.get('seller/rfq', { params }),
    sellerGetRecommendedRfqs: (params) => axiosClient.get('seller/rfq/recommended', { params }),

    /** Get a single RFQ detail (for seller view) */
    getRfqForSeller: (rfqId) => axiosClient.get(`seller/rfq/${rfqId}`),
    sellerGetRfqById: (rfqId) => axiosClient.get(`seller/rfq/${rfqId}`),

    /** Submit a new quote for an RFQ */
    submitQuote: (rfqId, data) =>
        axiosClient.post(`seller/rfq/${rfqId}/quote`, data),
    sellerSubmitQuote: (rfqId, data) =>
        axiosClient.post(`seller/rfq/${rfqId}/quote`, data),

    /** Update existing quote */
    updateQuote: (quoteId, data) =>
        axiosClient.put(`seller/quote/${quoteId}`, data),

    /** Withdraw a submitted quote */
    withdrawQuote: (quoteId) =>
        axiosClient.post(`seller/quote/${quoteId}/withdraw`),
};

export default rfqApi;
