import React, { useState, useEffect, useMemo } from 'react';
import { useSearchParams, useNavigate } from 'react-router-dom';
import { Filter, Search } from 'lucide-react';
import productApi from '../../api/productApi';
import useDebounce from '../../hooks/useDebounce';
import ProductCard from '../../components/products/ProductCard';
import ProductFiltersSidebar from '../../components/products/ProductFiltersSidebar';
import ProductFiltersDrawer from '../../components/products/ProductFiltersDrawer';
import SortDropdown from '../../components/products/SortDropdown';
import ProductGridSkeleton from '../../components/products/ProductGridSkeleton';
import Pagination from '../../components/common/Pagination';

const ProductListingPage = () => {
    const [searchParams, setSearchParams] = useSearchParams();

    // 1. Read filters directly from URL
    const filters = useMemo(() => ({
        keyword: searchParams.get('keyword') || '',
        category: searchParams.get('category') || '',
        minPrice: searchParams.get('minPrice') || '',
        maxPrice: searchParams.get('maxPrice') || '',
        rating: searchParams.get('rating') || '',
        verifiedSeller: searchParams.get('verifiedSeller') || '',
        incoterm: searchParams.get('incoterm') || '',
        leadTime: searchParams.get('leadTime') || '',
        country: searchParams.get('country') || '',
        sort: searchParams.get('sort') || 'latest',
        page: parseInt(searchParams.get('page')) || 0,
        size: parseInt(searchParams.get('size')) || 12,
    }), [searchParams]);

    // 2. Local state for keyword input (to allow typing without URL updates every keystroke)
    const [keywordInput, setKeywordInput] = useState(filters.keyword);

    // Sync local input when URL changes (e.g. back button)
    useEffect(() => {
        setKeywordInput(filters.keyword);
    }, [filters.keyword]);

    const debouncedKeyword = useDebounce(keywordInput, 500);

    // 3. Update URL when debounced keyword changes
    useEffect(() => {
        if (debouncedKeyword !== filters.keyword) {
            updateFilters({ keyword: debouncedKeyword, page: 0 });
        }
    }, [debouncedKeyword]);

    const [products, setProducts] = useState([]);
    const [totalItems, setTotalItems] = useState(0);
    const [totalPages, setTotalPages] = useState(0);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);
    const [isDrawerOpen, setIsDrawerOpen] = useState(false);

    // 4. Helper to update URL params
    const updateFilters = (newFilters) => {
        const current = {};
        searchParams.forEach((value, key) => {
            current[key] = value;
        });

        const merged = { ...current, ...newFilters };

        // Clean up empty values
        const clean = {};
        Object.keys(merged).forEach(key => {
            if (merged[key] !== '' && merged[key] !== null && merged[key] !== undefined) {
                clean[key] = merged[key];
            }
        });

        setSearchParams(clean);
    };

    // 5. Fetch Products (Depends on URL filters)
    useEffect(() => {
        const controller = new AbortController();

        const fetchProducts = async () => {
            setLoading(true);
            setError(null);
            try {
                // filters object is already derived from URL
                const response = await productApi.getProducts(filters);
                setProducts(response.data.items || []);
                setTotalItems(response.data.totalItems);
                setTotalPages(response.data.totalPages);
            } catch (err) {
                if (err.name !== 'CanceledError') {
                    console.error("Fetch error:", err);
                    setError("Failed to load products. Please try again.");
                }
            } finally {
                setLoading(false);
            }
        };

        fetchProducts();

        return () => controller.abort();
    }, [filters]); // dependency is the memoized filters object

    const handleFilterChange = (newFilters) => {
        updateFilters({ ...newFilters, page: 0 });
    };

    const handlePageChange = (newPage) => {
        updateFilters({ page: newPage });
        window.scrollTo({ top: 0, behavior: 'smooth' });
    };

    const handleSearchChange = (e) => {
        setKeywordInput(e.target.value);
    };

    const handleReset = () => {
        setSearchParams({}); // Clears all params
        setKeywordInput('');
    };

    return (
        <div className="min-h-screen bg-slate-50 pb-20">
            {/* Header / Search Bar */}
            <div className="bg-white border-b border-slate-200 sticky top-0 z-30">
                <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 h-16 flex items-center justify-between gap-4">
                    <div className="font-bold text-xl text-brand-700 hidden sm:block">Marketplace</div>

                    <div className="flex-1 max-w-2xl relative">
                        <Search className="absolute left-3 top-1/2 -translate-y-1/2 w-5 h-5 text-slate-400" />
                        <input
                            type="text"
                            placeholder="Search products (e.g., 'Spices', 'Textiles')..."
                            value={keywordInput}
                            onChange={handleSearchChange}
                            className="w-full pl-10 pr-4 py-2 bg-slate-100 border-transparent focus:bg-white border border-slate-200 rounded-xl outline-none focus:ring-2 focus:ring-brand-500/20 transition-all"
                        />
                    </div>

                    <button
                        onClick={() => setIsDrawerOpen(true)}
                        className="lg:hidden p-2 text-slate-600 hover:bg-slate-100 rounded-lg"
                    >
                        <Filter className="w-6 h-6" />
                    </button>
                </div>
            </div>

            <main className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
                <div className="flex gap-8">
                    {/* Sidebar Filters (Desktop) */}
                    <ProductFiltersSidebar
                        filters={filters}
                        onFilterChange={handleFilterChange}
                    />

                    {/* Mobile Drawer */}
                    <ProductFiltersDrawer
                        isOpen={isDrawerOpen}
                        onClose={() => setIsDrawerOpen(false)}
                        filters={filters}
                        onFilterChange={handleFilterChange}
                    />

                    {/* Main Grid */}
                    <div className="flex-1">
                        {/* Sort & Count */}
                        <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4 mb-6">
                            <p className="text-slate-600 text-sm">
                                Found <span className="font-semibold text-slate-900">{totalItems}</span> results
                            </p>
                            <SortDropdown
                                value={filters.sort}
                                onChange={(val) => handleFilterChange({ sort: val })}
                            />
                        </div>

                        {/* Content */}
                        {loading ? (
                            <ProductGridSkeleton />
                        ) : error ? (
                            <div className="text-center py-20">
                                <p className="text-red-500 mb-4">{error}</p>
                                <button
                                    onClick={() => window.location.reload()}
                                    className="px-4 py-2 bg-white border border-slate-300 rounded-lg text-slate-700 hover:bg-slate-50"
                                >
                                    Try Again
                                </button>
                            </div>
                        ) : (products || []).length === 0 ? (
                            <div className="text-center py-20 bg-white rounded-2xl border border-slate-200 border-dashed">
                                <div className="text-slate-400 mb-2">No products found matching your criteria.</div>
                                <button
                                    onClick={handleReset}
                                    className="text-brand-600 font-medium hover:underline"
                                >
                                    Clear all filters
                                </button>
                            </div>
                        ) : (
                            <>
                                <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 xl:grid-cols-3 gap-6">
                                    {products.map(product => (
                                        <ProductCard key={product.id} product={product} />
                                    ))}
                                </div>

                                <Pagination
                                    currentPage={filters.page}
                                    totalPages={totalPages}
                                    onPageChange={handlePageChange}
                                />
                            </>
                        )}
                    </div>
                </div>
            </main>
        </div>
    );
};

export default ProductListingPage;
