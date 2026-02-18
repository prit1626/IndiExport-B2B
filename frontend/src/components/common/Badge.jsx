import React from 'react';

const variants = {
    default: 'bg-slate-100 text-slate-800',
    primary: 'bg-brand-50 text-brand-700',
    success: 'bg-green-50 text-green-700',
    warning: 'bg-amber-50 text-amber-700',
    error: 'bg-red-50 text-red-700',
    outline: 'border border-slate-200 text-slate-600'
};

const sizes = {
    sm: 'text-xs px-2 py-0.5',
    md: 'text-sm px-2.5 py-0.5',
    lg: 'text-base px-3 py-1'
};

const Badge = ({ children, variant = 'default', size = 'sm', className = '' }) => {
    return (
        <span className={`inline-flex items-center font-medium rounded-full ${variants[variant]} ${sizes[size]} ${className}`}>
            {children}
        </span>
    );
};

export default Badge;
