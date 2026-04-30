import React from 'react';
import { formatCurrency } from '../../utils/currencyFormatter';
import { getBuyerCurrency } from '../../utils/getBuyerCurrency';
import { MapPin, Truck, Calendar, Lock, Package } from 'lucide-react';
import { format } from 'date-fns';
import Badge from '../common/Badge';

const OrderPaymentSummary = ({ order }) => {
    const currency = getBuyerCurrency();
    if (!order) return null;

    const { shippingAddress, items = [], totals = {}, shipping, currencySnapshot, status } = order;

    return (
        <div className="bg-white rounded-2xl border border-slate-200 shadow-sm p-6 sticky top-24">
            <div className="flex justify-between items-start mb-6">
                <h3 className="text-lg font-bold text-slate-900">Order Summary</h3>
                <Badge variant={status === 'PAID' ? 'success' : 'warning'}>{status}</Badge>
            </div>

            {/* Address & Shipping */}
            <div className="space-y-4 mb-6 text-sm">
                <div className="flex gap-3 text-slate-600">
                    <MapPin size={18} className="flex-shrink-0 mt-0.5" />
                    <div>
                        <p className="font-medium text-slate-900">{shippingAddress?.fullName || 'N/A'}</p>
                        <p>{shippingAddress?.streetAddress || ''}</p>
                        <p>
                            {shippingAddress?.city || ''}
                            {shippingAddress?.state ? `, ${shippingAddress.state}` : ''}
                            {shippingAddress?.postalCode ? ` ${shippingAddress.postalCode}` : ''}
                        </p>
                        <p>{shippingAddress?.country || ''}</p>
                    </div>
                </div>
                <div className="flex gap-3 text-slate-600">
                    <Truck size={18} className="flex-shrink-0 mt-0.5" />
                    <div>
                        <p className="font-medium text-slate-900">{(shipping?.shippingMode || 'Standard').toLowerCase()} Shipping</p>
                    </div>
                </div>
            </div>

            {/* Items (Collapsed/Scrollable if many) */}
            <div className="border-t border-slate-100 py-4 max-h-40 overflow-y-auto custom-scrollbar space-y-3">
                {items.map((item) => (
                    <div key={item.id} className="flex gap-3">
                        <div className="w-12 h-12 bg-slate-100 rounded border border-slate-200 flex items-center justify-center flex-shrink-0 text-slate-300">
                            {item.thumbnailUrl ? <img src={item.thumbnailUrl} className="w-full h-full object-cover rounded" /> : <Package size={20} />}
                        </div>
                        <div className="flex-1 min-w-0">
                            <p className="text-sm font-medium text-slate-900 truncate">{item.title}</p>
                            <p className="text-xs text-slate-500">{item.qty} x {formatCurrency(item.basePriceINRPaise / 100, currency)}</p>
                        </div>
                        <div className="text-right">
                            <p className="text-sm font-medium text-slate-900">{formatCurrency((item.basePriceINRPaise * item.qty) / 100, currency)}</p>
                        </div>
                    </div>
                ))}
            </div>

            {/* Totals */}
            <div className="border-t border-slate-100 pt-4 space-y-2">
                <div className="flex justify-between text-sm text-slate-600">
                    <span>Subtotal</span>
                    <span>{formatCurrency(totals.subtotalINRPaise / 100, currency)}</span>
                </div>
                <div className="flex justify-between text-sm text-slate-600">
                    <span>Shipping</span>
                    <span>{formatCurrency(totals.shippingINRPaise / 100, currency)}</span>
                </div>

                <div className="border-t border-slate-100 pt-3 mt-3 flex justify-between items-end">
                    <span className="font-bold text-slate-900">Total</span>
                    <div className="text-right">
                        <div className="text-xl font-bold text-brand-700">{formatCurrency(totals.grandTotalINRPaise / 100, currency)}</div>
                    </div>
                </div>
            </div>


        </div>
    );
};

export default OrderPaymentSummary;
