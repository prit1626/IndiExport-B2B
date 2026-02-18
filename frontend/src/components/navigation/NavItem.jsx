import React from 'react';
import { NavLink } from 'react-router-dom';

const NavItem = ({ to, icon: Icon, label, badge, collapsed }) => {
    return (
        <NavLink
            to={to}
            className={({ isActive }) => `
                flex items-center gap-3 px-4 py-3 rounded-xl transition-all duration-200 group
                ${isActive
                    ? 'bg-brand-600 text-white shadow-lg shadow-brand-500/30 font-bold'
                    : 'text-slate-500 dark:text-slate-400 hover:bg-slate-100 dark:hover:bg-slate-800 hover:text-slate-900 dark:hover:text-white'}
                ${collapsed ? 'justify-center' : ''}
            `}
        >
            <Icon size={22} className={`${collapsed ? '' : 'shrink-0'}`} />

            {!collapsed && (
                <>
                    <span className="flex-1 whitespace-nowrap">{label}</span>
                    {badge && (
                        <span className="bg-red-500 text-white text-[10px] font-bold px-1.5 py-0.5 rounded-full min-w-[18px] text-center">
                            {badge}
                        </span>
                    )}
                </>
            )}

            {collapsed && badge && (
                <div className="absolute top-2 right-2 w-2 h-2 bg-red-500 rounded-full border-2 border-white dark:border-slate-900" />
            )}
        </NavLink>
    );
};

export default NavItem;
