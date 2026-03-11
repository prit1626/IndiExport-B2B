import React from 'react';
import { useNavigate } from 'react-router-dom';
import { Star, ShieldCheck, MapPin } from 'lucide-react';
import { motion } from 'framer-motion';
import { formatCurrency } from '../../utils/currencyFormatter';
import { getBuyerCurrency } from '../../utils/getBuyerCurrency';

const ProductSearchCard = ({ product }) => {
    const navigate = useNavigate();
    const currency = getBuyerCurrency();

    // Map backend response fields to the card
    const title = product.name || product.title;
    const price = product.pricePaise || 0;
    const unit = product.quantityUnit || product.unit || 'unit';
    const thumbnail = product.thumbnail || (product.media && product.media.length > 0 ? product.media[0].mediaUrl : null);

    return (
        <motion.div
            initial={{ opacity: 0, scale: 0.95 }}
            animate={{ opacity: 1, scale: 1 }}
            whileHover={{ y: -4 }}
            onClick={() => navigate(`/products/${product.id}`)}
            className="bg-white rounded-xl border border-slate-200 overflow-hidden cursor-pointer hover:shadow-lg transition-all"
        >
            <div className="aspect-square bg-slate-100 relative">
                <img
                    src={thumbnail || 'https://via.placeholder.com/300?text=No+Image'}
                    alt={title}
                    className="w-full h-full object-cover"
                />
                <div className="absolute top-2 left-2 bg-white/90 backdrop-blur-sm px-2 py-0.5 rounded text-[10px] font-bold text-slate-600 uppercase">
                    {product.incoterm || 'EXW'}
                </div>
            </div>

            <div className="p-3">
                <div className="flex items-center gap-1 mb-1">
                    <span className="text-[10px] text-slate-500 truncate max-w-[100px]">
                        {product.seller?.companyName || 'Verified Supplier'}
                    </span>
                    {product.seller?.verified && <ShieldCheck className="w-3 h-3 text-emerald-500" />}
                </div>

                <h3 className="text-sm font-semibold text-slate-900 line-clamp-2 h-10 mb-2">
                    {title}
                </h3>

                <div className="flex items-center justify-between mt-auto">
                    <div>
                        <div className="text-brand-700 font-bold text-base">
                            {formatCurrency(price / 100, currency)}
                        </div>
                        <div className="text-[10px] text-slate-400">per {unit}</div>
                    </div>

                    <div className="flex items-center gap-0.5 text-xs text-amber-500 font-medium">
                        <Star className="w-3 h-3 fill-current" />
                        {(product.averageRatingMilli ? product.averageRatingMilli / 1000 : (product.averageRating || 0)).toFixed(1)}
                    </div>
                </div>

                <div className="mt-3 pt-3 border-t border-slate-50 flex items-center gap-1 text-[10px] text-slate-500">
                    <MapPin className="w-3 h-3" />
                    {product.originCountry || 'India'}
                </div>
            </div>
        </motion.div>
    );
};

export default ProductSearchCard;
