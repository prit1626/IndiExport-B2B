import React, { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import orderApi from '../../api/orderApi';
import TrackingTimeline from '../../components/orders/TrackingTimeline';
import TrackingSkeleton from '../../components/orders/TrackingSkeleton';
import OrdersErrorState from '../../components/orders/OrdersErrorState';
import { ArrowLeft, Package, Truck } from 'lucide-react';

const OrderTrackingPage = () => {
    const { id } = useParams();
    const navigate = useNavigate();

    const [tracking, setTracking] = useState(null);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);

    useEffect(() => {
        const fetchTracking = async () => {
            try {
                setLoading(true);
                const { data } = await orderApi.getBuyerOrderTracking(id);
                setTracking(data);
            } catch (err) {
                // If 404, usually means not shipped yet (or bad ID)
                if (err.response?.status === 404) {
                    setError("Tracking information not available yet. The order might not be shipped.");
                } else {
                    setError("Failed to load tracking information.");
                }
                console.error(err);
            } finally {
                setLoading(false);
            }
        };
        fetchTracking();
    }, [id]);

    return (
        <div className="min-h-screen bg-slate-50 py-8">
            <div className="container mx-auto px-4 max-w-3xl">
                <button
                    onClick={() => navigate(`/buyer/orders/${id}`)}
                    className="flex items-center gap-2 text-slate-500 hover:text-slate-700 mb-6 transition-colors"
                >
                    <ArrowLeft size={18} />
                    Back to Order Details
                </button>

                <div className="bg-white rounded-xl border border-slate-200 shadow-sm overflow-hidden">
                    {/* Header */}
                    <div className="p-6 border-b border-slate-100 bg-slate-50/50 flex items-center justify-between">
                        <div>
                            <h1 className="text-xl font-bold text-slate-900 flex items-center gap-2">
                                <Truck size={24} className="text-brand-600" />
                                Track Package
                            </h1>
                            <p className="text-sm text-slate-500 mt-1">
                                Order #{tracking?.orderId?.slice(0, 8) || id?.slice(0, 8) || '...'}
                            </p>
                        </div>
                        {tracking?.courier && (
                            <div className="text-right">
                                <p className="text-xs text-slate-500">Shipped via</p>
                                <p className="font-bold text-slate-900">{tracking.courier}</p>
                            </div>
                        )}
                    </div>

                    <div className="p-6">
                        {loading ? (
                            <TrackingSkeleton />
                        ) : error ? (
                            <OrdersErrorState message={error} onRetry={() => window.location.reload()} />
                        ) : (
                            <div>
                                <div className="mb-8">
                                    <h3 className="text-sm font-semibold text-slate-900 mb-2">Tracking Number</h3>
                                    <p className="font-mono text-lg bg-slate-100 inline-block px-3 py-1 rounded text-slate-700">
                                        {tracking.trackingNumber}
                                    </p>
                                    <p className="text-xs text-slate-400 mt-2">
                                        Current Status: <span className="font-medium text-slate-700">{tracking.status}</span>
                                    </p>
                                </div>

                                <div className="mt-8">
                                    <h3 className="text-sm font-semibold text-slate-900 mb-4 flex items-center gap-2">
                                        <Package size={16} />
                                        Shipment Updates
                                    </h3>
                                    <TrackingTimeline events={tracking.events} />
                                </div>
                            </div>
                        )}
                    </div>
                </div>
            </div>
        </div>
    );
};

export default OrderTrackingPage;
