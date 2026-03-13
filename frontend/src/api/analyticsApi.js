import axiosClient from './axiosClient';

const analyticsApi = {
    getBuyerAnalytics: async (params) => {
        return await axiosClient.get('analytics/buyer', { params });
    },
    getSellerAnalytics: async (params) => {
        return await axiosClient.get('analytics/seller', { params });
    },
    getSellerAdvancedAnalytics: async (params) => {
        return await axiosClient.get('analytics/seller/advanced', { params });
    },
    getSellerAdvancedMetrics: async (params) => {
        return await axiosClient.get('seller/advanced-analytics', { params });
    },
    getAdminAnalytics: async (params) => {
        return await axiosClient.get('analytics/admin', { params });
    },
    recordView: async (productId, country) => {
        return await axiosClient.post(`analytics/views/${productId}`, null, { params: { country } });
    }
};

export default analyticsApi;
