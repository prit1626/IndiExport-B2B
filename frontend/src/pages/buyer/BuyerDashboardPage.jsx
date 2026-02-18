import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { motion } from 'framer-motion';
import { ShoppingCart, Truck, CheckCircle, TrendingUp, Package } from 'lucide-react';
import { LineChart, Line, XAxis, YAxis, CartesianGrid, Tooltip, ResponsiveContainer, BarChart, Bar } from 'recharts';

import analyticsApi from '../../api/analyticsApi';
import { formatMoney } from '../../utils/formatMoney';
import { calculateDateRange } from '../../utils/dateUtils';

import DashboardHeader from '../../components/analytics/DashboardHeader';
import StatCard from '../../components/analytics/StatCard';
import ChartCard from '../../components/analytics/ChartCard';
import AnalyticsSkeleton from '../../components/analytics/AnalyticsSkeleton';
import ErrorState from '../../components/analytics/ErrorState';
import EmptyState from '../../components/analytics/EmptyState';

const BuyerDashboardPage = () => {
    const navigate = useNavigate();
    const [loading, setLoading] = useState(true);
    const [data, setData] = useState(null);
    const [error, setError] = useState(null);
    const [dateRange, setDateRange] = useState('30d');

    const fetchData = async () => {
        setLoading(true);
        setError(null);
        try {
            const { from, to } = calculateDateRange(dateRange);
            const response = await analyticsApi.getBuyerAnalytics({ from, to });
            setData(response.data);
        } catch (err) {
            console.error("Failed to fetch buyer analytics:", err);
            setError("Failed to load dashboard data.");
        } finally {
            setLoading(false);
        }
    };

    useEffect(() => {
        fetchData();
    }, [dateRange]);

    if (loading && !data) return <div className="p-6 max-w-7xl mx-auto"><AnalyticsSkeleton /></div>;
    if (error) return <div className="p-6 max-w-7xl mx-auto"><DashboardHeader title="Buyer Dashboard" /><ErrorState message={error} onRetry={fetchData} /></div>;

    const {
        totalOrders = 0,
        activeShipmentsCount = 0,
        completedOrders = 0,
        totalSpending = 0,
        ordersOverTime = [],
        spendingOverTime = [],
        lastOrders = []
    } = data || {};

    const hasCharts = ordersOverTime.length > 0 || spendingOverTime.length > 0;

    return (
        <div className="min-h-screen bg-slate-50 p-6 md:p-10">
            <div className="max-w-7xl mx-auto space-y-8">
                <DashboardHeader
                    title="Buyer Dashboard"
                    selectedRange={dateRange}
                    onDateRangeChange={setDateRange}
                    onRefresh={fetchData}
                    isRefreshing={loading}
                />

                {/* KPIs */}
                <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6">
                    <StatCard
                        title="Total Orders"
                        value={totalOrders}
                        icon={ShoppingCart}
                        color="blue"
                        onClick={() => navigate('/buyer/orders')}
                    />
                    <StatCard
                        title="Active Shipments"
                        value={activeShipmentsCount}
                        icon={Truck}
                        color="orange"
                        onClick={() => navigate('/buyer/orders')}
                    />
                    <StatCard
                        title="Completed Orders"
                        value={completedOrders}
                        icon={CheckCircle}
                        color="green"
                        onClick={() => navigate('/buyer/orders')}
                    />
                    <StatCard
                        title="Total Spending"
                        value={formatMoney(totalSpending)}
                        icon={TrendingUp}
                        color="purple"
                        onClick={() => navigate('/buyer/orders')}
                    />
                </div>

                {/* Charts Section */}
                {hasCharts && (
                    <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
                        <ChartCard title="Orders Trend">
                            {ordersOverTime.length > 0 ? (
                                <ResponsiveContainer width="100%" height={300}>
                                    <LineChart data={ordersOverTime}>
                                        <CartesianGrid strokeDasharray="3 3" vertical={false} stroke="#e2e8f0" />
                                        <XAxis dataKey="date" axisLine={false} tickLine={false} tick={{ fill: '#64748b', fontSize: 12 }} dy={10} />
                                        <YAxis axisLine={false} tickLine={false} tick={{ fill: '#64748b', fontSize: 12 }} />
                                        <Tooltip
                                            contentStyle={{ borderRadius: '12px', border: 'none', boxShadow: '0 4px 6px -1px rgb(0 0 0 / 0.1)' }}
                                        />
                                        <Line type="monotone" dataKey="count" stroke="#4f46e5" strokeWidth={3} dot={{ r: 4, fill: '#4f46e5' }} activeDot={{ r: 6 }} />
                                    </LineChart>
                                </ResponsiveContainer>
                            ) : <EmptyState message="No order history for this period" />}
                        </ChartCard>

                        <ChartCard title="Spending Trend">
                            {spendingOverTime.length > 0 ? (
                                <ResponsiveContainer width="100%" height={300}>
                                    <BarChart data={spendingOverTime}>
                                        <CartesianGrid strokeDasharray="3 3" vertical={false} stroke="#e2e8f0" />
                                        <XAxis dataKey="date" axisLine={false} tickLine={false} tick={{ fill: '#64748b', fontSize: 12 }} dy={10} />
                                        <YAxis axisLine={false} tickLine={false} tick={{ fill: '#64748b', fontSize: 12 }} />
                                        <Tooltip
                                            formatter={(value) => formatMoney(value)}
                                            contentStyle={{ borderRadius: '12px', border: 'none', boxShadow: '0 4px 6px -1px rgb(0 0 0 / 0.1)' }}
                                        />
                                        <Bar dataKey="amount" fill="#10b981" radius={[4, 4, 0, 0]} />
                                    </BarChart>
                                </ResponsiveContainer>
                            ) : <EmptyState message="No spending history for this period" />}
                        </ChartCard>
                    </div>
                )}

                {/* Recent Orders Table */}
                <motion.div
                    initial={{ opacity: 0, y: 10 }}
                    whileInView={{ opacity: 1, y: 0 }}
                    viewport={{ once: true }}
                    className="bg-white rounded-2xl border border-slate-200 overflow-hidden shadow-sm"
                >
                    <div className="p-6 border-b border-slate-100 flex items-center justify-between">
                        <h3 className="text-lg font-bold text-slate-800">Recent Orders</h3>
                    </div>

                    <div className="overflow-x-auto">
                        <table className="w-full text-left">
                            <thead className="bg-slate-50 text-slate-500 text-xs uppercase font-semibold">
                                <tr>
                                    <th className="px-6 py-4">Order ID</th>
                                    <th className="px-6 py-4">Total</th>
                                    <th className="px-6 py-4">Status</th>
                                    <th className="px-6 py-4">Date</th>
                                </tr>
                            </thead>
                            <tbody className="divide-y divide-slate-100">
                                {lastOrders.length > 0 ? (
                                    lastOrders.map((order) => (
                                        <tr key={order.orderId} className="hover:bg-slate-50/50 transition-colors">
                                            <td className="px-6 py-4 font-medium text-slate-900">
                                                #{order.orderId ? order.orderId.slice(0, 8) : '---'}
                                            </td>
                                            <td className="px-6 py-4 text-slate-600">
                                                {formatMoney(order.total)}
                                            </td>
                                            <td className="px-6 py-4">
                                                <span className={`px-2.5 py-1 rounded-full text-xs font-medium 
                                                    ${order.status === 'COMPLETED' ? 'bg-green-100 text-green-700' :
                                                        order.status === 'PENDING' ? 'bg-amber-100 text-amber-700' :
                                                            order.status === 'CANCELLED' ? 'bg-red-100 text-red-700' :
                                                                'bg-blue-100 text-blue-700'}`}>
                                                    {order.status}
                                                </span>
                                            </td>
                                            <td className="px-6 py-4 text-slate-500 text-sm">
                                                {new Date(order.createdAt).toLocaleDateString()}
                                            </td>
                                        </tr>
                                    ))
                                ) : (
                                    <tr>
                                        <td colSpan="4" className="px-6 py-12 text-center text-slate-400">
                                            <Package className="w-8 h-8 mx-auto mb-2 opacity-50" />
                                            No recent orders found
                                        </td>
                                    </tr>
                                )}
                            </tbody>
                        </table>
                    </div>
                </motion.div>
            </div>
        </div>
    );
};

export default BuyerDashboardPage;
