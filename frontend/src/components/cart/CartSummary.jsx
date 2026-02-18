import React from 'react';
import { ArrowRight, Lock } from 'lucide-react';
import { useNavigate } from 'react-router-dom';
import { formatMoney } from '../../utils/formatMoney';

const CartSummary = ({ cart }) => {
    const navigate = useNavigate();

    // Check if any item prevents checkout
    // Assuming backend might send `isAvailable` or similar flags, or we can check productActive
    const hasIssues = cart.items.some(item => !item.productActive);

    const handleCheckout = () => {
        navigate('/buyer/checkout');
    };

    return (
        <div className="bg-white rounded-2xl border border-slate-200 p-6 shadow-sm h-fit sticky top-24">
            <h3 className="text-lg font-bold text-slate-900 mb-6">Order Summary</h3>

            <div className="space-y-4 mb-6">
                <div className="flex justify-between text-slate-600">
                    <span>Subtotal ({cart.totalItems} items)</span>
                    <span className="font-medium text-slate-900">{formatMoney(cart.subtotalPaise)}</span>
                </div>
                <div className="flex justify-between text-slate-600">
                    <span>Shipping Estimate</span>
                    <span className="text-slate-400 italic">Calculated at checkout</span>
                </div>

                <div className="border-t border-slate-100 pt-4 flex justify-between items-end">
                    <span className="font-bold text-slate-900">Total</span>
                    <div className="text-right">
                        <span className="block text-xl font-bold text-brand-700">{formatMoney(cart.grandTotalPaise)}</span>
                        {/* <span className="text-xs text-slate-500">Including taxes</span> */}
                    </div>
                </div>
            </div>

            {hasIssues && (
                <div className="mb-4 bg-amber-50 border border-amber-200 rounded-lg p-3 text-sm text-amber-700 flex gap-2">
                    <Lock size={16} className="mt-0.5 flex-shrink-0" />
                    Some items are unavailable. Please remove them to proceed.
                </div>
            )}

            <button
                onClick={handleCheckout}
                disabled={hasIssues || cart.items.length === 0}
                className="w-full flex items-center justify-center gap-2 bg-brand-600 hover:bg-brand-700 disabled:bg-slate-300 disabled:cursor-not-allowed text-white font-bold py-3.5 rounded-xl transition-all shadow-brand-sm hover:shadow-brand-md"
            >
                Proceed to Checkout
                <ArrowRight size={18} />
            </button>

            <div className="mt-4 flex items-center justify-center gap-2 text-xs text-slate-500">
                <Lock size={12} />
                Secure Checkout
            </div>
        </div>
    );
};

export default CartSummary;
