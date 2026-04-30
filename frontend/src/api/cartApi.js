import axiosClient from './axiosClient';

const cartApi = {
    addToCart: async (payload) => {
        // payload: { productId, quantity }
        return await axiosClient.post('buyer/cart/add', payload);
    },

    getCart: async () => {
        return await axiosClient.get('buyer/cart');
    },

    updateCartItem: async (itemId, payload) => {
        return await axiosClient.put(`buyer/cart/item/${itemId}`, payload);
    },

    removeFromCart: async (itemId) => {
        return await axiosClient.delete(`buyer/cart/item/${itemId}`);
    },

    clearCart: async () => {
        return await axiosClient.delete('buyer/cart');
    }
};

export default cartApi;
