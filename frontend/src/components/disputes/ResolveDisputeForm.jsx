
import React from 'react';
import { useForm } from 'react-hook-form';
import { toast } from 'react-hot-toast';
import disputeApi from '../../api/disputeApi';

const ResolveDisputeForm = ({ disputeId, onResolve }) => {
    const { register, handleSubmit, watch, formState: { errors, isSubmitting } } = useForm();
    const action = watch('action');

    const onSubmit = async (data) => {
        try {
            await disputeApi.adminResolveDispute(disputeId, {
                action: data.action,
                amountINRPaise: data.amount ? Math.round(data.amount * 100) : 0, // Convert to paise
                notes: data.notes
            });
            toast.success('Dispute resolved successfully');
            if (onResolve) onResolve();
        } catch (error) {
            console.error(error);
            toast.error('Failed to resolve dispute');
        }
    };

    return (
        <form onSubmit={handleSubmit(onSubmit)} className="space-y-4">
            <div>
                <label className="block text-sm font-medium text-slate-700 mb-1">Action</label>
                <select
                    {...register('action', { required: 'Action is required' })}
                    className="w-full px-3 py-2 border border-slate-300 rounded-md shadow-sm focus:outline-none focus:ring-brand-500 focus:border-brand-500 text-sm"
                >
                    <option value="">Select Action</option>
                    <option value="REFUND">Full Refund</option>
                    <option value="PARTIAL_REFUND">Partial Refund</option>
                    <option value="REPLACEMENT">Replacement</option>
                    <option value="REJECT">Reject Dispute</option>
                </select>
                {errors.action && <p className="text-red-500 text-xs mt-1">{errors.action.message}</p>}
            </div>

            {(action === 'REFUND' || action === 'PARTIAL_REFUND') && (
                <div>
                    <label className="block text-sm font-medium text-slate-700 mb-1">
                        Refund Amount (INR)
                    </label>
                    <input
                        type="number"
                        step="0.01"
                        {...register('amount', {
                            required: 'Amount is required for refunds',
                            min: { value: 0.01, message: 'Amount must be greater than 0' }
                        })}
                        className="w-full px-3 py-2 border border-slate-300 rounded-md shadow-sm focus:outline-none focus:ring-brand-500 focus:border-brand-500 text-sm"
                        placeholder="0.00"
                    />
                    {errors.amount && <p className="text-red-500 text-xs mt-1">{errors.amount.message}</p>}
                </div>
            )}

            <div>
                <label className="block text-sm font-medium text-slate-700 mb-1">Resolution Notes</label>
                <textarea
                    {...register('notes', { required: 'Notes are required' })}
                    rows={3}
                    className="w-full px-3 py-2 border border-slate-300 rounded-md shadow-sm focus:outline-none focus:ring-brand-500 focus:border-brand-500 text-sm"
                    placeholder="Explain the resolution..."
                />
                {errors.notes && <p className="text-red-500 text-xs mt-1">{errors.notes.message}</p>}
            </div>

            <button
                type="submit"
                disabled={isSubmitting}
                className="w-full flex justify-center py-2 px-4 border border-transparent rounded-md shadow-sm text-sm font-medium text-white bg-brand-600 hover:bg-brand-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-brand-500 disabled:opacity-50"
            >
                {isSubmitting ? 'Resolving...' : 'Submit Resolution'}
            </button>
        </form>
    );
};

export default ResolveDisputeForm;
