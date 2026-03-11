import axiosClient from './axiosClient';

const paymentApi = {
    // Initiate payment (get razorpay order details)
    createPaymentOrder: async (orderId) => {
        return await axiosClient.post('payments/create', { orderId });
    },

    // Verify payment signature
    verifyPayment: async (orderId, payload) => {
        // payload: { razorpayPaymentId, razorpayOrderId, razorpaySignature }
        return await axiosClient.post('payments/verify', { orderId, ...payload });
    },

    // Get payment status (polling fallback)
    getPaymentStatus: async (orderId) => {
        return await axiosClient.get(`payments/status/${orderId}`);
    }
};

export default paymentApi;
