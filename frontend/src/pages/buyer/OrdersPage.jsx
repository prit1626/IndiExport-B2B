import React, { useState, useEffect } from 'react';
import { useSearchParams } from 'react-router-dom';
import orderApi from '../../api/orderApi';
import OrderCard from '../../components/orders/OrderCard';
import OrdersSkeleton from '../../components/orders/OrdersSkeleton';
import OrdersEmptyState from '../../components/orders/OrdersEmptyState';
import OrdersErrorState from '../../components/orders/OrdersErrorState';
import Pagination from '../../components/common/Pagination';
import { Filter, ArrowUpDown } from 'lucide-react';

const OrdersPage = () => {
    const [searchParams, setSearchParams] = useSearchParams();
    const [orders, setOrders] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);
    const [totalPages, setTotalPages] = useState(1);

    // Filters state
    const page = parseInt(searchParams.get('page') || '0');
    const status = searchParams.get('status') || '';
    const sort = searchParams.get('sort') || 'createdAt,desc';

    const fetchOrders = async () => {
        try {
            setLoading(true);
            setError(null);

            // Backend expects page 0-indexed
            const params = {
                page,
                size: 10,
                status: status === 'ALL' ? '' : status,
                sort
            };

            const response = await orderApi.getBuyerOrders(params);
            setOrders(response.data.items || []); // Allow items or content
            setTotalPages(response.data.totalPages || 1);
        } catch (err) {
            console.error("Failed to fetch orders:", err);
            setError(err.response?.data?.message || "Failed to load orders");
        } finally {
            setLoading(false);
        }
    };

    useEffect(() => {
        fetchOrders();
        // eslint-disable-next-line react-hooks/exhaustive-deps
    }, [page, status, sort]);

    const handleFilterChange = (key, value) => {
        const newParams = new URLSearchParams(searchParams);
        if (value) {
            newParams.set(key, value);
        } else {
            newParams.delete(key);
        }
        newParams.set('page', '0'); // Reset to first page
        setSearchParams(newParams);
    };

    const handlePageChange = (newPage) => {
        const newParams = new URLSearchParams(searchParams);
        newParams.set('page', newPage.toString());
        setSearchParams(newParams);
        window.scrollTo(0, 0);
    };

    const resetFilters = () => {
        setSearchParams({});
    };

    return (
        <div className="min-h-screen bg-slate-50 py-8">
            <div className="container mx-auto px-4 max-w-5xl">
                <div className="flex flex-col sm:flex-row justify-between items-start sm:items-center gap-4 mb-8">
                    <div>
                        <h1 className="text-2xl font-bold text-slate-900">Your Orders</h1>
                        <p className="text-slate-500">Track and manage your recent purchases</p>
                    </div>
                </div>

                {/* Filters */}
                <div className="bg-white p-4 rounded-xl border border-slate-200 shadow-sm mb-6 flex flex-col sm:flex-row gap-4 items-center justify-between">
                    <div className="flex items-center gap-2 w-full sm:w-auto">
                        <Filter size={18} className="text-slate-400" />
                        <select
                            value={status}
                            onChange={(e) => handleFilterChange('status', e.target.value)}
                            className="bg-slate-50 border border-slate-200 text-slate-700 text-sm rounded-lg focus:ring-brand-500 focus:border-brand-500 block w-full sm:w-48 p-2.5"
                        >
                            <option value="">All Statuses</option>
                            <option value="CREATED">Created</option>
                            <option value="PAID">Paid</option>
                            <option value="PROCESSING">Processing</option>
                            <option value="SHIPPED">Shipped</option>
                            <option value="DELIVERED">Delivered</option>
                            <option value="CANCELLED">Cancelled</option>
                        </select>
                    </div>

                    <div className="flex items-center gap-2 w-full sm:w-auto">
                        <ArrowUpDown size={18} className="text-slate-400" />
                        <select
                            value={sort}
                            onChange={(e) => handleFilterChange('sort', e.target.value)}
                            className="bg-slate-50 border border-slate-200 text-slate-700 text-sm rounded-lg focus:ring-brand-500 focus:border-brand-500 block w-full sm:w-48 p-2.5"
                        >
                            <option value="createdAt,desc">Newest First</option>
                            <option value="createdAt,asc">Oldest First</option>
                        </select>
                    </div>
                </div>

                {/* Content */}
                {loading ? (
                    <OrdersSkeleton />
                ) : error ? (
                    <OrdersErrorState message={error} onRetry={fetchOrders} />
                ) : orders.length === 0 ? (
                    <OrdersEmptyState isFilterActive={!!status} resetFilters={resetFilters} />
                ) : (
                    <div className="space-y-4">
                        {orders.map(order => (
                            <OrderCard key={order.id} order={order} />
                        ))}

                        {totalPages > 1 && (
                            <div className="pt-6 flex justify-center">
                                <Pagination
                                    currentPage={page}
                                    totalPages={totalPages}
                                    onPageChange={handlePageChange}
                                />
                            </div>
                        )}
                    </div>
                )}
            </div>
        </div>
    );
};

export default OrdersPage;
