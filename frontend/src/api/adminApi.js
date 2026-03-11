
import axiosClient from './axiosClient';

const adminApi = {
    // Admin Settings
    getAdminSettings: () => {
        return axiosClient.get('admin/settings');
    },
    updateAdminSettings: (data) => {
        return axiosClient.put('admin/settings', data);
    },

    // Terms & Conditions (Admin)
    adminPublishTerms: (data) => {
        // payload: { markdown: string, versionLabel: string }
        return axiosClient.put('admin/terms', data);
    },

    // Terms & Conditions (Public/Shared)
    getTerms: () => {
        return axiosClient.get('terms');
    },
    getTermsHistory: () => {
        // This is shared by getTerms root in our simplified implementation
        return axiosClient.get('terms');
    },
    acceptTerms: (versionId) => {
        return axiosClient.post('terms/accept', { termsVersionId: versionId });
    },

    // User Management (Admin)
    adminGetUsers: (params) => {
        return axiosClient.get('admin/users', { params });
    },
    toggleUserStatus: (userId) => {
        return axiosClient.post(`admin/users/${userId}/toggle-status`);
    },

    // Product Audit (Admin)
    adminGetProducts: (params) => {
        return axiosClient.get('admin/products', { params });
    },
    updateProductStatus: (productId, data) => {
        // data: { status: 'ACTIVE'|'INACTIVE'|'BLOCKED', reason: string }
        return axiosClient.patch(`admin/products/${productId}/status`, data);
    }
};

export default adminApi;
