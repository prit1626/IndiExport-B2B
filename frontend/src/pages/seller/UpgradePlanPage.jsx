import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { Check, ShieldCheck, Zap, Globe, MessageCircle, BarChart3, Package } from 'lucide-react';
import plansApi from '../../api/plansApi';
import profileApi from '../../api/profileApi';
import { formatMoney } from '../../utils/formatMoney';
import RazorpayUpgradeButton from '../../components/payment/RazorpayUpgradeButton';

const UpgradePlanPage = () => {
    const [pricePaise, setPricePaise] = useState(299900); // Admin default
    const [isLoading, setIsLoading] = useState(true);
    const [currentPlan, setCurrentPlan] = useState(null);

    useEffect(() => {
        const fetchPricingAndProfile = async () => {
            try {
                // Fetch pricing
                const pricingRes = await plansApi.getPricing();
                if (pricingRes.data?.advancedPlanPricePaise) {
                    setPricePaise(pricingRes.data.advancedPlanPricePaise);
                }

                // Fetch seller profile for plan info
                const profileRes = await profileApi.getSellerProfile();
                if (profileRes.data?.currentPlan) {
                    setCurrentPlan(profileRes.data.currentPlan);
                }
            } catch (error) {
                console.error("Failed to fetch data:", error);
            } finally {
                setIsLoading(false);
            }
        };
        fetchPricingAndProfile();
    }, []);

    const features = [
        { icon: <Package className="text-brand-500" />, title: "Unlimited Products", desc: "List as many products as you want without restrictions." },
        { icon: <Globe className="text-brand-500" />, title: "Global Reach", desc: "Sell to buyers in over 190 countries with automated shipping." },
        { icon: <Zap className="text-brand-500" />, title: "Priority Support", desc: "Get faster 24/7 dedicated support for all your queries." },
        { icon: <MessageCircle className="text-brand-500" />, title: "Unlimited Inquiries", desc: "Respond to every buyer inquiry without message limits." },
        { icon: <BarChart3 className="text-brand-500" />, title: "Advanced Analytics", desc: "Deep insights into your shop performance and visitor trends." },
        { icon: <ShieldCheck className="text-brand-500" />, title: "Verified Badge+", desc: "Unlock premium verification trust badge on all your listings." },
    ];

    return (
        <div className="min-h-screen bg-slate-50 py-12 px-4">
            <div className="max-w-6xl mx-auto">
                {/* Header */}
                <div className="text-center mb-16">
                    <span className="inline-block px-4 py-1.5 bg-brand-100 text-brand-700 rounded-full text-sm font-bold mb-4">
                        PRICING PLANS
                    </span>
                    <h1 className="text-4xl md:text-5xl font-extrabold text-slate-900 mb-4">
                        Grow Your Export Business
                    </h1>
                    <p className="text-xl text-slate-500 max-w-2xl mx-auto">
                        Scale your operations globally with our premium tools and unlimited product listings.
                    </p>
                </div>

                <div className="grid lg:grid-cols-2 gap-12 items-center">
                    {/* Features List */}
                    <div className="space-y-8">
                        <div className="grid sm:grid-cols-2 gap-6">
                            {features.map((feature, idx) => (
                                <div key={idx} className="bg-white p-6 rounded-2xl border border-slate-200 shadow-sm hover:shadow-md transition-shadow">
                                    <div className="p-3 bg-brand-50 rounded-xl w-fit mb-4">
                                        {feature.icon}
                                    </div>
                                    <h3 className="text-lg font-bold text-slate-900 mb-2">{feature.title}</h3>
                                    <p className="text-sm text-slate-500 leading-relaxed">{feature.desc}</p>
                                </div>
                            ))}
                        </div>
                    </div>

                    {/* Pricing Card */}
                    <div className="relative">
                        <div className="absolute -top-6 left-1/2 -translate-x-1/2 z-10">
                            <span className="bg-slate-900 text-white text-xs font-bold px-4 py-2 rounded-full tracking-widest uppercase shadow-xl ring-4 ring-slate-50">
                                Recommended
                            </span>
                        </div>

                        <div className="bg-white rounded-3xl border-2 border-brand-500 p-8 md:p-12 shadow-2xl relative overflow-hidden group">
                            {/* Decorative Elements */}
                            <div className="absolute top-0 right-0 -mr-20 -mt-20 w-64 h-64 bg-brand-50 rounded-full blur-3xl opacity-50 group-hover:opacity-100 transition-opacity"></div>

                            <div className="relative z-10">
                                <div className="mb-8">
                                    <h2 className="text-3xl font-extrabold text-slate-900 mb-2">Advanced Seller</h2>
                                    <p className="text-slate-500">Perfect for established exporters.</p>
                                </div>

                                <div className="flex items-baseline gap-2 mb-10">
                                    <span className="text-5xl font-black text-slate-900">
                                        {isLoading ? "..." : formatMoney(pricePaise, 'INR')}
                                    </span>
                                    <span className="text-slate-500 font-medium">/ year</span>
                                </div>

                                <div className="space-y-4 mb-10">
                                    <div className="flex items-center gap-3 text-slate-700">
                                        <div className="flex-shrink-0 w-5 h-5 bg-green-100 text-green-600 rounded-full flex items-center justify-center">
                                            <Check size={14} />
                                        </div>
                                        <span className="font-medium font-bold">Everything in Basic</span>
                                    </div>
                                    <div className="flex items-center gap-3 text-slate-700">
                                        <div className="flex-shrink-0 w-5 h-5 bg-green-100 text-green-600 rounded-full flex items-center justify-center">
                                            <Check size={14} />
                                        </div>
                                        <span className="font-medium font-bold">Unlimited Active Products</span>
                                    </div>
                                    <div className="flex items-center gap-3 text-slate-700">
                                        <div className="flex-shrink-0 w-5 h-5 bg-green-100 text-green-600 rounded-full flex items-center justify-center">
                                            <Check size={14} />
                                        </div>
                                        <span className="font-medium font-bold">Custom Shipping Templates</span>
                                    </div>
                                    <div className="flex items-center gap-3 text-slate-700">
                                        <div className="flex-shrink-0 w-5 h-5 bg-green-100 text-green-600 rounded-full flex items-center justify-center">
                                            <Check size={14} />
                                        </div>
                                        <span className="font-medium font-bold">Advanced RFQ Management</span>
                                    </div>
                                </div>

                                <RazorpayUpgradeButton
                                    onUpgradeSuccess={() => window.location.href = '/seller/dashboard'}
                                />

                                <p className="mt-6 text-center text-sm text-slate-400">
                                    Trusted by 5,000+ Indian exporters
                                </p>
                            </div>
                        </div>

                        {/* Current Plan Section */}
                        <div className="mt-12 bg-brand-50 rounded-2xl p-6 border border-brand-200 flex items-center justify-between">
                            <div>
                                <h4 className="font-bold text-brand-800">
                                    Current Plan: {currentPlan === 'ADVANCED_SELLER' ? 'Advanced Seller' : 'Basic Seller'}
                                </h4>
                                <p className="text-sm text-brand-600">
                                    {currentPlan === 'ADVANCED_SELLER' ? 'Unlimited Active Products' : '3–5 Active Products'}
                                </p>
                            </div>
                            <div className="flex flex-col items-end gap-1">
                                <span className="px-3 py-1 bg-brand-500 text-white text-xs font-bold rounded-lg uppercase">
                                    Active
                                </span>
                                <span className="text-[10px] text-brand-400 font-medium">Status: ACTIVE</span>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    );
};

export default UpgradePlanPage;
