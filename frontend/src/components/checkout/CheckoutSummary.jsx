import React from 'react';
import { formatMoney } from '../../utils/formatMoney';
import { Loader2 } from 'lucide-react';

const CheckoutSummary = ({ cart, loading, onSubmit, buttonText = "Confirm Order" }) => {
    return (
        <div className="bg-white rounded-2xl border border-slate-200 shadow-sm p-6 sticky top-24">
            <h3 className="text-lg font-bold text-slate-900 mb-4">In Your Cart</h3>

            <div className="space-y-4 max-h-[300px] overflow-y-auto pr-2 mb-6 custom-scrollbar">
                {cart.items.map(item => (
                    <div key={item.id} className="flex gap-4 py-2 border-b border-slate-50 last:border-0">
                        <div className="w-16 h-16 bg-slate-100 rounded-md overflow-hidden flex-shrink-0">
                            <img src={item.thumbnailUrl} alt={item.productName} className="w-full h-full object-cover" />
                        </div>
                        <div className="flex-1">
                            <p className="text-sm font-medium text-slate-900 line-clamp-1">{item.productName}</p>
                            <p className="text-xs text-slate-500 mb-1">{item.quantity} x {formatMoney(item.unitPricePaise)}</p>
                            <div className="flex justify-between items-center bg-slate-50 px-2 py-1 rounded text-xs">
                                <span className="text-slate-600">{item.shippingMode}</span>
                                <span className="font-semibold text-slate-900">{formatMoney(item.lineTotalPaise)}</span>
                            </div>
                        </div>
                    </div>
                ))}
            </div>

            <div className="space-y-3 pt-4 border-t border-slate-100">
                <div className="flex justify-between text-slate-600">
                    <span>Subtotal</span>
                    <span className="font-medium text-slate-900">{formatMoney(cart.subtotalPaise)}</span>
                </div>
                <div className="flex justify-between text-slate-600">
                    <span>Estimated Shipping</span>
                    <span className="text-slate-400 italic">Calculated next</span>
                </div>
            </div>

            <button
                onClick={onSubmit}
                disabled={loading}
                className="w-full mt-6 flex items-center justify-center gap-2 bg-brand-600 text-white font-bold py-3.5 rounded-xl hover:bg-brand-700 transition-all shadow-brand-sm disabled:opacity-70 disabled:cursor-not-allowed"
            >
                {loading && <Loader2 size={18} className="animate-spin" />}
                {buttonText}
            </button>
        </div>
    );
};

export default CheckoutSummary;
