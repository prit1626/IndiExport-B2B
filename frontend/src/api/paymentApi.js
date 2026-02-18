import axiosClient from './axiosClient';

const paymentApi = {
    // Initiate payment (get razorpay order details)
    createPaymentOrder: async (orderId) => {
        return await axiosClient.post(`/buyer/orders/${orderId}/pay`, { provider: 'RAZORPAY' });
    },

    // Verify payment signature
    verifyPayment: async (orderId, payload) => {
        // payload: { razorpayPaymentId, razorpayOrderId, razorpaySignature }
        return await axiosClient.post(`/buyer/orders/${orderId}/verify`, payload);
    },

    // Get payment status (polling fallback)
    getPaymentStatus: async (orderId) => {
        return await axiosClient.get(`/buyer/orders/${orderId}/payment-status`);
    }
};

export default paymentApi;
