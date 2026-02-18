import React from 'react';
import { Bell, Search, User, LogOut, Settings, HelpCircle, ChevronDown } from 'lucide-react';
import useAuthStore from '../../store/authStore';

const Topbar = ({ onMenuClick }) => {
    const { user, logout } = useAuthStore();

    return (
        <header className="h-20 bg-white/80 dark:bg-slate-950/80 backdrop-blur-md border-b border-slate-200 dark:border-slate-800 sticky top-0 z-30 px-6 flex items-center justify-between">
            <div className="flex items-center gap-4">
                <button
                    onClick={onMenuClick}
                    className="lg:hidden p-2 text-slate-500 hover:bg-slate-100 dark:hover:bg-slate-800 rounded-lg"
                >
                    <Search size={20} />
                </button>

                <div className="hidden sm:flex items-center gap-2 bg-slate-100 dark:bg-slate-900 px-4 py-2 rounded-xl text-slate-400 border border-transparent focus-within:border-brand-500/30 focus-within:bg-white dark:focus-within:bg-slate-950 transition-all w-64 md:w-96">
                    <Search size={18} />
                    <input
                        type="text"
                        placeholder="Search anything..."
                        className="bg-transparent border-none focus:ring-0 text-sm text-slate-900 dark:text-white w-full"
                    />
                </div>
            </div>

            <div className="flex items-center gap-2 md:gap-4">
                <button className="p-2.5 text-slate-500 hover:bg-slate-100 dark:hover:bg-slate-800 rounded-xl relative transition-all">
                    <Bell size={20} />
                    <span className="absolute top-2.5 right-2.5 w-2 h-2 bg-red-500 rounded-full border-2 border-white dark:border-slate-800"></span>
                </button>

                <div className="h-8 w-[1px] bg-slate-200 dark:bg-slate-800 mx-1"></div>

                <div className="group relative">
                    <button className="flex items-center gap-3 p-1.5 hover:bg-slate-100 dark:hover:bg-slate-800 rounded-xl transition-all">
                        <div className="w-9 h-9 bg-brand-100 dark:bg-brand-900/30 text-brand-600 dark:text-brand-400 rounded-lg flex items-center justify-center font-bold text-sm">
                            {user?.fullName?.charAt(0) || 'U'}
                        </div>
                        <div className="hidden md:block text-left">
                            <p className="text-xs font-black text-slate-900 dark:text-white leading-none">
                                {user?.fullName || 'User'}
                            </p>
                            <p className="text-[10px] font-bold text-slate-400 mt-1 uppercase tracking-wider">
                                {user?.role?.replace('ROLE_', '')}
                            </p>
                        </div>
                        <ChevronDown size={16} className="text-slate-400 group-hover:rotate-180 transition-transform duration-300" />
                    </button>

                    {/* Dropdown Menu */}
                    <div className="absolute right-0 mt-2 w-56 opacity-0 translate-y-2 pointer-events-none group-hover:opacity-100 group-hover:translate-y-0 group-hover:pointer-events-auto transition-all duration-200 z-50">
                        <div className="bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 rounded-2xl shadow-2xl p-2 overflow-hidden">
                            <button className="w-full flex items-center gap-3 px-3 py-2.5 text-sm font-medium text-slate-600 dark:text-slate-400 hover:bg-slate-50 dark:hover:bg-slate-800 hover:text-slate-900 dark:hover:text-white rounded-xl transition-all">
                                <User size={18} />
                                Profile Settings
                            </button>
                            <button className="w-full flex items-center gap-3 px-3 py-2.5 text-sm font-medium text-slate-600 dark:text-slate-400 hover:bg-slate-50 dark:hover:bg-slate-800 hover:text-slate-900 dark:hover:text-white rounded-xl transition-all">
                                <Settings size={18} />
                                Preferences
                            </button>
                            <button className="w-full flex items-center gap-3 px-3 py-2.5 text-sm font-medium text-slate-600 dark:text-slate-400 hover:bg-slate-50 dark:hover:bg-slate-800 hover:text-slate-900 dark:hover:text-white rounded-xl transition-all border-b border-slate-100 dark:border-slate-800 mb-1">
                                <HelpCircle size={18} />
                                Help Center
                            </button>
                            <button
                                onClick={logout}
                                className="w-full flex items-center gap-3 px-3 py-2.5 text-sm font-bold text-red-600 hover:bg-red-50 dark:hover:bg-red-900/10 rounded-xl transition-all"
                            >
                                <LogOut size={18} />
                                Sign Out
                            </button>
                        </div>
                    </div>
                </div>
            </div>
        </header>
    );
};

export default Topbar;
