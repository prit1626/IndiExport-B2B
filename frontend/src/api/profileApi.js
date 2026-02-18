import axiosClient from './axiosClient';

const profileApi = {
    // Buyer Profiles
    getBuyerProfile: async () => {
        return await axiosClient.get('/buyer/profile');
    },
    updateBuyerProfile: async (payload) => {
        return await axiosClient.put('/buyer/profile', payload);
    },
    uploadBuyerPhoto: async (formData) => {
        return await axiosClient.post('/buyer/profile/photo', formData, {
            headers: {
                'Content-Type': 'multipart/form-data',
            },
        });
    },

    // Seller Profiles
    getSellerProfile: async () => {
        return await axiosClient.get('/seller/profile');
    },
    updateSellerProfile: async (payload) => {
        return await axiosClient.put('/seller/profile', payload);
    },
    uploadSellerLogo: async (formData) => {
        return await axiosClient.post('/seller/profile/logo', formData, {
            headers: {
                'Content-Type': 'multipart/form-data',
            },
        });
    },

    // Public Seller Profile
    getPublicSellerProfile: async (id) => {
        return await axiosClient.get(`/seller/profile/public/${id}`);
    }
};

export default profileApi;
