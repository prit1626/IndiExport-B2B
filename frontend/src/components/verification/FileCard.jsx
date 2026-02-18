
import React from 'react';
import { FileText, Eye, CheckCircle2 } from 'lucide-react';

const FileCard = ({ label, fileName, fileUrl, onPreview, isUploaded }) => {
    return (
        <div className="bg-white border border-slate-200 rounded-lg p-4 hover:shadow-sm transition-shadow">
            <div className="flex items-start justify-between">
                <div className="flex items-center gap-3">
                    <div className={`p-2 rounded-lg ${isUploaded ? 'bg-emerald-100 text-emerald-600' : 'bg-slate-100 text-slate-500'}`}>
                        <FileText size={24} />
                    </div>
                    <div>
                        <p className="text-sm font-medium text-slate-900">{label}</p>
                        {isUploaded ? (
                            <p className="text-xs text-slate-500 truncate max-w-[200px]">{fileName || 'Document available'}</p>
                        ) : (
                            <p className="text-xs text-slate-400 italic">Not uploaded</p>
                        )}
                    </div>
                </div>
                {isUploaded && (
                    <div className="flex items-center gap-2">
                        {onPreview && (
                            <button
                                onClick={() => onPreview(fileUrl)}
                                className="p-1.5 text-slate-500 hover:text-brand-600 hover:bg-slate-50 rounded-md transition-colors"
                                title="Preview Document"
                            >
                                <Eye size={18} />
                            </button>
                        )}
                        <CheckCircle2 size={18} className="text-emerald-500" />
                    </div>
                )}
            </div>
        </div>
    );
};

export default FileCard;
