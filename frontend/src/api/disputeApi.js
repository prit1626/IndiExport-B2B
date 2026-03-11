
import axiosClient from './axiosClient';

const disputeApi = {
    // BUYER Operations
    raiseDispute: (formData) => {
        // formData should contain: orderId, reason, description, files (if supported) or evidenceUrls
        // If backend requires multipart/form-data for file upload directly:
        return axiosClient.post('buyer/disputes', formData, {
            headers: {
                'Content-Type': 'multipart/form-data',
            },
        });
    },

    buyerGetDisputes: (params) => {
        // params: { page, size, status, sort }
        return axiosClient.get('buyer/disputes', { params });
    },

    buyerGetDisputeById: (id) => {
        return axiosClient.get(`buyer/disputes/${id}`);
    },

    buyerAddEvidence: (id, formData) => {
        return axiosClient.post(`buyer/disputes/${id}/evidence`, formData, {
            headers: {
                'Content-Type': 'multipart/form-data',
            }
        });
    },

    // SELLER Operations
    sellerGetDisputes: (params) => {
        return axiosClient.get('seller/disputes', { params });
    },

    sellerGetDisputeById: (id) => {
        return axiosClient.get(`seller/disputes/${id}`);
    },

    sellerAddEvidence: (id, formData) => {
        return axiosClient.post(`seller/disputes/${id}/evidence`, formData, {
            headers: {
                'Content-Type': 'multipart/form-data',
            }
        });
    },

    sellerPayRefund: (id) => {
        return axiosClient.put(`seller/disputes/${id}/pay-refund`);
    },

    sellerVerifyRefund: (id, payload) => {
        return axiosClient.post(`seller/disputes/${id}/verify-refund`, payload);
    },

    // ADMIN Operations
    adminGetDisputes: (params) => {
        return axiosClient.get('admin/disputes', { params });
    },

    adminGetDisputeById: (id) => {
        return axiosClient.get(`admin/disputes/${id}`);
    },

    adminResolveDispute: (id, payload) => {
        return axiosClient.put(`admin/disputes/${id}/resolve`, payload);
    },

    // Shared / Utility
    getDisputeReasons: () => {
        // If backend provides an endpoint for reasons, use it. Otherwise, return static list or promise
        // return axiosClient.get('disputes/reasons');
        return Promise.resolve({
            data: [
                'ITEM_NOT_RECEIVED',
                'NOT_AS_DESCRIBED',
                'DAMAGED_ON_ARRIVAL',
                'WRONG_ITEM_SENT',
                'MISSING_PARTS',
                'FAKE_OR_COUNTERFEIT',
                'OTHER'
            ]
        });
    }
};

export default disputeApi;
