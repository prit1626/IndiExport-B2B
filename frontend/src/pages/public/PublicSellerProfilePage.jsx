import React, { useState, useEffect } from 'react';
import { useParams } from 'react-router-dom';
import { Building2, MapPin, Globe, ShieldCheck, Mail, Phone, Package, Star, ArrowLeft } from 'lucide-react';
import { motion } from 'framer-motion';
import profileApi from '../../api/profileApi';
import productApi from '../../api/productApi';
import ProfileHeader from '../../components/profile/ProfileHeader';
import ProductCard from '../../components/products/ProductCard';
import ProductGridSkeleton from '../../components/products/ProductGridSkeleton';
import ProfileSkeleton from '../../components/profile/ProfileSkeleton';
import ProfileErrorState from '../../components/profile/ProfileErrorState';

const PublicSellerProfilePage = () => {
    const { id } = useParams();
    const [profile, setProfile] = useState(null);
    const [products, setProducts] = useState([]);
    const [loading, setLoading] = useState(true);
    const [loadingProducts, setLoadingProducts] = useState(true);
    const [error, setError] = useState(null);

    useEffect(() => {
        if (id) {
            fetchData();
        }
    }, [id]);

    const fetchData = async () => {
        setLoading(true);
        setLoadingProducts(true);
        try {
            const profileData = await profileApi.getPublicSellerProfile(id);
            setProfile(profileData);

            const productData = await productApi.getProducts({ sellerId: id, size: 20 });
            setProducts(productData.content || []);

            setError(null);
        } catch (err) {
            console.error("Error fetching public profile:", err);
            setError(err);
        } finally {
            setLoading(false);
            setLoadingProducts(false);
        }
    };

    if (loading) return <div className="container mx-auto px-4 py-8"><ProfileSkeleton /></div>;
    if (error) return <ProfileErrorState error={error} onRetry={fetchData} />;

    return (
        <div className="min-h-screen bg-slate-50 dark:bg-slate-950 pb-20">
            <ProfileHeader profile={profile} role="SELLER" isPublic={true} />

            <div className="container mx-auto px-6">
                <div className="grid grid-cols-1 lg:grid-cols-4 gap-8">
                    {/* Left - Detailed Info */}
                    <div className="lg:col-span-1 space-y-6">
                        <div className="bg-white dark:bg-slate-900 rounded-3xl p-6 border border-slate-200 dark:border-slate-800 shadow-sm">
                            <h3 className="text-sm font-bold text-slate-400 uppercase tracking-widest mb-6">About Company</h3>

                            <div className="space-y-6">
                                {profile?.website && (
                                    <div className="flex items-start gap-3">
                                        <div className="p-2 bg-indigo-50 dark:bg-indigo-900/30 rounded-lg text-indigo-600 dark:text-indigo-400 shrink-0">
                                            <Globe size={18} />
                                        </div>
                                        <div className="min-w-0">
                                            <p className="text-[10px] font-bold text-slate-400 uppercase">Website</p>
                                            <a href={profile.website} target="_blank" rel="noopener noreferrer" className="text-sm font-medium text-indigo-600 truncate block hover:underline">
                                                {profile.website.replace(/^https?:\/\//, '')}
                                            </a>
                                        </div>
                                    </div>
                                )}

                                <div className="flex items-start gap-3">
                                    <div className="p-2 bg-emerald-50 dark:bg-emerald-900/30 rounded-lg text-emerald-600 dark:text-emerald-400 shrink-0">
                                        <MapPin size={18} />
                                    </div>
                                    <div>
                                        <p className="text-[10px] font-bold text-slate-400 uppercase">Registered Address</p>
                                        <p className="text-sm font-medium text-slate-700 dark:text-slate-300 leading-relaxed">
                                            {profile?.address}, {profile?.city}, {profile?.state} {profile?.postalCode}, {profile?.country}
                                        </p>
                                    </div>
                                </div>

                                <div className="flex items-start gap-3">
                                    <div className="p-2 bg-amber-50 dark:bg-amber-900/30 rounded-lg text-amber-600 dark:text-amber-400 shrink-0">
                                        <ShieldCheck size={18} />
                                    </div>
                                    <div>
                                        <p className="text-[10px] font-bold text-slate-400 uppercase">Compliance Status</p>
                                        <p className="text-sm font-bold text-slate-700 dark:text-slate-300">
                                            {profile?.iecStatus === 'VERIFIED' ? 'IEC Verified Exporter' : 'Registered Business'}
                                        </p>
                                    </div>
                                </div>
                            </div>
                        </div>

                        <div className="bg-white dark:bg-slate-900 rounded-3xl p-6 border border-slate-200 dark:border-slate-800 shadow-sm">
                            <h3 className="text-sm font-bold text-slate-400 uppercase tracking-widest mb-4">Seller Statistics</h3>
                            <div className="space-y-4">
                                <div className="flex justify-between items-center text-sm">
                                    <span className="text-slate-500 font-medium">Total Products</span>
                                    <span className="text-slate-900 dark:text-white font-bold">{profile?.totalProducts || 0}</span>
                                </div>
                                <div className="flex justify-between items-center text-sm">
                                    <span className="text-slate-500 font-medium">Average Rating</span>
                                    <div className="flex items-center gap-1 text-amber-500 font-bold">
                                        <Star size={14} fill="currentColor" />
                                        {(profile?.averageRatingMilli / 1000).toFixed(1) || '0.0'}
                                    </div>
                                </div>
                                <div className="flex justify-between items-center text-sm">
                                    <span className="text-slate-500 font-medium">Verified since</span>
                                    <span className="text-slate-900 dark:text-white font-bold">{new Date(profile?.createdAt).getFullYear()}</span>
                                </div>
                            </div>
                        </div>
                    </div>

                    {/* Right - Products Grid */}
                    <div className="lg:col-span-3 space-y-8">
                        <div className="flex items-center justify-between">
                            <h2 className="text-2xl font-black text-slate-900 dark:text-white flex items-center gap-3">
                                <Package className="text-indigo-600" />
                                Featured Products
                                <span className="text-sm font-bold bg-slate-100 dark:bg-slate-800 px-3 py-1 rounded-full text-slate-500">
                                    {products.length}
                                </span>
                            </h2>
                        </div>

                        {loadingProducts ? (
                            <ProductGridSkeleton />
                        ) : products.length > 0 ? (
                            <div className="grid grid-cols-1 sm:grid-cols-2 xl:grid-cols-3 gap-6">
                                {products.map((product, idx) => (
                                    <motion.div
                                        key={product.id}
                                        initial={{ opacity: 0, y: 20 }}
                                        animate={{ opacity: 1, y: 0 }}
                                        transition={{ delay: idx * 0.05 }}
                                    >
                                        <ProductCard product={product} />
                                    </motion.div>
                                ))}
                            </div>
                        ) : (
                            <div className="flex flex-col items-center justify-center py-20 bg-white dark:bg-slate-900 rounded-3xl border border-dashed border-slate-200 dark:border-slate-800">
                                <Package size={48} className="text-slate-200 mb-4" />
                                <h3 className="text-lg font-bold text-slate-900 dark:text-white">No products found</h3>
                                <p className="text-slate-500 text-sm">This seller hasn't listed any products yet.</p>
                            </div>
                        )}
                    </div>
                </div>
            </div>
        </div>
    );
};

export default PublicSellerProfilePage;
