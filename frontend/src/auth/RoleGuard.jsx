import React, { useEffect } from 'react';
import { Navigate, useLocation } from 'react-router-dom';
import useAuthStore from '../store/authStore';
import toast from 'react-hot-toast';

const RoleGuard = ({ children, allowedRoles }) => {
    const { user } = useAuthStore();
    const location = useLocation();

    // Determine normalized user roles
    const rawRoles = user?.roles || (user?.role ? user.role.split(',') : []);
    const normalizedUserRoles = rawRoles.map(role =>
        role.toString().toUpperCase().replace('ROLE_', '').trim()
    );

    // Normalize allowed roles for comparison
    const normalizedAllowedRoles = allowedRoles.map(role =>
        role.toString().toUpperCase().replace('ROLE_', '').trim()
    );

    const hasPermission = normalizedUserRoles.some(role =>
        normalizedAllowedRoles.includes(role)
    );

    // Redirect logic
    const defaultRole = normalizedUserRoles[0];
    const targetPath = defaultRole ? `/${defaultRole.toLowerCase()}/dashboard` : '/403';

    useEffect(() => {
        if (user && !hasPermission) {
            if (location.pathname !== targetPath && !location.pathname.startsWith('/auth')) {
                toast.error("You don't have permission to access that area. Redirecting to your dashboard.");
            }
        }
    }, [user, hasPermission, location.pathname, targetPath]);

    if (user && !hasPermission) {
        return <Navigate to={targetPath} replace />;
    }

    return children;
};

export default RoleGuard;
