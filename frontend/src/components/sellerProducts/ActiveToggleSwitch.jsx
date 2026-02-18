import React, { useState } from 'react';
import { Switch } from '@headlessui/react';
import toast from 'react-hot-toast';
import { Crown } from 'lucide-react';
import useAuthStore from '../../store/authStore';

const ActiveToggleSwitch = ({ isActive, onChange, activeCount }) => {
    const { user } = useAuthStore();
    const isBasicSeller = user?.role === 'SELLER' && user?.subscriptionPlan === 'BASIC';
    const MAX_ACTIVE_PRODUCTS = 5;

    const handleToggle = (checked) => {
        if (checked && isBasicSeller && activeCount >= MAX_ACTIVE_PRODUCTS) {
            toast.error(
                <div className="flex flex-col">
                    <span className="font-bold">Limit Reached</span>
                    <span className="text-xs">Upgrade to activate more products.</span>
                </div>,
                { icon: <Crown size={20} className="text-yellow-500" /> }
            );
            return;
        }
        onChange(checked);
    };

    return (
        <Switch
            checked={isActive}
            onChange={handleToggle}
            className={`${isActive ? 'bg-green-600' : 'bg-slate-300'
                } relative inline-flex h-6 w-11 items-center rounded-full transition-colors focus:outline-none focus:ring-2 focus:ring-green-500 focus:ring-offset-2`}
        >
            <span
                className={`${isActive ? 'translate-x-6' : 'translate-x-1'
                    } inline-block h-4 w-4 transform rounded-full bg-white transition-transform`}
            />
        </Switch>
    );
};

export default ActiveToggleSwitch;
