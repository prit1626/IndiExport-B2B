import React, { useState } from 'react';
import { toast } from 'react-hot-toast';
import { Lock, Loader2, CreditCard } from 'lucide-react';
import loadRazorpayScript from '../../utils/loadRazorpay';
import paymentApi from '../../api/paymentApi';
import { useNavigate } from 'react-router-dom';

const RazorpayPayButton = ({ order, onPaymentSuccess }) => {
    const [loading, setLoading] = useState(false);
    const navigate = useNavigate();

    const handlePayment = async () => {
        setLoading(true);
        try {
            // 1. Load Script
            const res = await loadRazorpayScript();
            if (!res) {
                toast.error('Razorpay SDK failed to load. Are you online?');
                setLoading(false);
                return;
            }

            // 2. Create Order on Backend
            const { data } = await paymentApi.createPaymentOrder(order.id);

            if (!data.razorpay) {
                throw new Error("Invalid response from server");
            }

            const { key, amountMinor, currency, razorpayOrderId, buyerName, buyerEmail, buyerPhone, notes } = data.razorpay;

            // 3. Open Razorpay Checktout
            const options = {
                key: key,
                amount: amountMinor,
                currency: currency,
                name: "IndiExport",
                description: `Order #${order?.orderNumber || order?.id?.slice(0, 8) || '...'}`,
                image: "/logo.png", // Ensure you have a logo or remove this
                order_id: razorpayOrderId,
                handler: async function (response) {
                    try {
                        // 4. Verify Payment on Backend
                        const verifyPayload = {
                            razorpayPaymentId: response.razorpay_payment_id,
                            razorpayOrderId: response.razorpay_order_id,
                            razorpaySignature: response.razorpay_signature
                        };

                        await paymentApi.verifyPayment(order?.id, verifyPayload);

                        toast.success("Payment Successful!");
                        if (onPaymentSuccess) {
                            onPaymentSuccess();
                        } else {
                            navigate(`/buyer/orders/${order?.id}`);
                        }
                    } catch (err) {
                        console.error(err);
                        toast.error("Payment verification failed. Please contact support.");
                    }
                },
                prefill: {
                    name: buyerName || order?.shippingAddress?.fullName || "",
                    email: buyerEmail || "",
                    contact: buyerPhone || order?.shippingAddress?.phone || ""
                },
                notes: notes,
                theme: {
                    color: "#0f172a"
                },
                modal: {
                    ondismiss: function () {
                        setLoading(false);
                        toast("Payment cancelled");
                    }
                }
            };

            const rzp1 = new window.Razorpay(options);
            rzp1.on('payment.failed', function (response) {
                toast.error(response.error.description || "Payment failed");
                setLoading(false);
            });
            rzp1.open();

        } catch (error) {
            console.error(error);
            toast.error(error.response?.data?.message || "Failed to initiate payment");
            setLoading(false);
        }
    };

    return (
        <button
            onClick={handlePayment}
            disabled={loading}
            className="w-full flex items-center justify-center gap-2 bg-brand-600 text-white font-bold py-4 rounded-xl hover:bg-brand-700 transition-all shadow-brand-md hover:shadow-brand-lg disabled:opacity-70 disabled:cursor-not-allowed group"
        >
            {loading ? (
                <>
                    <Loader2 size={20} className="animate-spin" />
                    Processing...
                </>
            ) : (
                <>
                    <CreditCard size={20} className="group-hover:scale-110 transition-transform" />
                    Pay with Razorpay
                </>
            )}
        </button>
    );
};

export default RazorpayPayButton;
