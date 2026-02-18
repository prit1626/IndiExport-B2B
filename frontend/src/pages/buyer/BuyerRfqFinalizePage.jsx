import React, { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import rfqFinalizeApi from '../../api/rfqFinalizeApi';
import toast from 'react-hot-toast';
import { Loader2 } from 'lucide-react';

// Components
import RfqSummaryCard from '../../components/rfqFinalize/RfqSummaryCard';
import QuotesList from '../../components/rfqFinalize/QuotesList';
import FinalizeBottomBar from '../../components/rfqFinalize/FinalizeBottomBar';
import FinalizeConfirmModal from '../../components/rfqFinalize/FinalizeConfirmModal';

const BuyerRfqFinalizePage = () => {
    const { id } = useParams();
    const navigate = useNavigate();

    const [rfq, setRfq] = useState(null);
    const [quotes, setQuotes] = useState([]);
    const [loading, setLoading] = useState(true);
    const [selectedQuoteId, setSelectedQuoteId] = useState(null);
    const [isModalOpen, setIsModalOpen] = useState(false);
    const [finalizing, setFinalizing] = useState(false);

    useEffect(() => {
        const loadData = async () => {
            setLoading(true);
            try {
                const [rfqRes, quotesRes] = await Promise.all([
                    rfqFinalizeApi.buyerGetRfqById(id),
                    rfqFinalizeApi.buyerGetRfqQuotes(id)
                ]);
                setRfq(rfqRes.data);
                setQuotes(quotesRes.data);
            } catch (error) {
                console.error(error);
                toast.error('Failed to load RFQ data');
                // navigate('/buyer/rfq-chats'); // Optional redirect on error
            } finally {
                setLoading(false);
            }
        };
        if (id) loadData();
    }, [id]);

    const handleFinalize = async () => {
        if (!selectedQuoteId) return;
        setFinalizing(true);
        try {
            const { data } = await rfqFinalizeApi.finalizeRfq(id, { selectedQuoteId });
            toast.success('Order created successfully!');
            navigate(`/buyer/orders/${data.orderId}/pay`);
        } catch (error) {
            console.error(error);
            toast.error(error.response?.data?.message || 'Failed to finalize order');
            setIsModalOpen(false); // Close modal to allow retry or re-selection
        } finally {
            setFinalizing(false);
        }
    };

    if (loading) {
        return (
            <div className="min-h-screen flex items-center justify-center bg-slate-50">
                <Loader2 className="animate-spin text-brand-600" size={32} />
            </div>
        );
    }

    if (!rfq) return null;

    const selectedQuote = quotes.find(q => q.id === selectedQuoteId);

    return (
        <div className="min-h-screen bg-slate-50 pb-24">
            <div className="bg-white border-b border-slate-200 py-6 mb-8">
                <div className="container mx-auto px-4 max-w-7xl">
                    <h1 className="text-2xl font-bold text-slate-800">Finalize Order</h1>
                    <p className="text-slate-500">Select a quote to generate your order.</p>
                </div>
            </div>

            <div className="container mx-auto px-4 max-w-7xl">
                <div className="grid grid-cols-1 lg:grid-cols-4 gap-8">
                    {/* Left: RFQ Details */}
                    <div className="lg:col-span-1">
                        <div className="sticky top-24">
                            <h3 className="font-semibold text-slate-800 mb-4 uppercase tracking-wider text-xs">RFQ Summary</h3>
                            <RfqSummaryCard rfq={rfq} />
                        </div>
                    </div>

                    {/* Right: Quotes */}
                    <div className="lg:col-span-3">
                        <h3 className="font-semibold text-slate-800 mb-4 uppercase tracking-wider text-xs">Received Quotes ({quotes.length})</h3>
                        <QuotesList
                            quotes={quotes}
                            selectedQuoteId={selectedQuoteId}
                            onSelect={setSelectedQuoteId}
                        />
                    </div>
                </div>
            </div>

            {/* Sticky Actions */}
            <FinalizeBottomBar
                isVisible={!!selectedQuoteId}
                onFinalizeClick={() => setIsModalOpen(true)}
            />

            {/* Modal */}
            <FinalizeConfirmModal
                isOpen={isModalOpen}
                onClose={() => setIsModalOpen(false)}
                onConfirm={handleFinalize}
                quote={selectedQuote}
                submitting={finalizing}
            />
        </div>
    );
};

export default BuyerRfqFinalizePage;
