
import React, { useState, useEffect } from 'react';
import { Dialog } from '@headlessui/react';
import { motion } from 'framer-motion';
import { useForm } from 'react-hook-form';
import { X, UploadCloud, FileText } from 'lucide-react';
import { toast } from 'react-hot-toast';
import disputeApi from '../../api/disputeApi';

const RaiseDisputeModal = ({ isOpen, onClose, orderId, onSuccess }) => {
    const { register, handleSubmit, formState: { errors, isSubmitting }, reset } = useForm();
    const [selectedFiles, setSelectedFiles] = useState([]);
    const [reasons, setReasons] = useState([
        'DAMAGED_GOODS',
        'INCORRECT_ITEM',
        'DELAYED_DELIVERY',
        'PAYMENT_CONFLICT',
        'OTHER'
    ]);

    useEffect(() => {
        if (isOpen) {
            fetchReasons();
        }
    }, [isOpen]);

    const fetchReasons = async () => {
        try {
            const response = await disputeApi.getDisputeReasons();
            if (response.data && response.data.length > 0) {
                setReasons(response.data);
            }
        } catch (error) {
            console.warn('Failed to fetch reasons, using defaults');
        }
    };

    const handleFileChange = (e) => {
        if (e.target.files) {
            const files = Array.from(e.target.files);

            // Validation: Max 5 files
            if (files.length > 5) {
                toast.error('Maximum 5 files allowed');
                return;
            }

            // Validation: Max 5MB per file
            const oversizedFiles = files.filter(file => file.size > 5 * 1024 * 1024);
            if (oversizedFiles.length > 0) {
                toast.error(`File ${oversizedFiles[0].name} exceeds 5MB limit`);
                return;
            }

            setSelectedFiles(files);
        }
    };

    const onSubmit = async (data) => {
        try {
            const formData = new FormData();
            formData.append('orderId', orderId); // Assuming orderId is passed or selected
            formData.append('reason', data.reason);
            formData.append('description', data.description);

            selectedFiles.forEach(file => {
                formData.append('files', file);
            });

            await disputeApi.raiseDispute(formData);
            toast.success('Dispute raised successfully');
            reset();
            setSelectedFiles([]);
            onSuccess();
            onClose();
        } catch (error) {
            console.error(error);
            toast.error('Failed to raise dispute');
        }
    };

    return (
        <Dialog open={isOpen} onClose={onClose} className="relative z-50">
            <div className="fixed inset-0 bg-black/30 backdrop-blur-sm" aria-hidden="true" />

            <div className="fixed inset-0 flex items-center justify-center p-4">
                <Dialog.Panel
                    as={motion.div}
                    initial={{ opacity: 0, scale: 0.95 }}
                    animate={{ opacity: 1, scale: 1 }}
                    className="mx-auto max-w-lg w-full bg-white rounded-xl shadow-2xl overflow-hidden"
                >
                    <div className="px-6 py-4 border-b border-slate-200 flex justify-between items-center bg-slate-50">
                        <Dialog.Title className="text-lg font-bold text-slate-900">Raise a Dispute</Dialog.Title>
                        <button onClick={onClose} className="text-slate-400 hover:text-slate-600 transition-colors">
                            <X size={24} />
                        </button>
                    </div>

                    <form onSubmit={handleSubmit(onSubmit)} className="p-6 space-y-4">
                        {/* Hidden Order ID field if needed, or just context */}

                        <div>
                            <label className="block text-sm font-medium text-slate-700 mb-1">Reason</label>
                            <select
                                {...register('reason', { required: 'Reason is required' })}
                                className="w-full px-3 py-2 border border-slate-300 rounded-md shadow-sm focus:outline-none focus:ring-brand-500 focus:border-brand-500 text-sm"
                            >
                                <option value="">Select Reason</option>
                                {reasons.map(r => (
                                    <option key={r} value={r}>{r.replace(/_/g, ' ')}</option>
                                ))}
                            </select>
                            {errors.reason && <p className="text-red-500 text-xs mt-1">{errors.reason.message}</p>}
                        </div>

                        <div>
                            <label className="block text-sm font-medium text-slate-700 mb-1">Description</label>
                            <textarea
                                {...register('description', { required: 'Description is required', minLength: { value: 20, message: 'Please provide more details (min 20 chars)' } })}
                                rows={4}
                                className="w-full px-3 py-2 border border-slate-300 rounded-md shadow-sm focus:outline-none focus:ring-brand-500 focus:border-brand-500 text-sm"
                                placeholder="Describe the issue in detail..."
                            />
                            {errors.description && <p className="text-red-500 text-xs mt-1">{errors.description.message}</p>}
                        </div>

                        <div>
                            <label className="block text-sm font-medium text-slate-700 mb-1">Evidence (Images/Videos)</label>
                            <div className="border-2 border-dashed border-slate-300 rounded-lg p-6 text-center hover:bg-slate-50 transition-colors cursor-pointer relative">
                                <input
                                    type="file"
                                    multiple
                                    accept="image/*,video/*"
                                    onChange={handleFileChange}
                                    className="absolute inset-0 w-full h-full opacity-0 cursor-pointer"
                                />
                                <UploadCloud className="mx-auto h-10 w-10 text-slate-400 mb-2" />
                                <p className="text-sm text-slate-600 font-medium">Click to upload files</p>
                                <p className="text-xs text-slate-400 mt-1">Max 5 files, 5MB each (JPG, PNG, Video)</p>
                            </div>
                            {selectedFiles.length > 0 && (
                                <div className="mt-3 space-y-1">
                                    {selectedFiles.map((file, idx) => (
                                        <div key={idx} className="flex items-center gap-2 text-xs text-slate-600 bg-slate-100 px-2 py-1 rounded">
                                            <FileText size={12} /> <span className="truncate">{file.name}</span>
                                        </div>
                                    ))}
                                </div>
                            )}
                        </div>

                        <div className="pt-4 flex justify-end gap-3">
                            <button
                                type="button"
                                onClick={onClose}
                                className="px-4 py-2 border border-slate-300 rounded-lg text-slate-700 hover:bg-slate-50 font-medium text-sm"
                            >
                                Cancel
                            </button>
                            <button
                                type="submit"
                                disabled={isSubmitting}
                                className="px-4 py-2 bg-brand-600 text-white rounded-lg hover:bg-brand-700 font-medium text-sm shadow-sm flex items-center gap-2 disabled:opacity-50"
                            >
                                {isSubmitting ? 'Submitting...' : 'Submit Dispute'}
                            </button>
                        </div>
                    </form>
                </Dialog.Panel>
            </div>
        </Dialog>
    );
};

export default RaiseDisputeModal;
