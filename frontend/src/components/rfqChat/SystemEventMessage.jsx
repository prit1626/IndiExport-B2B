import React from 'react';

const SystemEventMessage = ({ message }) => {
    return (
        <div className="flex justify-center my-4">
            <span className="bg-slate-100 text-slate-500 text-xs px-3 py-1.5 rounded-full shadow-sm border border-slate-200">
                {message.payload?.eventText || 'System Event'}
            </span>
        </div>
    );
};

export default SystemEventMessage;
