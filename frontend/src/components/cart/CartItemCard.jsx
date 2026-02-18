import React from 'react';
import { motion } from 'framer-motion';
import { Trash2, AlertCircle } from 'lucide-react';
import { Link } from 'react-router-dom';
import QtyStepper from './QtyStepper';
import ShippingModeSelect from './ShippingModeSelect';
import { formatMoney } from '../../utils/formatMoney';
import useCartStore from '../../store/cartStore';

const CartItemCard = ({ item }) => {
    const { updateItem, removeItem, itemLoading } = useCartStore();
    const isLoading = itemLoading[item.id];

    const handleQtyChange = (newQty) => {
        updateItem(item.id, { quantity: newQty });
    };

    const handleShippingChange = (newMode) => {
        updateItem(item.id, { quantity: item.quantity, shippingMode: newMode });
    };

    return (
        <motion.div
            layout
            initial={{ opacity: 0, y: 10 }}
            animate={{ opacity: 1, y: 0 }}
            exit={{ opacity: 0, scale: 0.95 }}
            className={`bg-white rounded-xl border p-4 sm:p-6 transition-all ${isLoading ? 'border-brand-200 ring-1 ring-brand-100 opacity-70' : 'border-slate-200 shadow-sm hover:shadow-md'
                }`}
        >
            <div className="flex flex-col sm:flex-row gap-6">
                {/* Thumbnail */}
                <div className="w-full sm:w-24 h-24 bg-slate-100 rounded-lg overflow-hidden flex-shrink-0 border border-slate-100">
                    {item.thumbnailUrl ? (
                        <img src={item.thumbnailUrl} alt={item.productName} className="w-full h-full object-cover" />
                    ) : (
                        <div className="w-full h-full flex items-center justify-center text-slate-300 text-xs">No Image</div>
                    )}
                </div>

                {/* Details */}
                <div className="flex-1 flex flex-col justify-between">
                    <div>
                        <div className="flex justify-between items-start">
                            <div>
                                <Link to={`/products/${item.productId}`} className="text-lg font-semibold text-slate-900 hover:text-brand-600 transition-colors line-clamp-1">
                                    {item.productName}
                                </Link>
                                <p className="text-sm text-slate-500">{item.sellerCompanyName}</p>
                            </div>
                            <button
                                onClick={() => removeItem(item.id)}
                                disabled={isLoading}
                                className="text-slate-400 hover:text-red-500 p-2 -mr-2 rounded-full hover:bg-red-50 transition-colors"
                            >
                                <Trash2 size={18} />
                            </button>
                        </div>

                        {!item.productActive && (
                            <div className="flex items-center gap-1 mt-2 text-xs font-medium text-amber-600 bg-amber-50 px-2 py-1 rounded w-fit">
                                <AlertCircle size={12} />
                                Product Unavailable
                            </div>
                        )}
                    </div>

                    {/* Controls */}
                    <div className="flex flex-col sm:flex-row items-start sm:items-center gap-4 mt-4 sm:mt-0">
                        <div className="flex items-center gap-4">
                            <QtyStepper
                                value={item.quantity}
                                min={item.minQty}
                                onChange={handleQtyChange}
                                disabled={isLoading}
                            />
                            <div className="w-32">
                                <ShippingModeSelect
                                    value={item.shippingMode}
                                    onChange={handleShippingChange}
                                    disabled={isLoading}
                                />
                            </div>
                        </div>

                        <div className="flex-1 text-right sm:ml-auto">
                            <p className="text-lg font-bold text-slate-900">
                                {formatMoney(item.lineTotalPaise)}
                            </p>
                            {/* <p className="text-xs text-slate-500">
                               approx. {item.currency} {(item.lineTotalMinor / 100).toFixed(2)}
                           </p> */}
                        </div>
                    </div>
                </div>
            </div>
        </motion.div>
    );
};

export default CartItemCard;
