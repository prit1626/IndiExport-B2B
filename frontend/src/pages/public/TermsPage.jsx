
import React, { useState, useEffect } from 'react';
import ReactMarkdown from 'react-markdown';
import { ShieldCheck, Calendar, Info } from 'lucide-react';
import { toast } from 'react-hot-toast';
import adminApi from '../../api/adminApi';
import useAuthStore from '../../store/authStore';
import TermsSkeleton from '../../components/terms/TermsSkeleton';
import AcceptTermsBar from '../../components/terms/AcceptTermsBar';
import { formatDate } from '../../utils/formatDate';

const TermsPage = () => {
    const { user, isAuthenticated } = useAuthStore();
    const [markdown, setMarkdown] = useState('');
    const [meta, setMeta] = useState(null); // activeVersion metadata
    const [userAcceptance, setUserAcceptance] = useState(null);
    const [loading, setLoading] = useState(true);
    const [accepting, setAccepting] = useState(false);

    const fetchTerms = async () => {
        setLoading(true);
        try {
            const { data } = await adminApi.getTerms();
            if (data.activeVersion) {
                setMarkdown(data.activeVersion.markdown);
                setMeta(data.activeVersion);
            }
            setUserAcceptance(data.userAcceptance);
        } catch (error) {
            console.error(error);
            toast.error('Failed to load terms');
        } finally {
            setLoading(false);
        }
    };

    useEffect(() => {
        fetchTerms();
    }, []);

    const handleAccept = async () => {
        if (!meta?.id) return;
        setAccepting(true);
        try {
            await adminApi.acceptTerms(meta.id);
            toast.success('Terms accepted successfully!');
            setUserAcceptance({ accepted: true, acceptedAt: new Date().toISOString() });
        } catch (error) {
            console.error(error);
            toast.error('Failed to accept terms');
        } finally {
            setAccepting(false);
        }
    };

    if (loading) return <TermsSkeleton />;

    const needsAcceptance = isAuthenticated && meta && (!userAcceptance || !userAcceptance.accepted);

    return (
        <div className="min-h-screen bg-slate-50 pb-32">
            {/* Header */}
            <div className="bg-slate-900 text-white py-12">
                <div className="container mx-auto px-4 max-w-4xl text-center">
                    <ShieldCheck className="w-16 h-16 text-brand-400 mx-auto mb-4" />
                    <h1 className="text-3xl md:text-4xl font-bold mb-2">Terms & Conditions</h1>
                    <p className="text-slate-400 text-lg">
                        Please read our terms carefully.
                        {meta && <span className="block mt-2 text-sm opacity-70">Version {meta.version} • Last Updated {formatDate(meta.publishedAt)}</span>}
                    </p>
                </div>
            </div>

            {/* Content using Prose for clean typography */}
            <div className="container mx-auto px-4 max-w-4xl -mt-8 relative z-10">
                <div className="bg-white rounded-xl shadow-xl p-8 md:p-12 prose prose-slate max-w-none prose-headings:text-brand-900 prose-a:text-brand-600">
                    {markdown ? (
                        <ReactMarkdown>{markdown}</ReactMarkdown>
                    ) : (
                        <div className="text-center py-20 text-slate-400 italic">
                            No terms published yet.
                        </div>
                    )}
                </div>

                {/* Acceptance Badge if already accepted */}
                {userAcceptance?.accepted && (
                    <div className="mt-8 flex items-center justify-center gap-2 text-emerald-600 font-medium bg-emerald-50 py-3 rounded-lg border border-emerald-100">
                        <ShieldCheck size={20} />
                        You accepted these terms on {formatDate(userAcceptance.acceptedAt)}
                    </div>
                )}
            </div>

            {/* Sticky Acceptance Bar */}
            {needsAcceptance && (
                <AcceptTermsBar
                    onAccept={handleAccept}
                    isAccepting={accepting}
                />
            )}
        </div>
    );
};

export default TermsPage;
