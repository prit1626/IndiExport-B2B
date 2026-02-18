import React, { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import sellerOrderApi from '../../api/sellerOrderApi';
import OrderTimeline from '../../components/orders/OrderTimeline';
import OrderStatusBadge from '../../components/orders/OrderStatusBadge';
import OrderItemsTable from '../../components/orders/OrderItemsTable';
import TotalsCard from '../../components/orders/TotalsCard';
import OrderDetailsSkeleton from '../../components/orders/OrderDetailsSkeleton'; // Reuse buyer skeleton
import OrdersErrorState from '../../components/orders/OrdersErrorState';
import TrackingUploadModal from '../../components/sellerOrders/TrackingUploadModal';
import { formatShortDate, formatTime } from '../../utils/formatDate';
import { MapPin, Phone, Mail, Truck, ArrowLeft, Package, User } from 'lucide-react';

const SellerOrderDetailsPage = () => {
    const { id } = useParams();
    const navigate = useNavigate();

    const [order, setOrder] = useState(null);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);
    const [isTrackingModalOpen, setIsTrackingModalOpen] = useState(false);
    const [trackingInfo, setTrackingInfo] = useState(null);

    const fetchOrder = async () => {
        try {
            setLoading(true);
            const { data } = await sellerOrderApi.sellerGetOrderById(id);
            setOrder(data);

            // Try fetch tracking if status implies it exists
            if (['SHIPPED', 'IN_TRANSIT', 'DELIVERED'].includes(data.status)) {
                try {
                    const trackRes = await sellerOrderApi.sellerGetTracking(id);
                    setTrackingInfo(trackRes.data);
                } catch (e) {
                    // Ignore tracking fetch error if not found
                }
            }
        } catch (err) {
            console.error("Failed to fetch seller order details:", err);
            setError(err.response?.data?.message || "Failed to load order details");
        } finally {
            setLoading(false);
        }
    };

    useEffect(() => {
        fetchOrder();
        // eslint-disable-next-line react-hooks/exhaustive-deps
    }, [id]);

    const handleTrackingSuccess = () => {
        fetchOrder(); // Refresh order to see status change if any
    };

    if (loading) return (
        <div className="container mx-auto px-4 py-8 max-w-5xl">
            <OrderDetailsSkeleton />
        </div>
    );

    if (error) return (
        <div className="container mx-auto px-4 py-8 max-w-5xl">
            <OrdersErrorState message={error} onRetry={fetchOrder} />
        </div>
    );

    if (!order) return null;

    const shippingAddress = order.shippingAddress || {}; // Fallback

    const canUploadTracking = ['PAID', 'PROCESSING', 'SHIPPED', 'IN_TRANSIT'].includes(order.status);

    return (
        <div className="min-h-screen bg-slate-50 py-8">
            <div className="container mx-auto px-4 max-w-5xl">

                {/* Header */}
                <div className="flex flex-col md:flex-row justify-between items-start md:items-center gap-4 mb-6">
                    <div className="flex items-center gap-3">
                        <button onClick={() => navigate('/seller/orders')} className="p-2 hover:bg-slate-200 rounded-full transition-colors text-slate-500">
                            <ArrowLeft size={20} />
                        </button>
                        <div>
                            <div className="flex items-center gap-3">
                                <h1 className="text-2xl font-bold text-slate-900">Order #{order.orderNumber || order.id?.slice(0, 8) || '...'}</h1>
                                <OrderStatusBadge status={order.status} />
                            </div>
                            <p className="text-sm text-slate-500 mt-1">
                                Placed on {formatShortDate(order.createdAt)} at {formatTime(order.createdAt)}
                            </p>
                        </div>
                    </div>

                    <div className="flex gap-3">
                        {canUploadTracking && (
                            <button
                                onClick={() => setIsTrackingModalOpen(true)}
                                className="flex items-center gap-2 bg-brand-600 text-white px-4 py-2 rounded-lg font-medium hover:bg-brand-700 transition-shadow shadow-sm active:scale-95"
                            >
                                <Truck size={18} />
                                {trackingInfo ? 'Update Tracking' : 'Upload Tracking'}
                            </button>
                        )}
                    </div>
                </div>

                <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
                    {/* Left Column: Details */}
                    <div className="lg:col-span-2 space-y-6">

                        {/* Items */}
                        <div className="bg-white rounded-xl border border-slate-200 overflow-hidden shadow-sm">
                            <div className="px-6 py-4 border-b border-slate-100 flex justify-between items-center">
                                <h3 className="font-semibold text-slate-900 flex items-center gap-2">
                                    <Package size={18} className="text-slate-400" />
                                    Order Items
                                </h3>
                                <span className="text-sm text-slate-500">{order.itemCount} items</span>
                            </div>
                            <div className="p-6">
                                <OrderItemsTable items={order.items || []} currency={order.currency} />
                            </div>
                        </div>

                        {/* Tracking Info Card (if exists) */}
                        {trackingInfo && (
                            <div className="bg-blue-50 rounded-xl border border-blue-100 p-6">
                                <h3 className="font-semibold text-blue-900 mb-3 flex items-center gap-2">
                                    <Truck size={18} />
                                    Shipping Information
                                </h3>
                                <div className="grid grid-cols-1 sm:grid-cols-2 gap-4 text-sm">
                                    <div>
                                        <p className="text-blue-500 text-xs uppercase font-medium">Courier</p>
                                        <p className="text-blue-900 font-medium">{trackingInfo.courier}</p>
                                    </div>
                                    <div>
                                        <p className="text-blue-500 text-xs uppercase font-medium">Tracking Number</p>
                                        <p className="text-blue-900 font-medium">{trackingInfo.trackingNumber}</p>
                                    </div>
                                    <div className="col-span-2">
                                        <p className="text-blue-500 text-xs uppercase font-medium">Status</p>
                                        <p className="text-blue-900">{trackingInfo.status}</p>
                                    </div>
                                </div>
                            </div>
                        )}

                        {/* Buyer Info */}
                        <div className="bg-white rounded-xl border border-slate-200 p-6 shadow-sm">
                            <h3 className="font-semibold text-slate-900 mb-4 flex items-center gap-2">
                                <User size={18} className="text-slate-400" />
                                Buyer Details
                            </h3>
                            <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
                                <div>
                                    <p className="text-xs text-slate-500 uppercase font-medium mb-1">Contact</p>
                                    <p className="font-medium text-slate-900">{shippingAddress.fullName || order.buyerName}</p>
                                    {shippingAddress.email && (
                                        <p className="text-slate-600 text-sm flex items-center gap-1 mt-1">
                                            <Mail size={14} /> {shippingAddress.email}
                                        </p>
                                    )}
                                    {shippingAddress.phone && (
                                        <p className="text-slate-600 text-sm flex items-center gap-1 mt-1">
                                            <Phone size={14} /> {shippingAddress.phone}
                                        </p>
                                    )}
                                </div>
                                <div>
                                    <p className="text-xs text-slate-500 uppercase font-medium mb-1">Shipping Address</p>
                                    <p className="text-slate-900 text-sm whitespace-pre-line">
                                        {shippingAddress.streetAddress}<br />
                                        {shippingAddress.city}, {shippingAddress.state} {shippingAddress.postalCode}<br />
                                        {shippingAddress.country}
                                    </p>
                                </div>
                            </div>
                        </div>
                    </div>

                    {/* Right Column: Totals & Timeline */}
                    <div className="space-y-6">
                        <TotalsCard totals={order.totals} currency={order.currency} />
                        <OrderTimeline status={order.status} createdAt={order.createdAt} />
                    </div>
                </div>
            </div>

            <TrackingUploadModal
                isOpen={isTrackingModalOpen}
                onClose={() => setIsTrackingModalOpen(false)}
                orderId={order.id}
                onSuccess={handleTrackingSuccess}
                existingTracking={trackingInfo}
            />
        </div>
    );
};

export default SellerOrderDetailsPage;
