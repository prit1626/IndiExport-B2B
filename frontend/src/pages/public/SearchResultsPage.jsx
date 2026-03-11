import React, { useState, useEffect } from 'react';
import { useSearchParams } from 'react-router-dom';
import productSearchApi from '../../api/productSearchApi';
import ProductSearchCard from '../../components/products/ProductSearchCard';
import Loader from '../../components/common/Loader';
import { Search, Filter, AlertCircle, ShoppingBag } from 'lucide-react';

const SearchResultsPage = () => {
    const [searchParams] = useSearchParams();
    const query = searchParams.get('q') || '';

    const [products, setProducts] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);
    const [pagination, setPagination] = useState({
        totalElements: 0,
        totalPages: 0,
        currentPage: 0
    });

    useEffect(() => {
        const fetchResults = async () => {
            if (!query) return;

            setLoading(true);
            setError(null);

            try {
                const response = await productSearchApi.searchProducts({
                    keyword: query,
                    page: 0,
                    size: 20
                });

                // Extract products from paginated response
                const content = response.content || [];
                setProducts(content);
                setPagination({
                    totalElements: response.totalElements || 0,
                    totalPages: response.totalPages || 0,
                    currentPage: response.number || 0
                });
            } catch (err) {
                console.error('Search failed:', err);
                setError('Failed to fetch search results. Please try again.');
            } finally {
                setLoading(false);
            }
        };

        fetchResults();
    }, [query]);

    if (!query) {
        return (
            <div className="flex flex-col items-center justify-center min-h-[60vh] px-4">
                <Search className="w-16 h-16 text-slate-200 mb-4" />
                <h2 className="text-xl font-semibold text-slate-900">Start searching for products</h2>
                <p className="text-slate-500 mt-2 text-center max-w-md">
                    Enter a keyword to find products across categories, materials, and locations.
                </p>
            </div>
        );
    }

    return (
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
            {/* Header */}
            <div className="flex flex-col md:flex-row md:items-center justify-between gap-4 mb-8">
                <div>
                    <h1 className="text-2xl font-bold text-slate-900 flex items-center gap-2">
                        Search Results for <span className="text-brand-600">"{query}"</span>
                    </h1>
                    <p className="text-slate-500 mt-1">
                        Found {pagination.totalElements} results
                    </p>
                </div>

                {/* Simplified filter button for UI placeholder */}
                <button className="inline-flex items-center gap-2 px-4 py-2 border border-slate-200 rounded-xl text-sm font-medium text-slate-700 hover:bg-slate-50 transition-colors md:self-end">
                    <Filter className="w-4 h-4" />
                    Filters
                </button>
            </div>

            {loading ? (
                <div className="flex items-center justify-center min-h-[40vh]">
                    <Loader />
                </div>
            ) : error ? (
                <div className="flex flex-col items-center justify-center min-h-[40vh] bg-red-50 rounded-3xl border border-red-100 p-8">
                    <AlertCircle className="w-12 h-12 text-red-500 mb-4" />
                    <h2 className="text-lg font-semibold text-red-900">Oops! Something went wrong</h2>
                    <p className="text-red-700 mt-2 text-center">{error}</p>
                    <button
                        onClick={() => window.location.reload()}
                        className="mt-6 px-6 py-2 bg-white border border-red-200 text-red-700 rounded-xl font-medium hover:bg-red-100 transition-colors"
                    >
                        Try Again
                    </button>
                </div>
            ) : products.length === 0 ? (
                <div className="flex flex-col items-center justify-center min-h-[40vh] bg-slate-50 rounded-3xl p-8 border border-slate-100 text-center">
                    <ShoppingBag className="w-16 h-16 text-slate-200 mb-4" />
                    <h2 className="text-xl font-bold text-slate-900">No products found</h2>
                    <p className="text-slate-500 mt-2 max-w-sm">
                        Try adjusting your search or filters to find what you're looking for.
                    </p>
                    <button
                        onClick={() => window.history.back()}
                        className="mt-6 px-6 py-2 bg-brand-600 text-white rounded-xl font-medium hover:bg-brand-700 transition-colors"
                    >
                        Go Back
                    </button>
                </div>
            ) : (
                <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 xl:grid-cols-5 gap-6">
                    {products.map((product) => (
                        <ProductSearchCard key={product.id} product={product} />
                    ))}
                </div>
            )}
        </div>
    );
};

export default SearchResultsPage;
