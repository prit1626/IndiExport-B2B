import React, { useState } from 'react';
import { useForm } from 'react-hook-form';
import { useNavigate, Link } from 'react-router-dom';
import { Mail, Lock, Loader2, ArrowRight } from 'lucide-react';
import useAuthStore from '../../store/authStore';
import { authApi } from '../../api/authApi';
import AuthLayout from '../../components/common/AuthLayout';
import { motion } from 'framer-motion';

const SellerSignupPage = () => {
    const { register, handleSubmit, watch, formState: { errors } } = useForm();
    const [isLoading, setIsLoading] = useState(false);
    const [serverError, setServerError] = useState('');
    const setTokens = useAuthStore((state) => state.setTokens);
    const setUser = useAuthStore((state) => state.setUser);
    const navigate = useNavigate();

    const password = watch('password');

    const onSubmit = async (data) => {
        setIsLoading(true);
        setServerError('');

        const payload = {
            email: data.email,
            password: data.password,
            // Defaults will be handled by backend
        };

        try {
            const response = await authApi.signupSeller(payload);

            if (response.data && response.data.accessToken) {
                const { accessToken, refreshToken, user } = response.data;
                setTokens({ accessToken, refreshToken });
                setUser(user);
                navigate('/seller/dashboard');
            } else {
                navigate('/auth/login', {
                    state: { message: 'Seller account created successfully. Please login.' }
                });
            }

        } catch (err) {
            console.error("Signup Error:", err);
            setServerError(err.response?.data?.message || 'Signup failed. Please try again.');
        } finally {
            setIsLoading(false);
        }
    };

    return (
        <AuthLayout
            title="Join as Seller"
            subtitle="Start exporting to global buyers"
        >
            <form onSubmit={handleSubmit(onSubmit)} className="space-y-4">

                {/* Email */}
                <div className="space-y-1">
                    <label className="text-sm font-medium text-slate-700">Email Address</label>
                    <div className="relative">
                        <Mail className="absolute left-3 top-1/2 -translate-y-1/2 w-5 h-5 text-slate-400" />
                        <input
                            {...register('email', { required: 'Email is required', pattern: { value: /^\S+@\S+$/i, message: 'Invalid email' } })}
                            className={`w-full pl-10 pr-4 py-2.5 rounded-xl border focus:ring-2 focus:ring-brand-500/20 outline-none transition-all ${errors.email ? 'border-red-300' : 'border-slate-200'}`}
                            placeholder="business@example.com"
                        />
                    </div>
                    {errors.email && <p className="text-xs text-red-500 pl-1">{errors.email.message}</p>}
                </div>

                {/* Password Components */}
                <div className="space-y-4">
                    <div className="space-y-1">
                        <label className="text-sm font-medium text-slate-700">Password</label>
                        <div className="relative">
                            <Lock className="absolute left-3 top-1/2 -translate-y-1/2 w-5 h-5 text-slate-400" />
                            <input
                                {...register('password', { required: 'Password is required', minLength: { value: 6, message: 'Min 6 chars' } })}
                                type="password"
                                className={`w-full pl-10 pr-4 py-2.5 rounded-xl border focus:ring-2 focus:ring-brand-500/20 outline-none transition-all ${errors.password ? 'border-red-300' : 'border-slate-200'}`}
                                placeholder="••••••••"
                            />
                        </div>
                        {errors.password && <p className="text-xs text-red-500 pl-1">{errors.password.message}</p>}
                    </div>

                    <div className="space-y-1">
                        <label className="text-sm font-medium text-slate-700">Confirm Password</label>
                        <div className="relative">
                            <Lock className="absolute left-3 top-1/2 -translate-y-1/2 w-5 h-5 text-slate-400" />
                            <input
                                {...register('confirmPassword', {
                                    required: 'Please confirm password',
                                    validate: val => val === password || 'Passwords do not match'
                                })}
                                type="password"
                                className={`w-full pl-10 pr-4 py-2.5 rounded-xl border focus:ring-2 focus:ring-brand-500/20 outline-none transition-all ${errors.confirmPassword ? 'border-red-300' : 'border-slate-200'}`}
                                placeholder="••••••••"
                            />
                        </div>
                        {errors.confirmPassword && <p className="text-xs text-red-500 pl-1">{errors.confirmPassword.message}</p>}
                    </div>
                </div>

                {serverError && (
                    <motion.div initial={{ opacity: 0 }} animate={{ opacity: 1 }} className="text-sm text-red-600 bg-red-50 p-3 rounded-lg border border-red-100 flex items-center gap-2">
                        <span>⚠️</span> {serverError}
                    </motion.div>
                )}

                <button
                    type="submit"
                    disabled={isLoading}
                    className="w-full py-3 px-4 bg-indigo-600 hover:bg-indigo-700 text-white rounded-xl font-medium shadow-lg shadow-indigo-500/30 flex items-center justify-center gap-2 transition-all mt-6 disabled:opacity-70 disabled:cursor-not-allowed"
                >
                    {isLoading ? <Loader2 className="w-5 h-5 animate-spin" /> : 'Register as Seller'}
                </button>

                <div className="mt-4 p-3 bg-blue-50 rounded-lg text-xs text-blue-700 border border-blue-100">
                    <strong>Note:</strong> You will need to complete your profile verification (KYC, Bank Details) later in your dashboard to start selling.
                </div>

                <p className="text-center text-sm text-slate-500 mt-6">
                    Already have an account? <Link to="/auth/login" className="text-brand-600 font-medium hover:underline">Sign In</Link>
                </p>
            </form>
        </AuthLayout>
    );
};

export default SellerSignupPage;
