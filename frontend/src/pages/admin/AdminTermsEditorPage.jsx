
import React, { useState, useEffect } from 'react';
import { Save, FileText, UploadCloud, RefreshCw } from 'lucide-react';
import { toast } from 'react-hot-toast';
import adminApi from '../../api/adminApi';
import TermsEditor from '../../components/terms/TermsEditor';
import TermsPreview from '../../components/terms/TermsPreview';
import TermsHistoryList from '../../components/terms/TermsHistoryList';

const AdminTermsEditorPage = () => {
    const [markdown, setMarkdown] = useState('');
    const [activeVersionId, setActiveVersionId] = useState(null);
    const [history, setHistory] = useState([]);
    const [loading, setLoading] = useState(true);
    const [publishing, setPublishing] = useState(false);
    const [versionLabel, setVersionLabel] = useState('');

    const fetchTerms = async () => {
        setLoading(true);
        try {
            const { data } = await adminApi.getTerms();
            if (data.activeVersion) {
                setMarkdown(data.activeVersion.markdown);
                setActiveVersionId(data.activeVersion.id);
            }
            if (data.history) {
                setHistory(data.history);
            }
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

    const handlePublish = async () => {
        if (!markdown.trim()) {
            toast.error('Content cannot be empty');
            return;
        }
        if (!versionLabel.trim()) {
            toast.error('Please provide a version label (e.g., v1.2)');
            return;
        }

        if (!window.confirm('Are you sure you want to publish this version? All users will need to re-accept.')) {
            return;
        }

        setPublishing(true);
        try {
            await adminApi.adminPublishTerms({
                markdown,
                versionLabel
            });
            toast.success('Terms published successfully!');
            setVersionLabel('');
            fetchTerms(); // Refresh history
        } catch (error) {
            console.error(error);
            toast.error('Failed to publish terms');
        } finally {
            setPublishing(false);
        }
    };

    if (loading) return <div className="p-10 flex justify-center"><RefreshCw className="animate-spin text-slate-400" /></div>;

    return (
        <div className="flex flex-col h-[calc(100vh-64px)] overflow-hidden bg-slate-50">
            {/* Header */}
            <div className="bg-white border-b border-slate-200 px-6 py-4 flex items-center justify-between shrink-0">
                <div>
                    <h1 className="text-xl font-bold text-slate-900 flex items-center gap-2">
                        <FileText className="text-brand-600" /> Terms & Conditions Editor
                    </h1>
                    <p className="text-sm text-slate-500">Draft, preview and publish new legal terms.</p>
                </div>

                <div className="flex items-center gap-3">
                    <input
                        type="text"
                        placeholder="Version e.g. v2.0"
                        value={versionLabel}
                        onChange={e => setVersionLabel(e.target.value)}
                        className="px-3 py-2 border border-slate-300 rounded-md text-sm w-32 focus:ring-brand-500 focus:border-brand-500"
                    />
                    <button
                        onClick={handlePublish}
                        disabled={publishing}
                        className="flex items-center gap-2 px-4 py-2 bg-brand-600 hover:bg-brand-700 text-white rounded-lg font-bold shadow-sm transition-all active:scale-95 disabled:opacity-50"
                    >
                        <UploadCloud size={18} /> {publishing ? 'Publishing...' : 'Publish'}
                    </button>
                </div>
            </div>

            {/* Main Content (Split View) */}
            <div className="flex-1 flex overflow-hidden">
                {/* Editor */}
                <div className="w-1/2 border-r border-slate-200 flex flex-col">
                    <TermsEditor
                        value={markdown}
                        onChange={setMarkdown}
                    />
                </div>

                {/* Preview */}
                <div className="w-1/2 flex flex-col bg-white">
                    <TermsPreview markdown={markdown} />
                </div>
            </div>

            {/* Bottom History Panel */}
            <div className="bg-white border-t border-slate-200 h-48 overflow-hidden shrink-0">
                <TermsHistoryList history={history} activeVersionId={activeVersionId} />
            </div>
        </div>
    );
};

export default AdminTermsEditorPage;
