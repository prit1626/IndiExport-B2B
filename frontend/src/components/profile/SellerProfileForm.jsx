import React from 'react';
import { useForm } from 'react-hook-form';
import { Save, Building2, Globe, Mail, Phone, MapPin, Loader2 } from 'lucide-react';
import { requiredValidator, phoneValidator, postalCodeValidator, urlValidator, emailValidator } from '../../utils/validators';

const SellerProfileForm = ({ initialData, onSubmit, loading }) => {
    const { register, handleSubmit, formState: { errors, isDirty } } = useForm({
        defaultValues: {
            companyName: initialData?.companyName || '',
            website: initialData?.website || '',
            businessEmail: initialData?.businessEmail || '',
            businessPhone: initialData?.businessPhone || '',
            address: initialData?.address || '',
            city: initialData?.city || '',
            state: initialData?.state || '',
            postalCode: initialData?.postalCode || '',
            country: initialData?.country || 'IN'
        }
    });

    return (
        <form onSubmit={handleSubmit(onSubmit)} className="space-y-8 animate-in fade-in slide-in-from-bottom-4 duration-500">
            {/* Business Information */}
            <div className="bg-white dark:bg-slate-900 rounded-2xl p-6 border border-slate-200 dark:border-slate-800 shadow-sm">
                <div className="flex items-center gap-2 mb-6">
                    <div className="p-2 bg-indigo-50 dark:bg-indigo-900/30 rounded-lg text-indigo-600 dark:text-indigo-400">
                        <Building2 size={20} />
                    </div>
                    <h3 className="text-lg font-bold text-slate-900 dark:text-white">Business Information</h3>
                </div>

                <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
                    <div className="space-y-2 md:col-span-2">
                        <label className="text-sm font-semibold text-slate-700 dark:text-slate-300">Company Name</label>
                        <input
                            {...register('companyName', requiredValidator('Company name'))}
                            className={`w-full px-4 py-3 rounded-xl bg-slate-50 dark:bg-slate-800 border ${errors.companyName ? 'border-rose-500' : 'border-slate-200 dark:border-slate-700'} focus:ring-2 focus:ring-indigo-500 outline-none transition-all`}
                        />
                        {errors.companyName && <p className="text-xs font-medium text-rose-500">{errors.companyName.message}</p>}
                    </div>

                    <div className="space-y-2">
                        <label className="text-sm font-semibold text-slate-700 dark:text-slate-300">Website</label>
                        <div className="relative">
                            <Globe className="absolute left-4 top-1/2 -translate-y-1/2 text-slate-400" size={18} />
                            <input
                                {...register('website', urlValidator)}
                                placeholder="https://example.com"
                                className={`w-full pl-11 pr-4 py-3 rounded-xl bg-slate-50 dark:bg-slate-800 border ${errors.website ? 'border-rose-500' : 'border-slate-200 dark:border-slate-700'} focus:ring-2 focus:ring-indigo-500 outline-none transition-all`}
                            />
                        </div>
                        {errors.website && <p className="text-xs font-medium text-rose-500">{errors.website.message}</p>}
                    </div>

                    <div className="space-y-2">
                        <label className="text-sm font-semibold text-slate-700 dark:text-slate-300">Business Email</label>
                        <div className="relative">
                            <Mail className="absolute left-4 top-1/2 -translate-y-1/2 text-slate-400" size={18} />
                            <input
                                {...register('businessEmail', emailValidator)}
                                className={`w-full pl-11 pr-4 py-3 rounded-xl bg-slate-50 dark:bg-slate-800 border ${errors.businessEmail ? 'border-rose-500' : 'border-slate-200 dark:border-slate-700'} focus:ring-2 focus:ring-indigo-500 outline-none transition-all`}
                            />
                        </div>
                        {errors.businessEmail && <p className="text-xs font-medium text-rose-500">{errors.businessEmail.message}</p>}
                    </div>

                    <div className="space-y-2">
                        <label className="text-sm font-semibold text-slate-700 dark:text-slate-300">Business Phone</label>
                        <div className="relative">
                            <Phone className="absolute left-4 top-1/2 -translate-y-1/2 text-slate-400" size={18} />
                            <input
                                {...register('businessPhone', phoneValidator)}
                                placeholder="+919876543210"
                                className={`w-full pl-11 pr-4 py-3 rounded-xl bg-slate-50 dark:bg-slate-800 border ${errors.businessPhone ? 'border-rose-500' : 'border-slate-200 dark:border-slate-700'} focus:ring-2 focus:ring-indigo-500 outline-none transition-all`}
                            />
                        </div>
                        {errors.businessPhone && <p className="text-xs font-medium text-rose-500">{errors.businessPhone.message}</p>}
                    </div>

                    <div className="space-y-2">
                        <label className="text-sm font-semibold text-slate-700 dark:text-slate-300">Country</label>
                        <input
                            value="India (IN)"
                            disabled
                            className="w-full px-4 py-3 rounded-xl bg-slate-100 dark:bg-slate-800/50 border border-slate-200 dark:border-slate-700 text-slate-500 cursor-not-allowed italic"
                        />
                        <p className="text-[10px] text-slate-400">Sellers must be based in India.</p>
                    </div>
                </div>
            </div>

            {/* Address */}
            <div className="bg-white dark:bg-slate-900 rounded-2xl p-6 border border-slate-200 dark:border-slate-800 shadow-sm">
                <div className="flex items-center gap-2 mb-6">
                    <div className="p-2 bg-emerald-50 dark:bg-emerald-900/30 rounded-lg text-emerald-600 dark:text-emerald-400">
                        <MapPin size={20} />
                    </div>
                    <h3 className="text-lg font-bold text-slate-900 dark:text-white">Registered Address</h3>
                </div>

                <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
                    <div className="space-y-2 md:col-span-2">
                        <label className="text-sm font-semibold text-slate-700 dark:text-slate-300">Street Address</label>
                        <textarea
                            {...register('address', requiredValidator('Address'))}
                            rows={3}
                            className={`w-full px-4 py-3 rounded-xl bg-slate-50 dark:bg-slate-800 border ${errors.address ? 'border-rose-500' : 'border-slate-200 dark:border-slate-700'} focus:ring-2 focus:ring-indigo-500 outline-none transition-all resize-none`}
                        />
                        {errors.address && <p className="text-xs font-medium text-rose-500">{errors.address.message}</p>}
                    </div>

                    <div className="space-y-2">
                        <label className="text-sm font-semibold text-slate-700 dark:text-slate-300">City</label>
                        <input
                            {...register('city', requiredValidator('City'))}
                            className={`w-full px-4 py-3 rounded-xl bg-slate-50 dark:bg-slate-800 border ${errors.city ? 'border-rose-500' : 'border-slate-200 dark:border-slate-700'} focus:ring-2 focus:ring-indigo-500 outline-none transition-all`}
                        />
                        {errors.city && <p className="text-xs font-medium text-rose-500">{errors.city.message}</p>}
                    </div>

                    <div className="space-y-2">
                        <label className="text-sm font-semibold text-slate-700 dark:text-slate-300">State</label>
                        <input
                            {...register('state', requiredValidator('State'))}
                            className={`w-full px-4 py-3 rounded-xl bg-slate-50 dark:bg-slate-800 border ${errors.state ? 'border-rose-500' : 'border-slate-200 dark:border-slate-700'} focus:ring-2 focus:ring-indigo-500 outline-none transition-all`}
                        />
                        {errors.state && <p className="text-xs font-medium text-rose-500">{errors.state.message}</p>}
                    </div>

                    <div className="space-y-2">
                        <label className="text-sm font-semibold text-slate-700 dark:text-slate-300">Postal Code</label>
                        <input
                            {...register('postalCode', postalCodeValidator)}
                            className={`w-full px-4 py-3 rounded-xl bg-slate-50 dark:bg-slate-800 border ${errors.postalCode ? 'border-rose-500' : 'border-slate-200 dark:border-slate-700'} focus:ring-2 focus:ring-indigo-500 outline-none transition-all`}
                        />
                        {errors.postalCode && <p className="text-xs font-medium text-rose-500">{errors.postalCode.message}</p>}
                    </div>
                </div>
            </div>

            <div className="flex justify-end pt-4 pb-12">
                <button
                    type="submit"
                    disabled={loading || !isDirty}
                    className={`flex items-center gap-2 px-8 py-3.5 rounded-2xl font-bold text-white shadow-xl transition-all active:scale-95 ${loading || !isDirty
                            ? 'bg-slate-400 cursor-not-allowed grayscale'
                            : 'bg-indigo-600 hover:bg-indigo-700 hover:shadow-indigo-500/25'
                        }`}
                >
                    {loading ? <Loader2 className="animate-spin" size={20} /> : <Save size={20} />}
                    Update Profile
                </button>
            </div>
        </form>
    );
};

export default SellerProfileForm;
