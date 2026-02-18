import React, { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import rfqApi from '../../api/rfqApi';
import RfqDetailsSkeleton from '../../components/rfq/RfqDetailsSkeleton';
import RfqMediaGallery from '../../components/rfq/RfqMediaGallery';
import QuoteModal from '../../components/rfq/QuoteModal';
import { ArrowLeft, Clock, MapPin, Package, User, CheckCircle2, AlertOctagon } from 'lucide-react';
import { formatShortDate } from '../../utils/formatDate';
import toast from 'react-hot-toast';

const SellerRfqDetailsPage = () => {
    const { id } = useParams();
    const navigate = useNavigate();

    const [rfq, setRfq] = useState(null);
    const [loading, setLoading] = useState(true);
    const [isQuoteModalOpen, setIsQuoteModalOpen] = useState(false);
    const [quoted, setQuoted] = useState(false); // Can track if already quoted in session or via backend status if available

    const fetchRfq = async () => {
        try {
            setLoading(true);
            const { data } = await rfqApi.sellerGetRfqById(id);
            setRfq(data);
        } catch (error) {
            console.error(error);
            toast.error('Failed to load RFQ details');
            navigate('/seller/rfq');
        } finally {
            setLoading(false);
        }
    };

    useEffect(() => {
        if (id) fetchRfq();
    }, [id]);

    if (loading) return <RfqDetailsSkeleton />;
    if (!rfq) return null;

    const isOpen = rfq.status === 'OPEN';

    return (
        <div className="min-h-screen bg-slate-50 py-8">
            <div className="container mx-auto px-4 max-w-5xl">

                {/* Back Button */}
                <button
                    onClick={() => navigate('/seller/rfq')}
                    className="flex items-center gap-2 text-slate-500 hover:text-slate-800 mb-6 transition-colors"
                >
                    <ArrowLeft size={20} /> Back to Browse
                </button>

                <div className="grid grid-cols-1 lg:grid-cols-3 gap-8">

                    {/* Left Column: Details */}
                    <div className="lg:col-span-2 space-y-6">
                        <div className="bg-white rounded-xl border border-slate-200 p-6 shadow-sm">
                            <div className="flex justify-between items-start mb-4">
                                <h1 className="text-2xl font-bold text-slate-900">{rfq.title}</h1>
                                <span className={`px-3 py-1 rounded-full text-xs font-bold ${isOpen ? 'bg-green-100 text-green-700' : 'bg-slate-100 text-slate-600'}`}>
                                    {rfq.status}
                                </span>
                            </div>

                            <div className="grid grid-cols-2 gap-4 mb-6">
                                <div className="flex items-center gap-2 text-slate-600">
                                    <Package size={18} className="text-brand-600" />
                                    <span>{rfq.qty} {rfq.unit}</span>
                                </div>
                                <div className="flex items-center gap-2 text-slate-600">
                                    <Clock size={18} className="text-brand-600" />
                                    <span>Posted {formatShortDate(rfq.createdAt)}</span>
                                </div>
                                <div className="flex items-center gap-2 text-slate-600">
                                    <MapPin size={18} className="text-brand-600" />
                                    <span>{rfq.destinationCountry} ({rfq.incoterm})</span>
                                </div>
                                <div className="flex items-center gap-2 text-slate-600">
                                    <User size={18} className="text-brand-600" />
                                    <span>{rfq.buyer?.name}</span>
                                </div>
                            </div>

                            <div className="prose prose-slate max-w-none">
                                <h3 className="text-lg font-semibold text-slate-900 mb-2">Requirements</h3>
                                <p className="text-slate-600 whitespace-pre-wrap">{rfq.details}</p>
                            </div>

                            <RfqMediaGallery media={rfq.media} />
                        </div>
                    </div>

                    {/* Right Column: Actions */}
                    <div className="space-y-6">
                        <div className="bg-white rounded-xl border border-slate-200 p-6 shadow-sm sticky top-24">
                            <h3 className="font-semibold text-slate-900 mb-4">Submit Your Quote</h3>

                            {rfq.targetPriceINRPaise && (
                                <div className="mb-6 p-4 bg-green-50 rounded-lg border border-green-100 text-center">
                                    <p className="text-xs text-green-600 uppercase font-bold tracking-wider mb-1">Target Price</p>
                                    <p className="text-2xl font-bold text-green-700">₹{(rfq.targetPriceINRPaise / 100).toLocaleString()}</p>
                                    <p className="text-xs text-green-600 mt-1">per {rfq.unit}</p>
                                </div>
                            )}

                            {isOpen ? (
                                <button
                                    onClick={() => setIsQuoteModalOpen(true)}
                                    className="w-full bg-brand-600 hover:bg-brand-700 text-white font-semibold py-3 rounded-lg shadow-md hover:shadow-lg transition-all active:scale-95"
                                >
                                    Submit Quote Now
                                </button>
                            ) : (
                                <div className="w-full bg-slate-100 text-slate-500 font-medium py-3 rounded-lg flex items-center justify-center gap-2 cursor-not-allowed">
                                    <AlertOctagon size={18} />
                                    RFQ Closed
                                </div>
                            )}

                            <p className="text-xs text-slate-500 text-center mt-4">
                                Submitting a quote starts a negotiation thread.
                            </p>
                        </div>
                    </div>

                </div>
            </div>

            <QuoteModal
                isOpen={isQuoteModalOpen}
                onClose={() => setIsQuoteModalOpen(false)}
                rfq={rfq}
                onSuccess={() => {
                    setQuoted(true);
                    fetchRfq(); // refresh
                }}
            />
        </div>
    );
};

export default SellerRfqDetailsPage;
