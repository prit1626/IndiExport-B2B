import axiosClient from './axiosClient';

const rfqChatApi = {
    // === Seller Actions ===
    sellerStartRfqChat: async (rfqId) => {
        return await axiosClient.post(`/seller/rfq/${rfqId}/chat/start`);
    },

    getSellerRfqChats: async (params) => {
        // params: page, size
        return await axiosClient.get('/seller/rfq-chats', { params });
    },

    // === Buyer Actions ===
    getBuyerRfqChats: async (params) => {
        // params: page, size
        return await axiosClient.get('/buyer/rfq-chats', { params });
    },

    // === Shared Actions ===
    getRfqChatMessages: async (chatId, params) => {
        // params: page, size
        return await axiosClient.get(`/rfq-chat/${chatId}/messages`, { params });
    },

    sendRfqChatMessage: async (chatId, payload) => {
        // payload: { messageType, text, attachmentUrl, ... }
        return await axiosClient.post(`/rfq-chat/${chatId}/message`, payload);
    },

    markRfqChatRead: async (chatId) => {
        // Assuming endpoint exists or we use a generic chat read endpoint
        // If not explicitly provided in prompt, we might skip or use a best-guess if needed for UI state
        // For now, let's assume a standard pattern if needed, or omit if strictly following prompt which didn't specify it.
        // Prompt didn't specify read endpoint, but UI has unread count. 
        // We'll skip implementation to avoid 404s unless confirmed.
        // Actually, previous chatApi had it. Let's add a placeholder or rely on fetching messages to implicitly read if backend supports it.
        // Implementing as best-effort if needed, but for now complying strict to prompt endpoints.
    }
};

export default rfqChatApi;
