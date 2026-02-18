
import React from 'react';

const TermsEditor = ({ value, onChange, disabled }) => {
    return (
        <div className="h-full flex flex-col">
            <div className="bg-slate-100 border-b border-slate-200 px-4 py-2 text-xs font-semibold text-slate-500 uppercase tracking-wider">
                Markdown Editor
            </div>
            <textarea
                value={value}
                onChange={(e) => onChange(e.target.value)}
                disabled={disabled}
                className="flex-1 w-full p-4 font-mono text-sm resize-none focus:outline-none focus:bg-slate-50"
                placeholder="# Enter Terms & Conditions here..."
            />
        </div>
    );
};

export default TermsEditor;
