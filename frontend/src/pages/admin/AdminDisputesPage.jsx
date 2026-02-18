
import React, { useState, useEffect } from 'react';
import { Search, Loader2, Gavel } from 'lucide-react';
import { toast } from 'react-hot-toast';
import { useSearchParams } from 'react-router-dom';
import disputeApi from '../../api/disputeApi';
import DisputeCard from '../../components/disputes/DisputeCard';
import DisputeDetailsModal from '../../components/disputes/DisputeDetailsModal';
import Pagination from '../../components/common/Pagination';

const AdminDisputesPage = () => {
    const [searchParams, setSearchParams] = useSearchParams();
    const [disputes, setDisputes] = useState([]);
    const [loading, setLoading] = useState(true);
    const [selectedDispute, setSelectedDispute] = useState(null);
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
                status: filterStatus !== 'ALL' ? filterStatus : undefined,
                sort: 'createdAt,desc'
            };
            const { data } = await disputeApi.adminGetDisputes(params);
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

    const handleResolveSuccess = () => {
        setSelectedDispute(null);
        fetchDisputes(); // Refresh list to show updated status
    };

    return (
        <div className="min-h-screen bg-slate-50 pb-20">
            {/* Header */}
            <div className="bg-white border-b border-slate-200 py-6">
                <div className="container mx-auto px-4 max-w-7xl">
                    <h1 className="text-2xl font-bold text-slate-900 flex items-center gap-2">
                        <Gavel className="text-brand-600" /> Admin Dispute Resolution
                    </h1>
                    <p className="text-slate-500 text-sm mt-1">Adjudicate claims and manage refunds</p>
                </div>
            </div>

            <div className="container mx-auto px-4 max-w-7xl py-8">
                {/* Filters */}
                <div className="flex flex-wrap gap-2 mb-6">
                    {['ALL', 'RAISED', 'UNDER_REVIEW', 'RESOLVED', 'REJECTED'].map(status => (
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
                        <p className="text-slate-500">No disputes found matching criteria.</p>
                    </div>
                ) : (
                    <>
                        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
                            {disputes.map(dispute => (
                                <DisputeCard
                                    key={dispute.id}
                                    dispute={dispute}
                                    onClick={() => setSelectedDispute(dispute)}
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

            {/* Modal */}
            <DisputeDetailsModal
                isOpen={!!selectedDispute}
                onClose={() => setSelectedDispute(null)}
                dispute={selectedDispute}
                onResolve={handleResolveSuccess}
            />
        </div>
    );
};

export default AdminDisputesPage;
