import React from 'react';
import { Circle, CheckCircle, Truck, Package, XCircle, Clock } from 'lucide-react';

const OrderStatusBadge = ({ status }) => {
    const getStatusConfig = (status) => {
        switch (status) {
            case 'CREATED':
            case 'PENDING_CONFIRMATION':
                return { color: 'bg-yellow-100 text-yellow-700 border-yellow-200', icon: Clock, label: 'Pending' };
            case 'CONFIRMED':
            case 'PAID':
                return { color: 'bg-blue-100 text-blue-700 border-blue-200', icon: CheckCircle, label: 'Paid & Confirmed' };
            case 'PROCESSING':
                return { color: 'bg-indigo-100 text-indigo-700 border-indigo-200', icon: Package, label: 'Processing' };
            case 'SHIPPED':
            case 'IN_TRANSIT':
                return { color: 'bg-purple-100 text-purple-700 border-purple-200', icon: Truck, label: 'Shipped' };
            case 'DELIVERED':
            case 'COMPLETED':
                return { color: 'bg-green-100 text-green-700 border-green-200', icon: CheckCircle, label: 'Delivered' };
            case 'CANCELLED':
                return { color: 'bg-red-100 text-red-700 border-red-200', icon: XCircle, label: 'Cancelled' };
            case 'REFUNDED':
                return { color: 'bg-gray-100 text-gray-700 border-gray-200', icon: RefreshCcw, label: 'Refunded' };
            default:
                return { color: 'bg-slate-100 text-slate-700 border-slate-200', icon: Circle, label: status };
        }
    };

    const config = getStatusConfig(status);
    const Icon = config.icon;

    return (
        <span className={`inline-flex items-center gap-1.5 px-3 py-1 rounded-full text-xs font-medium border ${config.color}`}>
            <Icon size={14} />
            {config.label}
        </span>
    );
};

export default OrderStatusBadge;
