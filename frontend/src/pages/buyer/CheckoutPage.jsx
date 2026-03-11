import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import useCartStore from '../../store/cartStore';
import useAuthStore from '../../store/authStore';
import checkoutApi from '../../api/checkoutApi';
import profileApi from '../../api/profileApi';
import AddressForm from '../../components/checkout/AddressForm';
import CheckoutSummary from '../../components/checkout/CheckoutSummary';
import TotalsBreakdown from '../../components/checkout/TotalsBreakdown';
import ShippingModeSelect from '../../components/cart/ShippingModeSelect';
import { toast } from 'react-hot-toast';
import { ArrowLeft } from 'lucide-react';

const CheckoutPage = () => {
    const navigate = useNavigate();
    const { cart, fetchCart } = useCartStore();
    const { user, isAuthenticated } = useAuthStore();
    const [loading, setLoading] = useState(false);
    const [checkoutData, setCheckoutData] = useState(null); // Response from backend
    const [shippingMode, setShippingMode] = useState('AIR');
    const [preferredCurrency, setPreferredCurrency] = useState('INR');

    useEffect(() => {
        if (!cart) {
            fetchCart();
        }
    }, [cart, fetchCart]);

    // Fetch buyer's preferred currency on mount
    useEffect(() => {
        if (isAuthenticated && user?.role === 'BUYER') {
            const fetchBuyerPreferences = async () => {
                try {
                    const response = await profileApi.getBuyerProfile();
                    if (response.data?.preferredCurrency) {
                        localStorage.setItem('preferredCurrency', response.data.preferredCurrency);
                        setPreferredCurrency(response.data.preferredCurrency);
                    }
                } catch (err) {
                    console.warn('Failed to fetch buyer preferences:', err);
                    // Silently fail - keep default INR
                }
            };
            fetchBuyerPreferences();
        }
    }, [isAuthenticated, user?.role]);

    useEffect(() => {
        // Redirect if cart empty
        if (cart && cart.items.length === 0) {
            navigate('/buyer/cart');
        }
    }, [cart, navigate]);

    const handleFormSubmit = async (formData) => {
        setLoading(true);
        try {
            // Validate cart and items exist
            if (!cart || !cart.items || cart.items.length === 0) {
                toast.error("Cart is empty. Please add items to cart.");
                setLoading(false);
                return;
            }

            // Log cart data for debugging
            console.log("Cart Items:", cart.items);
            console.log("Number of items:", cart.items.length);

            // Calculate total weight from cart items
            // Support multiple possible weight field locations with proper fallback logic
            let totalWeight = 0;
            
            try {
                totalWeight = cart.items.reduce((sum, item) => {
                    let itemWeight = 0.5; // Even lighter default fallback to 0.5 kg per item
                    let weightFound = false;

                    // Check for weight in various possible formats
                    if (item.weight && !isNaN(parseFloat(item.weight))) {
                        itemWeight = Math.max(parseFloat(item.weight), 0.1);
                        weightFound = true;
                        console.log(`Item Weight: ${item.weight} kg`);
                    } else if (item.weightGrams && !isNaN(item.weightGrams) && item.weightGrams > 0) {
                        itemWeight = Math.max(item.weightGrams / 1000, 0.1);
                        weightFound = true;
                        console.log(`Item Weight Grams: ${item.weightGrams}g → ${itemWeight}kg`);
                    } else if (item.productWeightGrams && !isNaN(item.productWeightGrams) && item.productWeightGrams > 0) {
                        itemWeight = Math.max(item.productWeightGrams / 1000, 0.1);
                        weightFound = true;
                        console.log(`Product Weight Grams: ${item.productWeightGrams}g → ${itemWeight}kg`);
                    } else if (item.product?.weight && !isNaN(parseFloat(item.product.weight))) {
                        itemWeight = Math.max(parseFloat(item.product.weight), 0.1);
                        weightFound = true;
                        console.log(`Product.weight: ${item.product.weight}kg`);
                    } else if (item.product?.weightGrams && !isNaN(item.product.weightGrams) && item.product.weightGrams > 0) {
                        itemWeight = Math.max(item.product.weightGrams / 1000, 0.1);
                        weightFound = true;
                        console.log(`Product.weightGrams: ${item.product.weightGrams}g → ${itemWeight}kg`);
                    }

                    if (!weightFound) {
                        console.log(`${item.productName}: No weight found, using default 0.5kg`);
                    }

                    const lineTotal = itemWeight * (item.quantity || 1);
                    console.log(`Item: ${item.productName}, Weight: ${itemWeight}kg, Qty: ${item.quantity || 1}, Total: ${lineTotal}kg`);
                    
                    return sum + lineTotal;
                }, 0);

                // Ensure totalWeight is at least minimum
                totalWeight = Math.max(totalWeight, 0.1);
                
            } catch (calcError) {
                console.error("Error calculating weight:", calcError);
                totalWeight = cart.items.length * 0.5; // Fallback: 0.5kg per item
            }

            console.log(`Total Weight Calculated: ${totalWeight}kg`);

            // Validate total weight is a valid positive number
            if (isNaN(totalWeight) || !isFinite(totalWeight) || totalWeight <= 0) {
                console.error(`Invalid totalWeight: ${totalWeight}`);
                toast.error("Unable to calculate valid cart weight. Please check cart items.");
                setLoading(false);
                return;
            }

            // Round to 2 decimal places
            const finalTotalWeight = parseFloat(totalWeight.toFixed(2));
            console.log(`Final Total Weight: ${finalTotalWeight} kg`);

            // Determine buyer currency based on country - support both "IN" and "INDIA"
            const countryValue = formData.country ? formData.country.toUpperCase() : '';
            const isIndia = countryValue === 'INDIA' || countryValue === 'IN' || countryValue === 'IND';
            const buyerCurrency = isIndia ? 'INR' : 'USD';
            console.log(`Country: ${formData.country}, Detected as India: ${isIndia}, Currency: ${buyerCurrency}`);

            // Build payload with ALL required fields
            const payload = {
                shippingAddress: {
                    address: formData.line1 + (formData.line2 ? ", " + formData.line2 : ""),
                    city: formData.city,
                    state: formData.state,
                    postalCode: formData.postalCode,
                    country: formData.country
                },
                shippingMode: shippingMode,
                buyerCurrency: buyerCurrency,
                totalWeight: Number(finalTotalWeight) // Explicitly convert to number
            };

            console.log("Checkout Payload being sent:", JSON.stringify(payload, null, 2));
            console.log("Total Weight Type:", typeof payload.totalWeight, "Value:", payload.totalWeight);

            const response = await checkoutApi.createCheckout(payload);
            console.log("Checkout Response:", response.data);
            setCheckoutData(response.data);
            toast.success("Order Created Successfully!");
        } catch (error) {
            console.error("Checkout Error Details:", {
                message: error.message,
                status: error.response?.status,
                statusText: error.response?.statusText,
                backendMessage: error.response?.data?.message,
                backendError: error.response?.data?.error,
                fullData: error.response?.data,
                config: error.config
            });
            
            // Provide more specific error messages based on status code
            let msg = "Checkout failed. Please try again.";
            if (error.response?.status === 503) {
                msg = error.response?.data?.message || "Service temporarily unavailable. The server is having issues processing your checkout.";
            } else if (error.response?.status === 400) {
                msg = error.response?.data?.message || "Invalid checkout data. Please check your address and try again.";
            } else if (error.response?.data?.message) {
                msg = error.response.data.message;
            }
            
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
