import axiosClient from './axiosClient';

const rfqFinalizeApi = {
    buyerGetRfqById: async (rfqId) => {
        return await axiosClient.get(`/buyer/rfq/${rfqId}`);
    },

    buyerGetRfqQuotes: async (rfqId) => {
        return await axiosClient.get(`/buyer/rfq/${rfqId}/quotes`);
    },

    finalizeRfq: async (rfqId, payload) => {
        // payload: { selectedQuoteId }
        // Response: { orderId, invoiceId, status }
        return await axiosClient.post(`/rfq/${rfqId}/finalize`, payload);
    }
};

export default rfqFinalizeApi;
