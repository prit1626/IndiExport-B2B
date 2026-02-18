import React from 'react';
import { motion, AnimatePresence } from 'framer-motion';
import { X, LogOut, Package } from 'lucide-react';
import NavItem from './NavItem';
import useAuthStore from '../../store/authStore';
import {
    LayoutDashboard, ShoppingCart, ShoppingBag, MessageSquare,
    ShieldAlert, User, ShieldCheck, TrendingUp, Settings, FileText, Users
} from 'lucide-react';

const MobileDrawer = ({ isOpen, onClose, role }) => {
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
        <AnimatePresence>
            {isOpen && (
                <>
                    {/* Backdrop */}
                    <motion.div
                        initial={{ opacity: 0 }}
                        animate={{ opacity: 1 }}
                        exit={{ opacity: 0 }}
                        onClick={onClose}
                        className="fixed inset-0 bg-slate-900/60 backdrop-blur-sm z-[100] lg:hidden"
                    />

                    {/* Drawer Content */}
                    <motion.div
                        initial={{ x: '-100%' }}
                        animate={{ x: 0 }}
                        exit={{ x: '-100%' }}
                        transition={{ type: 'spring', damping: 25, stiffness: 200 }}
                        className="fixed top-0 left-0 bottom-0 w-[280px] bg-white dark:bg-slate-950 z-[110] lg:hidden flex flex-col shadow-2xl"
                    >
                        <div className="h-20 px-6 flex items-center justify-between border-b border-slate-100 dark:border-slate-800">
                            <div className="flex items-center gap-2">
                                <div className="w-8 h-8 bg-brand-600 rounded-lg flex items-center justify-center text-white font-black text-sm">IE</div>
                                <span className="text-lg font-black text-slate-900 dark:text-white italic">IndiExport</span>
                            </div>
                            <button onClick={onClose} className="p-2 text-slate-500 hover:bg-slate-100 dark:hover:bg-slate-800 rounded-lg">
                                <X size={20} />
                            </button>
                        </div>

                        <nav className="flex-1 overflow-y-auto pt-6 px-4 space-y-2 scrollbar-none">
                            {navItems.map((item) => (
                                <NavItem
                                    key={item.to}
                                    {...item}
                                    onClose={onClose} // Optional: close drawer on click
                                />
                            ))}
                        </nav>

                        <div className="p-4 border-t border-slate-100 dark:border-slate-800">
                            <button
                                onClick={() => {
                                    logout();
                                    onClose();
                                }}
                                className="w-full flex items-center gap-3 px-4 py-3 rounded-xl text-red-500 hover:bg-red-50 dark:hover:bg-red-900/10 transition-all font-bold"
                            >
                                <LogOut size={20} />
                                <span className="text-sm">Sign Out</span>
                            </button>
                        </div>
                    </motion.div>
                </>
            )}
        </AnimatePresence>
    );
};

export default MobileDrawer;
