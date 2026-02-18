import React from 'react';
import { useForm } from 'react-hook-form';
import { Save, User, Building2, MapPin, CreditCard, Loader2 } from 'lucide-react';
import { requiredValidator, phoneValidator, postalCodeValidator } from '../../utils/validators';

const BuyerProfileForm = ({ initialData, onSubmit, loading }) => {
    const { register, handleSubmit, formState: { errors, isDirty } } = useForm({
        defaultValues: {
            firstName: initialData?.firstName || '',
            lastName: initialData?.lastName || '',
            phoneNumber: initialData?.phoneNumber || '',
            companyName: initialData?.companyName || '',
            country: initialData?.country || 'US',
            state: initialData?.state || '',
            city: initialData?.city || '',
            address: initialData?.address || '',
            postalCode: initialData?.postalCode || '',
            preferredCurrency: initialData?.preferredCurrency || 'USD'
        }
    });

    return (
        <form onSubmit={handleSubmit(onSubmit)} className="space-y-8 animate-in fade-in slide-in-from-bottom-4 duration-500">
            {/* Personal Information */}
            <div className="bg-white dark:bg-slate-900 rounded-2xl p-6 border border-slate-200 dark:border-slate-800 shadow-sm">
                <div className="flex items-center gap-2 mb-6">
                    <div className="p-2 bg-indigo-50 dark:bg-indigo-900/30 rounded-lg text-indigo-600 dark:text-indigo-400">
                        <User size={20} />
                    </div>
                    <h3 className="text-lg font-bold text-slate-900 dark:text-white">Personal Information</h3>
                </div>

                <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
                    <div className="space-y-2">
                        <label className="text-sm font-semibold text-slate-700 dark:text-slate-300">First Name</label>
                        <input
                            {...register('firstName', requiredValidator('First name'))}
                            className={`w-full px-4 py-3 rounded-xl bg-slate-50 dark:bg-slate-800 border ${errors.firstName ? 'border-rose-500' : 'border-slate-200 dark:border-slate-700'} focus:ring-2 focus:ring-indigo-500 outline-none transition-all`}
                        />
                        {errors.firstName && <p className="text-xs font-medium text-rose-500">{errors.firstName.message}</p>}
                    </div>

                    <div className="space-y-2">
                        <label className="text-sm font-semibold text-slate-700 dark:text-slate-300">Last Name</label>
                        <input
                            {...register('lastName', requiredValidator('Last name'))}
                            className={`w-full px-4 py-3 rounded-xl bg-slate-50 dark:bg-slate-800 border ${errors.lastName ? 'border-rose-500' : 'border-slate-200 dark:border-slate-700'} focus:ring-2 focus:ring-indigo-500 outline-none transition-all`}
                        />
                        {errors.lastName && <p className="text-xs font-medium text-rose-500">{errors.lastName.message}</p>}
                    </div>

                    <div className="space-y-2">
                        <label className="text-sm font-semibold text-slate-700 dark:text-slate-300">Contact Number</label>
                        <input
                            {...register('phoneNumber', phoneValidator)}
                            placeholder="+1234567890"
                            className={`w-full px-4 py-3 rounded-xl bg-slate-50 dark:bg-slate-800 border ${errors.phoneNumber ? 'border-rose-500' : 'border-slate-200 dark:border-slate-700'} focus:ring-2 focus:ring-indigo-500 outline-none transition-all`}
                        />
                        {errors.phoneNumber && <p className="text-xs font-medium text-rose-500">{errors.phoneNumber.message}</p>}
                    </div>

                    <div className="space-y-2">
                        <label className="text-sm font-semibold text-slate-700 dark:text-slate-300">Email Address</label>
                        <input
                            value={initialData?.email || ''}
                            disabled
                            className="w-full px-4 py-3 rounded-xl bg-slate-100 dark:bg-slate-800/50 border border-slate-200 dark:border-slate-700 text-slate-500 cursor-not-allowed italic"
                        />
                        <p className="text-[10px] text-slate-400">Email cannot be changed.</p>
                    </div>
                </div>
            </div>

            {/* Company & Billing */}
            <div className="bg-white dark:bg-slate-900 rounded-2xl p-6 border border-slate-200 dark:border-slate-800 shadow-sm">
                <div className="flex items-center gap-2 mb-6">
                    <div className="p-2 bg-amber-50 dark:bg-amber-900/30 rounded-lg text-amber-600 dark:text-amber-400">
                        <Building2 size={20} />
                    </div>
                    <h3 className="text-lg font-bold text-slate-900 dark:text-white">Business Details</h3>
                </div>

                <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
                    <div className="space-y-2 md:col-span-2">
                        <label className="text-sm font-semibold text-slate-700 dark:text-slate-300">Company Name</label>
                        <input
                            {...register('companyName')}
                            placeholder="Optional"
                            className="w-full px-4 py-3 rounded-xl bg-slate-50 dark:bg-slate-800 border border-slate-200 dark:border-slate-700 focus:ring-2 focus:ring-indigo-500 outline-none transition-all"
                        />
                    </div>

                    <div className="space-y-2">
                        <label className="text-sm font-semibold text-slate-700 dark:text-slate-300">Country</label>
                        <input
                            {...register('country', requiredValidator('Country'))}
                            placeholder="ISO Code (e.g., US, DE, IN)"
                            className={`w-full px-4 py-3 rounded-xl bg-slate-50 dark:bg-slate-800 border ${errors.country ? 'border-rose-500' : 'border-slate-200 dark:border-slate-700'} focus:ring-2 focus:ring-indigo-500 outline-none transition-all`}
                        />
                    </div>

                    <div className="space-y-2">
                        <label className="text-sm font-semibold text-slate-700 dark:text-slate-300">Preferred Currency</label>
                        <select
                            {...register('preferredCurrency')}
                            className="w-full px-4 py-3 rounded-xl bg-slate-50 dark:bg-slate-800 border border-slate-200 dark:border-slate-700 focus:ring-2 focus:ring-indigo-500 outline-none transition-all appearance-none"
                        >
                            <option value="USD">USD - US Dollar</option>
                            <option value="EUR">EUR - Euro</option>
                            <option value="INR">INR - Indian Rupee</option>
                            <option value="GBP">GBP - British Pound</option>
                            <option value="AUD">AUD - Australian Dollar</option>
                        </select>
                    </div>
                </div>
            </div>

            {/* Address */}
            <div className="bg-white dark:bg-slate-900 rounded-2xl p-6 border border-slate-200 dark:border-slate-800 shadow-sm">
                <div className="flex items-center gap-2 mb-6">
                    <div className="p-2 bg-emerald-50 dark:bg-emerald-900/30 rounded-lg text-emerald-600 dark:text-emerald-400">
                        <MapPin size={20} />
                    </div>
                    <h3 className="text-lg font-bold text-slate-900 dark:text-white">Address Information</h3>
                </div>

                <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
                    <div className="space-y-2 md:col-span-2">
                        <label className="text-sm font-semibold text-slate-700 dark:text-slate-300">Street Address</label>
                        <textarea
                            {...register('address')}
                            rows={3}
                            className="w-full px-4 py-3 rounded-xl bg-slate-50 dark:bg-slate-800 border border-slate-200 dark:border-slate-700 focus:ring-2 focus:ring-indigo-500 outline-none transition-all resize-none"
                        />
                    </div>

                    <div className="space-y-2">
                        <label className="text-sm font-semibold text-slate-700 dark:text-slate-300">City</label>
                        <input
                            {...register('city')}
                            className="w-full px-4 py-3 rounded-xl bg-slate-50 dark:bg-slate-800 border border-slate-200 dark:border-slate-700 focus:ring-2 focus:ring-indigo-500 outline-none transition-all"
                        />
                    </div>

                    <div className="space-y-2">
                        <label className="text-sm font-semibold text-slate-700 dark:text-slate-300">State / Province</label>
                        <input
                            {...register('state')}
                            className="w-full px-4 py-3 rounded-xl bg-slate-50 dark:bg-slate-800 border border-slate-200 dark:border-slate-700 focus:ring-2 focus:ring-indigo-500 outline-none transition-all"
                        />
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
                    Save Changes
                </button>
            </div>
        </form>
    );
};

export default BuyerProfileForm;
