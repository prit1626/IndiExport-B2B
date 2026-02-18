import React, { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import orderApi from '../../api/orderApi';
import OrderPaymentSummary from '../../components/payment/OrderPaymentSummary';
import RazorpayPayButton from '../../components/payment/RazorpayPayButton';
import PaymentSkeleton from '../../components/payment/PaymentSkeleton';
import ErrorState from '../../components/analytics/ErrorState';
import { ShieldCheck, CheckCircle, ArrowRight } from 'lucide-react';

const PaymentPage = () => {
    const { id } = useParams();
    const navigate = useNavigate();

    const [order, setOrder] = useState(null);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);

    // Fetch Order Details
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
                console.log("Fetched order data for payment:", orderData);
                setOrder(orderData);
            } catch (err) {
                console.error("Order fetch error:", err);
                setError(err.response?.data?.message || "Failed to load order details.");
            } finally {
                setLoading(false);
            }
        };
        fetchOrder();
    }, [id]);

    if (loading) return <PaymentSkeleton />;

    if (error) return (
        <div className="min-h-screen py-20 flex justify-center bg-slate-50">
            <ErrorState title="Payment Error" message={error} onRetry={() => window.location.reload()} />
        </div>
    );

    if (!order) return null;

    if (order?.status === 'PAID') {
        return (
            <div className="min-h-screen bg-slate-50 flex items-center justify-center p-4">
                <div className="bg-white p-8 rounded-2xl shadow-sm text-center max-w-md w-full border border-slate-200 animate-fade-in">
                    <div className="w-16 h-16 bg-green-100 text-green-600 rounded-full flex items-center justify-center mx-auto mb-4">
                        <CheckCircle size={32} />
                    </div>
                    <h2 className="text-2xl font-bold text-slate-900 mb-2">Payment Completed</h2>
                    <p className="text-slate-500 mb-6">Thank you! Your order has been successfully paid.</p>
                    <button
                        onClick={() => navigate(`/buyer/orders/${order.id}`)}
                        className="w-full flex items-center justify-center gap-2 bg-brand-600 text-white font-bold py-3 rounded-xl hover:bg-brand-700 transition-colors"
                    >
                        View Order Details
                        <ArrowRight size={18} />
                    </button>
                </div>
            </div>
        );
    }

    return (
        <div className="min-h-screen bg-slate-50 pb-20">
            {/* Header */}
            <div className="bg-white border-b border-slate-200 mb-8 sticky top-0 z-10">
                <div className="container mx-auto px-4 py-4 flex items-center gap-2 text-slate-600">
                    <ShieldCheck size={20} className="text-green-600" />
                    <span className="font-semibold text-slate-900">Secure Payment</span>
                    <span className="text-slate-300">|</span>
                    <span className="text-sm">Order #{order?.orderNumber || id.slice(0, 8)}</span>
                </div>
            </div>

            <div className="container mx-auto px-4">
                <div className="flex flex-col lg:flex-row gap-8 lg:gap-12 items-start max-w-5xl mx-auto">

                    {/* Left Panel: Payment Actions */}
                    <div className="flex-1 w-full order-2 lg:order-1 space-y-6">
                        <div className="bg-white rounded-2xl border border-slate-200 p-6 sm:p-8 shadow-sm">
                            <h2 className="text-xl font-bold text-slate-900 mb-2">Complete Your Payment</h2>
                            <p className="text-slate-500 mb-8 text-sm">Select a payment method to proceed securely.</p>

                            <div className="space-y-4">
                                <RazorpayPayButton
                                    order={order}
                                    onPaymentSuccess={() => navigate(`/buyer/orders/${order.id}`)}
                                />

                                <div className="flex items-center justify-center gap-4 py-4 opacity-60 grayscale hover:grayscale-0 transition-all">
                                    <img src="https://razorpay.com/assets/razorpay-glyph.svg" alt="Razorpay" className="h-6" />
                                    <span className="text-xs text-slate-400">Trusted by 5M+ Businesses</span>
                                </div>
                            </div>
                        </div>

                        {/* Security Badge */}
                        <div className="bg-slate-100 rounded-xl p-4 flex items-start gap-3 border border-slate-200">
                            <ShieldCheck className="text-slate-400 flex-shrink-0" size={20} />
                            <div>
                                <h4 className="text-sm font-semibold text-slate-700">100% Secure Payment</h4>
                                <p className="text-xs text-slate-500 mt-1">
                                    Your payment information is encrypted and processed securely by Razorpay. We do not store your card details.
                                </p>
                            </div>
                        </div>
                    </div>

                    {/* Right Panel: Summary */}
                    <div className="w-full lg:w-96 flex-shrink-0 order-1 lg:order-2">
                        <OrderPaymentSummary order={order} />
                    </div>

                </div>
            </div>
        </div>
    );
};

export default PaymentPage;
