import React from 'react';
import { Plus, Minus } from 'lucide-react';

const QtyStepper = ({ value, min = 1, max = 9999, onChange, disabled = false }) => {
    const handleDecrement = () => {
        if (value > min) onChange(value - 1);
    };

    const handleIncrement = () => {
        if (value < max) onChange(value + 1);
    };

    return (
        <div className="flex items-center border border-slate-200 rounded-lg bg-white overflow-hidden w-fit">
            <button
                type="button"
                onClick={handleDecrement}
                disabled={disabled || value <= min}
                className="p-2 hover:bg-slate-50 disabled:opacity-50 disabled:cursor-not-allowed transition-colors text-slate-600"
            >
                <Minus size={16} />
            </button>
            <div className="w-12 text-center font-medium text-slate-900 text-sm">
                {value}
            </div>
            <button
                type="button"
                onClick={handleIncrement}
                disabled={disabled || value >= max}
                className="p-2 hover:bg-slate-50 disabled:opacity-50 disabled:cursor-not-allowed transition-colors text-slate-600"
            >
                <Plus size={16} />
            </button>
        </div>
    );
};

export default QtyStepper;
