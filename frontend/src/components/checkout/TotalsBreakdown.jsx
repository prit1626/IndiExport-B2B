import React from 'react';
import { formatMoney } from '../../utils/formatMoney';
import { ArrowRight, Lock, Calendar } from 'lucide-react';
import { format } from 'date-fns';

const TotalsBreakdown = ({ checkoutResponse, onProceed }) => {
    const {
        orders = [],
        totalSubtotalPaise,
        totalShippingPaise,
        grandTotalPaise,
        grandTotalConverted,
        buyerCurrency,
        exchangeRateMicros,
        rateTimestamp
    } = checkoutResponse;

    // Aggregate shipping info from orders for the top-level display
    const firstOrder = orders[0];
    const shippingMode = firstOrder?.shippingMode || 'Unknown';
    const etaMin = firstOrder?.estimatedDeliveryDaysMin || 0;
    const etaMax = firstOrder?.estimatedDeliveryDaysMax || 0;

    const exchangeRate = exchangeRateMicros / 1000000;

    return (
        <div className="bg-white rounded-2xl border border-slate-200 shadow-lg p-8 animate-fade-in">
            <div className="text-center mb-8">
                <div className="w-16 h-16 bg-green-100 text-green-600 rounded-full flex items-center justify-center mx-auto mb-4">
                    <Lock size={32} />
                </div>
                <h2 className="text-2xl font-bold text-slate-900">Order Created!</h2>
                <p className="text-slate-500">Review your final totals before payment.</p>
            </div>

            <div className="space-y-6 max-w-md mx-auto">
                {/* Shipping Quote Info */}
                <div className="bg-blue-50 border border-blue-100 rounded-xl p-4">
                    <h4 className="text-sm font-semibold text-blue-900 mb-2">Shipping: {shippingMode} Freight</h4>
                    <p className="text-sm text-blue-700">Estimated Delivery: <span className="font-bold">{etaMin} - {etaMax} Days</span></p>
                </div>

                {/* Currency Snapshot */}
                <div className="bg-slate-50 border border-slate-100 rounded-xl p-4 text-xs text-slate-500 flex flex-col gap-1">
                    <div className="flex items-center gap-2">
                        <Lock size={12} />
                        <span className="font-semibold text-slate-700">Exchange Rate Locked</span>
                    </div>
                    <p>1 INR = {exchangeRate.toFixed(4)} {buyerCurrency}</p>
                    <p className="flex items-center gap-1">
                        <Calendar size={12} />
                        {rateTimestamp && format(new Date(rateTimestamp), 'MMM dd, yyyy HH:mm')}
                    </p>
                </div>

                {/* Totals Table */}
                <div className="space-y-3 pt-4">
                    <div className="flex justify-between text-slate-600">
                        <span>Subtotal</span>
                        <div className="text-right">
                            <div className="font-medium text-slate-900">{formatMoney(totalSubtotalPaise)}</div>
                        </div>
                    </div>

                    <div className="flex justify-between text-slate-600">
                        <span>Shipping Cost</span>
                        <div className="text-right">
                            <div className="font-medium text-slate-900">{formatMoney(totalShippingPaise)}</div>
                        </div>
                    </div>

                    <div className="border-t border-slate-200 pt-4 flex justify-between items-center">
                        <span className="text-lg font-bold text-slate-900">Grand Total</span>
                        <div className="text-right">
                            <div className="text-xl font-bold text-brand-700">{formatMoney(grandTotalPaise)}</div>
                            <div className="text-sm text-brand-500 font-medium">{buyerCurrency} {(grandTotalConverted / 100).toFixed(2)}</div>
                        </div>
                    </div>
                </div>

                <button
                    onClick={onProceed}
                    className="w-full flex items-center justify-center gap-2 bg-brand-600 text-white font-bold py-4 rounded-xl hover:bg-brand-700 transition-all shadow-brand-md hover:shadow-brand-lg mt-8"
                >
                    Proceed to Payment
                    <ArrowRight size={20} />
                </button>
            </div>
        </div>
    );
};

export default TotalsBreakdown;
