import React from 'react';
import { Package, MapPin, Clock } from 'lucide-react';
import { formatShortDate } from '../../utils/formatDate';
import RfqMediaGallery from '../rfq/RfqMediaGallery'; // Reusing existing component

const RfqSummaryCard = ({ rfq }) => {
    return (
        <div className="bg-white rounded-xl border border-slate-200 p-6 shadow-sm mb-6">
            <h2 className="text-xl font-bold text-slate-900 mb-4">{rfq.title}</h2>

            <div className="flex flex-wrap gap-4 text-sm text-slate-600 mb-4">
                <span className="flex items-center gap-1.5 bg-slate-50 px-3 py-1.5 rounded-lg border border-slate-100">
                    <Package size={16} className="text-brand-600" />
                    <span className="font-medium text-slate-900">{rfq.qty} {rfq.unit}</span>
                </span>
                <span className="flex items-center gap-1.5 bg-slate-50 px-3 py-1.5 rounded-lg border border-slate-100">
                    <MapPin size={16} className="text-brand-600" />
                    <span>{rfq.destinationCountry} ({rfq.incoterm})</span>
                </span>
                <span className="flex items-center gap-1.5 bg-slate-50 px-3 py-1.5 rounded-lg border border-slate-100">
                    <Clock size={16} className="text-brand-600" />
                    <span>Posted {formatShortDate(rfq.createdAt)}</span>
                </span>
            </div>

            <div className="prose prose-sm prose-slate max-w-none mb-4">
                <p>{rfq.details}</p>
            </div>

            {/* Media Gallery Reuse */}
            <RfqMediaGallery media={rfq.media} />
        </div>
    );
};

export default RfqSummaryCard;
