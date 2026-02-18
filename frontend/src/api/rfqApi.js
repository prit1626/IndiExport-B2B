import axiosClient from './axiosClient';

const rfqApi = {
    // === Seller RFQs ===
    sellerGetRfqs: async (params) => {
        // params: page, size, keyword, category, destinationCountry, minQty, sort
        return await axiosClient.get('/seller/rfq', { params });
    },

    sellerGetRfqById: async (id) => {
        return await axiosClient.get(`/seller/rfq/${id}`);
    },

    sellerSubmitQuote: async (rfqId, payload) => {
        // payload: { priceINRPaise, minQty, leadTimeDays, incoterm, notes }
        return await axiosClient.post(`/seller/rfq/${rfqId}/quote`, payload);
    }
};

export default rfqApi;
