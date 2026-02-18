import axiosClient from './axiosClient';

const orderApi = {
    // Get all buyer orders with pagination, sorting & status filter
    // URL: GET /buyer/orders?page=0&size=10&status=&sort=
    getBuyerOrders: async (params) => {
        return await axiosClient.get('/buyer/orders', { params });
    },

    // Get single order details including items, invoice & shipping
    // URL: GET /buyer/orders/{id}
    getOrderById: async (orderId) => {
        return await axiosClient.get(`/buyer/orders/${orderId}`);
    },

    // Get tracking information
    // URL: GET /buyer/orders/{id}/tracking
    getBuyerOrderTracking: async (orderId) => {
        return await axiosClient.get(`/buyer/orders/${orderId}/tracking`);
    },

    // Download Invoice (Used for both proforma and final)
    // Actually the requirement says "Clicking invoice button should open: GET /invoices/{id}/download in a new tab"
    // So we might not need an API function if we use window.open, but adding a helper is good practice if we want to fetch URL
    // For now assuming direct link usage in component, or we can use this to get a pre-signed URL if needed.
    // But implementation plan requirement says "return full working code for orderApi.js", so sticking to core order functions.
};

export default orderApi;
