import axiosClient from './axiosClient';

const productApi = {
    // Public Endpoints
    getProducts: async (params) => {
        return await axiosClient.get('/products', { params });
    },

    getProduct: async (id) => {
        return await axiosClient.get(`/products/${id}`);
    },

    // Seller Endpoints
    sellerCreateProduct: async (payload) => {
        return await axiosClient.post('/sellers/products', payload);
    },

    sellerUpdateProduct: async (id, payload) => {
        return await axiosClient.put(`/sellers/products/${id}`, payload);
    },

    sellerGetProducts: async (params) => {
        // params: page, size, keyword, status
        return await axiosClient.get('/sellers/products', { params });
    },

    sellerGetProductById: async (id) => {
        return await axiosClient.get(`/sellers/products/${id}`);
    },

    sellerDeleteProduct: async (id) => {
        return await axiosClient.delete(`/sellers/products/${id}`);
    },

    sellerUploadProductMedia: async (id, formData) => {
        return await axiosClient.post(`/sellers/products/${id}/media`, formData, {
            headers: {
                'Content-Type': 'multipart/form-data',
            },
        });
    }
};

export default productApi;
