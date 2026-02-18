import React from 'react';
import { Circle, CheckCircle, MapPin, Clock } from 'lucide-react';
import { formatDate } from '../../utils/formatDate';

const TrackingTimeline = ({ events = [] }) => {
    if (!events || events.length === 0) {
        return (
            <div className="text-center py-10 text-slate-500">
                <p>No tracking events available yet.</p>
            </div>
        );
    }

    return (
        <div className="space-y-8 pl-4 border-l-2 border-slate-100 relative">
            {events.map((event, index) => {
                const isLatest = index === 0;

                return (
                    <div key={index} className="relative pl-6">
                        <div
                            className={`absolute -left-[9px] top-1 w-4 h-4 rounded-full border-2 border-white
                            ${isLatest ? 'bg-green-500 ring-4 ring-green-100' : 'bg-slate-300'}
                            `}
                        ></div>

                        <div className="flex flex-col sm:flex-row sm:justify-between sm:items-start gap-1">
                            <div>
                                <h4 className={`font-medium ${isLatest ? 'text-slate-900' : 'text-slate-600'}`}>
                                    {event.label}
                                </h4>
                                {event.location && (
                                    <div className="flex items-center gap-1 text-sm text-slate-500 mt-0.5">
                                        <MapPin size={14} />
                                        <span>{event.location}</span>
                                    </div>
                                )}
                            </div>
                            <div className="flex items-center gap-1 text-xs text-slate-400 mt-1 sm:mt-0">
                                <Clock size={12} />
                                <span>{formatDate(event.time)}</span>
                            </div>
                        </div>
                    </div>
                );
            })}
        </div>
    );
};

export default TrackingTimeline;
