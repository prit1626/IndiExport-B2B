import React from 'react';
import { formatMoney } from '../../utils/formatMoney';

const OrderItemsTable = ({ items, currency }) => {
    return (
        <div className="overflow-x-auto">
            <table className="w-full text-left border-collapse">
                <thead>
                    <tr className="border-b border-slate-200 text-xs font-semibold text-slate-500 uppercase tracking-wider">
                        <th className="py-3 pr-4">Product</th>
                        <th className="py-3 px-4 text-center">Qty</th>
                        <th className="py-3 px-4 text-right">Price</th>
                        <th className="py-3 px-4 text-right">Total</th>
                    </tr>
                </thead>
                <tbody className="divide-y divide-slate-100">
                    {items.map((item) => (
                        <tr key={item.id || item.productId}>
                            <td className="py-4 pr-4">
                                <div className="flex items-center gap-3">
                                    <div className="w-12 h-12 rounded-lg bg-slate-100 flex-shrink-0 overflow-hidden border border-slate-200">
                                        {item.thumbnailUrl ? (
                                            <img src={item.thumbnailUrl} alt={item.title} className="w-full h-full object-cover" />
                                        ) : (
                                            <div className="w-full h-full flex items-center justify-center text-xs text-slate-400">No Img</div>
                                        )}
                                    </div>
                                    <div>
                                        <p className="font-medium text-slate-900 line-clamp-2">{item.title}</p>
                                        <p className="text-xs text-slate-500">{item.unit}</p>
                                    </div>
                                </div>
                            </td>
                            <td className="py-4 px-4 text-center text-slate-700">
                                {item.qty}
                            </td>
                            <td className="py-4 px-4 text-right text-slate-600 text-sm">
                                {item.convertedPriceMinor
                                    ? formatMoney(item.convertedPriceMinor, currency)
                                    : formatMoney(item.basePriceINRPaise, 'INR')
                                }
                            </td>
                            <td className="py-4 px-4 text-right font-medium text-slate-900 text-sm">
                                {item.convertedPriceMinor
                                    ? formatMoney(item.convertedPriceMinor * item.qty, currency)
                                    : formatMoney(item.basePriceINRPaise * item.qty, 'INR')
                                }
                            </td>
                        </tr>
                    ))}
                </tbody>
            </table>
        </div>
    );
};

export default OrderItemsTable;
