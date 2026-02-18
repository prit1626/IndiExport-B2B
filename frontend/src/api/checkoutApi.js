import axiosClient from './axiosClient';

const checkoutApi = {
    createCheckout: async (payload) => {
        // payload: { address: { ... }, shippingMode: "AIR" }
        return await axiosClient.post('/buyer/checkout', payload);
    }
};

export default checkoutApi;
