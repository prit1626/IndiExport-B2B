import { Navigate, useLocation } from 'react-router-dom';
import useAuthStore from '../store/authStore';
import Loader from '../components/common/Loader';

const ProtectedRoute = ({ children }) => {
    const { isAuthenticated, isLoading } = useAuthStore();
    const location = useLocation();

    if (isLoading) {
        return (
            <div className="flex h-screen w-full items-center justify-center bg-slate-50">
                <Loader className="h-10 w-10 text-brand-600" />
            </div>
        );
    }

    if (!isAuthenticated) {
        return <Navigate to="/auth/login" state={{ from: location }} replace />;
    }

    return children;
};

export default ProtectedRoute;
