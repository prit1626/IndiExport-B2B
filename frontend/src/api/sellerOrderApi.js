import axiosClient from './axiosClient';

const sellerOrderApi = {
    // Get paginated list of orders
    sellerGetOrders: async (params) => {
        // params: { page, size, status, sort }
        // status options: 'ALL', 'CREATED', 'PAID', 'PROCESSING', 'SHIPPED', 'DELIVERED', 'COMPLETED', 'CANCELLED'
        return await axiosClient.get('sellers/orders', { params });
    },

    // Get single order details
    sellerGetOrderById: async (orderId) => {
        return await axiosClient.get(`sellers/orders/${orderId}`);
    },

    // Update order status
    sellerUpdateOrderStatus: async (orderId, status) => {
        return await axiosClient.put(`sellers/orders/${orderId}/status`, { status });
    },

    // Upload new tracking info
    // URL: /api/v1/seller/orders/{orderId}/tracking
    sellerUploadTracking: async (orderId, data) => {
        return await axiosClient.post(`seller/orders/${orderId}/tracking`, data);
    },

    // Update existing tracking info
    sellerUpdateTracking: async (orderId, data) => {
        return await axiosClient.put(`seller/orders/${orderId}/tracking`, data);
    },

    // Add tracking event
    sellerAddTrackingEvent: async (orderId, data) => {
        // data: { status, location, description, timestamp }
        return await axiosClient.post(`seller/orders/${orderId}/tracking/events`, data);
    },

    // Get tracking details
    sellerGetTracking: async (orderId) => {
        return await axiosClient.get(`seller/orders/${orderId}/tracking`);
    },

    // Dashboard stats (optional usage)
    sellerGetDashboard: async () => {
        return await axiosClient.get('sellers/dashboard');
    }
};

export default sellerOrderApi;
