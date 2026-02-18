import React, { useState } from 'react';
import {
    LayoutDashboard, Package, ShoppingCart, ShoppingBag, MessageSquare,
    ShieldAlert, User, LogOut, ChevronLeft, ChevronRight,
    ShieldCheck, TrendingUp, Settings, FileText, Users, CreditCard
} from 'lucide-react';
import NavItem from './NavItem';
import useAuthStore from '../../store/authStore';

const Sidebar = ({ role }) => {
    const [collapsed, setCollapsed] = useState(false);
    const logout = useAuthStore(state => state.logout);

    const getNavItems = () => {
        switch (role) {
            case 'BUYER':
                return [
                    { to: '/buyer/dashboard', icon: LayoutDashboard, label: 'Dashboard' },
                    { to: '/products', icon: Package, label: 'Browse Products' },
                    { to: '/buyer/cart', icon: ShoppingCart, label: 'My Cart' },
                    { to: '/buyer/orders', icon: ShoppingBag, label: 'My Orders' },
                    { to: '/buyer/inquiries', icon: MessageSquare, label: 'Inquiries' },
                    { to: '/buyer/disputes', icon: ShieldAlert, label: 'Disputes' },
                    { to: '/buyer/profile', icon: User, label: 'My Profile' },
                ];
            case 'SELLER':
                return [
                    { to: '/seller/dashboard', icon: LayoutDashboard, label: 'Dashboard' },
                    { to: '/seller/products', icon: Package, label: 'Products' },
                    { to: '/seller/orders', icon: ShoppingBag, label: 'Orders' },
                    { to: '/seller/inquiries', icon: MessageSquare, label: 'Inquiries' },
                    { to: '/seller/verification', icon: ShieldCheck, label: 'Verification' },
                    { to: '/seller/upgrade', icon: TrendingUp, label: 'Upgrade Plan' },
                    { to: '/seller/disputes', icon: ShieldAlert, label: 'Disputes' },
                    { to: '/seller/profile', icon: User, label: 'Business Profile' },
                ];
            case 'ADMIN':
                return [
                    { to: '/admin/dashboard', icon: LayoutDashboard, label: 'Admin Dashboard' },
                    { to: '/admin/sellers/verification', icon: ShieldCheck, label: 'Seller Verification' },
                    { to: '/admin/users', icon: Users, label: 'Manage Users' },
                    { to: '/admin/products', icon: Package, label: 'Products Audit' },
                    { to: '/admin/disputes', icon: ShieldAlert, label: 'Disputes' },
                    { to: '/admin/settings', icon: Settings, label: 'Platform Settings' },
                    { to: '/admin/terms', icon: FileText, label: 'Manage Terms' },
                ];
            default:
                return [];
        }
    };

    const navItems = getNavItems();

    return (
        <aside
            className={`
                bg-white dark:bg-slate-950 border-r border-slate-200 dark:border-slate-800 
                hidden lg:flex flex-col h-screen sticky top-0 transition-all duration-300 z-40
                ${collapsed ? 'w-20' : 'w-72'}
            `}
        >
            {/* Header */}
            <div className="h-20 px-6 flex items-center justify-between border-b border-slate-50 dark:border-slate-900 overflow-hidden">
                {!collapsed && (
                    <div className="flex items-center gap-2">
                        <div className="w-8 h-8 bg-brand-600 rounded-lg flex items-center justify-center text-white font-black text-sm">
                            IE
                        </div>
                        <span className="text-lg font-black text-slate-900 dark:text-white tracking-tight italic">
                            IndiExport
                        </span>
                    </div>
                )}
                {collapsed && (
                    <div className="w-8 h-8 bg-brand-600 rounded-lg flex items-center justify-center text-white font-black text-sm mx-auto">
                        IE
                    </div>
                )}
            </div>

            {/* Menu Items */}
            <nav className="flex-1 overflow-y-auto py-6 px-4 space-y-2 scrollbar-none">
                {navItems.map((item) => (
                    <NavItem
                        key={item.to}
                        {...item}
                        collapsed={collapsed}
                    />
                ))}
            </nav>

            {/* Footer */}
            <div className="p-4 border-t border-slate-50 dark:border-slate-900 space-y-2">
                <button
                    onClick={() => setCollapsed(!collapsed)}
                    className="w-full flex items-center justify-center gap-3 px-4 py-3 rounded-xl text-slate-500 hover:bg-slate-100 dark:hover:bg-slate-800 transition-all"
                >
                    {collapsed ? <ChevronRight size={20} /> : <><ChevronLeft size={20} /> <span className="text-sm font-bold">Collapse Sidebar</span></>}
                </button>

                <button
                    onClick={logout}
                    className={`
                        w-full flex items-center gap-3 px-4 py-3 rounded-xl text-red-500 hover:bg-red-50 dark:hover:bg-red-900/10 transition-all font-bold
                        ${collapsed ? 'justify-center' : ''}
                    `}
                >
                    <LogOut size={20} />
                    {!collapsed && <span className="text-sm">Sign Out</span>}
                </button>
            </div>
        </aside>
    );
};

export default Sidebar;
