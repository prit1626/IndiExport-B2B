import React, { useState, useEffect } from 'react';
import { motion } from 'framer-motion';
import { Users, DollarSign, ShoppingCart, AlertTriangle, UserPlus } from 'lucide-react';
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

const AdminDashboardPage = () => {
    const [loading, setLoading] = useState(true);
    const [data, setData] = useState(null);
    const [error, setError] = useState(null);
    const [dateRange, setDateRange] = useState('30d');

    const fetchData = async () => {
        setLoading(true);
        setError(null);
        try {
            const { from, to } = calculateDateRange(dateRange);
            const response = await analyticsApi.getAdminAnalytics({ from, to });
            setData(response.data);
        } catch (err) {
            console.error("Failed to fetch admin analytics:", err);
            setError("Failed to load dashboard data.");
        } finally {
            setLoading(false);
        }
    };

    useEffect(() => {
        fetchData();
    }, [dateRange]);

    if (loading && !data) return <div className="p-6 max-w-7xl mx-auto"><AnalyticsSkeleton /></div>;
    if (error) return <div className="p-6 max-w-7xl mx-auto"><DashboardHeader title="Admin Dashboard" /><ErrorState message={error} onRetry={fetchData} /></div>;

    const {
        platformOrdersCount = 0,
        platformGMVINRPaise = 0,
        platformCommissionINRPaise = 0,
        disputesOpenCount = 0,
        newSellersCount = 0,
        newBuyersCount = 0,
        ordersOverTime = [],
        disputesOverTime = [],
        topCountries = []
    } = data || {};

    return (
        <div className="min-h-screen bg-slate-50 p-6 md:p-10">
            <div className="max-w-7xl mx-auto space-y-8">
                <DashboardHeader
                    title="Admin Dashboard"
                    selectedRange={dateRange}
                    onDateRangeChange={setDateRange}
                    onRefresh={fetchData}
                    isRefreshing={loading}
                />

                {/* KPIs */}
                <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
                    <StatCard
                        title="Platform GMV"
                        value={formatMoney(platformGMVINRPaise)}
                        icon={DollarSign}
                        color="green"
                    />
                    <StatCard
                        title="Platform Commission"
                        value={formatMoney(platformCommissionINRPaise)}
                        icon={DollarSign}
                        color="blue"
                    />
                    <StatCard
                        title="Total Orders"
                        value={platformOrdersCount}
                        icon={ShoppingCart}
                        color="purple"
                    />
                    <StatCard
                        title="Open Disputes"
                        value={disputesOpenCount}
                        icon={AlertTriangle}
                        color="orange"
                    />
                    <StatCard
                        title="New Sellers"
                        value={newSellersCount}
                        icon={Users}
                        color="brand"
                    />
                    <StatCard
                        title="New Buyers"
                        value={newBuyersCount}
                        icon={UserPlus}
                        color="brand"
                    />
                </div>

                {/* Charts Section */}
                <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
                    <ChartCard title="Orders Trend">
                        {ordersOverTime.length > 0 ? (
                            <ResponsiveContainer width="100%" height={300}>
                                <LineChart data={ordersOverTime}>
                                    <CartesianGrid strokeDasharray="3 3" vertical={false} stroke="#e2e8f0" />
                                    <XAxis dataKey="date" axisLine={false} tickLine={false} tick={{ fill: '#64748b', fontSize: 12 }} dy={10} />
                                    <YAxis axisLine={false} tickLine={false} tick={{ fill: '#64748b', fontSize: 12 }} />
                                    <Tooltip contentStyle={{ borderRadius: '12px', border: 'none', boxShadow: '0 4px 6px -1px rgb(0 0 0 / 0.1)' }} />
                                    <Line type="monotone" dataKey="count" stroke="#4f46e5" strokeWidth={3} dot={{ r: 4, fill: '#4f46e5' }} activeDot={{ r: 6 }} />
                                </LineChart>
                            </ResponsiveContainer>
                        ) : <EmptyState message="No order history" />}
                    </ChartCard>

                    <ChartCard title="Disputes Trend">
                        {disputesOverTime.length > 0 ? (
                            <ResponsiveContainer width="100%" height={300}>
                                <LineChart data={disputesOverTime}>
                                    <CartesianGrid strokeDasharray="3 3" vertical={false} stroke="#e2e8f0" />
                                    <XAxis dataKey="date" axisLine={false} tickLine={false} tick={{ fill: '#64748b', fontSize: 12 }} dy={10} />
                                    <YAxis axisLine={false} tickLine={false} tick={{ fill: '#64748b', fontSize: 12 }} />
                                    <Tooltip contentStyle={{ borderRadius: '12px', border: 'none', boxShadow: '0 4px 6px -1px rgb(0 0 0 / 0.1)' }} />
                                    <Line type="monotone" dataKey="count" stroke="#ef4444" strokeWidth={3} dot={{ r: 4, fill: '#ef4444' }} activeDot={{ r: 6 }} />
                                </LineChart>
                            </ResponsiveContainer>
                        ) : <EmptyState message="No disputes recorded" />}
                    </ChartCard>

                    <ChartCard title="Top Countries by Orders">
                        {topCountries.length > 0 ? (
                            <ResponsiveContainer width="100%" height={300}>
                                <BarChart data={topCountries}>
                                    <CartesianGrid strokeDasharray="3 3" vertical={false} stroke="#e2e8f0" />
                                    <XAxis dataKey="country" axisLine={false} tickLine={false} tick={{ fill: '#64748b', fontSize: 12 }} dy={10} />
                                    <YAxis axisLine={false} tickLine={false} tick={{ fill: '#64748b', fontSize: 12 }} />
                                    <Tooltip />
                                    <Bar dataKey="orders" fill="#0ea5e9" radius={[4, 4, 0, 0]} />
                                </BarChart>
                            </ResponsiveContainer>
                        ) : <EmptyState message="No country data available" />}
                    </ChartCard>
                </div>
            </div>
        </div>
    );
};

export default AdminDashboardPage;
