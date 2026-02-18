
import axiosClient from './axiosClient';

const sellerVerificationApi = {
    // KYC Status
    getKycStatus: () => {
        return axiosClient.get('/seller/kyc/status');
    },

    // document uploads
    uploadIec: (iecNumber, file) => {
        const formData = new FormData();
        formData.append('iecNumber', iecNumber);
        formData.append('file', file);
        return axiosClient.post('/seller/kyc/upload-iec', formData, {
            headers: { 'Content-Type': 'multipart/form-data' }
        });
    },
    uploadPan: (panNumber, file) => {
        const formData = new FormData();
        formData.append('panNumber', panNumber);
        formData.append('file', file);
        return axiosClient.post('/seller/kyc/upload-pan', formData, {
            headers: { 'Content-Type': 'multipart/form-data' }
        });
    },
    uploadGstin: (gstinNumber, file) => {
        const formData = new FormData();
        if (gstinNumber) formData.append('gstinNumber', gstinNumber);
        formData.append('file', file);
        return axiosClient.post('/seller/kyc/upload-gstin', formData, {
            headers: { 'Content-Type': 'multipart/form-data' }
        });
    },

    // Bank Details (Form Data, no file)
    updateBankDetails: (data) => {
        return axiosClient.post('/seller/kyc/upload-bank-details', data);
    }
};

export default sellerVerificationApi;
