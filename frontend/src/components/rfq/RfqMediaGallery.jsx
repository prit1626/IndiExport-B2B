import React from 'react';

const RfqMediaGallery = ({ media }) => {
    if (!media || media.length === 0) return null;

    return (
        <div className="grid grid-cols-2 md:grid-cols-4 gap-4 mt-4">
            {media.map((item, index) => (
                <div key={index} className="aspect-square bg-slate-100 rounded-lg overflow-hidden border border-slate-200">
                    {item.type === 'IMAGE' ? (
                        <img
                            src={item.url}
                            alt={`Attachment ${index + 1}`}
                            className="w-full h-full object-cover hover:scale-105 transition-transform cursor-pointer"
                            onClick={() => window.open(item.url, '_blank')}
                        />
                    ) : (
                        <div className="w-full h-full flex flex-col items-center justify-center p-2 text-center text-slate-500">
                            <span className="text-xs font-medium uppercase tracking-wide">{item.type}</span>
                            <a href={item.url} target="_blank" rel="noreferrer" className="mt-2 text-xs text-brand-600 hover:underline">
                                View File
                            </a>
                        </div>
                    )}
                </div>
            ))}
        </div>
    );
};

export default RfqMediaGallery;
