import axiosClient from './axiosClient';

const rfqChatApi = {
    // === Seller Actions ===
    sellerStartRfqChat: async (rfqId) => {
        return await axiosClient.post(`seller/rfq/${rfqId}/chat/start`);
    },

    getSellerRfqChats: async (params) => {
        // params: page, size
        return await axiosClient.get('seller/rfq-chats', { params });
    },

    // === Buyer Actions ===
    getBuyerRfqChats: async (params) => {
        // params: page, size
        return await axiosClient.get('buyer/rfq-chats', { params });
    },

    // === Shared Actions ===
    getRfqChatMessages: async (chatId, params) => {
        // params: page, size
        return await axiosClient.get(`rfq-chat/${chatId}/messages`, { params });
    },

    uploadAttachment: async (chatId, formData) => {
        return await axiosClient.post(`rfq-chat/${chatId}/upload`, formData, {
            headers: {
                'Content-Type': 'multipart/form-data'
            }
        });
    },

    sendRfqChatMessage: async (chatId, payload) => {
        // payload: { messageType, text, attachmentUrl, ... }
        return await axiosClient.post(`rfq-chat/${chatId}/message`, payload);
    },

    sendPriceProposal: async (chatId, payload) => {
        // payload: { proposedPriceMinor, currency, leadTimeDays }
        return await axiosClient.post(`rfq-chat/${chatId}/price-proposal`, payload);
    },

    acceptProposal: async (chatId, messageId) => {
        return await axiosClient.post(`rfq-chat/${chatId}/accept-proposal`, { messageId });
    }
};

export default rfqChatApi;
