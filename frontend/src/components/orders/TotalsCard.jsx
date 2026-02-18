import React from 'react';
import { formatMoney } from '../../utils/formatMoney';

const TotalsCard = ({ totals, currencySnapshot }) => {
    const isInternational = currencySnapshot && currencySnapshot.buyerCurrency !== 'INR';
    const currency = isInternational ? currencySnapshot.buyerCurrency : 'INR';

    // Helper to get correct value based on currency
    const getValue = (inrKey, convertedKey) => {
        if (isInternational && totals[convertedKey]) {
            return formatMoney(totals[convertedKey], currency);
        }
        return formatMoney(totals[inrKey], 'INR');
    };

    return (
        <div className="bg-slate-50 rounded-xl p-6 space-y-3">
            <h3 className="font-semibold text-slate-900 mb-2">Order Summary</h3>

            <div className="flex justify-between text-slate-600 text-sm">
                <span>Subtotal</span>
                <span>{getValue('subtotalINRPaise', 'subtotalConvertedMinor')}</span>
            </div>

            <div className="flex justify-between text-slate-600 text-sm">
                <span>Shipping</span>
                <span>{getValue('shippingINRPaise', 'shippingConvertedMinor')}</span>
            </div>

            {totals.taxINRPaise > 0 && (
                <div className="flex justify-between text-slate-600 text-sm">
                    <span>Tax / GST</span>
                    <span>{getValue('taxINRPaise', 'taxConvertedMinor')}</span>
                </div>
            )}

            <div className="border-t border-slate-200 mt-3 pt-3 flex justify-between items-center">
                <span className="font-bold text-slate-900">Grand Total</span>
                <span className="font-bold text-slate-900 text-lg">
                    {getValue('grandTotalINRPaise', 'grandTotalConvertedMinor')}
                </span>
            </div>

            {isInternational && currencySnapshot && (
                <div className="mt-4 text-xs text-slate-400 bg-white p-3 rounded border border-slate-200">
                    <p>Exchange Rate Used: 1 {currency} = {(currencySnapshot.exchangeRateMicros / 1000000).toFixed(6)} INR</p>
                    {currencySnapshot.providerName && (
                        <p>Processed via {currencySnapshot.providerName}</p>
                    )}
                </div>
            )}
        </div>
    );
};

export default TotalsCard;
