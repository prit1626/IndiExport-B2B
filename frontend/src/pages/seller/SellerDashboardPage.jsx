import React, { useEffect, useState } from 'react';
import axiosClient from '../../api/axiosClient';
import useAuth from '../../auth/useAuth';
import Loader from '../../components/common/Loader';
import { motion } from 'framer-motion';
import { Package, Smartphone, DollarSign, Star, TrendingUp } from 'lucide-react';

const StatCard = ({ title, value, icon: Icon, color }) => (
    <motion.div
        initial={{ opacity: 0, y: 20 }}
        animate={{ opacity: 1, y: 0 }}
        className="bg-white rounded-2xl p-6 shadow-sm border border-slate-100"
    >
        <div className="flex items-center justify-between">
            <div>
                <p className="text-sm font-medium text-slate-500">{title}</p>
                <p className="text-2xl font-bold text-slate-900 mt-2">{value}</p>
            </div>
            <div className={`p-3 rounded-xl ${color}`}>
                <Icon className="w-6 h-6 text-white" />
            </div>
        </div>
    </motion.div>
);

const SellerDashboardPage = () => {
    const { user } = useAuth();
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);
    const [data, setData] = useState(null);

    useEffect(() => {
        const fetchDashboardData = async () => {
            try {
                setLoading(true);
                // Correct endpoint: plural 'sellers'
                const response = await axiosClient.get('/sellers/dashboard');
                setData(response.data);
            } catch (err) {
                console.error("Dashboard fetch error:", err);
                setError("Failed to load dashboard data. Please try again.");
            } finally {
                setLoading(false);
            }
        };

        fetchDashboardData();
    }, []);

    if (loading) {
        return (
            <div className="flex h-96 items-center justify-center">
                <Loader />
            </div>
        );
    }

    if (error) {
        return (
            <div className="p-6">
                <div className="bg-red-50 text-red-600 p-4 rounded-xl border border-red-100">
                    {error}
                </div>
            </div>
        );
    }

    return (
        <div className="p-6 space-y-6">
            <div className="flex justify-between items-center">
                <h1 className="text-2xl font-bold text-slate-900">
                    Seller Dashboard
                </h1>
                <div className="text-sm text-slate-500">
                    {user?.companyName || 'My Company'}
                </div>
            </div>

            <div className="grid grid-cols-1 md:grid-cols-4 gap-6">
                <StatCard
                    title="Active Products"
                    value={data?.activeProducts || 0}
                    icon={Package} // Use Package for products
                    color="bg-blue-500"
                />
                <StatCard
                    title="Total Revenue"
                    value={`$${(data?.totalRevenue || 0).toLocaleString()}`}
                    icon={DollarSign}
                    color="bg-emerald-500"
                />
                <StatCard
                    title="Total Orders"
                    value={data?.ordersCount || 0}
                    icon={TrendingUp}
                    color="bg-indigo-500"
                />
                <StatCard
                    title="Avg Rating"
                    value={data?.averageRating || 0.0}
                    icon={Star}
                    color="bg-amber-500"
                />
            </div>

            {/* Placeholder for Recent Orders Table */}
            <motion.div
                initial={{ opacity: 0 }}
                animate={{ opacity: 1 }}
                transition={{ delay: 0.2 }}
                className="bg-white rounded-2xl shadow-sm border border-slate-100 overflow-hidden"
            >
                <div className="p-6 border-b border-slate-100">
                    <h2 className="text-lg font-semibold text-slate-900">Recent Orders</h2>
                </div>
                <div className="p-6 text-center text-slate-500 py-12">
                    No recent orders found.
                </div>
            </motion.div>
        </div>
    );
};

export default SellerDashboardPage;
