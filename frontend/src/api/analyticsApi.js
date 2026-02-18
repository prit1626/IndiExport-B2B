import axiosClient from './axiosClient';

const analyticsApi = {
    getBuyerAnalytics: async (params) => {
        return await axiosClient.get('/analytics/buyer', { params });
    },
    getSellerAnalytics: async (params) => {
        return await axiosClient.get('/analytics/seller', { params });
    },
    getSellerAdvancedAnalytics: async (params) => {
        return await axiosClient.get('/analytics/seller/advanced', { params });
    },
    getAdminAnalytics: async (params) => {
        return await axiosClient.get('/analytics/admin', { params });
    }
};

export default analyticsApi;
