import axiosClient from './axiosClient';

const cartApi = {
    addToCart: async (payload) => {
        // payload: { productId, quantity }
        return await axiosClient.post('/buyer/cart/add', payload);
    },

    getCart: async () => {
        return await axiosClient.get('/buyer/cart');
    },

    updateCartItem: async (itemId, quantity) => {
        return await axiosClient.put(`/buyer/cart/items/${itemId}`, { quantity });
    },

    removeFromCart: async (itemId) => {
        return await axiosClient.delete(`/buyer/cart/items/${itemId}`);
    },

    clearCart: async () => {
        return await axiosClient.delete('/buyer/cart');
    }
};

export default cartApi;
