import React from 'react';
import { motion } from 'framer-motion';
import { BadgeCheck, ShieldCheck, MapPin, Globe, Building2, User } from 'lucide-react';

const ProfileHeader = ({ profile, role, isPublic = false }) => {
    const isSeller = role === 'SELLER';
    const isVerified = isSeller ? profile?.iecStatus === 'VERIFIED' : true;

    return (
        <div className="relative mb-8">
            <div className="h-48 w-full bg-gradient-to-r from-indigo-600 via-purple-600 to-pink-600 rounded-3xl overflow-hidden relative">
                <div className="absolute inset-0 bg-black/10 backdrop-blur-[2px]" />
                <div className="absolute inset-0 opacity-20 bg-[url('https://www.transparenttextures.com/patterns/cubes.png')]" />
            </div>

            <div className="container mx-auto px-6 -mt-16 relative z-10">
                <div className="bg-white/80 dark:bg-slate-900/80 backdrop-blur-xl border border-white/20 dark:border-white/10 rounded-2xl p-6 shadow-2xl flex flex-col md:flex-row items-center md:items-end gap-6">
                    <div className="relative group">
                        <div className="h-32 w-32 rounded-2xl overflow-hidden border-4 border-white dark:border-slate-800 shadow-xl bg-slate-200 dark:bg-slate-700">
                            {profile?.profilePictureUrl || profile?.companyLogoUrl ? (
                                <img
                                    src={profile.profilePictureUrl || profile.companyLogoUrl}
                                    alt="Profile"
                                    className="w-full h-full object-cover"
                                />
                            ) : (
                                <div className="w-full h-full flex items-center justify-center text-slate-400">
                                    {isSeller ? <Building2 size={48} /> : <User size={48} />}
                                </div>
                            )}
                        </div>
                        {isVerified && (
                            <div className="absolute -bottom-2 -right-2 bg-blue-500 text-white p-1.5 rounded-lg shadow-lg">
                                <BadgeCheck size={18} />
                            </div>
                        )}
                    </div>

                    <div className="flex-1 text-center md:text-left">
                        <div className="flex flex-wrap items-center justify-center md:justify-start gap-3 mb-2">
                            <h1 className="text-3xl font-extrabold text-slate-900 dark:text-white leading-tight">
                                {isSeller ? profile?.companyName : `${profile?.firstName} ${profile?.lastName}`}
                            </h1>
                            {isSeller && (
                                <span className={`px-2.5 py-0.5 rounded-full text-xs font-bold uppercase tracking-wider ${profile?.currentPlan === 'ADVANCED_SELLER'
                                        ? 'bg-amber-100 text-amber-700 border border-amber-200'
                                        : 'bg-slate-100 text-slate-700 border border-slate-200'
                                    }`}>
                                    {profile?.currentPlan?.replace('_', ' ') || 'BASIC SELLER'}
                                </span>
                            )}
                        </div>

                        <div className="flex flex-wrap items-center justify-center md:justify-start gap-4 text-slate-500 dark:text-slate-400 text-sm font-medium">
                            <div className="flex items-center gap-1.5">
                                <MapPin size={16} className="text-indigo-500" />
                                {profile?.city && profile?.country ? `${profile.city}, ${profile.country}` : 'Location not set'}
                            </div>
                            {profile?.email && (
                                <div className="flex items-center gap-1.5">
                                    <Globe size={16} className="text-indigo-500" />
                                    {profile.email}
                                </div>
                            )}
                            <div className="flex items-center gap-1.5">
                                <ShieldCheck size={16} className="text-emerald-500" />
                                Member since {new Date(profile?.createdAt).getFullYear() || '2024'}
                            </div>
                        </div>
                    </div>

                    {!isPublic && (
                        <div className="flex gap-3">
                            {/* Action buttons could go here (e.g., share, preview public) */}
                        </div>
                    )}
                </div>
            </div>
        </div>
    );
};

export default ProfileHeader;
