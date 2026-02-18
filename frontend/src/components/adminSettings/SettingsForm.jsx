
import React, { useEffect } from 'react';
import { useForm } from 'react-hook-form';
import { Save, IndianRupee, Percent, ShieldAlert, Clock } from 'lucide-react';
import { toast } from 'react-hot-toast';

const SettingsForm = ({ initialData, onSave, isSaving }) => {
    const { register, handleSubmit, formState: { errors }, reset } = useForm({
        defaultValues: initialData
    });

    // Reset form when initialData loads/changes
    useEffect(() => {
        if (initialData) {
            reset({
                advancedSellerPlanPriceINRPaise: initialData.advancedSellerPlanPriceINRPaise / 100, // Convert to Rupees for display
                platformCommissionPercent: initialData.platformCommissionPercent,
                disputeWindowDays: initialData.disputeWindowDays,
                autoReleaseDays: initialData.autoReleaseDays
            });
        }
    }, [initialData, reset]);

    const onSubmit = (data) => {
        // Convert back to paise for API
        const payload = {
            ...data,
            advancedSellerPlanPriceINRPaise: Math.round(data.advancedSellerPlanPriceINRPaise * 100)
        };
        onSave(payload);
    };

    return (
        <form onSubmit={handleSubmit(onSubmit)} className="max-w-4xl mx-auto space-y-6">

            {/* Section 1: Financials */}
            <div className="bg-white rounded-xl border border-slate-200 shadow-sm overflow-hidden">
                <div className="px-6 py-4 border-b border-slate-200 bg-slate-50">
                    <h3 className="text-lg font-semibold text-slate-900 flex items-center gap-2">
                        <IndianRupee size={20} className="text-brand-600" /> Financial Settings
                    </h3>
                </div>
                <div className="p-6 grid gap-6 md:grid-cols-2">
                    <div>
                        <label className="block text-sm font-medium text-slate-700 mb-1">
                            Advanced Seller Plan Price (₹)
                        </label>
                        <div className="relative">
                            <div className="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none">
                                <span className="text-slate-500">₹</span>
                            </div>
                            <input
                                type="number"
                                step="0.01"
                                {...register('advancedSellerPlanPriceINRPaise', {
                                    required: 'Price is required',
                                    min: { value: 0, message: 'Price cannot be negative' }
                                })}
                                className="w-full pl-8 pr-3 py-2 border border-slate-300 rounded-lg focus:ring-brand-500 focus:border-brand-500"
                            />
                        </div>
                        {errors.advancedSellerPlanPriceINRPaise && <p className="text-red-500 text-xs mt-1">{errors.advancedSellerPlanPriceINRPaise.message}</p>}
                    </div>

                    <div>
                        <label className="block text-sm font-medium text-slate-700 mb-1">
                            Platform Commission (%)
                        </label>
                        <div className="relative">
                            <div className="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none">
                                <Percent size={16} className="text-slate-500" />
                            </div>
                            <input
                                type="number"
                                step="0.1"
                                {...register('platformCommissionPercent', {
                                    required: 'Commission is required',
                                    min: { value: 0, message: 'Min 0%' },
                                    max: { value: 100, message: 'Max 100%' }
                                })}
                                className="w-full pl-10 pr-3 py-2 border border-slate-300 rounded-lg focus:ring-brand-500 focus:border-brand-500"
                            />
                        </div>
                        {errors.platformCommissionPercent && <p className="text-red-500 text-xs mt-1">{errors.platformCommissionPercent.message}</p>}
                    </div>
                </div>
            </div>

            {/* Section 2: Rules & Timelines */}
            <div className="bg-white rounded-xl border border-slate-200 shadow-sm overflow-hidden">
                <div className="px-6 py-4 border-b border-slate-200 bg-slate-50">
                    <h3 className="text-lg font-semibold text-slate-900 flex items-center gap-2">
                        <Clock size={20} className="text-brand-600" /> Platform Rules & Timelines
                    </h3>
                </div>
                <div className="p-6 grid gap-6 md:grid-cols-2">
                    <div>
                        <label className="block text-sm font-medium text-slate-700 mb-1">
                            Dispute Window (Days)
                        </label>
                        <p className="text-xs text-slate-500 mb-2">Time after delivery for buyer to raise dispute</p>
                        <div className="relative">
                            <div className="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none">
                                <ShieldAlert size={16} className="text-slate-500" />
                            </div>
                            <input
                                type="number"
                                {...register('disputeWindowDays', {
                                    required: 'Required',
                                    min: { value: 0, message: 'Cannot be negative' },
                                    valueAsNumber: true
                                })}
                                className="w-full pl-10 pr-3 py-2 border border-slate-300 rounded-lg focus:ring-brand-500 focus:border-brand-500"
                            />
                        </div>
                        {errors.disputeWindowDays && <p className="text-red-500 text-xs mt-1">{errors.disputeWindowDays.message}</p>}
                    </div>

                    <div>
                        <label className="block text-sm font-medium text-slate-700 mb-1">
                            Auto-Release Escrow (Days)
                        </label>
                        <p className="text-xs text-slate-500 mb-2">Time after delivery to release funds if no dispute</p>
                        <div className="relative">
                            <div className="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none">
                                <Clock size={16} className="text-slate-500" />
                            </div>
                            <input
                                type="number"
                                {...register('autoReleaseDays', {
                                    required: 'Required',
                                    min: { value: 0, message: 'Cannot be negative' },
                                    valueAsNumber: true
                                })}
                                className="w-full pl-10 pr-3 py-2 border border-slate-300 rounded-lg focus:ring-brand-500 focus:border-brand-500"
                            />
                        </div>
                        {errors.autoReleaseDays && <p className="text-red-500 text-xs mt-1">{errors.autoReleaseDays.message}</p>}
                    </div>
                </div>
            </div>

            <div className="flex justify-end pt-4">
                <button
                    type="submit"
                    disabled={isSaving}
                    className="flex items-center gap-2 px-6 py-3 bg-brand-600 text-white rounded-lg font-bold shadow-md hover:bg-brand-700 disabled:opacity-50 transition-all active:scale-95"
                >
                    <Save size={20} />
                    {isSaving ? 'Saving Changes...' : 'Save Configuration'}
                </button>
            </div>
        </form>
    );
};

export default SettingsForm;
