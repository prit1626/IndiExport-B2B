import React, { useState } from 'react';
import { useNavigate, useLocation } from 'react-router-dom';
import { ShoppingCart, MessageCircle, Truck, Package, Globe, CheckCircle, ShieldCheck, Building2 } from 'lucide-react';
import useAuthStore from '../../store/authStore';
import cartApi from '../../api/cartApi';
import chatApi from '../../api/chatApi';
import { formatMoney } from '../../utils/formatMoney';
import { format as formatDate } from 'date-fns';
import Badge from '../common/Badge';
import RatingStars from '../common/RatingStars';
import { toast } from 'react-hot-toast';

const ProductInfoPanel = ({ product }) => {
    const navigate = useNavigate();
    const user = useAuthStore(state => state.user);
    const isAuthenticated = useAuthStore(state => state.isAuthenticated);
    const [loading, setLoading] = useState({ cart: false, chat: false });

    const location = useLocation();

    // Helper to check roles robustly
    const hasRole = (roleName) => {
        if (!user) return false;

        // Logical fallbacks
        if (roleName === 'BUYER' && user.buyerDetails) return true;
        if (roleName === 'SELLER' && user.sellerDetails) return true;

        // Collect all possible roles into a flat array of strings
        let rawRoles = [];
        if (Array.isArray(user.roles)) {
            rawRoles = user.roles;
        } else if (typeof user.role === 'string') {
            // Split by comma if multiple roles are joined (common in some token structures)
            rawRoles = user.role.split(',');
        } else if (user.role) {
            rawRoles = [user.role];
        }

        const normalizedGoal = roleName.toUpperCase().replace('ROLE_', '').trim();

        return rawRoles.some(r => {
            if (!r) return false;
            const rStr = typeof r === 'string' ? r : String(r);
            const normalizedR = rStr.toUpperCase().trim().replace('ROLE_', '');
            return normalizedR === normalizedGoal;
        });
    };

    const handleAddToCart = async () => {
        if (!isAuthenticated) return navigate('/auth/login', { state: { from: location.pathname } });

        if (hasRole('SELLER')) {
            toast.error('Only buyers can add to cart. Please sign up as a buyer.');
            return navigate('/auth/signup/buyer');
        }

        if (!hasRole('BUYER')) {
            toast.error('Only buyers can add to cart');
            return;
        }

        setLoading(prev => ({ ...prev, cart: true }));
        try {
            await cartApi.addToCart({ productId: product.id, quantity: product.minQty || 1 });
            toast.success('Added to cart');
        } catch (error) {
            toast.error(error.response?.data?.message || 'Failed to add to cart');
        } finally {
            setLoading(prev => ({ ...prev, cart: false }));
        }
    };

    const handleStartInquiry = async () => {
        if (!isAuthenticated) return navigate('/auth/login', { state: { from: location.pathname } });

        if (hasRole('SELLER')) {
            toast.error('Only buyers can start inquiries. Please sign up as a buyer.');
            return navigate('/auth/signup/buyer');
        }

        if (!hasRole('BUYER')) {
            toast.error('Only buyers can start inquiries');
            return;
        }

        setLoading(prev => ({ ...prev, chat: true }));
        try {
            const { data } = await chatApi.startInquiry(product.id);
            navigate(`/buyer/inquiries/${data.chatId}`);
        } catch (error) {
            toast.error(error.response?.data?.message || 'Failed to start inquiry');
        } finally {
            setLoading(prev => ({ ...prev, chat: false }));
        }
    };

    return (
        <div className="space-y-6">
            {/* Header */}
            <div>
                <h1 className="text-3xl font-bold text-slate-900">{product.title}</h1>
                <div className="flex items-center gap-4 mt-2">
                    <div className="flex items-center gap-1 text-amber-500 font-medium">
                        <span className="text-lg">{product.averageRating?.toFixed(1) || 'New'}</span>
                        <RatingStars rating={product.averageRating || 0} size={18} />
                    </div>
                    <span className="text-slate-300">|</span>
                    <span className="text-slate-600">{product.totalReviews || 0} Reviews</span>
                </div>
            </div>

            {/* Price Card */}
            <div className="bg-slate-50 rounded-2xl p-6 border border-slate-100">
                <div className="flex items-baseline gap-2">
                    <span className="text-3xl font-bold text-brand-700">
                        {formatMoney(product.basePriceINRPaise)}
                    </span>
                    <span className="text-sm text-slate-500">IND / Unit</span>
                </div>

                {product.convertedPriceMinor && (
                    <div className="mt-1 text-sm text-slate-500">
                        Approx. {product.currency} {(product.convertedPriceMinor / 100).toFixed(2)}
                        <span className="ml-2 text-xs text-slate-400">
                            (Rate: {product.exchangeRateUsed} as of {product.rateTimestamp ? formatDate(new Date(product.rateTimestamp), 'MMM dd, HH:mm') : ''})
                        </span>
                    </div>
                )}
            </div>

            {/* Key Specs Grid */}
            <div className="grid grid-cols-2 gap-4">
                <SpecItem icon={Package} label="Min Order" value={`${product.minQty} ${product.unit}`} />
                <SpecItem icon={Truck} label="Lead Time" value={`${product.leadTimeDays} Days`} />
                <SpecItem icon={Globe} label="Origin" value="India" />
                <SpecItem icon={ShieldCheck} label="Incoterm" value={product.incoterms} />
            </div>

            {/* Seller Info */}
            <div className="flex items-center p-4 bg-white border border-slate-200 rounded-2xl shadow-sm hover:border-brand-300 transition-all group/seller cursor-pointer"
                onClick={() => product.sellerId && navigate(`/sellers/${product.sellerId}`)}>
                <div className="flex-1">
                    <p className="text-[10px] text-slate-500 uppercase font-black tracking-widest mb-1">Sold By</p>
                    <h3 className="text-lg font-bold text-slate-900 flex items-center gap-2 group-hover/seller:text-brand-600 transition-colors">
                        {product.sellerCompanyName}
                        {product.iecVerified && <Badge variant="success" size="sm">IEC Verified</Badge>}
                    </h3>
                    <p className="text-xs text-brand-600 font-bold mt-0.5 opacity-0 group-hover/seller:opacity-100 transition-all transform translate-y-1 group-hover/seller:translate-y-0 flex items-center gap-1">
                        View Seller Profile <Globe size={12} />
                    </p>
                </div>
                <div className="h-10 w-10 flex items-center justify-center rounded-xl bg-slate-50 text-slate-400 group-hover/seller:bg-brand-50 group-hover/seller:text-brand-500 transition-all">
                    <Building2 size={24} />
                </div>
            </div>

            {/* Actions */}
            <div className="flex gap-4 pt-4">
                <button
                    onClick={handleStartInquiry}
                    disabled={loading.chat}
                    className="flex-1 flex items-center justify-center gap-2 px-6 py-3.5 border-2 border-brand-600 text-brand-700 font-semibold rounded-xl hover:bg-brand-50 transition-colors disabled:opacity-50"
                >
                    <MessageCircle size={20} />
                    {loading.chat ? 'Starting...' : 'Chat with Seller'}
                </button>
                <button
                    onClick={handleAddToCart}
                    disabled={loading.cart}
                    className="flex-1 flex items-center justify-center gap-2 px-6 py-3.5 bg-brand-600 text-white font-semibold rounded-xl shadow-brand-lg hover:bg-brand-700 transition-all disabled:opacity-50"
                >
                    <ShoppingCart size={20} />
                    {loading.cart ? 'Adding...' : 'Add to Cart'}
                </button>
            </div>
        </div>
    );
};

const SpecItem = ({ icon: Icon, label, value }) => (
    <div className="flex items-center gap-3 text-sm">
        <div className="p-2 bg-slate-100 rounded-lg text-slate-600">
            <Icon size={18} />
        </div>
        <div>
            <p className="text-slate-500 text-xs">{label}</p>
            <p className="font-medium text-slate-900">{value || 'N/A'}</p>
        </div>
    </div>
);

export default ProductInfoPanel;
