
import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { ShieldCheck, Upload, ArrowRight } from 'lucide-react';
import sellerVerificationApi from '../../api/sellerVerificationApi';
import VerificationSkeleton from '../../components/verification/VerificationSkeleton';
import SellerVerificationStatusCard from '../../components/verification/SellerVerificationStatusCard';
import FileCard from '../../components/verification/FileCard';
import DocumentPreview from '../../components/verification/DocumentPreview';

const SellerVerificationPage = () => {
    const navigate = useNavigate();
    const [kycData, setKycData] = useState(null);
    const [loading, setLoading] = useState(true);
    const [previewUrl, setPreviewUrl] = useState(null);

    useEffect(() => {
        fetchKycStatus();
    }, []);

    const fetchKycStatus = async () => {
        setLoading(true);
        try {
            const { data } = await sellerVerificationApi.getKycStatus();
            setKycData(data);
        } catch (error) {
            console.error("Failed to load KYC status", error);
        } finally {
            setLoading(false);
        }
    };

    if (loading) return <VerificationSkeleton />;

    const status = kycData?.verificationStatus || 'NOT_VERIFIED';
    const isActionNeeded = status === 'NOT_VERIFIED' || status === 'NEED_MORE_INFO' || status === 'REJECTED';

    return (
        <div className="min-h-screen bg-slate-50 pb-20">
            {/* Header */}
            <div className="bg-white border-b border-slate-200 py-8">
                <div className="container mx-auto px-4 max-w-4xl">
                    <h1 className="text-2xl font-bold text-slate-900 flex items-center gap-2">
                        <ShieldCheck className="text-brand-600" /> Seller Verification
                    </h1>
                    <p className="text-slate-500 mt-1">Submit your documents to start selling globally on IndiExport.</p>
                </div>
            </div>

            <div className="container mx-auto px-4 max-w-4xl py-8">
                {/* Status Card */}
                <SellerVerificationStatusCard
                    status={status}
                    rejectionReason={kycData?.rejectionReason}
                />

                {/* Info & Requirements */}
                <div className="bg-white rounded-xl border border-slate-200 shadow-sm overflow-hidden mb-6">
                    <div className="px-6 py-4 border-b border-slate-200 bg-slate-50 flex justify-between items-center">
                        <h3 className="font-semibold text-slate-900">Document Checklist</h3>
                        {/* Only show "Upload" button if action needed */}
                        {isActionNeeded && (
                            <button
                                onClick={() => navigate('/seller/verification/upload')}
                                className="flex items-center gap-2 px-4 py-2 bg-brand-600 hover:bg-brand-700 text-white text-sm font-bold rounded-lg transition-colors shadow-sm"
                            >
                                <Upload size={16} /> Upload / Update Documents
                            </button>
                        )}
                    </div>
                    <div className="p-6 grid gap-4 md:grid-cols-2">
                        <FileCard
                            label="IEC Certificate"
                            isUploaded={kycData?.iecUploaded}
                            fileName={kycData?.iecUploaded ? "Document Uploaded" : null}
                        />
                        <FileCard
                            label="PAN Card"
                            isUploaded={kycData?.panUploaded}
                            fileName={kycData?.panUploaded ? "Document Uploaded" : null}
                        />
                        <FileCard
                            label="GSTIN Certificate"
                            isUploaded={kycData?.gstinUploaded}
                            fileName={kycData?.gstinUploaded ? "Document Uploaded" : null}
                        />
                        <FileCard
                            label="Bank Details"
                            isUploaded={kycData?.bankDetailsUploaded}
                            fileName={kycData?.bankDetailsUploaded ? "Details Submitted" : null}
                        />
                    </div>
                </div>

                {/* Call to Action */}
                {status === 'NOT_VERIFIED' && (
                    <div className="flex justify-end">
                        <button
                            onClick={() => navigate('/seller/verification/upload')}
                            className="flex items-center gap-2 px-8 py-4 bg-slate-900 text-white rounded-xl font-bold shadow-lg hover:bg-slate-800 transition-all hover:scale-[1.01]"
                        >
                            Continue to Upload <ArrowRight size={20} />
                        </button>
                    </div>
                )}
            </div>

            {/* Document Preview Modal */}
            <DocumentPreview
                isOpen={!!previewUrl}
                onClose={() => setPreviewUrl(null)}
                fileUrl={previewUrl}
            />
        </div>
    );
};

export default SellerVerificationPage;
