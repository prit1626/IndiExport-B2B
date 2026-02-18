import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import useCartStore from '../../store/cartStore';
import checkoutApi from '../../api/checkoutApi';
import AddressForm from '../../components/checkout/AddressForm';
import CheckoutSummary from '../../components/checkout/CheckoutSummary';
import TotalsBreakdown from '../../components/checkout/TotalsBreakdown';
import ShippingModeSelect from '../../components/cart/ShippingModeSelect';
import { toast } from 'react-hot-toast';
import { ArrowLeft } from 'lucide-react';

const CheckoutPage = () => {
    const navigate = useNavigate();
    const { cart, fetchCart } = useCartStore();
    const [loading, setLoading] = useState(false);
    const [checkoutData, setCheckoutData] = useState(null); // Response from backend
    const [shippingMode, setShippingMode] = useState('AIR');

    useEffect(() => {
        if (!cart) {
            fetchCart();
        }
    }, [cart, fetchCart]);

    useEffect(() => {
        // Redirect if cart empty
        if (cart && cart.items.length === 0) {
            navigate('/buyer/cart');
        }
    }, [cart, navigate]);

    const handleFormSubmit = async (formData) => {
        setLoading(true);
        try {
            // Backend CheckoutRequest expects:
            // shippingAddress: { address (line1), city, state, postalCode, country }
            // shippingMode: AIR | SEA | ...
            // buyerCurrency: "INR" (normalization happens in backend)

            const payload = {
                shippingAddress: {
                    address: formData.line1 + (formData.line2 ? ", " + formData.line2 : ""),
                    city: formData.city,
                    state: formData.state,
                    postalCode: formData.postalCode,
                    country: formData.country
                },
                shippingMode: shippingMode,
                buyerCurrency: 'INR' // Defaulting to INR for now as it's the base
            };

            const response = await checkoutApi.createCheckout(payload);
            setCheckoutData(response.data);
            toast.success("Order Created Successfully!");
        } catch (error) {
            console.error(error);
            const msg = error.response?.data?.message || "Checkout failed. Please try again.";
            toast.error(msg);
        } finally {
            setLoading(false);
        }
    };

    const handleProceedToPayment = () => {
        // Find the first order ID from the created orders list
        const firstOrderId = checkoutData?.orders?.[0]?.orderId;
        if (firstOrderId) {
            navigate(`/buyer/orders/${firstOrderId}/pay`);
        } else {
            console.error("No order ID found in checkout data", checkoutData);
            toast.error("Could not find order to pay for. Please check your orders list.");
        }
    };

    if (!cart) return <div className="p-8 text-center">Loading checkout...</div>;

    if (checkoutData) {
        return (
            <div className="min-h-screen bg-slate-50 py-12 px-4">
                <div className="container mx-auto max-w-3xl">
                    <TotalsBreakdown
                        checkoutResponse={checkoutData}
                        onProceed={handleProceedToPayment}
                    />
                </div>
            </div>
        );
    }

    return (
        <div className="min-h-screen bg-slate-50 pb-20">
            <div className="container mx-auto px-4 py-8">
                <button
                    onClick={() => navigate('/buyer/cart')}
                    className="flex items-center gap-2 text-slate-500 hover:text-brand-600 mb-8 transition-colors"
                >
                    <ArrowLeft size={18} />
                    Back to Cart
                </button>

                <h1 className="text-3xl font-bold text-slate-900 mb-8">Checkout</h1>

                <div className="flex flex-col lg:flex-row gap-8 items-start">
                    {/* Left Column: Form & Settings */}
                    <div className="flex-1 w-full bg-white rounded-2xl border border-slate-200 p-8 shadow-sm">
                        {/* Shipping Mode Global Overwrite (Optional UX decision, prompt implied per-checkout mode) */}
                        <div className="mb-8 border-b border-slate-100 pb-8">
                            <h3 className="text-lg font-bold text-slate-900 mb-4">Shipping Preference</h3>
                            <p className="text-sm text-slate-500 mb-4">Select the primary shipping mode for this order used for the quote.</p>
                            <div className="max-w-xs">
                                <ShippingModeSelect value={shippingMode} onChange={setShippingMode} />
                            </div>
                        </div>

                        <AddressForm
                            onSubmit={handleFormSubmit}
                            disabled={loading}
                        />
                    </div>

                    {/* Right Column: Summary */}
                    <div className="w-full lg:w-96 flex-shrink-0">
                        <CheckoutSummary
                            cart={cart}
                            loading={loading}
                            onSubmit={() => document.getElementById('address-form').requestSubmit()}
                            buttonText="Calculated Shipping & Create Order"
                        />
                        <p className="text-xs text-slate-500 text-center mt-4">
                            By placing this order, you agree to our Terms of Service and Privacy Policy.
                        </p>
                    </div>
                </div>
            </div>
        </div>
    );
};

export default CheckoutPage;
