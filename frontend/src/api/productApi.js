import axiosClient from './axiosClient';

const productApi = {
    getProducts: async (params) => {
        // Remove empty/null params to keep URL clean
        const cleanParams = Object.fromEntries(
            Object.entries(params).filter(([_, v]) => v != null && v !== '')
        );
        return await axiosClient.get('/products', { params: cleanParams });
    },

    getProductById: async (id) => {
        return await axiosClient.get(`/products/${id}`);
    }
};

export default productApi;
