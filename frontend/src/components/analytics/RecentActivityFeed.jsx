import React from 'react';
import { Eye, MessageSquare, FileText, Clock } from 'lucide-react';
import { formatDistanceToNow } from 'date-fns';

const RecentActivityFeed = ({ activities }) => {
    const getIcon = (type) => {
        switch (type) {
            case 'PRODUCT_VIEW': return <Eye className="w-4 h-4 text-blue-500" />;
            case 'INQUIRY': return <MessageSquare className="w-4 h-4 text-orange-500" />;
            case 'RFQ_QUOTE': return <FileText className="w-4 h-4 text-green-500" />;
            default: return <Clock className="w-4 h-4 text-slate-400" />;
        }
    };

    return (
        <div className="bg-white rounded-xl shadow-sm border border-slate-200">
            <div className="p-4 border-b border-slate-100">
                <h3 className="font-semibold text-slate-800 flex items-center gap-2">
                    <Clock className="w-4 h-4 text-brand-600" />
                    Recent Buyer Activity
                </h3>
            </div>
            <div className="divide-y divide-slate-100">
                {activities && activities.length > 0 ? (
                    activities.map((activity, idx) => (
                        <div key={idx} className="p-4 flex gap-4 hover:bg-slate-50 transition-colors">
                            <div className="mt-1 p-2 bg-slate-50 rounded-full h-fit">
                                {getIcon(activity.type)}
                            </div>
                            <div className="flex-1">
                                <p className="text-sm font-medium text-slate-700 leading-snug">
                                    {activity.description}
                                </p>
                                <p className="text-xs text-slate-400 mt-1">
                                    {formatTime(activity.timestamp)}
                                </p>
                            </div>
                        </div>
                    ))
                ) : (
                    <div className="p-8 text-center text-slate-400 text-sm">
                        No recent activity found
                    </div>
                )}
            </div>
        </div>
    );
};

// Standard JS Date formatting
const formatTime = (ts) => {
    try {
        const date = new Date(ts);
        const now = new Date();
        const diffInMinutes = Math.floor((now - date) / 1000 / 60);
        
        if (diffInMinutes < 1) return 'Just now';
        if (diffInMinutes < 60) return `${diffInMinutes}m ago`;
        
        const diffInHours = Math.floor(diffInMinutes / 60);
        if (diffInHours < 24) return `${diffInHours}h ago`;
        
        const diffInDays = Math.floor(diffInHours / 24);
        if (diffInDays < 7) return `${diffInDays}d ago`;
        
        return date.toLocaleDateString();
    } catch(e) {
        return 'Recently';
    }
};

export default RecentActivityFeed;
