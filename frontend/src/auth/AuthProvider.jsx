import { useEffect } from 'react';
import useAuthStore from '../store/authStore';
import { authApi } from '../api/authApi';
import Loader from '../components/common/Loader'; // Assuming Loader exists/will exist

const AuthProvider = ({ children }) => {
    const bootstrapAuth = useAuthStore((state) => state.bootstrapAuth);
    const isBootstrapping = useAuthStore((state) => state.isBootstrapping);

    useEffect(() => {
        // Pass the actual API function to avoid circular imports in store
        bootstrapAuth(authApi.getMe);
    }, [bootstrapAuth]);

    if (isBootstrapping) {
        return (
            <div className="flex h-screen w-full items-center justify-center bg-slate-50">
                <Loader className="h-10 w-10 text-brand-600" />
                <span className="ml-3 text-slate-600">Initializing...</span>
            </div>
        );
    }

    return children;
};

export default AuthProvider;
