
import React, { useState, useEffect } from 'react';
import { ShieldCheck, Loader2 } from 'lucide-react';
import adminSellerApi from '../../api/adminSellerApi';
import SellerVerificationTable from '../../components/verification/SellerVerificationTable';

const AdminSellerVerificationPage = () => {
    const [sellers, setSellers] = useState([]);
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        fetchPendingSellers();
    }, []);

    const fetchPendingSellers = async () => {
        setLoading(true);
        try {
            const { data } = await adminSellerApi.getPendingSellers();
            setSellers(data || []);
        } catch (error) {
            console.error("Failed to load pending sellers", error);
        } finally {
            setLoading(false);
        }
    };

    return (
        <div className="min-h-screen bg-slate-50 pb-20">
            {/* Header */}
            <div className="bg-white border-b border-slate-200 py-6">
                <div className="container mx-auto px-4 max-w-6xl">
                    <h1 className="text-2xl font-bold text-slate-900 flex items-center gap-2">
                        <ShieldCheck className="text-brand-600" /> Seller Verification Queue
                    </h1>
                    <p className="text-slate-500 text-sm mt-1">Review and approve new seller applications.</p>
                </div>
            </div>

            <div className="container mx-auto px-4 max-w-6xl py-8">
                {loading ? (
                    <div className="flex justify-center py-20">
                        <Loader2 className="animate-spin text-brand-600" size={40} />
                    </div>
                ) : (
                    <SellerVerificationTable sellers={sellers} />
                )}
            </div>
        </div>
    );
};

export default AdminSellerVerificationPage;
