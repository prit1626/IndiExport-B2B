import React from 'react';
import { useNavigate } from 'react-router-dom';
import { Package, Truck, ArrowRight, Calendar } from 'lucide-react';
import OrderStatusBadge from './OrderStatusBadge';
import { formatMoney } from '../../utils/formatMoney';
import { formatShortDate } from '../../utils/formatDate';

const OrderCard = ({ order }) => {
    const navigate = useNavigate();

    return (
        <div
            onClick={() => navigate(`/buyer/orders/${order.id}`)}
            className="group bg-white rounded-xl border border-slate-200 p-5 shadow-sm hover:shadow-md transition-all cursor-pointer relative overflow-hidden"
        >
            <div className="flex flex-col sm:flex-row justify-between items-start sm:items-center gap-4 mb-4">
                <div>
                    <div className="flex items-center gap-3 mb-1">
                        <span className="font-bold text-slate-900">#{order.orderNumber || order.id?.slice(0, 8) || '...'}</span>
                        <OrderStatusBadge status={order.status} />
                    </div>
                    <div className="flex items-center gap-2 text-sm text-slate-500">
                        <Calendar size={14} />
                        <span>Placed on {formatShortDate(order.createdAt)}</span>
                    </div>
                </div>
                <div className="text-right">
                    <p className="text-sm text-slate-500">Total Amount</p>
                    <p className="font-bold text-slate-900 text-lg">
                        {order.grandTotalConvertedMinor != null
                            ? formatMoney(order.grandTotalConvertedMinor, order.currency)
                            : formatMoney(order.grandTotalINRPaise, 'INR')
                        }
                    </p>
                </div>
            </div>

            <div className="flex items-center gap-6 text-sm text-slate-600 border-t border-slate-100 pt-4">
                <div className="flex items-center gap-2">
                    <Package size={16} className="text-slate-400" />
                    <span>{order.itemCount} Items</span>
                </div>
                <div className="flex items-center gap-2">
                    <Truck size={16} className="text-slate-400" />
                    <span>{order.shippingMode} Shipping</span>
                </div>
                {order.etaMaxDays && (
                    <div className="hidden sm:block text-slate-400">
                        Top {order.etaMinDays}-{order.etaMaxDays} Days
                    </div>
                )}
            </div>

            <div className="absolute right-4 bottom-4 opacity-0 group-hover:opacity-100 transition-opacity transform translate-x-2 group-hover:translate-x-0">
                <div className="bg-brand-50 text-brand-600 p-2 rounded-full">
                    <ArrowRight size={20} />
                </div>
            </div>
        </div>
    );
};

export default OrderCard;
