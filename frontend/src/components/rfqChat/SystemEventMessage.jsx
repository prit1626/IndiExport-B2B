import React from 'react';
import { Info } from 'lucide-react';

/**
 * Rendered for messageType === 'SYSTEM'.
 * Shows a centered pill with the system event text.
 */
const SystemEventMessage = ({ message }) => {
    const text = message.messageText || 'System event';
    return (
        <div className="flex justify-center my-3 px-4">
            <span className="inline-flex items-center gap-1.5 bg-slate-100 text-slate-500 text-[11px] px-3 py-1.5 rounded-full border border-slate-200 shadow-xs max-w-md text-center leading-relaxed">
                <Info size={11} className="shrink-0 text-slate-400" />
                {text}
            </span>
        </div>
    );
};

export default SystemEventMessage;
