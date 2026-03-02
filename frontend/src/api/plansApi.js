import axiosClient from './axiosClient';

const plansApi = {
    getPricing: async () => {
        return await axiosClient.get('/plans/pricing');
    },

    initiateUpgrade: async () => {
        return await axiosClient.post('/plans/upgrade/initiate');
    },

    verifyUpgrade: async (payload) => {
        return await axiosClient.post('/plans/upgrade/verify', payload);
    }
};

export default plansApi;
