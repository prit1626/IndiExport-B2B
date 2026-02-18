import React, { useState, useEffect } from 'react';
import { User, Building2, Zap, Banknote, ShieldCheck, Camera, LogOut } from 'lucide-react';
import { motion, AnimatePresence } from 'framer-motion';
import toast from 'react-hot-toast';
import profileApi from '../../api/profileApi';
import { authApi } from '../../api/authApi';
import useAuthStore from '../../store/authStore';
import ProfileHeader from '../../components/profile/ProfileHeader';
import ProfileTabs from '../../components/profile/ProfileTabs';
import ProfilePhotoUploader from '../../components/profile/ProfilePhotoUploader';
import SellerProfileForm from '../../components/profile/SellerProfileForm';
import ComplianceInfoCard from '../../components/profile/ComplianceInfoCard';
import PlanInfoCard from '../../components/profile/PlanInfoCard';
import PayoutInfoCard from '../../components/profile/PayoutInfoCard';
import ProfileSkeleton from '../../components/profile/ProfileSkeleton';
import ProfileErrorState from '../../components/profile/ProfileErrorState';

const SellerProfilePage = () => {
    const { user, refreshUser } = useAuthStore();
    const [profile, setProfile] = useState(null);
    const [loading, setLoading] = useState(true);
    const [updating, setUpdating] = useState(false);
    const [error, setError] = useState(null);
    const [activeTab, setActiveTab] = useState('company');

    const tabs = [
        { id: 'company', label: 'Company Info', icon: Building2 },
        { id: 'compliance', label: 'Compliance & KYC', icon: ShieldCheck },
        { id: 'billing', label: 'Billing & Payouts', icon: Banknote },
        { id: 'plan', label: 'Plan & Tier', icon: Zap },
    ];

    useEffect(() => {
        fetchProfile();
    }, []);

    const fetchProfile = async () => {
        setLoading(true);
        try {
            const response = await profileApi.getSellerProfile();
            setProfile(response.data);
            setError(null);
        } catch (err) {
            console.error("Error fetching profile:", err);
            setError(err);
        } finally {
            setLoading(false);
        }
    };

    const handleUpdateProfile = async (data) => {
        setUpdating(true);
        try {
            const response = await profileApi.updateSellerProfile(data);
            setProfile(response.data);
            await refreshUser(authApi.getMe);
            toast.success("Company profile updated");
        } catch (err) {
            console.error("Error updating profile:", err);
            toast.error("Failed to update profile");
        } finally {
            setUpdating(false);
        }
    };

    const handleLogoUpload = async (formData) => {
        const response = await profileApi.uploadSellerLogo(formData);
        setProfile(prev => ({ ...prev, companyLogoUrl: response.data.logoUrl }));
        await refreshUser(authApi.getMe);
    };

    if (loading) return <div className="container mx-auto px-4 py-8"><ProfileSkeleton /></div>;
    if (error) return <ProfileErrorState error={error} onRetry={fetchProfile} />;

    return (
        <div className="min-h-screen bg-slate-50 dark:bg-slate-950 pb-20">
            <ProfileHeader profile={profile} role="SELLER" />

            <div className="container mx-auto px-6">
                <div className="grid grid-cols-1 lg:grid-cols-4 gap-8">
                    {/* Left Sidebar */}
                    <div className="lg:col-span-1 space-y-6">
                        <div className="bg-white dark:bg-slate-900 rounded-3xl p-8 border border-slate-200 dark:border-slate-800 shadow-sm text-center">
                            <ProfilePhotoUploader
                                currentPhoto={profile?.companyLogoUrl}
                                onUpload={handleLogoUpload}
                                type="seller"
                            />

                            <div className="mt-8 grid grid-cols-2 gap-4">
                                <div className="p-3 bg-slate-50 dark:bg-slate-800 rounded-2xl border border-slate-100 dark:border-slate-700">
                                    <p className="text-[10px] font-bold text-slate-400 uppercase tracking-wider mb-1">Products</p>
                                    <p className="text-xl font-black text-slate-900 dark:text-white">{profile?.totalProducts || 0}</p>
                                </div>
                                <div className="p-3 bg-slate-50 dark:bg-slate-800 rounded-2xl border border-slate-100 dark:border-slate-700">
                                    <p className="text-[10px] font-bold text-slate-400 uppercase tracking-wider mb-1">Rating</p>
                                    <p className="text-xl font-black text-slate-900 dark:text-white">{(profile?.averageRatingMilli / 1000).toFixed(1) || '0.0'}</p>
                                </div>
                            </div>

                            <button className="w-full mt-8 flex items-center justify-center gap-2 py-3 px-4 rounded-xl border border-slate-200 dark:border-slate-800 text-rose-500 font-bold text-sm hover:bg-rose-50 dark:hover:bg-rose-950/30 transition-colors">
                                <LogOut size={18} />
                                Logout Session
                            </button>
                        </div>

                        <div className="bg-indigo-600 rounded-3xl p-6 text-white shadow-xl shadow-indigo-600/20">
                            <h4 className="font-bold flex items-center gap-2 mb-2">
                                <Zap size={18} />
                                Seller Tip
                            </h4>
                            <p className="text-xs text-indigo-100 leading-relaxed font-medium">
                                Companies with a verified IEC and high-quality logo receive 40% more inquiries from global buyers.
                            </p>
                        </div>
                    </div>

                    {/* Main Content Area */}
                    <div className="lg:col-span-3">
                        <div className="bg-white dark:bg-slate-900 rounded-3xl border border-slate-200 dark:border-slate-800 shadow-sm overflow-hidden min-h-[600px]">
                            <ProfileTabs tabs={tabs} activeTab={activeTab} onTabChange={setActiveTab} />

                            <div className="p-8">
                                <AnimatePresence mode="wait">
                                    {activeTab === 'company' && (
                                        <motion.div
                                            key="company-tab"
                                            initial={{ opacity: 0, x: 20 }}
                                            animate={{ opacity: 1, x: 0 }}
                                            exit={{ opacity: 0, x: -20 }}
                                        >
                                            <SellerProfileForm
                                                initialData={profile}
                                                onSubmit={handleUpdateProfile}
                                                loading={updating}
                                            />
                                        </motion.div>
                                    )}

                                    {activeTab === 'compliance' && (
                                        <motion.div
                                            key="compliance-tab"
                                            initial={{ opacity: 0, x: 20 }}
                                            animate={{ opacity: 1, x: 0 }}
                                            exit={{ opacity: 0, x: -20 }}
                                        >
                                            <div className="max-w-2xl">
                                                <ComplianceInfoCard profile={profile} />
                                            </div>
                                        </motion.div>
                                    )}

                                    {activeTab === 'billing' && (
                                        <motion.div
                                            key="billing-tab"
                                            initial={{ opacity: 0, x: 20 }}
                                            animate={{ opacity: 1, x: 0 }}
                                            exit={{ opacity: 0, x: -20 }}
                                        >
                                            <div className="max-w-2xl">
                                                <PayoutInfoCard profile={profile} />
                                            </div>
                                        </motion.div>
                                    )}

                                    {activeTab === 'plan' && (
                                        <motion.div
                                            key="plan-tab"
                                            initial={{ opacity: 0, x: 20 }}
                                            animate={{ opacity: 1, x: 0 }}
                                            exit={{ opacity: 0, x: -20 }}
                                        >
                                            <div className="max-w-2xl">
                                                <PlanInfoCard profile={profile} />
                                            </div>
                                        </motion.div>
                                    )}
                                </AnimatePresence>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    );
};

export default SellerProfilePage;
