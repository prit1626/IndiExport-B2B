import React from 'react';
import { useNavigate } from 'react-router-dom';
import { Package, Truck, ArrowRight, Calendar, User, MapPin } from 'lucide-react';
import OrderStatusBadge from '../orders/OrderStatusBadge';
import { formatMoney } from '../../utils/formatMoney';
import { formatShortDate } from '../../utils/formatDate';

const SellerOrderCard = ({ order }) => {
    const navigate = useNavigate();

    return (
        <div
            onClick={() => navigate(`/seller/orders/${order.id}`)}
            className="group bg-white rounded-xl border border-slate-200 p-5 shadow-sm hover:shadow-md transition-all cursor-pointer relative overflow-hidden"
        >
            <div className="flex flex-col sm:flex-row justify-between items-start sm:items-center gap-4 mb-4">
                <div>
                    <div className="flex items-center gap-3 mb-1">
                        <span className="font-bold text-slate-900">#{order.orderNumber || order.id.slice(0, 8)}</span>
                        <OrderStatusBadge status={order.status} />
                    </div>
                    <div className="flex items-center gap-2 text-sm text-slate-500 mb-1">
                        <Calendar size={14} />
                        <span>Placed on {formatShortDate(order.createdAt)}</span>
                    </div>
                    <div className="flex items-center gap-2 text-sm text-slate-600">
                        <User size={14} className="text-slate-400" />
                        <span className="font-medium">{order.buyerName || 'Unknown Buyer'}</span>
                        <span className="text-slate-300 mx-1">|</span>
                        <MapPin size={14} className="text-slate-400" />
                        <span>{order.shippingCountry || 'N/A'}</span>
                    </div>
                </div>
                <div className="text-right">
                    <p className="text-sm text-slate-500">Order Value</p>
                    <p className="font-bold text-slate-900 text-lg">
                        {/* Always show INR for seller, maybe converted too? Keeping it simple with INR as primary */}
                        {formatMoney(order.grandTotalINRPaise, 'INR')}
                    </p>
                    {order.currency !== 'INR' && order.grandTotalConvertedMinor && (
                        <p className="text-xs text-slate-400">
                            ({formatMoney(order.grandTotalConvertedMinor, order.currency)})
                        </p>
                    )}
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
                {/* Add ETA if available in future */}
            </div>

            <div className="absolute right-4 bottom-4 opacity-0 group-hover:opacity-100 transition-opacity transform translate-x-2 group-hover:translate-x-0">
                <div className="bg-brand-50 text-brand-600 p-2 rounded-full">
                    <ArrowRight size={20} />
                </div>
            </div>
        </div>
    );
};

export default SellerOrderCard;
