import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { motion } from 'framer-motion';
import { DollarSign, ShoppingCart, Package, Clock, CheckCircle, TrendingUp, Globe, BarChart2 } from 'lucide-react';
import { LineChart, Line, XAxis, YAxis, CartesianGrid, Tooltip, ResponsiveContainer, BarChart, Bar, PieChart, Pie, Cell, Legend, AreaChart, Area } from 'recharts';

import analyticsApi from '../../api/analyticsApi';
import useAuthStore from '../../store/authStore';
import { formatMoney } from '../../utils/formatMoney';
import { calculateDateRange } from '../../utils/dateUtils';

import DashboardHeader from '../../components/analytics/DashboardHeader';
import StatCard from '../../components/analytics/StatCard';
import ChartCard from '../../components/analytics/ChartCard';
import AnalyticsSkeleton from '../../components/analytics/AnalyticsSkeleton';
import ErrorState from '../../components/analytics/ErrorState';
import UpgradeLockedSection from '../../components/analytics/UpgradeLockedSection';
import EmptyState from '../../components/analytics/EmptyState';

const COLORS = ['#0088FE', '#00C49F', '#FFBB28', '#FF8042', '#8884d8'];

const SellerDashboardPage = () => {
    const navigate = useNavigate();
    const { user } = useAuthStore();
    const isAdvanced = user?.sellerPlan === 'ADVANCED_SELLER';

    const [loading, setLoading] = useState(true);
    const [basicData, setBasicData] = useState(null);
    const [advancedData, setAdvancedData] = useState(null);
    const [error, setError] = useState(null);
    const [dateRange, setDateRange] = useState('30d');

    const fetchData = async () => {
        setLoading(true);
        setError(null);
        try {
            const { from, to } = calculateDateRange(dateRange);

            // Parallel fetch if advanced
            const promises = [analyticsApi.getSellerAnalytics({ from, to })];
            if (isAdvanced) {
                promises.push(analyticsApi.getSellerAdvancedAnalytics({ from, to, groupBy: 'MONTH' }));
            }

            const results = await Promise.all(promises);
            setBasicData(results[0].data);
            if (isAdvanced) {
                setAdvancedData(results[1].data);
            }
        } catch (err) {
            console.error("Failed to fetch seller analytics:", err);
            setError("Failed to load dashboard data.");
        } finally {
            setLoading(false);
        }
    };

    useEffect(() => {
        fetchData();
    }, [dateRange, isAdvanced]);

    if (loading && !basicData) return <div className="p-6 max-w-7xl mx-auto"><AnalyticsSkeleton /></div>;
    if (error) return <div className="p-6 max-w-7xl mx-auto"><DashboardHeader title="Seller Dashboard" /><ErrorState message={error} onRetry={fetchData} /></div>;

    const {
        totalSalesCount = 0,
        totalRevenueINRPaise = 0,
        pendingOrdersCount = 0,
        shippedOrdersCount = 0,
        deliveredOrdersCount = 0,
        payoutHoldingCount = 0,
        payoutReleasedCount = 0,
        revenueOverTime = [],
        ordersByStatus = []
    } = basicData || {};

    const {
        monthlyRevenue = [],
        salesByCountry = [],
        topProducts = []
    } = advancedData || {};

    return (
        <div className="min-h-screen bg-slate-50 p-6 md:p-10">
            <div className="max-w-7xl mx-auto space-y-8">
                <DashboardHeader
                    title="Seller Dashboard"
                    selectedRange={dateRange}
                    onDateRangeChange={setDateRange}
                    onRefresh={fetchData}
                    isRefreshing={loading}
                />

                {/* Basic KPIs */}
                <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6">
                    <StatCard
                        title="Total Revenue"
                        value={formatMoney(totalRevenueINRPaise)}
                        icon={DollarSign}
                        color="green"
                        onClick={() => navigate('/seller/orders')}
                    />
                    <StatCard
                        title="Total Orders"
                        value={totalSalesCount}
                        icon={ShoppingCart}
                        color="blue"
                        onClick={() => navigate('/seller/orders')}
                    />
                    <StatCard
                        title="Pending Orders"
                        value={pendingOrdersCount}
                        icon={Clock}
                        color="orange"
                        onClick={() => navigate('/seller/orders?status=PENDING')}
                    />
                    <StatCard
                        title="Delivered"
                        value={deliveredOrdersCount}
                        icon={CheckCircle}
                        color="purple"
                        onClick={() => navigate('/seller/orders?status=DELIVERED')}
                    />
                </div>

                {/* Basic Charts */}
                <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
                    <ChartCard title="Revenue Trend">
                        {revenueOverTime.length > 0 ? (
                            <ResponsiveContainer width="100%" height={300}>
                                <AreaChart data={revenueOverTime}>
                                    <defs>
                                        <linearGradient id="colorRevenue" x1="0" y1="0" x2="0" y2="1">
                                            <stop offset="5%" stopColor="#10b981" stopOpacity={0.8} />
                                            <stop offset="95%" stopColor="#10b981" stopOpacity={0} />
                                        </linearGradient>
                                    </defs>
                                    <CartesianGrid strokeDasharray="3 3" vertical={false} stroke="#e2e8f0" />
                                    <XAxis dataKey="date" axisLine={false} tickLine={false} tick={{ fill: '#64748b', fontSize: 12 }} dy={10} />
                                    <YAxis axisLine={false} tickLine={false} tick={{ fill: '#64748b', fontSize: 12 }} />
                                    <Tooltip
                                        formatter={(value) => formatMoney(value)}
                                        contentStyle={{ borderRadius: '12px', border: 'none', boxShadow: '0 4px 6px -1px rgb(0 0 0 / 0.1)' }}
                                    />
                                    <Area type="monotone" dataKey="amount" stroke="#10b981" fillOpacity={1} fill="url(#colorRevenue)" />
                                </AreaChart>
                            </ResponsiveContainer>
                        ) : <EmptyState message="No revenue data available" />}
                    </ChartCard>

                    <ChartCard title="Orders by Status">
                        {ordersByStatus.length > 0 ? (
                            <ResponsiveContainer width="100%" height={300}>
                                <PieChart>
                                    <Pie
                                        data={ordersByStatus}
                                        cx="50%"
                                        cy="50%"
                                        innerRadius={60}
                                        outerRadius={80}
                                        paddingAngle={5}
                                        dataKey="count"
                                    >
                                        {ordersByStatus.map((entry, index) => (
                                            <Cell key={`cell-${index}`} fill={COLORS[index % COLORS.length]} />
                                        ))}
                                    </Pie>
                                    <Tooltip contentStyle={{ borderRadius: '12px', border: 'none', boxShadow: '0 4px 6px -1px rgb(0 0 0 / 0.1)' }} />
                                    <Legend verticalAlign="bottom" height={36} />
                                </PieChart>
                            </ResponsiveContainer>
                        ) : <EmptyState message="No orders yet" />}
                    </ChartCard>
                </div>

                {/* Advanced Analytics Section */}
                <div className="pt-8 border-t border-slate-200">
                    <div className="flex items-center gap-2 mb-6">
                        <TrendingUp className="w-6 h-6 text-brand-600" />
                        <h2 className="text-xl font-bold text-slate-900">Advanced Analytics</h2>
                        {isAdvanced && <span className="text-xs px-2 py-1 bg-brand-100 text-brand-700 rounded-full font-medium">PRO</span>}
                    </div>

                    {!isAdvanced ? (
                        <UpgradeLockedSection />
                    ) : (
                        <div className="space-y-6">
                            <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
                                <ChartCard title="Sales by Country">
                                    {salesByCountry.length > 0 ? (
                                        <ResponsiveContainer width="100%" height={300}>
                                            <BarChart data={salesByCountry} layout="vertical">
                                                <CartesianGrid strokeDasharray="3 3" horizontal={false} stroke="#e2e8f0" />
                                                <XAxis type="number" hide />
                                                <YAxis dataKey="country" type="category" width={100} tick={{ fontSize: 12 }} />
                                                <Tooltip />
                                                <Bar dataKey="sales" fill="#6366f1" radius={[0, 4, 4, 0]} />
                                            </BarChart>
                                        </ResponsiveContainer>
                                    ) : <EmptyState message="No international sales yet" />}
                                </ChartCard>

                                <ChartCard title="Top Performing Products">
                                    {topProducts.length > 0 ? (
                                        <ResponsiveContainer width="100%" height={300}>
                                            <BarChart data={topProducts}>
                                                <CartesianGrid strokeDasharray="3 3" vertical={false} stroke="#e2e8f0" />
                                                <XAxis dataKey="name" tick={{ fontSize: 10 }} interval={0} angle={-15} textAnchor="end" height={60} />
                                                <YAxis />
                                                <Tooltip />
                                                <Bar dataKey="revenue" fill="#8b5cf6" radius={[4, 4, 0, 0]} />
                                            </BarChart>
                                        </ResponsiveContainer>
                                    ) : <EmptyState message="No product data available" />}
                                </ChartCard>
                            </div>
                        </div>
                    )}
                </div>
            </div>
        </div>
    );
};

export default SellerDashboardPage;
