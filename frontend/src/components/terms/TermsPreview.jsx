
import React from 'react';
import ReactMarkdown from 'react-markdown';

const TermsPreview = ({ markdown }) => {
    return (
        <div className="h-full flex flex-col bg-white">
            <div className="bg-slate-100 border-b border-slate-200 px-4 py-2 text-xs font-semibold text-slate-500 uppercase tracking-wider">
                Live Preview
            </div>
            <div className="flex-1 overflow-auto p-8 prose prose-slate max-w-none">
                {markdown ? (
                    <ReactMarkdown>{markdown}</ReactMarkdown>
                ) : (
                    <p className="text-slate-400 italic">Preview will appear here...</p>
                )}
            </div>
        </div>
    );
};

export default TermsPreview;
