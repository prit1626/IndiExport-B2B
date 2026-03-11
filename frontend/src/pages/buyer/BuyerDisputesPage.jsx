
import React, { useState, useEffect } from 'react';
import { Plus, Search, Filter, Loader2, AlertCircle } from 'lucide-react';
import { toast } from 'react-hot-toast';
import { useSearchParams, useNavigate } from 'react-router-dom';
import disputeApi from '../../api/disputeApi';
import DisputeCard from '../../components/disputes/DisputeCard';
import DisputeDetailsModal from '../../components/disputes/DisputeDetailsModal';
import RaiseDisputeModal from '../../components/disputes/RaiseDisputeModal';
import Pagination from '../../components/common/Pagination';

const BuyerDisputesPage = () => {
    const [searchParams, setSearchParams] = useSearchParams();
    const navigate = useNavigate();
    const [disputes, setDisputes] = useState([]);
    const [loading, setLoading] = useState(true);
    const [selectedDispute, setSelectedDispute] = useState(null);
    const [isRaiseModalOpen, setIsRaiseModalOpen] = useState(false);
    const [totalPages, setTotalPages] = useState(0);

    const currentPage = parseInt(searchParams.get('page') || '0');
    const filterStatus = searchParams.get('status') || 'ALL';

    useEffect(() => {
        fetchDisputes();
    }, [currentPage, filterStatus]);

    const fetchDisputes = async () => {
        setLoading(true);
        try {
            const params = {
                page: currentPage,
                size: 20,
                status: filterStatus !== 'ALL' ? filterStatus : undefined
            };
            const { data } = await disputeApi.buyerGetDisputes(params);
            setDisputes(data.content || []);
            setTotalPages(data.totalPages || 0);
        } catch (error) {
            console.error(error);
            toast.error('Failed to load disputes');
        } finally {
            setLoading(false);
        }
    };

    const handlePageChange = (newPage) => {
        setSearchParams({ page: newPage, status: filterStatus });
        window.scrollTo(0, 0);
    };

    const handleStatusChange = (newStatus) => {
        setSearchParams({ page: 0, status: newStatus });
    };

    const handleViewDetails = async (dispute) => {
        try {
            const { data } = await disputeApi.buyerGetDisputeById(dispute.id);
            setSelectedDispute(data.data || data);
        } catch (error) {
            toast.error('Failed to load dispute details');
        }
    };

    return (
        <div className="min-h-screen bg-slate-50 pb-20">
            {/* Header */}
            <div className="bg-white border-b border-slate-200 py-6">
                <div className="container mx-auto px-4 max-w-5xl flex flex-col md:flex-row md:items-center justify-between gap-4">
                    <div>
                        <h1 className="text-2xl font-bold text-slate-900">My Disputes</h1>
                        <p className="text-slate-500 text-sm mt-1">Manage and track your order issues</p>
                    </div>
                    <button
                        onClick={() => navigate('/buyer/orders')}
                        className="bg-red-600 hover:bg-red-700 text-white px-5 py-2.5 rounded-lg flex items-center gap-2 font-bold shadow-sm transition-all active:scale-95"
                    >
                        <AlertCircle size={20} /> Raise New Dispute
                    </button>
                </div>
            </div>

            <div className="container mx-auto px-4 max-w-5xl py-8">
                {/* Filters */}
                <div className="flex flex-wrap gap-2 mb-6">
                    {['ALL', 'OPEN', 'UNDER_REVIEW', 'RESOLVED', 'REJECTED'].map(status => (
                        <button
                            key={status}
                            onClick={() => handleStatusChange(status)}
                            className={`px-4 py-2 rounded-full text-sm font-medium transition-colors border ${filterStatus === status
                                ? 'bg-slate-800 text-white border-slate-800'
                                : 'bg-white text-slate-600 border-slate-200 hover:bg-slate-50'
                                }`}
                        >
                            {status.replace('_', ' ')}
                        </button>
                    ))}
                </div>

                {/* List */}
                {loading ? (
                    <div className="flex justify-center py-20">
                        <Loader2 className="animate-spin text-brand-600" size={40} />
                    </div>
                ) : disputes.length === 0 ? (
                    <div className="text-center py-20 bg-white rounded-xl border border-dashed border-slate-300">
                        <div className="bg-slate-50 w-16 h-16 rounded-full flex items-center justify-center mx-auto mb-4">
                            <AlertCircle className="text-slate-400" size={32} />
                        </div>
                        <h3 className="text-lg font-medium text-slate-900 mb-1">No disputes found</h3>
                        <p className="text-slate-500 mb-6 max-w-sm mx-auto">
                            You haven't raised any disputes yet. If you have an issue with an order, you can raise one here.
                        </p>
                        <button
                            onClick={() => navigate('/buyer/orders')}
                            className="text-brand-600 font-bold hover:underline"
                        >
                            Raise a Dispute via Orders Page
                        </button>
                    </div>
                ) : (
                    <>
                        <div className="grid gap-4">
                            {disputes.map(dispute => (
                                <DisputeCard
                                    key={dispute.id}
                                    dispute={dispute}
                                    onClick={() => handleViewDetails(dispute)}
                                />
                            ))}
                        </div>
                        <Pagination
                            currentPage={currentPage}
                            totalPages={totalPages}
                            onPageChange={handlePageChange}
                        />
                    </>
                )}
            </div>

            {/* Modals */}
            <DisputeDetailsModal
                isOpen={!!selectedDispute}
                onClose={() => setSelectedDispute(null)}
                dispute={selectedDispute}
                onResolve={() => {
                    fetchDisputes();
                    setSelectedDispute(null);
                }}
            />

            <RaiseDisputeModal
                isOpen={isRaiseModalOpen}
                onClose={() => setIsRaiseModalOpen(false)}
                onSuccess={fetchDisputes}
                // Removed hardcoded orderId. Users should navigate from Order Details.
                orderId={null}
            />
        </div>
    );
};

export default BuyerDisputesPage;
