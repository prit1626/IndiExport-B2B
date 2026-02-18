import React, { useState } from 'react';
import { useForm } from 'react-hook-form';
import { useNavigate, Link, useLocation } from 'react-router-dom';
import { Mail, Lock, ArrowRight, Loader2 } from 'lucide-react';
import useAuthStore from '../../store/authStore';
import { authApi } from '../../api/authApi';
import AuthLayout from '../../components/common/AuthLayout';
import { motion } from 'framer-motion';

const LoginPage = () => {
    const { register, handleSubmit, formState: { errors } } = useForm();
    const [isLoading, setIsLoading] = useState(false);
    const [serverError, setServerError] = useState('');
    const login = useAuthStore((state) => state.login);
    const navigate = useNavigate();
    const location = useLocation();

    const from = location.state?.from || '/';

    const onSubmit = async (data) => {
        setIsLoading(true);
        setServerError('');

        const success = await login(authApi.login, {
            email: data.email,
            password: data.password
        });

        if (success) {
            const user = useAuthStore.getState().user;

            // If we came from a specific page (like a product page), go back there
            if (from !== '/') {
                return navigate(from, { replace: true });
            }

            if (user?.role === 'ADMIN') navigate('/admin/dashboard');
            else if (user?.role === 'SELLER') navigate('/seller/dashboard');
            else navigate('/buyer/dashboard');
        } else {
            setServerError(useAuthStore.getState().error || 'Invalid credentials');
            setIsLoading(false);
        }
    };

    return (
        <AuthLayout
            title="Welcome Back"
            subtitle="Sign in to access your dashboard"
        >
            <form onSubmit={handleSubmit(onSubmit)} className="space-y-5">

                {/* Email Field */}
                <div className="space-y-1">
                    <label className="text-sm font-medium text-slate-700">Email Address</label>
                    <div className="relative">
                        <Mail className="absolute left-3 top-1/2 -translate-y-1/2 w-5 h-5 text-slate-400" />
                        <input
                            {...register('email', {
                                required: 'Email is required',
                                pattern: {
                                    value: /^[A-Z0-9._%+-]+@[A-Z0-9.-]+\.[A-Z]{2,}$/i,
                                    message: "Invalid email address"
                                }
                            })}
                            type="email"
                            className={`w-full pl-10 pr-4 py-2.5 rounded-xl border focus:ring-2 focus:ring-brand-500/20 outline-none transition-all
                ${errors.email ? 'border-red-300 focus:border-red-500' : 'border-slate-200 focus:border-brand-500'}
              `}
                            placeholder="you@company.com"
                        />
                    </div>
                    {errors.email && <p className="text-xs text-red-500 pl-1">{errors.email.message}</p>}
                </div>

                {/* Password Field */}
                <div className="space-y-1">
                    <div className="flex justify-between">
                        <label className="text-sm font-medium text-slate-700">Password</label>
                        <Link to="/auth/forgot-password" className="text-xs text-brand-600 hover:underline">
                            Forgot password?
                        </Link>
                    </div>
                    <div className="relative">
                        <Lock className="absolute left-3 top-1/2 -translate-y-1/2 w-5 h-5 text-slate-400" />
                        <input
                            {...register('password', {
                                required: 'Password is required',
                                minLength: { value: 8, message: 'Password must be at least 8 characters' }
                            })}
                            type="password"
                            className={`w-full pl-10 pr-4 py-2.5 rounded-xl border focus:ring-2 focus:ring-brand-500/20 outline-none transition-all
                ${errors.password ? 'border-red-300 focus:border-red-500' : 'border-slate-200 focus:border-brand-500'}
              `}
                            placeholder="••••••••"
                        />
                    </div>
                    {errors.password && <p className="text-xs text-red-500 pl-1">{errors.password.message}</p>}
                </div>

                {/* Server Error */}
                {serverError && (
                    <motion.div
                        initial={{ opacity: 0, height: 0 }}
                        animate={{ opacity: 1, height: 'auto' }}
                        className="text-sm text-red-600 bg-red-50 p-3 rounded-lg border border-red-100"
                    >
                        {serverError}
                    </motion.div>
                )}

                {/* Submit Button */}
                <button
                    type="submit"
                    disabled={isLoading}
                    className="w-full py-3 px-4 bg-brand-600 hover:bg-brand-700 text-white rounded-xl font-medium shadow-lg shadow-brand-500/30 flex items-center justify-center gap-2 transition-all disabled:opacity-70 disabled:cursor-not-allowed"
                >
                    {isLoading ? (
                        <Loader2 className="w-5 h-5 animate-spin" />
                    ) : (
                        <>
                            Sign In <ArrowRight className="w-5 h-5" />
                        </>
                    )}
                </button>

                {/* Footer Links */}
                <div className="text-center text-sm text-slate-500 mt-6">
                    Don't have an account?{' '}
                    <div className="flex justify-center gap-4 mt-2">
                        <Link to="/auth/signup/buyer" className="text-brand-600 font-medium hover:underline">
                            Join as Buyer
                        </Link>
                        <span className="text-slate-300">|</span>
                        <Link to="/auth/signup/seller" className="text-brand-600 font-medium hover:underline">
                            Join as Seller
                        </Link>
                    </div>
                </div>
            </form>
        </AuthLayout>
    );
};

export default LoginPage;
