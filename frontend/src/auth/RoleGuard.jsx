import { Navigate } from 'react-router-dom';
import useAuthStore from '../store/authStore';

const RoleGuard = ({ children, allowedRoles }) => {
    const { user } = useAuthStore();

    if (user) {
        // Handle both 'role' (from login) and 'roles' (from /me)
        const userRoles = user.roles || (user.role ? [user.role] : []);
        const hasPermission = userRoles.some(role => allowedRoles.includes(role));

        if (!hasPermission) {
            return (
                <div className="flex h-screen items-center justify-center bg-slate-50">
                    <div className="text-center">
                        <h2 className="text-2xl font-bold text-slate-800">Access Denied</h2>
                        <p className="text-slate-600 mt-2">You do not have permission to view this page.</p>
                    </div>
                </div>
            );
        }
    }

    return children;
};

export default RoleGuard;
