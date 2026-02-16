import { useContext } from 'react';
// We don't really need a Context since we use Zustand, 
// but the user asked for useAuth.js which usually wraps auth logic.
// However, the AuthProvider we built just renders children. 
// A typical useAuth in this architecture just exports the store values.

import useAuthStore from '../store/authStore';
import { authApi } from '../api/authApi';

const useAuth = () => {
    const user = useAuthStore((state) => state.user);
    const isAuthenticated = useAuthStore((state) => state.isAuthenticated);
    const isLoading = useAuthStore((state) => state.isLoading);
    const error = useAuthStore((state) => state.error);
    const login = useAuthStore((state) => state.login);
    const logout = useAuthStore((state) => state.logout);

    const loginWithApi = (email, password) => login(authApi.login, { email, password });

    return {
        user,
        isAuthenticated,
        isLoading,
        error,
        login: loginWithApi,
        logout,
    };
};

export default useAuth;
