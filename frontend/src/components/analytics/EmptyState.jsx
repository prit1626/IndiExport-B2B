import React from 'react';
import { BarChart2 } from 'lucide-react';

const EmptyState = ({ message }) => {
    return (
        <div className="flex flex-col items-center justify-center h-full min-h-[200px] text-center p-6">
            <div className="w-10 h-10 bg-slate-50 text-slate-400 rounded-full flex items-center justify-center mb-3">
                <BarChart2 className="w-5 h-5" />
            </div>
            <p className="text-slate-500 font-medium">{message || "No data available for this period"}</p>
        </div>
    );
};

export default EmptyState;
