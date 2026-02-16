import axiosClient from './axiosClient';

export const authApi = {
    login: (credentials) => axiosClient.post('/auth/login', credentials),
    signupBuyer: (data) => axiosClient.post('/auth/signup/buyer', data),
    signupSeller: (data) => axiosClient.post('/auth/signup/seller', data),

    // Refresh handled via axios interceptor mostly, but exposed if needed manually
    refreshToken: (token) => axiosClient.post('/auth/refresh', { refreshToken: token }),

    logout: (refreshToken) => axiosClient.post('/auth/logout', { refreshToken }),

    getMe: () => axiosClient.get('/auth/me'),
};
