
import axiosClient from './axiosClient';

const disputeApi = {
    // BUYER Operations
    raiseDispute: (formData) => {
        // formData should contain: orderId, reason, description, files (if supported) or evidenceUrls
        // If backend requires multipart/form-data for file upload directly:
        return axiosClient.post('/disputes', formData, {
            headers: {
                'Content-Type': 'multipart/form-data',
            },
        });
    },

    buyerGetDisputes: (params) => {
        // params: { page, size, status, sort }
        return axiosClient.get('/buyer/disputes', { params });
    },

    buyerGetDisputeById: (id) => {
        return axiosClient.get(`/buyer/disputes/${id}`);
    },

    // SELLER Operations
    sellerGetDisputes: (params) => {
        return axiosClient.get('/seller/disputes', { params });
    },

    sellerGetDisputeById: (id) => {
        return axiosClient.get(`/seller/disputes/${id}`);
    },

    // ADMIN Operations
    adminGetDisputes: (params) => {
        return axiosClient.get('/admin/disputes', { params });
    },

    adminGetDisputeById: (id) => {
        return axiosClient.get(`/admin/disputes/${id}`);
    },

    adminResolveDispute: (id, payload) => {
        // payload: { action, amountINRPaise, notes }
        return axiosClient.put(`/admin/disputes/${id}/resolve`, payload);
    },

    // Shared / Utility
    getDisputeReasons: () => {
        // If backend provides an endpoint for reasons, use it. Otherwise, return static list or promise
        // return axiosClient.get('/disputes/reasons');
        return Promise.resolve({
            data: [
                'DAMAGED_GOODS',
                'INCORRECT_ITEM',
                'DELAYED_DELIVERY',
                'PAYMENT_CONFLICT',
                'OTHER'
            ]
        });
    }
};

export default disputeApi;
