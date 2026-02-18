import React from 'react';
import { useNavigate } from 'react-router-dom';
import { Star, ShieldCheck, MapPin, Clock, Calendar } from 'lucide-react';
import { motion } from 'framer-motion';

const ProductCard = ({ product }) => {
    const navigate = useNavigate();

    // Format currency
    const formatPrice = (amount, currency = 'USD') => {
        return new Intl.NumberFormat('en-US', {
            style: 'currency',
            currency: currency,
            minimumFractionDigits: 0,
            maximumFractionDigits: 2,
        }).format(amount);
    };

    const formatINR = (paise) => {
        return new Intl.NumberFormat('en-IN', {
            style: 'currency',
            currency: 'INR',
            minimumFractionDigits: 0,
            maximumFractionDigits: 0,
        }).format(paise / 100);
    };

    return (
        <motion.div
            initial={{ opacity: 0, y: 20 }}
            animate={{ opacity: 1, y: 0 }}
            whileHover={{ y: -5, boxShadow: "0 10px 30px -10px rgba(0,0,0,0.1)" }}
            transition={{ duration: 0.2 }}
            onClick={() => navigate(`/products/${product.id}`)}
            className="bg-white rounded-2xl border border-slate-200 overflow-hidden cursor-pointer group"
        >
            {/* Image */}
            <div className="relative aspect-[4/3] overflow-hidden bg-slate-100">
                <img
                    src={product.thumbnail || 'https://via.placeholder.com/400x300?text=No+Image'}
                    alt={product.title}
                    className="w-full h-full object-cover transition-transform duration-500 group-hover:scale-105"
                />

                {/* Incoterm Badge */}
                <div className="absolute top-3 left-3 bg-white/90 backdrop-blur-sm px-2.5 py-1 rounded-lg text-xs font-semibold text-slate-700 shadow-sm">
                    {product.incoterm || 'FOB'}
                </div>
            </div>

            <div className="p-4">
                {/* Seller Info */}
                <div className="flex items-center justify-between mb-2">
                    <div className="flex items-center gap-1.5 text-xs text-slate-500">
                        <span className="truncate max-w-[120px]">{product.seller?.companyName}</span>
                        {product.seller?.verified && (
                            <ShieldCheck className="w-3.5 h-3.5 text-emerald-500" />
                        )}
                    </div>
                    <div className="flex items-center gap-1 text-xs font-medium text-amber-500 bg-amber-50 px-1.5 py-0.5 rounded">
                        <Star className="w-3 h-3 fill-current" />
                        {product.averageRating?.toFixed(1) || '0.0'} <span className="text-slate-400 font-normal">({product.totalReviews || 0})</span>
                    </div>
                </div>

                {/* Title */}
                <h3 className="text-base font-semibold text-slate-900 mb-1 line-clamp-2 min-h-[48px]">
                    {product.title}
                </h3>

                {/* Chips */}
                <div className="flex flex-wrap gap-2 mb-4">
                    <div className="flex items-center gap-1 text-xs text-slate-500 bg-slate-50 px-2 py-1 rounded-md border border-slate-100">
                        <MapPin className="w-3 h-3" /> {product.originCountry || 'IN'}
                    </div>
                    {(product.leadTimeDays && product.leadTimeDays > 0) && (
                        <div className="flex items-center gap-1 text-xs text-slate-500 bg-slate-50 px-2 py-1 rounded-md border border-slate-100">
                            <Clock className="w-3 h-3" /> {product.leadTimeDays}d lead
                        </div>
                    )}
                </div>

                {/* Price Section */}
                <div className="space-y-1">
                    <div className="flex items-baseline gap-1">
                        <span className="text-lg font-bold text-brand-700">
                            {product.convertedPrice
                                ? formatPrice(product.convertedPrice.convertedPriceMinor / 100, product.convertedPrice.currency)
                                : formatINR(product.pricePaise)
                            }
                        </span>
                        <span className="text-sm text-slate-500 font-normal">/ {product.unit}</span>
                    </div>

                    {product.convertedPrice && (
                        <div className="flex items-center justify-between text-xs text-slate-400">
                            <span>{formatINR(product.pricePaise)}</span>
                            <span title={`Rate: ${(product.convertedPrice.exchangeRateMicros / 1000000).toFixed(2)}`}>
                                ~ {(product.convertedPrice.exchangeRateMicros / 1000000).toFixed(2)} INR/{product.convertedPrice.currency}
                            </span>
                        </div>
                    )}
                </div>
            </div>
        </motion.div>
    );
};

export default ProductCard;
