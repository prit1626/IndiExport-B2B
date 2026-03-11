import axiosClient from './axiosClient';

const productSearchApi = {
    searchProducts: async (params) => {
        // params: keyword, category, country, minPrice, maxPrice, sortBy, page, size
        return await axiosClient.get('/products/search', { params });
    },
    getSuggestions: async (keyword) => {
        return await axiosClient.get('/products/suggestions', { params: { keyword } });
    }
};

export default productSearchApi;
