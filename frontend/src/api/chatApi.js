import axiosClient from './axiosClient';

const chatApi = {
    // === Threads ===
    getBuyerInquiryThreads: async () => {
        return await axiosClient.get('/buyer/inquiries');
    },

    getSellerInquiryThreads: async () => {
        return await axiosClient.get('/seller/inquiries');
    },

    // === Messages ===
    getChatMessages: async (chatId, params = { page: 0, size: 50 }) => {
        return await axiosClient.get(`/chat/${chatId}/messages`, { params });
    },

    sendChatMessage: async (chatId, payload) => {
        // payload: { messageType: 'TEXT'|'FILE', text, attachmentUrl, fileName, fileType }
        return await axiosClient.post(`/chat/${chatId}/message`, payload);
    },

    markChatRead: async (chatId) => {
        return await axiosClient.put(`/chat/${chatId}/read`);
    },

    // === Attachments ===
    uploadChatAttachment: async (chatId, file, onUploadProgress) => {
        const formData = new FormData();
        formData.append('file', file);

        return await axiosClient.post(`/chat/${chatId}/upload`, formData, {
            headers: {
                'Content-Type': 'multipart/form-data'
            },
            onUploadProgress
        });
    },

    // === Initiatives ===
    startInquiry: async (productId) => {
        return await axiosClient.post(`/buyer/products/${productId}/inquiry/start`);
    }
};

export default chatApi;
