import React, { useEffect, useState } from 'react';
import useCartStore from '../../store/cartStore';
import useAuthStore from '../../store/authStore';
import profileApi from '../../api/profileApi';
import CartItemCard from '../../components/cart/CartItemCard';
import CartSummary from '../../components/cart/CartSummary';
import CartSkeleton from '../../components/cart/CartSkeleton';
import EmptyCart from '../../components/cart/EmptyCart';
import ErrorState from '../../components/analytics/ErrorState';
import { AnimatePresence } from 'framer-motion';

const CartPage = () => {
    const { cart, loading, error, fetchCart } = useCartStore();
    const { user, isAuthenticated } = useAuthStore();
    const [preferredCurrency, setPreferredCurrency] = useState('INR');

    useEffect(() => {
        fetchCart();
    }, []);

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

    if (loading && !cart) return <CartSkeleton />;

    if (error) return (
        <div className="container mx-auto px-4 py-16 flex justify-center">
            <ErrorState
                title="Failed to load cart"
                message={error}
                onRetry={fetchCart}
            />
        </div>
    );

    if (!cart || cart.items.length === 0) {
        return (
            <div className="container mx-auto px-4 py-8">
                <h1 className="text-3xl font-bold text-slate-900 mb-8">Shopping Cart</h1>
                <EmptyCart />
            </div>
        );
    }

    return (
        <div className="bg-slate-50 min-h-screen pb-20">
            <div className="container mx-auto px-4 py-8">
                <h1 className="text-3xl font-bold text-slate-900 mb-8">Shopping Cart</h1>

                <div className="flex flex-col lg:flex-row gap-8 items-start">
                    {/* Items List */}
                    <div className="flex-1 w-full space-y-4">
                        <AnimatePresence>
                            {cart.items.map(item => (
                                <CartItemCard key={item.id} item={item} preferredCurrency={preferredCurrency} />
                            ))}
                        </AnimatePresence>
                    </div>

                    {/* Summary Sidebar */}
                    <div className="w-full lg:w-96 flex-shrink-0">
                        <CartSummary cart={cart} preferredCurrency={preferredCurrency} />
                    </div>
                </div>
            </div>
        </div>
    );
};

export default CartPage;
