import React, { useState } from 'react';
import { UploadCloud, Loader2, X } from 'lucide-react';
import { toast } from 'react-hot-toast';
import disputeApi from '../../api/disputeApi';
import useAuthStore from '../../store/authStore';

const AddEvidenceForm = ({ disputeId, onEvidenceAdded }) => {
    const { user } = useAuthStore();
    const [file, setFile] = useState(null);
    const [isUploading, setIsUploading] = useState(false);

    const handleFileChange = (e) => {
        if (e.target.files && e.target.files[0]) {
            const selectedFile = e.target.files[0];
            if (selectedFile.size > 5 * 1024 * 1024) {
                toast.error('File size must be less than 5MB');
                return;
            }
            if (!selectedFile.type.startsWith('image/')) {
                toast.error('Only image files are allowed');
                return;
            }
            setFile(selectedFile);
        }
    };

    const handleUpload = async () => {
        if (!file) return;

        setIsUploading(true);
        try {
            const formData = new FormData();
            formData.append('file', file);

            if (user?.roles?.includes('SELLER')) {
                await disputeApi.sellerAddEvidence(disputeId, formData);
            } else if (user?.roles?.includes('BUYER')) {
                await disputeApi.buyerAddEvidence(disputeId, formData);
            } else {
                throw new Error('Unauthorized role');
            }

            toast.success('Evidence uploaded successfully');
            setFile(null);
            if (onEvidenceAdded) {
                onEvidenceAdded();
            }
        } catch (error) {
            console.error('Upload Error:', error);
            toast.error(error.response?.data?.message || 'Failed to upload evidence');
        } finally {
            setIsUploading(false);
        }
    };

    return (
        <div className="bg-white border text-sm border-slate-200 rounded-lg p-4">
            <h4 className="font-medium text-slate-800 mb-3">Add New Evidence</h4>

            {!file ? (
                <div className="border-2 border-dashed border-slate-300 rounded-lg p-4 text-center">
                    <input
                        type="file"
                        id="evidence-upload"
                        className="hidden"
                        accept="image/*"
                        onChange={handleFileChange}
                    />
                    <label
                        htmlFor="evidence-upload"
                        className="cursor-pointer flex flex-col items-center gap-2 text-slate-500 hover:text-brand-600 transition-colors"
                    >
                        <UploadCloud size={24} />
                        <span>Click to upload image</span>
                    </label>
                </div>
            ) : (
                <div className="flex items-center justify-between p-3 bg-slate-50 rounded-lg border border-slate-200">
                    <span className="truncate max-w-[200px] text-slate-700">{file.name}</span>
                    <button
                        onClick={() => setFile(null)}
                        className="text-slate-400 hover:text-red-500"
                        disabled={isUploading}
                    >
                        <X size={16} />
                    </button>
                </div>
            )}

            <button
                onClick={handleUpload}
                disabled={!file || isUploading}
                className="mt-3 w-full flex items-center justify-center gap-2 bg-slate-900 text-white py-2 rounded-lg font-medium hover:bg-slate-800 disabled:opacity-50 disabled:cursor-not-allowed transition-colors"
            >
                {isUploading ? (
                    <>
                        <Loader2 className="animate-spin" size={16} /> Uploading...
                    </>
                ) : (
                    'Submit Evidence'
                )}
            </button>
        </div>
    );
};

export default AddEvidenceForm;
