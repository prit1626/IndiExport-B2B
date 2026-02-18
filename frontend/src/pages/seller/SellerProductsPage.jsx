import React, { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import { Plus, Search, Loader2 } from 'lucide-react';
import toast from 'react-hot-toast';
import { debounce } from 'lodash';
import productApi from '../../api/productApi';
import useAuthStore from '../../store/authStore';

// Components
import ProductCard from '../../components/sellerProducts/ProductCard';
import UpgradeCtaBanner from '../../components/sellerProducts/UpgradeCtaBanner';

const SellerProductsPage = () => {
    const { user } = useAuthStore();
    const [products, setProducts] = useState([]);
    const [loading, setLoading] = useState(true);
    const [keyword, setKeyword] = useState('');
    const [filterStatus, setFilterStatus] = useState('ALL');

    // Derived state
    const activeProducts = products.filter(p => p.status === 'ACTIVE');
    const isBasicSeller = user?.role === 'SELLER' && user?.subscriptionPlan === 'BASIC';
    const showUpgradeBanner = isBasicSeller && activeProducts.length >= 5;

    useEffect(() => {
        fetchProducts();
    }, [filterStatus]);

    const fetchProducts = async () => {
        setLoading(true);
        try {
            const params = {
                page: 0,
                size: 50, // Should use pagination in real app
                keyword: keyword,
                status: filterStatus !== 'ALL' ? filterStatus : undefined
            };
            const { data } = await productApi.sellerGetProducts(params);
            setProducts(data.content || []);
        } catch (error) {
            console.error(error);
            toast.error('Failed to load products');
        } finally {
            setLoading(false);
        }
    };

    const handleSearch = debounce((val) => {
        setKeyword(val);
        // Trigger fetch with new keyword (can add dependency or call fetch directly)
        // Here, we'll just let effect handle or call fetching manually if needed.
        // For simplicity, let's just re-fetch:
        productApi.sellerGetProducts({ page: 0, size: 50, keyword: val }).then(res => {
            setProducts(res.data.content || []);
        });
    }, 500);

    const handleToggleActive = async (id, isActive) => {
        // Optimistic update
        const oldProducts = [...products];
        const newStatus = isActive ? 'ACTIVE' : 'DRAFT';
        setProducts(products.map(p => p.id === id ? { ...p, status: newStatus } : p));

        try {
            await productApi.sellerUpdateProduct(id, { status: newStatus });
            toast.success(isActive ? 'Product activated' : 'Product deactivated');
        } catch (error) {
            setProducts(oldProducts); // Revert
            console.error(error);
            toast.error(error.response?.data?.message || 'Failed to update status');
        }
    };

    const handleDelete = async (id) => {
        if (!window.confirm('Are you sure you want to delete this product?')) return;

        try {
            await productApi.sellerDeleteProduct(id);
            setProducts(products.filter(p => p.id !== id));
            toast.success('Product deleted');
        } catch (error) {
            toast.error('Failed to delete product');
        }
    };

    return (
        <div className="min-h-screen bg-slate-50 pb-20">
            {/* Header */}
            <div className="bg-white border-b border-slate-200 py-6">
                <div className="container mx-auto px-4 max-w-7xl flex flex-col md:flex-row md:items-center justify-between gap-4">
                    <div>
                        <h1 className="text-2xl font-bold text-slate-800">My Products</h1>
                        <p className="text-slate-500 text-sm">
                            Active: <span className="font-semibold text-slate-900">{activeProducts.length}</span>
                            {isBasicSeller && <span className="text-slate-400"> / 5 Limit</span>}
                        </p>
                    </div>
                    <Link
                        to="/seller/products/new"
                        className="bg-brand-600 hover:bg-brand-700 text-white px-5 py-2.5 rounded-lg flex items-center gap-2 font-bold shadow-sm transition-all active:scale-95"
                    >
                        <Plus size={20} /> Add New Product
                    </Link>
                </div>
            </div>

            <div className="container mx-auto px-4 max-w-7xl py-8">
                <UpgradeCtaBanner isVisible={showUpgradeBanner} />

                {/* Filters */}
                <div className="flex flex-col md:flex-row gap-4 mb-6">
                    <div className="relative flex-1">
                        <Search className="absolute left-3 top-1/2 -translate-y-1/2 text-slate-400" size={20} />
                        <input
                            onChange={(e) => handleSearch(e.target.value)}
                            className="w-full pl-10 pr-4 py-2 rounded-lg border border-slate-300 focus:ring-2 focus:ring-brand-500 focus:border-brand-500 outline-none"
                            placeholder="Search by title, brand..."
                        />
                    </div>
                    <div className="flex gap-2">
                        {['ALL', 'ACTIVE', 'INACTIVE'].map(status => (
                            <button
                                key={status}
                                onClick={() => setFilterStatus(status)}
                                className={`px-4 py-2 rounded-lg text-sm font-medium transition-colors ${filterStatus === status
                                    ? 'bg-slate-800 text-white'
                                    : 'bg-white border border-slate-300 text-slate-600 hover:bg-slate-50'
                                    }`}
                            >
                                {status.charAt(0) + status.slice(1).toLowerCase()}
                            </button>
                        ))}
                    </div>
                </div>

                {/* Grid */}
                {loading ? (
                    <div className="flex justify-center py-20">
                        <Loader2 className="animate-spin text-brand-600" size={40} />
                    </div>
                ) : products.length === 0 ? (
                    <div className="text-center py-20 bg-white rounded-xl border border-dashed border-slate-300">
                        <p className="text-slate-500 mb-4">No products found.</p>
                        <Link to="/seller/products/new" className="text-brand-600 font-bold hover:underline">Create your first product</Link>
                    </div>
                ) : (
                    <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4 gap-6">
                        {products.map(product => (
                            <ProductCard
                                key={product.id}
                                product={product}
                                activeCount={activeProducts.length}
                                onToggleActive={handleToggleActive}
                                onDelete={handleDelete}
                            />
                        ))}
                    </div>
                )}
            </div>
        </div>
    );
};

export default SellerProductsPage;
