
import axiosClient from './axiosClient';

const adminSellerApi = {
    // List pending sellers
    getPendingSellers: () => {
        return axiosClient.get('/admin/sellers/pending-verification');
    },

    // Get Single Seller KYC
    getSellerKyc: (sellerId) => {
        return axiosClient.get(`/admin/sellers/${sellerId}/kyc`);
    },

    // Verify (Approve)
    verifySeller: (sellerId) => {
        return axiosClient.put(`/admin/sellers/${sellerId}/verify`);
    },

    // Reject
    rejectSeller: (sellerId, reason) => {
        return axiosClient.put(`/admin/sellers/${sellerId}/reject`, { reason });
    }
};

export default adminSellerApi;
