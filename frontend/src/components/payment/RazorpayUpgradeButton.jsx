import React, { useState } from 'react';
import { toast } from 'react-hot-toast';
import { Sparkles, Loader2 } from 'lucide-react';
import loadRazorpayScript from '../../utils/loadRazorpay';
import plansApi from '../../api/plansApi';

const RazorpayUpgradeButton = ({ onUpgradeSuccess }) => {
    const [loading, setLoading] = useState(false);

    const handleUpgrade = async () => {
        setLoading(true);
        try {
            // 1. Load Script
            const res = await loadRazorpayScript();
            if (!res) {
                toast.error('Razorpay SDK failed to load. Are you online?');
                setLoading(false);
                return;
            }

            // 2. Create Upgrade Payment Order on Backend
            const { data } = await plansApi.initiateUpgrade();

            // Note: Use keyId, amount, etc. from response
            const { keyId, amount, currency, razorpayOrderId, buyerName, buyerEmail, buyerPhone, notes } = data;

            // 3. Open Razorpay Checkout
            const options = {
                key: keyId,
                amount: amount,
                currency: currency,
                name: "IndiExport",
                description: "Upgrade to Advanced Plan",
                image: "/logo.png",
                order_id: razorpayOrderId,
                handler: async function (response) {
                    try {
                        // 4. Verify Payment on Backend
                        const verifyPayload = {
                            razorpayPaymentId: response.razorpay_payment_id,
                            razorpayOrderId: response.razorpay_order_id,
                            razorpaySignature: response.razorpay_signature
                        };

                        await plansApi.verifyUpgrade(verifyPayload);

                        toast.success("Upgrade Successful! Welcome to Advanced Plan.");
                        if (onUpgradeSuccess) {
                            onUpgradeSuccess();
                        } else {
                            window.location.reload();
                        }
                    } catch (err) {
                        console.error(err);
                        toast.error("Upgrade verification failed. Please contact support.");
                    } finally {
                        setLoading(false);
                    }
                },
                prefill: {
                    name: buyerName || "",
                    email: buyerEmail || "",
                    contact: buyerPhone || ""
                },
                notes: notes,
                theme: {
                    color: "#0f172a"
                },
                modal: {
                    ondismiss: function () {
                        setLoading(false);
                        toast("Upgrade cancelled");
                    }
                }
            };

            const rzp1 = new window.Razorpay(options);
            rzp1.on('payment.failed', function (response) {
                toast.error(response.error.description || "Upgrade failed");
                setLoading(false);
            });
            rzp1.open();

        } catch (error) {
            console.error(error);
            toast.error(error.response?.data?.message || "Failed to initiate upgrade");
            setLoading(false);
        }
    };

    return (
        <button
            onClick={handleUpgrade}
            disabled={loading}
            className="w-full flex items-center justify-center gap-2 bg-brand-600 text-white font-bold py-4 rounded-xl hover:bg-brand-700 transition-all shadow-brand-md hover:shadow-brand-lg disabled:opacity-70 disabled:cursor-not-allowed group text-lg"
        >
            {loading ? (
                <>
                    <Loader2 size={24} className="animate-spin" />
                    Processing...
                </>
            ) : (
                <>
                    <Sparkles size={24} className="group-hover:scale-110 transition-transform" />
                    Upgrade to Advanced Now
                </>
            )}
        </button>
    );
};

export default RazorpayUpgradeButton;
