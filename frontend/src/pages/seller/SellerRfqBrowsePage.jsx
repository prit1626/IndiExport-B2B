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
    const [tab, setTab] = useState('RECOMMENDED'); // Default to RECOMMENDED
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

                let response;
                if (tab === 'RECOMMENDED') {
                    // Recommended RFQs might not use all filters, but let's pass them just in case (e.g. pagination)
                    response = await rfqApi.sellerGetRecommendedRfqs({ page: params.page, size: params.size, sort: params.sort });
                } else {
                    response = await rfqApi.sellerGetRfqs(params);
                }

                const { data } = response;
                setRfqs(data.content || []);
                setTotalElements(data.totalElements);
                setTotalPages(data.totalPages);

                // Update URL (silent replace) - only for ALL tab as RECOMMENDED is dynamic
                if (tab === 'ALL') {
                    setSearchParams(params, { replace: true });
                } else {
                    setSearchParams({ tab: 'recommended', page: params.page }, { replace: true });
                }

            } catch (error) {
                console.error(error);
                toast.error('Failed to load RFQs');
            } finally {
                setLoading(false);
            }
        };

        fetchRfqs();
    }, [filters, tab]);

    const handleFilterChange = (newFilters) => {
        setFilters(prev => ({ ...prev, ...newFilters }));
    };

    const handleTabChange = (newTab) => {
        setTab(newTab);
        setFilters(prev => ({ ...prev, page: 0 })); // Reset page when switching tabs
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
                            {tab === 'ALL' ? (
                                <RfqFilters filters={filters} onFilterChange={handleFilterChange} />
                            ) : (
                                <div className="bg-white p-6 rounded-xl border border-slate-200 shadow-sm">
                                    <h3 className="font-semibold text-slate-900 mb-2">Recommendation Engine</h3>
                                    <p className="text-sm text-slate-500 leading-relaxed">
                                        We match your business based on your active product names, descriptions, and tags.
                                    </p>
                                    <div className="mt-4 pt-4 border-t border-slate-100">
                                        <p className="text-xs text-slate-400">
                                            Update your products to get more accurate recommendations.
                                        </p>
                                    </div>
                                </div>
                            )}
                        </div>
                    </div>

                    {/* Main Content */}
                    <div className="flex-1">
                        <header className="mb-6">
                            <h1 className="text-2xl font-bold text-slate-800 mb-6">Browse Requests</h1>
                            
                            <div className="flex items-center justify-between border-b border-slate-200">
                                <div className="flex gap-8">
                                    <button
                                        onClick={() => handleTabChange('RECOMMENDED')}
                                        className={`pb-4 text-sm font-medium transition-colors relative ${
                                            tab === 'RECOMMENDED' 
                                            ? 'text-brand-600' 
                                            : 'text-slate-500 hover:text-slate-700'
                                        }`}
                                    >
                                        Recommended RFQs
                                        {tab === 'RECOMMENDED' && (
                                            <div className="absolute bottom-0 left-0 right-0 h-0.5 bg-brand-600 rounded-full" />
                                        )}
                                    </button>
                                    <button
                                        onClick={() => handleTabChange('ALL')}
                                        className={`pb-4 text-sm font-medium transition-colors relative ${
                                            tab === 'ALL' 
                                            ? 'text-brand-600' 
                                            : 'text-slate-500 hover:text-slate-700'
                                        }`}
                                    >
                                        All RFQs
                                        {tab === 'ALL' && (
                                            <div className="absolute bottom-0 left-0 right-0 h-0.5 bg-brand-600 rounded-full" />
                                        )}
                                    </button>
                                </div>
                                <span className="pb-4 text-sm text-slate-500">{totalElements} Request{totalElements !== 1 ? 's' : ''} found</span>
                            </div>
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
                                <p className="text-slate-500">
                                    {tab === 'RECOMMENDED' 
                                        ? "We couldn't find any RFQs matching your products yet." 
                                        : "Try adjusting your filters to find more opportunities."}
                                </p>
                                {tab === 'ALL' && (
                                    <button
                                        onClick={() => handleFilterChange({ keyword: '', minQty: '', destinationCountry: '', page: 0 })}
                                        className="mt-4 text-brand-600 font-medium hover:underline"
                                    >
                                        Clear all filters
                                    </button>
                                )}
                            </div>
                        )}
                    </div>
                </div>
            </div>
        </div>
    );
};

export default SellerRfqBrowsePage;
