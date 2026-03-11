import React, { useState, useRef, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { motion } from 'framer-motion';
import { ArrowLeft, ArrowRight, Package, Globe, Anchor, DollarSign, Upload, Check, Loader2, X, Image as ImageIcon } from 'lucide-react';
import rfqApi from '../../api/rfqApi';
import profileApi from '../../api/profileApi';
import { uploadFile } from '../../api/uploadApi';
import toast from 'react-hot-toast';
import useAuthStore from '../../store/authStore';

const UNITS = ['PCS', 'KG', 'TON', 'MT', 'LTR', 'CBM', 'BOX', 'ROLL'];

const STEPS = [
    { id: 1, title: 'Product Details', icon: <Package size={18} /> },
    { id: 2, title: 'Destination', icon: <Globe size={18} /> },
    { id: 3, title: 'Pricing & Timeline', icon: <DollarSign size={18} /> },
];

const inputCls = 'w-full bg-slate-50 border border-slate-200 rounded-xl px-4 py-3 text-slate-800 text-sm focus:outline-none focus:ring-2 focus:ring-brand-400 focus:border-transparent transition';
const labelCls = 'block text-sm font-medium text-slate-700 mb-1.5';
const selectCls = `${inputCls} appearance-none cursor-pointer`;

export default function BuyerRfqCreatePage() {
    const navigate = useNavigate();
    const { user } = useAuthStore();
    const [step, setStep] = useState(1);
    const [submitting, setSubmitting] = useState(false);
    const [uploading, setUploading] = useState(false);
    const [uploadProgress, setUploadProgress] = useState(0);
    const [mediaFiles, setMediaFiles] = useState([]); // { file, previewUrl, uploadedUrl }
    const fileInputRef = useRef();

    const [form, setForm] = useState({
        title: '', details: '', quantity: '', unit: 'PCS',
        destinationCountry: '',
        shippingMode: 'AIR',
        incoterm: 'FOB',
        targetPriceMinor: '', targetCurrency: 'USD', mediaUrls: [],
    });

    useEffect(() => {
        const fetchProfile = async () => {
            try {
                const res = await profileApi.getBuyerProfile();
                if (res.data) {
                    setForm(f => ({
                        ...f,
                        destinationCountry: res.data.country || '',
                        targetCurrency: res.data.preferredCurrency || 'USD'
                    }));
                }
            } catch (err) {
                console.error("Failed to fetch buyer profile for RFQ defaults", err);
            }
        };
        fetchProfile();
    }, []);

    const set = (field) => (e) => setForm(f => ({ ...f, [field]: e.target.value }));

    const handleFilePick = async (e) => {
        const files = Array.from(e.target.files || []);
        if (!files.length) return;

        const oversized = files.find(f => f.size > 10 * 1024 * 1024);
        if (oversized) { toast.error(`${oversized.name} exceeds 10 MB`); return; }
        if (mediaFiles.length + files.length > 6) { toast.error('Max 6 files allowed'); return; }

        setUploading(true);
        setUploadProgress(0);
        const total = files.length;
        let done = 0;

        const newEntries = [];
        for (const file of files) {
            const previewUrl = file.type.startsWith('image/') ? URL.createObjectURL(file) : null;
            try {
                const url = await uploadFile(file, (pct) => {
                    setUploadProgress(Math.round(((done + pct / 100) / total) * 100));
                });
                newEntries.push({ file, previewUrl, uploadedUrl: url });
                done++;
                setUploadProgress(Math.round((done / total) * 100));
            } catch (err) {
                toast.error(`Failed to upload ${file.name}`);
            }
        }

        setMediaFiles(prev => [...prev, ...newEntries]);
        setUploading(false);
        setUploadProgress(0);
        // Reset input so the same file can be re-selected
        if (fileInputRef.current) fileInputRef.current.value = '';
    };

    const removeMedia = (idx) => {
        setMediaFiles(prev => {
            const next = [...prev];
            if (next[idx].previewUrl) URL.revokeObjectURL(next[idx].previewUrl);
            next.splice(idx, 1);
            return next;
        });
    };

    const handleSubmit = async () => {
        if (!form.title || !form.quantity || !form.destinationCountry) {
            toast.error('Please fill all required fields');
            return;
        }
        setSubmitting(true);
        try {
            const payload = {
                ...form,
                quantity: parseInt(form.quantity),
                targetPriceMinor: form.targetPriceMinor ? Math.round(parseFloat(form.targetPriceMinor) * 100) : null,
                mediaUrls: mediaFiles.map(m => m.uploadedUrl).filter(Boolean),
            };
            const res = await rfqApi.createRfq(payload);
            toast.success('RFQ created! Sellers will start quoting soon.');
            navigate(`/buyer/rfq/${res.data.id}`);
        } catch (err) {
            toast.error(err.response?.data?.message || 'Failed to create RFQ');
        } finally {
            setSubmitting(false);
        }
    };

    return (
        <div className="min-h-screen bg-gradient-to-br from-slate-50 to-brand-50/30 p-6">
            <div className="max-w-2xl mx-auto">
                {/* Header */}
                <div className="flex items-center gap-3 mb-8">
                    <button onClick={() => navigate('/buyer/rfq')} className="p-2 rounded-lg hover:bg-slate-200 transition-colors">
                        <ArrowLeft size={20} className="text-slate-600" />
                    </button>
                    <div>
                        <h1 className="text-2xl font-bold text-slate-900">Create RFQ</h1>
                        <p className="text-slate-500 text-sm">Request competitive quotes from verified sellers</p>
                    </div>
                </div>

                {/* Step Indicator */}
                <div className="flex items-center gap-2 mb-8">
                    {STEPS.map((s, i) => (
                        <React.Fragment key={s.id}>
                            <div
                                className={`flex items-center gap-2 px-3 py-2 rounded-xl text-sm font-medium transition-all ${step === s.id ? 'bg-brand-600 text-white shadow-md shadow-brand-200' :
                                    step > s.id ? 'bg-emerald-100 text-emerald-700' : 'bg-slate-100 text-slate-500'
                                    }`}
                            >
                                {step > s.id ? <Check size={16} /> : s.icon}
                                <span className="hidden sm:inline">{s.title}</span>
                            </div>
                            {i < STEPS.length - 1 && <div className={`flex-1 h-0.5 rounded ${step > s.id ? 'bg-emerald-300' : 'bg-slate-200'}`} />}
                        </React.Fragment>
                    ))}
                </div>

                <motion.div
                    key={step}
                    initial={{ opacity: 0, x: 20 }}
                    animate={{ opacity: 1, x: 0 }}
                    exit={{ opacity: 0, x: -20 }}
                    className="bg-white rounded-2xl border border-slate-200 shadow-sm p-6 space-y-5"
                >
                    {step === 1 && (
                        <>
                            <h2 className="text-lg font-semibold text-slate-800 flex items-center gap-2">
                                <Package size={20} className="text-brand-600" /> Product Details
                            </h2>
                            <div>
                                <label className={labelCls}>Title <span className="text-red-500">*</span></label>
                                <input className={inputCls} value={form.title} onChange={set('title')} placeholder="e.g. Organic Cotton T-Shirts, 500 units" />
                            </div>
                            <div>
                                <label className={labelCls}>Description / Specifications</label>
                                <textarea className={`${inputCls} resize-none`} rows={4} value={form.details} onChange={set('details')} placeholder="Describe product specs, quality requirements, packaging, etc." />
                            </div>
                            <div className="grid grid-cols-2 gap-4">
                                <div>
                                    <label className={labelCls}>Quantity <span className="text-red-500">*</span></label>
                                    <input type="number" min={1} className={inputCls} value={form.quantity} onChange={set('quantity')} placeholder="500" />
                                </div>
                                <div>
                                    <label className={labelCls}>Unit</label>
                                    <select className={selectCls} value={form.unit} onChange={set('unit')}>
                                        {UNITS.map(u => <option key={u}>{u}</option>)}
                                    </select>
                                </div>
                            </div>
                        </>
                    )}

                    {step === 2 && (
                        <>
                            <h2 className="text-lg font-semibold text-slate-800 flex items-center gap-2">
                                <Globe size={20} className="text-brand-600" /> Destination
                            </h2>
                            <div>
                                <label className={labelCls}>Destination Country <span className="text-red-500">*</span></label>
                                <input
                                    className={`${inputCls} bg-slate-100 text-slate-500 cursor-not-allowed`}
                                    value={form.destinationCountry}
                                    readOnly
                                    disabled
                                    placeholder="e.g. US, DE, GB"
                                    maxLength={2}
                                />
                                <p className="text-xs text-slate-400 mt-1">Sourced automatically from your profile</p>
                            </div>
                            <div className="grid grid-cols-2 gap-4">
                                <div>
                                    <label className={labelCls}>Shipping Mode <span className="text-red-500">*</span></label>
                                    <select className={selectCls} value={form.shippingMode} onChange={set('shippingMode')}>
                                        {['AIR', 'SEA', 'ROAD', 'COURIER'].map(m => <option key={m}>{m}</option>)}
                                    </select>
                                </div>
                                <div>
                                    <label className={labelCls}>Incoterm <span className="text-red-500">*</span></label>
                                    <select className={selectCls} value={form.incoterm} onChange={set('incoterm')}>
                                        {['EXW', 'FCA', 'FAS', 'FOB', 'CFR', 'CIF', 'CPT', 'CIP', 'DAP', 'DPU', 'DDP'].map(i => <option key={i}>{i}</option>)}
                                    </select>
                                </div>
                            </div>
                        </>
                    )}

                    {step === 3 && (
                        <>
                            <h2 className="text-lg font-semibold text-slate-800 flex items-center gap-2">
                                <DollarSign size={20} className="text-brand-600" /> Pricing & Timeline
                            </h2>
                            <div className="grid grid-cols-2 gap-4">
                                <div>
                                    <label className={labelCls}>Target Price (optional)</label>
                                    <input type="number" min={0} className={inputCls} value={form.targetPriceMinor} onChange={set('targetPriceMinor')} placeholder="e.g. 12.50" />
                                    <p className="text-xs text-slate-400 mt-1">Per unit in target currency</p>
                                </div>
                                <div>
                                    <label className={labelCls}>Target Currency</label>
                                    <div className="relative">
                                        <select
                                            className={`${selectCls} bg-slate-100 text-slate-500 cursor-not-allowed pr-10`}
                                            value={form.targetCurrency}
                                            disabled
                                        >
                                            {['USD', 'EUR', 'GBP', 'AED', 'JPY', 'INR'].map(c => <option key={c}>{c}</option>)}
                                        </select>
                                        <div className="absolute inset-y-0 right-3 flex items-center pointer-events-none">
                                            <span className="text-slate-400">🔒</span>
                                        </div>
                                    </div>
                                    <p className="text-[10px] text-slate-400 mt-1 italic">Locked to your profile currency</p>
                                </div>
                            </div>
                            <div className="bg-brand-50 rounded-xl p-4 text-sm text-brand-700 border border-brand-100">
                                <strong>💡 Tip:</strong> Setting a target price helps sellers understand your budget and submit more competitive quotes.
                            </div>
                            {/* Media upload */}
                            <div>
                                <label className={labelCls}>Product Images / Spec Sheets <span className="text-slate-400 font-normal">(optional, max 6 · 10 MB each)</span></label>

                                {/* Hidden file input */}
                                <input
                                    ref={fileInputRef}
                                    type="file"
                                    multiple
                                    accept="image/*,.pdf,.doc,.docx,.xls,.xlsx"
                                    className="hidden"
                                    onChange={handleFilePick}
                                    disabled={uploading}
                                />

                                {/* Preview grid */}
                                {mediaFiles.length > 0 && (
                                    <div className="grid grid-cols-3 gap-2 mb-3">
                                        {mediaFiles.map((m, i) => (
                                            <div key={i} className="relative group rounded-xl overflow-hidden border border-slate-200 bg-slate-50 aspect-square">
                                                {m.previewUrl ? (
                                                    <img src={m.previewUrl} alt="preview" className="w-full h-full object-cover" />
                                                ) : (
                                                    <div className="w-full h-full flex items-center justify-center">
                                                        <ImageIcon size={24} className="text-slate-400" />
                                                        <span className="text-[10px] text-slate-500 mt-1 px-1 text-center truncate">{m.file.name}</span>
                                                    </div>
                                                )}
                                                <button
                                                    type="button"
                                                    onClick={() => removeMedia(i)}
                                                    className="absolute top-1 right-1 bg-black/50 hover:bg-red-600 text-white rounded-full p-0.5 opacity-0 group-hover:opacity-100 transition-opacity"
                                                >
                                                    <X size={12} />
                                                </button>
                                                <div className="absolute bottom-1 left-1 bg-emerald-500 rounded-full w-3 h-3" title="Uploaded" />
                                            </div>
                                        ))}
                                    </div>
                                )}

                                {/* Upload drop zone / click area */}
                                <div
                                    onClick={() => !uploading && fileInputRef.current?.click()}
                                    className={`border-2 border-dashed rounded-xl p-5 text-center transition-colors ${uploading
                                        ? 'border-brand-300 bg-brand-50 cursor-wait'
                                        : 'border-slate-300 hover:bg-slate-50 cursor-pointer'
                                        }`}
                                >
                                    {uploading ? (
                                        <>
                                            <Loader2 size={24} className="mx-auto text-brand-500 animate-spin mb-2" />
                                            <p className="text-sm text-brand-600 font-medium">Uploading… {uploadProgress}%</p>
                                            <div className="w-full bg-slate-200 rounded-full h-1.5 mt-2 overflow-hidden">
                                                <motion.div
                                                    className="bg-brand-500 h-full rounded-full"
                                                    style={{ width: `${uploadProgress}%` }}
                                                    transition={{ duration: 0.25 }}
                                                />
                                            </div>
                                        </>
                                    ) : (
                                        <>
                                            <Upload size={24} className="mx-auto text-slate-400 mb-2" />
                                            <p className="text-sm text-slate-600 font-medium">
                                                {mediaFiles.length > 0 ? 'Add more files' : 'Click to upload images or spec sheets'}
                                            </p>
                                            <p className="text-xs text-slate-400 mt-1">JPG, PNG, PDF, DOC, XLS · Max 10 MB per file</p>
                                        </>
                                    )}
                                </div>
                            </div>
                        </>
                    )}
                </motion.div>

                {/* Navigation Buttons */}
                <div className="flex justify-between mt-6">
                    <button
                        onClick={() => step > 1 ? setStep(s => s - 1) : navigate('/buyer/rfq')}
                        className="flex items-center gap-2 px-5 py-2.5 rounded-xl border border-slate-300 text-slate-600 font-medium hover:bg-slate-100 transition-colors"
                    >
                        <ArrowLeft size={16} /> Back
                    </button>
                    {step < 3 ? (
                        <button
                            onClick={() => setStep(s => s + 1)}
                            className="flex items-center gap-2 px-5 py-2.5 rounded-xl bg-brand-600 text-white font-semibold hover:bg-brand-700 transition-colors shadow-md shadow-brand-200"
                        >
                            Next <ArrowRight size={16} />
                        </button>
                    ) : (
                        <motion.button
                            whileHover={{ scale: 1.02 }}
                            whileTap={{ scale: 0.98 }}
                            onClick={handleSubmit}
                            disabled={submitting}
                            className="flex items-center gap-2 px-6 py-2.5 rounded-xl bg-gradient-to-r from-emerald-500 to-emerald-600 text-white font-semibold shadow-md shadow-emerald-200 disabled:opacity-60 disabled:cursor-not-allowed transition-all"
                        >
                            {submitting ? <Loader2 size={16} className="animate-spin" /> : <Check size={16} />}
                            Submit RFQ
                        </motion.button>
                    )}
                </div>
            </div>
        </div>
    );
}
