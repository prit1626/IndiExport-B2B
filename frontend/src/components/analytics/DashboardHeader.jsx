import React from 'react';
import { Calendar, RefreshCw } from 'lucide-react';
import { getDateRangePresets } from '../../utils/dateUtils';

const DashboardHeader = ({ title, onRefresh, onDateRangeChange, selectedRange, isRefreshing }) => {
    const presets = getDateRangePresets();

    return (
        <div className="flex flex-col md:flex-row md:items-center justify-between gap-4 mb-8">
            <div>
                <h1 className="text-2xl font-bold text-slate-900">{title}</h1>
                <p className="text-slate-500 text-sm mt-1">Overview of your activity and performance</p>
            </div>

            <div className="flex items-center gap-3 bg-white p-1.5 rounded-xl border border-slate-200 shadow-sm">
                <div className="flex items-center gap-2 px-3 py-1.5 border-r border-slate-100">
                    <Calendar className="w-4 h-4 text-slate-400" />
                    <select
                        className="bg-transparent text-sm font-medium text-slate-700 outline-none cursor-pointer"
                        value={selectedRange}
                        onChange={(e) => onDateRangeChange(e.target.value)}
                    >
                        {presets.map(preset => (
                            <option key={preset.value} value={preset.value}>{preset.label}</option>
                        ))}
                    </select>
                </div>

                <button
                    onClick={onRefresh}
                    disabled={isRefreshing}
                    className={`p-2 rounded-lg hover:bg-slate-50 text-slate-500 transition-all ${isRefreshing ? 'animate-spin text-brand-600' : ''}`}
                    title="Refresh Data"
                >
                    <RefreshCw className="w-4 h-4" />
                </button>
            </div>
        </div>
    );
};

export default DashboardHeader;
