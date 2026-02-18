import React from 'react';
import { Pencil, Trash2, Eye, EyeOff } from 'lucide-react';
import { Link } from 'react-router-dom';
import ActiveToggleSwitch from './ActiveToggleSwitch';

const ProductCard = ({ product, activeCount, onToggleActive, onDelete }) => {
    return (
        <div className="bg-white rounded-xl border border-slate-200 shadow-sm hover:shadow-md transition-shadow overflow-hidden flex flex-col">
            {/* Thumbnail */}
            <div className="h-48 bg-slate-100 relative">
                {product.media && product.media.length > 0 ? (
                    <img
                        src={product.media.find(m => m.type === 'IMAGE')?.url || product.media[0].url}
                        alt={product.title}
                        className="w-full h-full object-cover"
                    />
                ) : (
                    <div className="w-full h-full flex items-center justify-center text-slate-400">
                        No Image
                    </div>
                )}
                {/* Status Badge */}
                <div className={`absolute top-2 right-2 px-2 py-0.5 rounded text-[10px] font-bold uppercase tracking-wide border ${product.active
                    ? 'bg-green-100/90 text-green-700 border-green-200'
                    : 'bg-slate-100/90 text-slate-500 border-slate-200'
                    }`}>
                    {product.status}
                </div>
            </div>

            {/* Content */}
            <div className="p-4 flex-1 flex flex-col">
                <h3 className="font-bold text-slate-900 mb-1 truncate" title={product.title}>{product.title}</h3>
                <div className="mb-4">
                    <span className="text-lg font-bold text-brand-700">₹{(product.pricePaise / 100).toLocaleString()}</span>
                    <span className="text-xs text-slate-500 ml-1">/ {product.unit}</span>
                </div>

                <div className="text-xs text-slate-500 space-y-1 mb-4 flex-1">
                    <p>Min Qty: <span className="font-medium text-slate-700">{product.minQty}</span></p>
                    <p>Lead Time: <span className="font-medium text-slate-700">{product.leadTimeDays} Days</span></p>
                </div>

                {/* Actions */}
                <div className="flex items-center justify-between pt-4 border-t border-slate-100">
                    <div className="flex items-center gap-2">
                        <ActiveToggleSwitch
                            isActive={product.status === 'ACTIVE'}
                            activeCount={activeCount}
                            onChange={(val) => onToggleActive(product.id, val)}
                        />
                        <span className="text-xs text-slate-500">{product.status === 'ACTIVE' ? 'On' : 'Off'}</span>
                    </div>

                    <div className="flex items-center gap-2">
                        <Link
                            to={`/seller/products/${product.id}/edit`}
                            className="p-2 text-slate-500 hover:text-brand-600 hover:bg-brand-50 rounded-full transition-colors"
                        >
                            <Pencil size={18} />
                        </Link>
                        <button
                            onClick={() => onDelete(product.id)}
                            className="p-2 text-slate-500 hover:text-red-600 hover:bg-red-50 rounded-full transition-colors"
                        >
                            <Trash2 size={18} />
                        </button>
                    </div>
                </div>
            </div>
        </div>
    );
};

export default ProductCard;
