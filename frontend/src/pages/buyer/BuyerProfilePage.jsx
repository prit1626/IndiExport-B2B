import React, { useState, useEffect } from 'react';
import { User, MapPin, Settings, Camera, Shield, LogOut } from 'lucide-react';
import { motion, AnimatePresence } from 'framer-motion';
import toast from 'react-hot-toast';
import profileApi from '../../api/profileApi';
import { authApi } from '../../api/authApi';
import useAuthStore from '../../store/authStore';
import ProfileHeader from '../../components/profile/ProfileHeader';
import ProfileTabs from '../../components/profile/ProfileTabs';
import ProfilePhotoUploader from '../../components/profile/ProfilePhotoUploader';
import BuyerProfileForm from '../../components/profile/BuyerProfileForm';
import ProfileSkeleton from '../../components/profile/ProfileSkeleton';
import ProfileErrorState from '../../components/profile/ProfileErrorState';

const BuyerProfilePage = () => {
    const { user, refreshUser } = useAuthStore();
    const [profile, setProfile] = useState(null);
    const [loading, setLoading] = useState(true);
    const [updating, setUpdating] = useState(false);
    const [error, setError] = useState(null);
    const [activeTab, setActiveTab] = useState('profile');

    const tabs = [
        { id: 'profile', label: 'Profile Info', icon: User },
        { id: 'address', label: 'Address & Billing', icon: MapPin },
        { id: 'preferences', label: 'Preferences', icon: Settings },
        { id: 'security', label: 'Security', icon: Shield },
    ];

    useEffect(() => {
        fetchProfile();
    }, []);

    const fetchProfile = async () => {
        setLoading(true);
        try {
            const response = await profileApi.getBuyerProfile();
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
            const response = await profileApi.updateBuyerProfile(data);
            setProfile(response.data);
            await refreshUser(authApi.getMe); // Sync auth store
            toast.success("Profile updated successfully");
        } catch (err) {
            console.error("Error updating profile:", err);
            toast.error("Failed to update profile");
        } finally {
            setUpdating(false);
        }
    };

    const handlePhotoUpload = async (formData) => {
        const response = await profileApi.uploadBuyerPhoto(formData);
        setProfile(prev => ({ ...prev, profilePictureUrl: response.data.profilePhotoUrl }));
        await refreshUser(authApi.getMe);
    };

    if (loading) return <div className="container mx-auto px-4 py-8"><ProfileSkeleton /></div>;
    if (error) return <ProfileErrorState error={error} onRetry={fetchProfile} />;

    return (
        <div className="min-h-screen bg-slate-50 dark:bg-slate-950 pb-20">
            <ProfileHeader profile={profile} role="BUYER" />

            <div className="container mx-auto px-6">
                <div className="grid grid-cols-1 lg:grid-cols-4 gap-8">
                    {/* Left Sidebar - Photo & Summary */}
                    <div className="lg:col-span-1 space-y-6">
                        <div className="bg-white dark:bg-slate-900 rounded-3xl p-8 border border-slate-200 dark:border-slate-800 shadow-sm text-center">
                            <ProfilePhotoUploader
                                currentPhoto={profile?.profilePictureUrl}
                                onUpload={handlePhotoUpload}
                            />

                            <div className="mt-8 space-y-1">
                                <p className="text-sm font-bold text-slate-400 uppercase tracking-widest">Account ID</p>
                                <p className="text-[10px] font-mono text-slate-500 break-all bg-slate-50 dark:bg-slate-800 p-2 rounded-lg">{profile?.userId}</p>
                            </div>

                            <button className="w-full mt-8 flex items-center justify-center gap-2 py-3 px-4 rounded-xl border border-slate-200 dark:border-slate-800 text-rose-500 font-bold text-sm hover:bg-rose-50 dark:hover:bg-rose-950/30 transition-colors">
                                <LogOut size={18} />
                                Sign Out
                            </button>
                        </div>
                    </div>

                    {/* Main Content Area */}
                    <div className="lg:col-span-3">
                        <div className="bg-white dark:bg-slate-900 rounded-3xl border border-slate-200 dark:border-slate-800 shadow-sm overflow-hidden">
                            <ProfileTabs tabs={tabs} activeTab={activeTab} onTabChange={setActiveTab} />

                            <div className="p-8">
                                <AnimatePresence mode="wait">
                                    {activeTab === 'profile' && (
                                        <motion.div
                                            key="profile-tab"
                                            initial={{ opacity: 0, y: 10 }}
                                            animate={{ opacity: 1, y: 0 }}
                                            exit={{ opacity: 0, y: -10 }}
                                            transition={{ duration: 0.2 }}
                                        >
                                            <BuyerProfileForm
                                                initialData={profile}
                                                onSubmit={handleUpdateProfile}
                                                loading={updating}
                                            />
                                        </motion.div>
                                    )}

                                    {activeTab === 'address' && (
                                        <motion.div
                                            key="address-tab"
                                            initial={{ opacity: 0, y: 10 }}
                                            animate={{ opacity: 1, y: 0 }}
                                            exit={{ opacity: 0, y: -10 }}
                                            className="py-12 text-center"
                                        >
                                            <div className="max-w-xs mx-auto space-y-4">
                                                <div className="w-16 h-16 bg-slate-100 dark:bg-slate-800 rounded-full flex items-center justify-center mx-auto text-slate-400">
                                                    <MapPin size={32} />
                                                </div>
                                                <h4 className="text-xl font-bold text-slate-900 dark:text-white">Multiple Addresses</h4>
                                                <p className="text-slate-500 text-sm">You can manage secondary shipping addresses and billing profiles here. Coming soon.</p>
                                            </div>
                                        </motion.div>
                                    )}

                                    {activeTab === 'preferences' && (
                                        <motion.div
                                            key="preferences-tab"
                                            initial={{ opacity: 0, y: 10 }}
                                            animate={{ opacity: 1, y: 0 }}
                                            exit={{ opacity: 0, y: -10 }}
                                            className="py-12 text-center"
                                        >
                                            <div className="max-w-xs mx-auto space-y-4">
                                                <div className="w-16 h-16 bg-slate-100 dark:bg-slate-800 rounded-full flex items-center justify-center mx-auto text-slate-400">
                                                    <Settings size={32} />
                                                </div>
                                                <h4 className="text-xl font-bold text-slate-900 dark:text-white">User Preferences</h4>
                                                <p className="text-slate-500 text-sm">Notifications, marketing, and display preferences. Coming soon.</p>
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

export default BuyerProfilePage;
