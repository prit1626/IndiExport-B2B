import React from 'react';
import { Outlet, Link, NavLink, useNavigate } from 'react-router-dom';
import { ShoppingCart, LogIn, UserPlus, Search, Menu, X, ChevronDown, Package, Home } from 'lucide-react';
import useAuthStore from '../store/authStore';

const PublicLayout = () => {
    return (
        <div className="min-h-screen flex flex-col bg-slate-50 dark:bg-slate-950">
            <PublicNavbar />
            <main className="flex-1">
                <Outlet />
            </main>
            <PublicFooter />
        </div>
    );
};

const PublicNavbar = () => {
    const { isAuthenticated, user, logout } = useAuthStore();
    const navigate = useNavigate();
    const [isMenuOpen, setIsMenuOpen] = React.useState(false);

    return (
        <nav className="h-20 bg-white/80 dark:bg-slate-900/80 backdrop-blur-md border-b border-slate-200 dark:border-slate-800 sticky top-0 z-[100]">
            <div className="container mx-auto h-full px-6 flex items-center justify-between">
                {/* Logo */}
                <Link to="/" className="flex items-center gap-2 group">
                    <div className="w-10 h-10 bg-brand-600 rounded-xl flex items-center justify-center text-white font-black text-xl shadow-lg shadow-brand-500/20 group-hover:rotate-6 transition-transform">
                        IE
                    </div>
                    <span className="text-2xl font-black text-slate-900 dark:text-white tracking-tighter italic">
                        IndiExport
                    </span>
                </Link>

                {/* Desktop Menu */}
                <div className="hidden lg:flex items-center gap-8">
                    <NavLink to="/" className={({ isActive }) => `text-sm font-black transition-colors ${isActive ? 'text-brand-600' : 'text-slate-600 dark:text-slate-300 hover:text-brand-600'}`}>
                        Home
                    </NavLink>
                    <NavLink to="/products" className={({ isActive }) => `text-sm font-black transition-colors ${isActive ? 'text-brand-600' : 'text-slate-600 dark:text-slate-300 hover:text-brand-600'}`}>
                        Products
                    </NavLink>
                    <NavLink to="/terms" className={({ isActive }) => `text-sm font-black transition-colors ${isActive ? 'text-brand-600' : 'text-slate-600 dark:text-slate-300 hover:text-brand-600'}`}>
                        Terms
                    </NavLink>
                </div>

                {/* Actions */}
                <div className="hidden lg:flex items-center gap-4">
                    {isAuthenticated ? (
                        <div className="flex items-center gap-4">
                            <button
                                onClick={() => {
                                    const role = user?.role?.replace('ROLE_', '');
                                    navigate(`/${role.toLowerCase()}/dashboard`);
                                }}
                                className="px-6 py-2.5 bg-brand-600 text-white font-black text-sm rounded-xl shadow-lg shadow-brand-500/20 hover:bg-brand-700 transition-all"
                            >
                                Dashboard
                            </button>
                            <button onClick={logout} className="text-slate-400 hover:text-red-500 transition-colors">
                                <LogIn size={20} className="rotate-180" />
                            </button>
                        </div>
                    ) : (
                        <div className="flex items-center gap-3">
                            <Link to="/auth/login" className="px-6 py-2.5 text-slate-700 dark:text-slate-300 font-bold hover:bg-slate-100 dark:hover:bg-slate-800 rounded-xl transition-all">
                                Login
                            </Link>
                            <div className="group relative">
                                <button className="flex items-center gap-2 px-6 py-2.5 bg-slate-900 dark:bg-white text-white dark:text-slate-900 font-black text-sm rounded-xl shadow-xl transition-all hover:scale-105 active:scale-95">
                                    Join Marketplace <ChevronDown size={14} />
                                </button>
                                <div className="absolute right-0 mt-2 w-48 opacity-0 translate-y-2 pointer-events-none group-hover:opacity-100 group-hover:translate-y-0 group-hover:pointer-events-auto transition-all duration-200 z-50">
                                    <div className="bg-white dark:bg-slate-800 border border-slate-200 dark:border-slate-700 rounded-2xl shadow-2xl p-2">
                                        <Link to="/auth/signup/buyer" className="flex items-center gap-2 px-3 py-2 text-sm font-bold text-slate-600 dark:text-slate-300 hover:bg-slate-50 dark:hover:bg-slate-700 rounded-lg">
                                            <ShoppingCart size={16} /> I'm a Buyer
                                        </Link>
                                        <Link to="/auth/signup/seller" className="flex items-center gap-2 px-3 py-2 text-sm font-bold text-slate-600 dark:text-slate-300 hover:bg-slate-50 dark:hover:bg-slate-700 rounded-lg">
                                            <Package size={16} /> I'm a Seller
                                        </Link>
                                    </div>
                                </div>
                            </div>
                        </div>
                    )}
                </div>

                {/* Mobile Toggle */}
                <button
                    onClick={() => setIsMenuOpen(!isMenuOpen)}
                    className="lg:hidden p-2 text-slate-600 dark:text-slate-300 hover:bg-slate-100 dark:hover:bg-slate-800 rounded-xl transition-all"
                >
                    {isMenuOpen ? <X size={24} /> : <Menu size={24} />}
                </button>
            </div>

            {/* Mobile Menu */}
            {isMenuOpen && (
                <div className="lg:hidden absolute top-20 left-0 right-0 bg-white dark:bg-slate-900 border-b border-slate-100 dark:border-slate-800 shadow-2xl z-[90]">
                    <div className="p-6 space-y-4">
                        <Link to="/" onClick={() => setIsMenuOpen(false)} className="block text-lg font-bold text-slate-900 dark:text-white">Home</Link>
                        <Link to="/products" onClick={() => setIsMenuOpen(false)} className="block text-lg font-bold text-slate-900 dark:text-white">Products</Link>
                        <Link to="/terms" onClick={() => setIsMenuOpen(false)} className="block text-lg font-bold text-slate-900 dark:text-white">Terms</Link>
                        <hr className="border-slate-100 dark:border-slate-800" />
                        {!isAuthenticated ? (
                            <div className="space-y-4">
                                <Link to="/auth/login" onClick={() => setIsMenuOpen(false)} className="block text-lg font-bold text-brand-600">Login</Link>
                                <div className="grid grid-cols-2 gap-4">
                                    <Link to="/auth/signup/buyer" onClick={() => setIsMenuOpen(false)} className="px-4 py-3 bg-slate-100 dark:bg-slate-800 rounded-xl text-sm font-black text-center">Buyer</Link>
                                    <Link to="/auth/signup/seller" onClick={() => setIsMenuOpen(false)} className="px-4 py-3 bg-slate-100 dark:bg-slate-800 rounded-xl text-sm font-black text-center">Seller</Link>
                                </div>
                            </div>
                        ) : (
                            <button
                                onClick={() => {
                                    const role = user?.role?.replace('ROLE_', '');
                                    navigate(`/${role.toLowerCase()}/dashboard`);
                                    setIsMenuOpen(false);
                                }}
                                className="w-full py-4 bg-brand-600 text-white font-black rounded-xl"
                            >
                                Go to Dashboard
                            </button>
                        )}
                    </div>
                </div>
            )}
        </nav>
    );
};

const PublicFooter = () => (
    <footer className="bg-white dark:bg-slate-950 border-t border-slate-200 dark:border-slate-800 py-12">
        <div className="container mx-auto px-6 text-center">
            <p className="text-slate-500 dark:text-slate-400 text-sm font-medium">
                &copy; 2026 IndiExport Marketplace. All rights reserved.
            </p>
        </div>
    </footer>
);

export default PublicLayout;
