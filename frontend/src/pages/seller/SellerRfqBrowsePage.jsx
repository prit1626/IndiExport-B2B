import React, { useState, useEffect } from 'react';
import { useNavigate, useSearchParams } from 'react-router-dom';
import rfqApi from '../../api/rfqApi';
import RfqCard from '../../components/rfq/RfqCard';
import RfqFilters from '../../components/rfq/RfqFilters';
import RfqBrowseSkeleton from '../../components/rfq/RfqBrowseSkeleton';
import toast from 'react-hot-toast';
import { PackageOpen } from 'lucide-react';

const SellerRfqBrowsePage = () => {
    const navigate = useNavigate();
    const [searchParams, setSearchParams] = useSearchParams();

    // Parse filters from URL
    const initialFilters = {
        keyword: searchParams.get('keyword') || '',
        category: searchParams.get('category') || '',
        destinationCountry: searchParams.get('destinationCountry') || '',
        minQty: searchParams.get('minQty') || '',
        sort: searchParams.get('sort') || 'createdAt,desc',
        page: parseInt(searchParams.get('page')) || 0,
        size: 10
    };

    const [filters, setFilters] = useState(initialFilters);
    const [rfqs, setRfqs] = useState([]);
    const [loading, setLoading] = useState(true);
    const [totalElements, setTotalElements] = useState(0);
    const [totalPages, setTotalPages] = useState(0);

    useEffect(() => {
        const fetchRfqs = async () => {
            setLoading(true);
            try {
                // Remove empty keys
                const params = Object.fromEntries(
                    Object.entries(filters).filter(([_, v]) => v != null && v !== '')
                );

                const { data } = await rfqApi.sellerGetRfqs(params);
                setRfqs(data.content || []);
                setTotalElements(data.totalElements);
                setTotalPages(data.totalPages);

                // Update URL (silent replace)
                setSearchParams(params, { replace: true });

            } catch (error) {
                console.error(error);
                toast.error('Failed to load RFQs');
            } finally {
                setLoading(false);
            }
        };

        fetchRfqs();
    }, [filters]);

    const handleFilterChange = (newFilters) => {
        setFilters(prev => ({ ...prev, ...newFilters }));
    };

    const handlePageChange = (newPage) => {
        if (newPage >= 0 && newPage < totalPages) {
            handleFilterChange({ page: newPage });
            window.scrollTo({ top: 0, behavior: 'smooth' });
        }
    };

    return (
        <div className="min-h-screen bg-slate-50 py-8">
            <div className="container mx-auto px-4 max-w-7xl">
                <div className="flex flex-col md:flex-row gap-8">
                    {/* Sidebar Filters */}
                    <div className="w-full md:w-64 flex-shrink-0">
                        <div className="sticky top-24">
                            <RfqFilters filters={filters} onFilterChange={handleFilterChange} />
                        </div>
                    </div>

                    {/* Main Content */}
                    <div className="flex-1">
                        <header className="mb-6 flex justify-between items-center">
                            <h1 className="text-2xl font-bold text-slate-800">Browse Requests</h1>
                            <span className="text-sm text-slate-500">{totalElements} Request{totalElements !== 1 ? 's' : ''} found</span>
                        </header>

                        {loading ? (
                            <RfqBrowseSkeleton />
                        ) : rfqs.length > 0 ? (
                            <div className="space-y-4">
                                {rfqs.map(rfq => (
                                    <RfqCard
                                        key={rfq.id}
                                        rfq={rfq}
                                        onClick={() => navigate(`/seller/rfq/${rfq.id}`)}
                                    />
                                ))}

                                {/* Simple Pagination */}
                                {totalPages > 1 && (
                                    <div className="flex justify-center gap-2 mt-8">
                                        <button
                                            disabled={filters.page === 0}
                                            onClick={() => handlePageChange(filters.page - 1)}
                                            className="px-4 py-2 border border-slate-300 rounded-lg hover:bg-white disabled:opacity-50"
                                        >
                                            Previous
                                        </button>
                                        <span className="px-4 py-2 text-slate-600">
                                            Page {filters.page + 1} of {totalPages}
                                        </span>
                                        <button
                                            disabled={filters.page === totalPages - 1}
                                            onClick={() => handlePageChange(filters.page + 1)}
                                            className="px-4 py-2 border border-slate-300 rounded-lg hover:bg-white disabled:opacity-50"
                                        >
                                            Next
                                        </button>
                                    </div>
                                )}
                            </div>
                        ) : (
                            <div className="flex flex-col items-center justify-center py-12 bg-white rounded-xl border border-slate-200">
                                <PackageOpen size={48} className="text-slate-300 mb-4" />
                                <h3 className="text-lg font-semibold text-slate-900">No requests found</h3>
                                <p className="text-slate-500">Try adjusting your filters to find more opportunities.</p>
                                <button
                                    onClick={() => handleFilterChange({ keyword: '', minQty: '', destinationCountry: '', page: 0 })}
                                    className="mt-4 text-brand-600 font-medium hover:underline"
                                >
                                    Clear all filters
                                </button>
                            </div>
                        )}
                    </div>
                </div>
            </div>
        </div>
    );
};

export default SellerRfqBrowsePage;
