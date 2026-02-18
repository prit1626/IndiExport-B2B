
import React, { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { ArrowLeft, Building2, Calendar, Mail, FileText, CreditCard } from 'lucide-react';
import { toast } from 'react-hot-toast';
import adminSellerApi from '../../api/adminSellerApi';
import VerificationSkeleton from '../../components/verification/VerificationSkeleton';
import FileCard from '../../components/verification/FileCard';
import DocumentPreview from '../../components/verification/DocumentPreview';
import AdminVerifyActionsPanel from '../../components/verification/AdminVerifyActionsPanel';
import { formatDate } from '../../utils/formatDate';

const AdminSellerDetailsPage = () => {
    const { id } = useParams();
    const navigate = useNavigate();
    const [kycData, setKycData] = useState(null);
    const [loading, setLoading] = useState(true);
    const [actionLoading, setActionLoading] = useState(false);
    const [previewUrl, setPreviewUrl] = useState(null);

    useEffect(() => {
        fetchSellerKyc();
    }, [id]);

    const fetchSellerKyc = async () => {
        setLoading(true);
        try {
            const { data } = await adminSellerApi.getSellerKyc(id);
            setKycData(data);
        } catch (error) {
            console.error(error);
            toast.error("Failed to load seller KYC");
        } finally {
            setLoading(false);
        }
    };

    const handleVerify = async () => {
        if (!window.confirm("Approve this seller?")) return;
        setActionLoading(true);
        try {
            await adminSellerApi.verifySeller(id);
            toast.success("Seller Verified Successfully");
            fetchSellerKyc();
        } catch (error) {
            toast.error("Verification failed");
        } finally {
            setActionLoading(false);
        }
    };

    const handleReject = async (reason) => {
        setActionLoading(true);
        try {
            await adminSellerApi.rejectSeller(id, reason);
            toast.success("Seller Rejected");
            fetchSellerKyc();
        } catch (error) {
            toast.error("Rejection failed");
        } finally {
            setActionLoading(false);
        }
    };

    if (loading) return <VerificationSkeleton />;
    if (!kycData) return <div className="p-10 text-center">Seller not found</div>;

    return (
        <div className="min-h-screen bg-slate-50 pb-20">
            {/* Header */}
            <div className="bg-white border-b border-slate-200 py-6 sticky top-0 z-30">
                <div className="container mx-auto px-4 max-w-6xl flex items-center justify-between">
                    <div className="flex items-center gap-3">
                        <button onClick={() => navigate('/admin/sellers/verification')} className="p-2 hover:bg-slate-100 rounded-full transition-colors">
                            <ArrowLeft size={20} />
                        </button>
                        <div>
                            <h1 className="text-xl font-bold text-slate-900">{kycData.companyName || 'Unknown Company'}</h1>
                            <p className="text-xs text-slate-500">ID: {kycData.sellerId}</p>
                        </div>
                    </div>
                    <span className="px-3 py-1 bg-slate-100 text-slate-600 rounded-full text-xs font-bold uppercase">
                        {kycData.verificationStatus}
                    </span>
                </div>
            </div>

            <div className="container mx-auto px-4 max-w-6xl py-8 grid grid-cols-1 lg:grid-cols-3 gap-8">

                {/* Main Content (Docs & Details) */}
                <div className="lg:col-span-2 space-y-6">

                    {/* Basic Info */}
                    <div className="bg-white rounded-xl border border-slate-200 shadow-sm p-6">
                        <h2 className="font-bold text-slate-900 mb-4 flex items-center gap-2"><Building2 size={18} /> Business Details</h2>
                        <div className="grid grid-cols-2 gap-4 text-sm">
                            <div>
                                <span className="block text-slate-500 mb-1">Company Name</span>
                                <span className="font-medium">{kycData.companyName}</span>
                            </div>
                            <div>
                                <span className="block text-slate-500 mb-1">Submitted On</span>
                                <span className="font-medium">{formatDate(kycData.submittedAt)}</span>
                            </div>
                            <div>
                                <span className="block text-slate-500 mb-1">IEC Number</span>
                                <span className="font-mono bg-slate-100 px-2 py-1 rounded">{kycData.iecNumber || 'N/A'}</span>
                            </div>
                            <div>
                                <span className="block text-slate-500 mb-1">PAN Number</span>
                                <span className="font-mono bg-slate-100 px-2 py-1 rounded">{kycData.panNumber || 'N/A'}</span>
                            </div>
                            <div>
                                <span className="block text-slate-500 mb-1">GSTIN</span>
                                <span className="font-mono bg-slate-100 px-2 py-1 rounded">{kycData.gstinNumber || 'N/A'}</span>
                            </div>
                        </div>
                    </div>

                    {/* Documents */}
                    <div className="bg-white rounded-xl border border-slate-200 shadow-sm p-6">
                        <h2 className="font-bold text-slate-900 mb-4 flex items-center gap-2"><FileText size={18} /> Uploaded Documents</h2>
                        <div className="grid gap-4">
                            <FileCard
                                label="IEC Document"
                                isUploaded={!!kycData.iecDocumentUrl}
                                fileUrl={kycData.iecDocumentUrl}
                                onPreview={setPreviewUrl}
                            />
                            <FileCard
                                label="PAN Document"
                                isUploaded={!!kycData.panDocumentUrl}
                                fileUrl={kycData.panDocumentUrl}
                                onPreview={setPreviewUrl}
                            />
                            <FileCard
                                label="GSTIN Document"
                                isUploaded={!!kycData.gstinDocumentUrl}
                                fileUrl={kycData.gstinDocumentUrl}
                                onPreview={setPreviewUrl}
                            />
                        </div>
                    </div>

                    {/* Bank Details */}
                    <div className="bg-white rounded-xl border border-slate-200 shadow-sm p-6">
                        <h2 className="font-bold text-slate-900 mb-4 flex items-center gap-2"><CreditCard size={18} /> Bank Information</h2>
                        <div className="grid grid-cols-2 gap-y-4 gap-x-8 text-sm">
                            <div>
                                <span className="block text-slate-500 mb-1">Account Holder</span>
                                <span className="font-medium">{kycData.bankAccountHolderName || '-'}</span>
                            </div>
                            <div>
                                <span className="block text-slate-500 mb-1">Account Number</span>
                                <span className="font-mono">{kycData.bankAccountNumberMasked || '****'}</span>
                            </div>
                            <div>
                                <span className="block text-slate-500 mb-1">Bank Name</span>
                                <span className="font-medium">{kycData.bankName || '-'}</span>
                            </div>
                            <div>
                                <span className="block text-slate-500 mb-1">IFSC Code</span>
                                <span className="font-mono">{kycData.bankIfscCode || '-'}</span>
                            </div>
                            <div>
                                <span className="block text-slate-500 mb-1">Preference</span>
                                <span className="px-2 py-0.5 bg-blue-50 text-blue-700 rounded text-xs font-bold">{kycData.payoutMethodPreference || '-'}</span>
                            </div>
                        </div>
                    </div>
                </div>

                {/* Sidebar Actions */}
                <div className="lg:col-span-1">
                    <AdminVerifyActionsPanel
                        status={kycData.verificationStatus}
                        onVerify={handleVerify}
                        onReject={handleReject}
                        isProcessing={actionLoading}
                    />
                </div>
            </div>

            <DocumentPreview
                isOpen={!!previewUrl}
                onClose={() => setPreviewUrl(null)}
                fileUrl={previewUrl}
            />
        </div>
    );
};

export default AdminSellerDetailsPage;
