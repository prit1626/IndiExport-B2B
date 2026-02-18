import React from 'react';
import { CheckCircle, Clock, Package, Truck, Award } from 'lucide-react';
import { formatShortDate } from '../../utils/formatDate';

const steps = [
    { key: 'CREATED', label: 'Order Placed', icon: Clock },
    { key: 'PAID', label: 'Payment Confirmed', icon: Award },
    { key: 'PROCESSING', label: 'Processing', icon: Package },
    { key: 'SHIPPED', label: 'Shipped', icon: Truck },
    { key: 'DELIVERED', label: 'Delivered', icon: CheckCircle },
];

const OrderTimeline = ({ currentStatus, timelineEvents = [] }) => {
    // Map status to index
    const statusOrder = ['CREATED', 'PENDING_CONFIRMATION', 'CONFIRMED', 'PAID', 'PROCESSING', 'SHIPPED', 'IN_TRANSIT', 'DELIVERED', 'COMPLETED'];

    // Normalize current status for the timeline steps
    let activeIndex = 0;
    if (currentStatus === 'CANCELLED' || currentStatus === 'REFUNDED') {
        activeIndex = 0; // Show 'Order Placed' as active
    } else {
        const normalizedStatus = currentStatus === 'IN_TRANSIT' ? 'SHIPPED' :
            currentStatus === 'COMPLETED' ? 'DELIVERED' :
                currentStatus === 'PENDING_CONFIRMATION' || currentStatus === 'CONFIRMED' ? 'CREATED' :
                    currentStatus;

        activeIndex = steps.findIndex(s => s.key === normalizedStatus);
        // If exact match not found (e.g. PAID might be skipped if we jump to PROCESSING), find partial
        if (activeIndex === -1) {
            // Fallback logic
            if (statusOrder.indexOf(currentStatus) >= statusOrder.indexOf('DELIVERED')) activeIndex = 4;
            else if (statusOrder.indexOf(currentStatus) >= statusOrder.indexOf('SHIPPED')) activeIndex = 3;
            else if (statusOrder.indexOf(currentStatus) >= statusOrder.indexOf('PROCESSING')) activeIndex = 2;
            else if (statusOrder.indexOf(currentStatus) >= statusOrder.indexOf('PAID')) activeIndex = 1;
            else activeIndex = 0;
        }
    }

    return (
        <div className="w-full py-6">
            <div className="flex items-center justify-between relative">
                {/* Connecting Line */}
                <div className="absolute left-0 top-1/2 transform -translate-y-1/2 w-full h-1 bg-slate-100 -z-10"></div>
                <div
                    className="absolute left-0 top-1/2 transform -translate-y-1/2 h-1 bg-green-500 transition-all duration-500 -z-10"
                    style={{ width: `${(activeIndex / (steps.length - 1)) * 100}%` }}
                ></div>

                {steps.map((step, index) => {
                    const Icon = step.icon;
                    const isActive = index <= activeIndex;
                    const isCompleted = index < activeIndex;

                    return (
                        <div key={step.key} className="flex flex-col items-center gap-2 bg-white px-2">
                            <div
                                className={`w-8 h-8 rounded-full flex items-center justify-center transition-colors border-2 
                                ${isActive ? 'bg-green-500 border-green-500 text-white' : 'bg-white border-slate-200 text-slate-300'}`}
                            >
                                {isCompleted ? <CheckCircle size={16} /> : <Icon size={16} />}
                            </div>
                            <span className={`text-xs font-medium ${isActive ? 'text-slate-900' : 'text-slate-400'}`}>
                                {step.label}
                            </span>
                        </div>
                    );
                })}
            </div>
        </div>
    );
};

export default OrderTimeline;
