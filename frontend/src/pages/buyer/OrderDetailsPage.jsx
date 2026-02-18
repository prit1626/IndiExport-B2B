import React, { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import orderApi from '../../api/orderApi';
import OrderTimeline from '../../components/orders/OrderTimeline';
import OrderStatusBadge from '../../components/orders/OrderStatusBadge';
import OrderItemsTable from '../../components/orders/OrderItemsTable';
import TotalsCard from '../../components/orders/TotalsCard';
import OrderDetailsSkeleton from '../../components/orders/OrderDetailsSkeleton';
import OrdersErrorState from '../../components/orders/OrdersErrorState';
import InvoiceButtons from '../../components/orders/InvoiceButtons';
import { formatShortDate, formatTime } from '../../utils/formatDate';
import { MapPin, Phone, Mail, Truck, CreditCard, ArrowLeft } from 'lucide-react';

const OrderDetailsPage = () => {
    const { id } = useParams();
    const navigate = useNavigate();

    const [order, setOrder] = useState(null);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);

    useEffect(() => {
        const fetchOrder = async () => {
            try {
                setLoading(true);
                const response = await orderApi.getOrderById(id);
                // Handle various response formats (raw body, axios response, or wrapped data)
                let orderData = response.data || response;
                if (orderData && orderData.data && !orderData.shippingAddress) {
                    orderData = orderData.data;
                }
                console.log("Fetched order data details:", orderData);
                setOrder(orderData);
            } catch (err) {
                console.error("Failed to fetch order details:", err);
                setError(err.response?.data?.message || "Failed to load order details");
            } finally {
                setLoading(false);
            }
        };
        fetchOrder();
    }, [id]);

    // Fetched via useEffect


    if (loading) return (
        <div className="container mx-auto px-4 py-8 max-w-5xl">
            <OrderDetailsSkeleton />
        </div>
    );

    if (error) return (
        <div className="container mx-auto px-4 py-8 max-w-5xl">
            <OrdersErrorState message={error} onRetry={() => window.location.reload()} />
        </div>
    );

    if (!order) return null;

    return (
        <div className="min-h-screen bg-slate-50 py-8">
            <div className="container mx-auto px-4 max-w-5xl">

                {/* Header */}
                <div className="flex flex-col md:flex-row justify-between items-start md:items-center gap-4 mb-6">
                    <div className="flex items-center gap-3">
                        <button onClick={() => navigate('/buyer/orders')} className="p-2 hover:bg-slate-200 rounded-full transition-colors text-slate-500">
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
                        {/* Pay Button */}
                        {(order.status === 'CREATED' || order.status === 'PENDING_CONFIRMATION') && (
                            <button
                                onClick={() => navigate(`/buyer/orders/${order.id}/pay`)}
                                className="flex items-center gap-2 bg-brand-600 text-white px-4 py-2 rounded-lg font-medium hover:bg-brand-700 transition-shadow shadow-sm active:scale-95"
                            >
                                <CreditCard size={18} />
                                Pay Now
                            </button>
                        )}

                        {/* Track Button */}
                        {(order.status === 'SHIPPED' || order.status === 'IN_TRANSIT' || order.status === 'DELIVERED') && (
                            <button
                                onClick={() => navigate(`/buyer/orders/${order.id}/tracking`)}
                                className="flex items-center gap-2 bg-white text-slate-700 px-4 py-2 rounded-lg border border-slate-300 font-medium hover:bg-slate-50 transition-colors"
                            >
                                <Truck size={18} />
                                Track Order
                            </button>
                        )}

                        {/* Invoice Buttons */}
                        <InvoiceButtons order={order} />
                    </div>
                </div>

                <div className="flex flex-col lg:flex-row gap-8">
                    {/* Left Column: Timeline & Items */}
                    <div className="flex-1 space-y-6">
                        {/* Timeline */}
                        <div className="bg-white p-6 rounded-xl border border-slate-200 shadow-sm">
                            <OrderTimeline currentStatus={order.status} />
                        </div>

                        {/* Items */}
                        <div className="bg-white rounded-xl border border-slate-200 shadow-sm overflow-hidden">
                            <div className="px-6 py-4 border-b border-slate-100 bg-slate-50/50">
                                <h3 className="font-semibold text-slate-900">Order Items</h3>
                            </div>
                            <div className="p-6 pt-2">
                                <OrderItemsTable
                                    items={order.items || []}
                                    currency={order.currencySnapshot?.buyerCurrency || 'INR'}
                                />
                            </div>
                        </div>

                        {/* Mobile Shopping Info (Visible only on small screens if moved) - Keeping specific layout for now */}
                    </div>

                    {/* Right Column: Address & Totals */}
                    <div className="w-full lg:w-96 space-y-6">

                        {/* Shipping Address */}
                        <div className="bg-white rounded-xl border border-slate-200 shadow-sm p-6">
                            <h3 className="font-semibold text-slate-900 mb-4 flex items-center gap-2">
                                <MapPin size={18} className="text-slate-400" />
                                Shipping Details
                            </h3>
                            <div className="space-y-3 text-sm text-slate-600">
                                <p className="font-medium text-slate-900">{order.shippingAddress?.fullName || 'N/A'}</p>
                                <p>{order.shippingAddress?.streetAddress || ''}</p>
                                <p>
                                    {order.shippingAddress?.city || ''}
                                    {order.shippingAddress?.state ? `, ${order.shippingAddress.state}` : ''}
                                    {order.shippingAddress?.postalCode ? ` ${order.shippingAddress.postalCode}` : ''}
                                </p>
                                <p>{order.shippingAddress?.country || ''}</p>

                                <div className="pt-3 border-t border-slate-100 space-y-2">
                                    <div className="flex items-center gap-2">
                                        <Phone size={14} className="text-slate-400" />
                                        <span>{order.shippingAddress?.phone || 'N/A'}</span>
                                    </div>
                                    <div className="flex items-center gap-2">
                                        <Mail size={14} className="text-slate-400" />
                                        <span>{order.buyer?.email || 'N/A'}</span>
                                    </div>
                                </div>
                            </div>
                        </div>

                        {/* Shipping Info */}
                        <div className="bg-white rounded-xl border border-slate-200 shadow-sm p-6">
                            <h3 className="font-semibold text-slate-900 mb-4 flex items-center gap-2">
                                <Truck size={18} className="text-slate-400" />
                                Shipping Mode
                            </h3>
                            <div className="flex justify-between items-center text-sm">
                                <span className="text-slate-600">Mode</span>
                                <span className="font-medium text-slate-900">{order.shipping?.shippingMode || 'Standard'}</span>
                            </div>
                            {order.shipping?.courier && (
                                <div className="flex justify-between items-center text-sm mt-2">
                                    <span className="text-slate-600">Courier</span>
                                    <span className="font-medium text-slate-900">{order.shipping.courier}</span>
                                </div>
                            )}
                            {order.shipping?.trackingNumber && (
                                <div className="mt-3 pt-3 border-t border-slate-100">
                                    <p className="text-xs text-slate-500 mb-1">Tracking Number</p>
                                    <p className="font-mono text-sm bg-slate-100 p-1.5 rounded text-slate-700 select-all">
                                        {order.shipping.trackingNumber}
                                    </p>
                                </div>
                            )}
                        </div>

                        {/* Totals */}
                        <div className="shadow-sm border border-slate-200 rounded-xl overflow-hidden">
                            <TotalsCard
                                totals={order.totals || {}}
                                currencySnapshot={order.currencySnapshot}
                            />
                        </div>

                    </div>
                </div>
            </div>
        </div>
    );
};

export default OrderDetailsPage;
