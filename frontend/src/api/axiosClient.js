import axios from 'axios';
import useAuthStore from '../store/authStore';

const baseURL = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8081/api/v1';

const axiosClient = axios.create({
    baseURL,
    timeout: 15000,
    headers: {
        'Content-Type': 'application/json',
    },
});

// For multiple parallel requests, we need a queue to hold them while refreshing
let isRefreshing = false;
let failedQueue = [];

const processQueue = (error, token = null) => {
    failedQueue.forEach((prom) => {
        if (error) {
            prom.reject(error);
        } else {
            prom.resolve(token);
        }
    });
    failedQueue = [];
};

// Request Interceptor: Attach Token
axiosClient.interceptors.request.use(
    (config) => {
        // Get token from localStorage directly to avoid circular dependency or stale state
        const token = localStorage.getItem('accessToken');
        if (token) {
            config.headers.Authorization = `Bearer ${token}`;
        }
        return config;
    },
    (error) => Promise.reject(error)
);

// Response Interceptor: Handle 401 & Refresh
axiosClient.interceptors.response.use(
    (response) => response,
    async (error) => {
        const originalRequest = error.config;

        // Prevent infinite loops: if request was already retried, fail immediately
        if (error.response?.status === 401 && !originalRequest._retry) {
            if (isRefreshing) {
                // If already refreshing, queue this request
                return new Promise((resolve, reject) => {
                    failedQueue.push({
                        resolve: (token) => {
                            originalRequest.headers.Authorization = `Bearer ${token}`;
                            resolve(axiosClient(originalRequest));
                        },
                        reject: (err) => {
                            reject(err);
                        },
                    });
                });
            }

            originalRequest._retry = true;
            isRefreshing = true;

            try {
                const refreshToken = localStorage.getItem('refreshToken');

                // Sanity Check: If no refresh token, logout immediately
                if (!refreshToken) {
                    throw new Error('No refresh token available');
                }

                // We use a separate instance for refresh to avoid interceptor loops
                const response = await axios.post(`${baseURL}/auth/refresh`, {
                    refreshToken,
                });

                const { accessToken, refreshToken: newRefreshToken } = response.data;

                // Update tokens
                localStorage.setItem('accessToken', accessToken);
                // If backend rotates refresh token, update it; otherwise keep old
                if (newRefreshToken) {
                    localStorage.setItem('refreshToken', newRefreshToken);
                }

                // Update Store (Sync)
                useAuthStore.getState().setTokens({
                    accessToken,
                    refreshToken: newRefreshToken || refreshToken
                });

                // Process queued requests
                processQueue(null, accessToken);

                // Retry original
                originalRequest.headers.Authorization = `Bearer ${accessToken}`;
                return axiosClient(originalRequest);

            } catch (err) {
                processQueue(err, null);
                // Logout & Redirect
                useAuthStore.getState().logout();
                // Ideally invoke a redirect or window.location - handled by AuthProvider/Router usually
                // But hitting /login is safe
                window.location.href = '/auth/login';
                return Promise.reject(err);
            } finally {
                isRefreshing = false;
            }
        }

        return Promise.reject(error);
    }
);

export default axiosClient;
