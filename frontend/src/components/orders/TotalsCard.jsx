import React from 'react';
import { formatCurrency } from '../../utils/currencyFormatter';
import { getBuyerCurrency } from '../../utils/getBuyerCurrency';

const TotalsCard = ({ totals, currencySnapshot }) => {
    const currency = getBuyerCurrency();

    // Helper to get correct value based on currency
    const getValue = (inrKey) => {
        if (!totals) return '';
        return formatCurrency((totals[inrKey] || 0) / 100, currency);
    };

    return (
        <div className="bg-slate-50 rounded-xl p-6 space-y-3">
            <h3 className="font-semibold text-slate-900 mb-2">Order Summary</h3>

            <div className="flex justify-between text-slate-600 text-sm">
                <span>Subtotal</span>
                <span>{getValue('subtotalINRPaise')}</span>
            </div>

            <div className="flex justify-between text-slate-600 text-sm">
                <span>Shipping</span>
                <span>{getValue('shippingINRPaise')}</span>
            </div>

            {totals?.taxINRPaise > 0 && (
                <div className="flex justify-between text-slate-600 text-sm">
                    <span>Tax / GST</span>
                    <span>{getValue('taxINRPaise')}</span>
                </div>
            )}

            <div className="border-t border-slate-200 mt-3 pt-3 flex justify-between items-center">
                <span className="font-bold text-slate-900">Grand Total</span>
                <span className="font-bold text-slate-900 text-lg">
                    {getValue('grandTotalINRPaise')}
                </span>
            </div>


        </div>
    );
};

export default TotalsCard;
