import React from 'react';
import { Truck } from 'lucide-react'; // anchor used as substitute for ship/sea if needed, or stick to Truck

const modes = [
    { value: 'SEA', label: 'Sea Freight' },
    { value: 'AIR', label: 'Air Freight' },
    { value: 'ROAD', label: 'Road Transport' },
    { value: 'COURIER', label: 'Express Courier' }
];

const ShippingModeSelect = ({ value, onChange, disabled }) => {
    return (
        <div className="relative">
            <select
                value={value}
                onChange={(e) => onChange(e.target.value)}
                disabled={disabled}
                className="w-full pl-3 pr-8 py-2 bg-slate-50 border border-slate-200 rounded-lg text-sm font-medium text-slate-700 outline-none focus:ring-2 focus:ring-brand-500 disabled:opacity-60 cursor-pointer appearance-none"
            >
                {modes.map((mode) => (
                    <option key={mode.value} value={mode.value}>
                        {mode.label}
                    </option>
                ))}
            </select>
            {/* Custom arrow or icon could go here if appearance-none is used completely */}
        </div>
    );
};

export default ShippingModeSelect;
