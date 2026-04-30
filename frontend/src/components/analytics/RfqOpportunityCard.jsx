import React from 'react';
import { Target, FileText, Award } from 'lucide-react';

const RfqOpportunityCard = ({ matching, responded, won }) => {
    const responseRate = matching > 0 ? (responded / matching) * 100 : 0;
    const winRate = responded > 0 ? (won / responded) * 100 : 0;

    return (
        <div className="bg-white rounded-xl shadow-sm border border-slate-200 p-6">
            <h3 className="font-semibold text-slate-800 mb-6 flex items-center gap-2">
                <Target className="w-5 h-5 text-brand-600" />
                RFQ Opportunity Analytics
            </h3>

            <div className="grid grid-cols-1 md:grid-cols-3 gap-6 mb-8">
                <div className="text-center p-4 bg-slate-50 rounded-lg">
                    <div className="text-xs text-slate-500 uppercase font-medium mb-1">Potential Leads</div>
                    <div className="text-2xl font-bold text-slate-900">{matching}</div>
                    <div className="text-[10px] text-slate-400 mt-1">Matching your categories</div>
                </div>
                <div className="text-center p-4 bg-slate-50 rounded-lg">
                    <div className="text-xs text-slate-500 uppercase font-medium mb-1">Response Rate</div>
                    <div className="text-2xl font-bold text-blue-600">{responseRate.toFixed(1)}%</div>
                    <div className="text-[10px] text-slate-400 mt-1">{responded} quotes sent</div>
                </div>
                <div className="text-center p-4 bg-slate-50 rounded-lg">
                    <div className="text-xs text-slate-500 uppercase font-medium mb-1">Win Rate</div>
                    <div className="text-2xl font-bold text-green-600">{winRate.toFixed(1)}%</div>
                    <div className="text-[10px] text-slate-400 mt-1">{won} quotes accepted</div>
                </div>
            </div>

            <div className="space-y-4">
                <div className="flex items-center justify-between text-sm">
                    <span className="text-slate-600 flex items-center gap-2">
                        <FileText className="w-4 h-4" /> Quotes Sent
                    </span>
                    <span className="font-semibold text-slate-900">{responded}</span>
                </div>
                <div className="w-full bg-slate-100 rounded-full h-2">
                    <div 
                        className="bg-blue-500 h-2 rounded-full transition-all duration-1000" 
                        style={{ width: `${Math.min(100, responseRate)}%` }}
                    />
                </div>

                <div className="flex items-center justify-between text-sm pt-2">
                    <span className="text-slate-600 flex items-center gap-2">
                        <Award className="w-4 h-4" /> RFQs Won
                    </span>
                    <span className="font-semibold text-slate-900">{won}</span>
                </div>
                <div className="w-full bg-slate-100 rounded-full h-2">
                    <div 
                        className="bg-green-500 h-2 rounded-full transition-all duration-1000" 
                        style={{ width: `${Math.min(100, winRate)}%` }}
                    />
                </div>
            </div>
        </div>
    );
};

export default RfqOpportunityCard;
