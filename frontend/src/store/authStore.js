import { create } from 'zustand';

const useAuthStore = create((set, get) => ({
    user: null,
    accessToken: localStorage.getItem('accessToken'),
    refreshToken: localStorage.getItem('refreshToken'),
    isAuthenticated: false,
    isBootstrapping: true,
    isLoading: false,
    error: null,

    setTokens: ({ accessToken, refreshToken }) => {
        if (accessToken) {
            localStorage.setItem('accessToken', accessToken);
        }
        if (refreshToken) {
            localStorage.setItem('refreshToken', refreshToken);
        }
        set((state) => ({
            accessToken: accessToken || state.accessToken,
            refreshToken: refreshToken || state.refreshToken
        }));
    },

    setUser: (user) => set({ user, isAuthenticated: !!user }),

    // Actions
    login: async (apiLoginFn, credentials) => {
        set({ isLoading: true, error: null });
        try {
            const response = await apiLoginFn(credentials);
            const { accessToken, refreshToken, user } = response.data;

            get().setTokens({ accessToken, refreshToken });
            set({ user, isAuthenticated: true, isLoading: false });
            return true;
        } catch (err) {
            set({
                isLoading: false,
                error: err.response?.data?.message || 'Login failed'
            });
            return false;
        }
    },

    logout: () => {
        localStorage.removeItem('accessToken');
        localStorage.removeItem('refreshToken');
        set({
            user: null,
            accessToken: null,
            refreshToken: null,
            isAuthenticated: false,
            error: null
        });
    },

    // Called on App Mount
    bootstrapAuth: async (getMeFn) => {
        set({ isBootstrapping: true });
        const token = localStorage.getItem('accessToken');

        if (!token) {
            set({ isBootstrapping: false, isAuthenticated: false });
            return;
        }

        try {
            // getMeFn should be the API call to /auth/me
            // If 401, axios interceptor handles refresh. If refresh fails, it throws.
            const response = await getMeFn();
            set({ user: response.data, isAuthenticated: true, isBootstrapping: false });
        } catch (error) {
            // If we reach here, it implies refresh failed or other network error
            // We should treat as not authenticated
            get().logout();
            set({ isBootstrapping: false, isAuthenticated: false });
        }
    }
}));

export default useAuthStore;
