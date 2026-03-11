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

    setUser: (user) => {
        if (user) {
            // Normalize role vs roles from different API endpoints (LoginResponse vs MeResponse)
            if (user.roles && !user.role) {
                user.role = user.roles[0];
            } else if (user.role && !user.roles) {
                user.roles = [user.role];
            }
            localStorage.setItem("user", JSON.stringify(user));
        } else {
            localStorage.removeItem("user");
        }
        set({ user, isAuthenticated: !!user });
    },

    // Actions
    login: async (apiLoginFn, credentials) => {
        set({ isLoading: true, error: null });
        try {
            const response = await apiLoginFn(credentials);
            const { accessToken, refreshToken, user } = response.data;

            get().setTokens({ accessToken, refreshToken });
            get().setUser(user);
            set({ isLoading: false });
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
        localStorage.removeItem('user');
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
            const response = await getMeFn();
            get().setUser(response.data);
            set({ isBootstrapping: false });
        } catch (error) {
            get().logout();
            set({ isBootstrapping: false, isAuthenticated: false });
        }
    },

    refreshUser: async (getMeFn) => {
        try {
            const response = await getMeFn();
            get().setUser(response.data);
        } catch (error) {
            console.error("Failed to refresh user:", error);
        }
    }
}));

export default useAuthStore;
