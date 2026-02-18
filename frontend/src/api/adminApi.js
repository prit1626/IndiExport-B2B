
import axiosClient from './axiosClient';

const adminApi = {
    // Admin Settings
    getAdminSettings: () => {
        return axiosClient.get('/admin/settings');
    },
    updateAdminSettings: (data) => {
        return axiosClient.put('/admin/settings', data);
    },

    // Terms & Conditions (Admin)
    adminPublishTerms: (data) => {
        // payload: { markdown: string, versionLabel: string }
        return axiosClient.put('/admin/terms', data);
    },

    // Terms & Conditions (Public/Shared)
    getTerms: () => {
        return axiosClient.get('/terms');
    },
    acceptTerms: (versionId) => {
        return axiosClient.post('/terms/accept', { versionId });
    }
};

export default adminApi;
