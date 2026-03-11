import axiosClient from './axiosClient';

const orderApi = {
    // Get all buyer orders with pagination, sorting & status filter
    // URL: GET /buyer/orders?page=0&size=10&status=&sort=
    getBuyerOrders: async (params) => {
        return await axiosClient.get('buyer/orders', { params });
    },

    // Get single order details including items, invoice & shipping
    // URL: GET /buyer/orders/{id}
    getOrderById: async (orderId) => {
        return await axiosClient.get(`buyer/orders/${orderId}`);
    },

    // Get tracking information
    // URL: GET /buyer/orders/{id}/tracking
    getBuyerOrderTracking: async (orderId) => {
        return await axiosClient.get(`buyer/orders/${orderId}/tracking`);
    },
};

export default orderApi;
