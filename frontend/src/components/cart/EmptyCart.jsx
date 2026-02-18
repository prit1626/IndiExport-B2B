import React from 'react';
import { ShoppingCart, ArrowRight } from 'lucide-react';
import { useNavigate } from 'react-router-dom';

const EmptyCart = () => {
    const navigate = useNavigate();

    return (
        <div className="min-h-[60vh] flex flex-col items-center justify-center p-8 text-center bg-white rounded-2xl border border-slate-200 shadow-sm mx-auto max-w-2xl mt-8">
            <div className="w-20 h-20 bg-slate-50 rounded-full flex items-center justify-center mb-6">
                <ShoppingCart size={40} className="text-slate-300" />
            </div>

            <h2 className="text-2xl font-bold text-slate-900 mb-2">Your cart is empty</h2>
            <p className="text-slate-500 max-w-md mb-8">
                Looks like you haven't added anything to your cart yet. Browse our products to find best Indian exports.
            </p>

            <button
                onClick={() => navigate('/products')}
                className="flex items-center gap-2 px-8 py-3 bg-brand-600 text-white rounded-xl font-semibold hover:bg-brand-700 transition-colors shadow-brand-md"
            >
                Browse Products
                <ArrowRight size={18} />
            </button>
        </div>
    );
};

export default EmptyCart;
