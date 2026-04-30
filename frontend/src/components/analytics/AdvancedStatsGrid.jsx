import React from 'react';
import { Eye, MessageSquare, TrendingUp, Calendar } from 'lucide-react';
import StatCard from './StatCard';

const AdvancedStatsGrid = ({ viewStats, inquiryStats }) => {
    return (
        <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
            <div className="space-y-4">
                <h3 className="text-sm font-semibold text-slate-500 uppercase tracking-wider flex items-center gap-2">
                    <Eye className="w-4 h-4" /> Product Views
                </h3>
                <div className="grid grid-cols-2 gap-4">
                    <StatCard 
                        title="Today" 
                        value={viewStats?.today || 0} 
                        icon={Calendar} 
                        color="blue" 
                        compact 
                    />
                    <StatCard 
                        title="This Week" 
                        value={viewStats?.thisWeek || 0} 
                        icon={TrendingUp} 
                        color="green" 
                        compact 
                    />
                    <StatCard 
                        title="This Month" 
                        value={viewStats?.thisMonth || 0} 
                        icon={Calendar} 
                        color="purple" 
                        compact 
                    />
                    <StatCard 
                        title="Total Lifetime" 
                        value={viewStats?.total || 0} 
                        icon={Eye} 
                        color="slate" 
                        compact 
                    />
                </div>
            </div>

            <div className="space-y-4">
                <h3 className="text-sm font-semibold text-slate-500 uppercase tracking-wider flex items-center gap-2">
                    <MessageSquare className="w-4 h-4" /> Inquiries
                </h3>
                <div className="grid grid-cols-2 gap-4">
                    <StatCard 
                        title="Today" 
                        value={inquiryStats?.today || 0} 
                        icon={Calendar} 
                        color="orange" 
                        compact 
                    />
                    <StatCard 
                        title="This Week" 
                        value={inquiryStats?.thisWeek || 0} 
                        icon={TrendingUp} 
                        color="red" 
                        compact 
                    />
                    <StatCard 
                        title="This Month" 
                        value={inquiryStats?.thisMonth || 0} 
                        icon={Calendar} 
                        color="pink" 
                        compact 
                    />
                    <StatCard 
                        title="Total Lifetime" 
                        value={inquiryStats?.total || 0} 
                        icon={MessageSquare} 
                        color="slate" 
                        compact 
                    />
                </div>
            </div>
        </div>
    );
}

export default AdvancedStatsGrid;
