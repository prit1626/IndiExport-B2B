
import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { ArrowLeft, CheckCircle2 } from 'lucide-react';
import { toast } from 'react-hot-toast';
import sellerVerificationApi from '../../api/sellerVerificationApi';
import VerificationSkeleton from '../../components/verification/VerificationSkeleton';
import SellerUploadDropzone from '../../components/verification/SellerUploadDropzone';
import SellerBankDetailsForm from '../../components/verification/SellerBankDetailsForm';

const SellerDocumentsUploadPage = () => {
    const navigate = useNavigate();
    const [loading, setLoading] = useState(true);
    const [kycData, setKycData] = useState(null);
    const [uploading, setUploading] = useState({ iec: false, pan: false, gstin: false });
    const [iecNumber, setIecNumber] = useState('');
    const [panNumber, setPanNumber] = useState('');
    const [gstinNumber, setGstinNumber] = useState('');

    useEffect(() => {
        fetchKycStatus();
    }, []);

    const fetchKycStatus = async () => {
        setLoading(true);
        try {
            const { data } = await sellerVerificationApi.getKycStatus();
            setKycData(data);
            // Pre-fill fields if we had them stored, but API response only has booleans. 
            // In a real app we'd fetch profile details too, but for now we start empty or rely on user re-entry if needed.
        } catch (error) {
            console.error(error);
            toast.error("Failed to load current status");
        } finally {
            setLoading(false);
        }
    };

    const handleUpload = async (type, file) => {
        setUploading(prev => ({ ...prev, [type]: true }));
        try {
            if (type === 'iec') {
                if (!iecNumber) { toast.error("Please enter IEC Number first"); return; }
                await sellerVerificationApi.uploadIec(iecNumber, file);
            } else if (type === 'pan') {
                if (!panNumber) { toast.error("Please enter PAN Number first"); return; }
                await sellerVerificationApi.uploadPan(panNumber, file);
            } else if (type === 'gstin') {
                await sellerVerificationApi.uploadGstin(gstinNumber, file);
            }

            toast.success(`${type.toUpperCase()} uploaded successfully`);
            fetchKycStatus(); // Refresh status marks
        } catch (error) {
            console.error(error);
            toast.error("Upload failed");
        } finally {
            setUploading(prev => ({ ...prev, [type]: false }));
        }
    };

    const handleBankSave = async (data) => {
        try {
            await sellerVerificationApi.updateBankDetails(data);
            toast.success("Bank details saved");
            fetchKycStatus();
        } catch (error) {
            console.error(error);
            toast.error("Failed to save bank details");
        }
    };

    if (loading) return <VerificationSkeleton />;

    return (
        <div className="min-h-screen bg-slate-50 pb-20">
            {/* Header */}
            <div className="bg-white border-b border-slate-200 py-6 sticky top-0 z-30">
                <div className="container mx-auto px-4 max-w-3xl flex items-center justify-between">
                    <div className="flex items-center gap-3">
                        <button onClick={() => navigate('/seller/verification')} className="p-2 hover:bg-slate-100 rounded-full transition-colors">
                            <ArrowLeft size={20} />
                        </button>
                        <h1 className="text-xl font-bold text-slate-900">Upload Verification Documents</h1>
                    </div>
                </div>
            </div>

            <div className="container mx-auto px-4 max-w-3xl py-8 space-y-8">

                {/* IEC Section */}
                <div className="bg-white p-6 rounded-xl border border-slate-200 shadow-sm">
                    <h2 className="text-lg font-bold text-slate-900 mb-4 flex items-center gap-2">
                        1. IEC Certificate {kycData?.iecUploaded && <CheckCircle2 className="text-emerald-500" size={20} />}
                    </h2>
                    <div className="mb-4">
                        <label className="block text-sm font-medium text-slate-700 mb-1">Items of Export Code (IEC) Number</label>
                        <input
                            type="text"
                            value={iecNumber}
                            onChange={(e) => setIecNumber(e.target.value)}
                            className="w-full px-3 py-2 border border-slate-300 rounded-lg focus:ring-brand-500 focus:border-brand-500"
                            placeholder="e.g. 0500000001"
                        />
                    </div>
                    <SellerUploadDropzone
                        label="Upload IEC Scan"
                        onUpload={(file) => handleUpload('iec', file)}
                        isLoading={uploading.iec}
                        existingFileUrl={kycData?.iecUploaded ? "uploaded" : null}
                    />
                </div>

                {/* PAN Section */}
                <div className="bg-white p-6 rounded-xl border border-slate-200 shadow-sm">
                    <h2 className="text-lg font-bold text-slate-900 mb-4 flex items-center gap-2">
                        2. PAN Card {kycData?.panUploaded && <CheckCircle2 className="text-emerald-500" size={20} />}
                    </h2>
                    <div className="mb-4">
                        <label className="block text-sm font-medium text-slate-700 mb-1">Company/Personal PAN Number</label>
                        <input
                            type="text"
                            value={panNumber}
                            onChange={(e) => setPanNumber(e.target.value)}
                            className="w-full px-3 py-2 border border-slate-300 rounded-lg focus:ring-brand-500 focus:border-brand-500 uppercase"
                            placeholder="e.g. ABCDE1234F"
                        />
                    </div>
                    <SellerUploadDropzone
                        label="Upload PAN Scan"
                        onUpload={(file) => handleUpload('pan', file)}
                        isLoading={uploading.pan}
                        existingFileUrl={kycData?.panUploaded ? "uploaded" : null}
                    />
                </div>

                {/* GST Section */}
                <div className="bg-white p-6 rounded-xl border border-slate-200 shadow-sm">
                    <h2 className="text-lg font-bold text-slate-900 mb-4 flex items-center gap-2">
                        3. GSTIN (Optional) {kycData?.gstinUploaded && <CheckCircle2 className="text-emerald-500" size={20} />}
                    </h2>
                    <div className="mb-4">
                        <label className="block text-sm font-medium text-slate-700 mb-1">GST Identification Number</label>
                        <input
                            type="text"
                            value={gstinNumber}
                            onChange={(e) => setGstinNumber(e.target.value)}
                            className="w-full px-3 py-2 border border-slate-300 rounded-lg focus:ring-brand-500 focus:border-brand-500 uppercase"
                            placeholder="e.g. 22AAAAA0000A1Z5"
                        />
                    </div>
                    <SellerUploadDropzone
                        label="Upload GST Certificate"
                        onUpload={(file) => handleUpload('gstin', file)}
                        isLoading={uploading.gstin}
                        existingFileUrl={kycData?.gstinUploaded ? "uploaded" : null}
                    />
                </div>

                {/* Bank Details Section */}
                <SellerBankDetailsForm
                    initialData={kycData} // Passed even if empty, form handles defaults
                    onSave={handleBankSave}
                    isSaving={false} // can add specific loading state if needed
                />

                <div className="flex justify-end pt-6">
                    <button
                        onClick={() => navigate('/seller/verification')}
                        className="px-8 py-3 bg-brand-600 text-white font-bold rounded-lg shadow hover:bg-brand-700 transition-colors"
                    >
                        Finish & Review Status
                    </button>
                </div>
            </div>
        </div>
    );
};

export default SellerDocumentsUploadPage;
