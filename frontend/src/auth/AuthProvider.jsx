import { useEffect } from 'react';
import useAuthStore from '../store/authStore';
import { authApi } from '../api/authApi';
import LoadingScreen from '../pages/common/LoadingScreen';

const AuthProvider = ({ children }) => {
    const bootstrapAuth = useAuthStore((state) => state.bootstrapAuth);
    const isBootstrapping = useAuthStore((state) => state.isBootstrapping);

    useEffect(() => {
        // Boostrap authentication on mount
        bootstrapAuth(authApi.getMe);
    }, [bootstrapAuth]);

    if (isBootstrapping) {
        return <LoadingScreen />;
    }

    return children;
};

export default AuthProvider;
