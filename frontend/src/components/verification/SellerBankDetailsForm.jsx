
import React from 'react';
import { useForm } from 'react-hook-form';
import { Save, CreditCard } from 'lucide-react';

const SellerBankDetailsForm = ({ initialData, onSave, isSaving }) => {
    const { register, handleSubmit, formState: { errors } } = useForm({
        defaultValues: {
            bankAccountHolderName: initialData?.bankAccountHolderName || '',
            bankAccountNumber: initialData?.bankAccountNumberMasked ? '' : (initialData?.bankAccountNumber || ''), // Don't prefill masked
            bankIfscCode: initialData?.bankIfscCode || '',
            bankName: initialData?.bankName || '',
            bankBranch: initialData?.bankBranch || '',
            payoutMethodPreference: initialData?.payoutMethodPreference || 'RAZORPAY_ROUTE'
        }
    });

    const onSubmit = (data) => {
        onSave(data);
    };

    return (
        <form onSubmit={handleSubmit(onSubmit)} className="bg-white rounded-xl border border-slate-200 shadow-sm overflow-hidden mt-6">
            <div className="px-6 py-4 border-b border-slate-200 bg-slate-50 flex justify-between items-center">
                <h3 className="text-lg font-semibold text-slate-900 flex items-center gap-2">
                    <CreditCard size={20} className="text-brand-600" /> Bank Details
                </h3>
            </div>

            <div className="p-6 grid gap-6 md:grid-cols-2">
                <div>
                    <label className="block text-sm font-medium text-slate-700 mb-1">Account Holder Name</label>
                    <input
                        {...register('bankAccountHolderName', { required: 'Required' })}
                        className="w-full px-3 py-2 border border-slate-300 rounded-lg focus:ring-brand-500 focus:border-brand-500"
                        placeholder="As per bank records"
                    />
                    {errors.bankAccountHolderName && <p className="text-red-500 text-xs mt-1">{errors.bankAccountHolderName.message}</p>}
                </div>

                <div>
                    <label className="block text-sm font-medium text-slate-700 mb-1">Account Number</label>
                    <input
                        type="password"
                        {...register('bankAccountNumber', {
                            required: 'Required',
                            pattern: { value: /^[0-9]{9,18}$/, message: 'Invalid Account Number' }
                        })}
                        className="w-full px-3 py-2 border border-slate-300 rounded-lg focus:ring-brand-500 focus:border-brand-500 font-mono"
                        placeholder="Enter account number"
                    />
                    {errors.bankAccountNumber && <p className="text-red-500 text-xs mt-1">{errors.bankAccountNumber.message}</p>}
                </div>

                <div>
                    <label className="block text-sm font-medium text-slate-700 mb-1">IFSC Code</label>
                    <input
                        {...register('bankIfscCode', {
                            required: 'Required',
                            pattern: { value: /^[A-Z]{4}[0]{1}[A-Z0-9]{6}$/, message: 'Invalid IFSC Code' }
                        })}
                        className="w-full px-3 py-2 border border-slate-300 rounded-lg focus:ring-brand-500 focus:border-brand-500 uppercase"
                        placeholder="e.g. SBIN0123456"
                    />
                    {errors.bankIfscCode && <p className="text-red-500 text-xs mt-1">{errors.bankIfscCode.message}</p>}
                </div>

                <div>
                    <label className="block text-sm font-medium text-slate-700 mb-1">Bank Name</label>
                    <input
                        {...register('bankName', { required: 'Required' })}
                        className="w-full px-3 py-2 border border-slate-300 rounded-lg focus:ring-brand-500 focus:border-brand-500"
                    />
                    {errors.bankName && <p className="text-red-500 text-xs mt-1">{errors.bankName.message}</p>}
                </div>

                <div>
                    <label className="block text-sm font-medium text-slate-700 mb-1">Branch Name</label>
                    <input
                        {...register('bankBranch')}
                        className="w-full px-3 py-2 border border-slate-300 rounded-lg focus:ring-brand-500 focus:border-brand-500"
                    />
                </div>

                <div>
                    <label className="block text-sm font-medium text-slate-700 mb-1">Payout Preference</label>
                    <select
                        {...register('payoutMethodPreference', { required: 'Required' })}
                        className="w-full px-3 py-2 border border-slate-300 rounded-lg focus:ring-brand-500 focus:border-brand-500"
                    >
                        <option value="RAZORPAY_ROUTE">Razorpay Route</option>
                        <option value="STRIPE_CONNECT">Stripe Connect</option>
                        <option value="MANUAL_TRANSFER">Manual Transfer</option>
                    </select>
                </div>
            </div>

            <div className="bg-slate-50 px-6 py-4 flex justify-end">
                <button
                    type="submit"
                    disabled={isSaving}
                    className="flex items-center gap-2 px-6 py-2 bg-slate-900 text-white rounded-lg font-medium shadow hover:bg-slate-800 disabled:opacity-50 transition-all"
                >
                    <Save size={18} /> {isSaving ? 'Saving...' : 'Save Bank Details'}
                </button>
            </div>
        </form>
    );
};

export default SellerBankDetailsForm;
