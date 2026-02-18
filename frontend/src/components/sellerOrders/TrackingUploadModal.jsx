import React, { useState } from 'react';
import { useForm } from 'react-hook-form';
import { Dialog } from '@headlessui/react';
import { X, Loader2, Truck } from 'lucide-react';
import toast from 'react-hot-toast';
import sellerOrderApi from '../../api/sellerOrderApi';

const TrackingUploadModal = ({ isOpen, onClose, orderId, onSuccess, existingTracking }) => {
    const [submitting, setSubmitting] = useState(false);
    const { register, handleSubmit, formState: { errors }, reset } = useForm({
        defaultValues: {
            courier: existingTracking?.courier || '',
            trackingNumber: existingTracking?.trackingNumber || '',
            notes: existingTracking?.notes || ''
        }
    });

    const onSubmit = async (data) => {
        setSubmitting(true);
        try {
            await sellerOrderApi.sellerUploadTracking(orderId, data);
            toast.success('Tracking updated successfully');
            onSuccess();
            onClose();
            reset();
        } catch (error) {
            console.error(error);
            toast.error(error.response?.data?.message || 'Failed to update tracking');
        } finally {
            setSubmitting(false);
        }
    };

    if (!isOpen) return null;

    return (
        <Dialog open={isOpen} onClose={onClose} className="relative z-50">
            <div className="fixed inset-0 bg-black/30" aria-hidden="true" />
            <div className="fixed inset-0 flex items-center justify-center p-4">
                <Dialog.Panel className="w-full max-w-md rounded-xl bg-white p-6 shadow-xl">
                    <div className="flex justify-between items-center mb-4">
                        <Dialog.Title className="text-lg font-bold text-slate-900 flex items-center gap-2">
                            <Truck size={20} className="text-brand-600" />
                            Update Tracking
                        </Dialog.Title>
                        <button onClick={onClose} className="text-slate-400 hover:text-slate-600">
                            <X size={20} />
                        </button>
                    </div>

                    <form onSubmit={handleSubmit(onSubmit)} className="space-y-4">
                        <div>
                            <label className="block text-sm font-medium text-slate-700 mb-1">Courier / Carrier *</label>
                            <input
                                {...register('courier', { required: 'Courier name is required' })}
                                className="w-full rounded-lg border-slate-300 focus:ring-brand-500 focus:border-brand-500 text-sm"
                                placeholder="e.g. DHL, FedEx, BlueDart"
                            />
                            {errors.courier && <p className="text-xs text-red-500 mt-1">{errors.courier.message}</p>}
                        </div>

                        <div>
                            <label className="block text-sm font-medium text-slate-700 mb-1">Tracking Number *</label>
                            <input
                                {...register('trackingNumber', { required: 'Tracking number is required' })}
                                className="w-full rounded-lg border-slate-300 focus:ring-brand-500 focus:border-brand-500 text-sm"
                                placeholder="e.g. 1234567890"
                            />
                            {errors.trackingNumber && <p className="text-xs text-red-500 mt-1">{errors.trackingNumber.message}</p>}
                        </div>

                        <div>
                            <label className="block text-sm font-medium text-slate-700 mb-1">Notes (Optional)</label>
                            <textarea
                                {...register('notes')}
                                rows={3}
                                className="w-full rounded-lg border-slate-300 focus:ring-brand-500 focus:border-brand-500 text-sm"
                                placeholder="Additional shipping details..."
                            />
                        </div>

                        <div className="flex justify-end gap-3 pt-4">
                            <button
                                type="button"
                                onClick={onClose}
                                className="px-4 py-2 text-slate-700 font-medium hover:bg-slate-50 rounded-lg"
                            >
                                Cancel
                            </button>
                            <button
                                type="submit"
                                disabled={submitting}
                                className="px-4 py-2 bg-brand-600 text-white font-medium rounded-lg hover:bg-brand-700 disabled:opacity-70 flex items-center gap-2"
                            >
                                {submitting && <Loader2 size={16} className="animate-spin" />}
                                Save Tracking
                            </button>
                        </div>
                    </form>
                </Dialog.Panel>
            </div>
        </Dialog>
    );
};

export default TrackingUploadModal;
