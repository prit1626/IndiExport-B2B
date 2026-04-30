import axiosClient from './axiosClient';

const reviewApi = {
    createReview: (data) => axiosClient.post('reviews', data),
    
    getProductReviews: (productId, params) => 
        axiosClient.get(`reviews/product/${productId}`, { params }),
    
    getProductRatingSummary: (productId) => 
        axiosClient.get(`reviews/product/${productId}/rating`),
    
    // Admin endpoints
    getAllReviews: (params) => axiosClient.get('admin/reviews', { params }),
    
    hideReview: (reviewId, reason) => 
        axiosClient.put(`admin/reviews/${reviewId}/hide`, null, { params: { reason } }),
    
    restoreReview: (reviewId) => 
        axiosClient.put(`admin/reviews/${reviewId}/restore`)
};

export default reviewApi;
