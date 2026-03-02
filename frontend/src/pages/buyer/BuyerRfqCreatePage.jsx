import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { motion } from 'framer-motion';
import { ArrowLeft, ArrowRight, Package, Globe, Anchor, DollarSign, Upload, Check, Loader2 } from 'lucide-react';
import rfqApi from '../../api/rfqApi';
import toast from 'react-hot-toast';

const INCOTERMS = ['EXW', 'FCA', 'CPT', 'CIP', 'DAP', 'DPU', 'DDP', 'FAS', 'FOB', 'CFR', 'CIF'];
const SHIPPING_MODES = ['OCEAN', 'AIR', 'ROAD', 'RAIL'];
const UNITS = ['PCS', 'KG', 'TON', 'MT', 'LTR', 'CBM', 'BOX', 'ROLL'];

const STEPS = [
    { id: 1, title: 'Product Details', icon: <Package size={18} /> },
    { id: 2, title: 'Destination & Shipping', icon: <Globe size={18} /> },
    { id: 3, title: 'Pricing & Timeline', icon: <DollarSign size={18} /> },
];

const inputCls = 'w-full bg-slate-50 border border-slate-200 rounded-xl px-4 py-3 text-slate-800 text-sm focus:outline-none focus:ring-2 focus:ring-brand-400 focus:border-transparent transition';
const labelCls = 'block text-sm font-medium text-slate-700 mb-1.5';
const selectCls = `${inputCls} appearance-none cursor-pointer`;

export default function BuyerRfqCreatePage() {
    const navigate = useNavigate();
    const [step, setStep] = useState(1);
    const [submitting, setSubmitting] = useState(false);
    const [form, setForm] = useState({
        title: '', details: '', quantity: '', unit: 'PCS',
        destinationCountry: '', shippingMode: 'OCEAN', incoterm: 'FOB',
        targetPriceMinor: '', targetCurrency: 'USD', mediaUrls: [],
    });

    const set = (field) => (e) => setForm(f => ({ ...f, [field]: e.target.value }));

    const handleSubmit = async () => {
        if (!form.title || !form.quantity || !form.destinationCountry || !form.incoterm) {
            toast.error('Please fill all required fields');
            return;
        }
        setSubmitting(true);
        try {
            const payload = {
                ...form,
                quantity: parseInt(form.quantity),
                targetPriceMinor: form.targetPriceMinor ? Math.round(parseFloat(form.targetPriceMinor) * 100) : null,
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
                                <Globe size={20} className="text-brand-600" /> Destination & Shipping
                            </h2>
                            <div>
                                <label className={labelCls}>Destination Country <span className="text-red-500">*</span></label>
                                <input className={inputCls} value={form.destinationCountry} onChange={set('destinationCountry')} placeholder="e.g. US, DE, GB" maxLength={2} />
                                <p className="text-xs text-slate-400 mt-1">ISO 2-letter country code</p>
                            </div>
                            <div>
                                <label className={labelCls}>Shipping Mode</label>
                                <select className={selectCls} value={form.shippingMode} onChange={set('shippingMode')}>
                                    {SHIPPING_MODES.map(m => <option key={m}>{m}</option>)}
                                </select>
                            </div>
                            <div>
                                <label className={labelCls}>Incoterm <span className="text-red-500">*</span></label>
                                <select className={selectCls} value={form.incoterm} onChange={set('incoterm')}>
                                    {INCOTERMS.map(t => <option key={t}>{t}</option>)}
                                </select>
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
                                    <select className={selectCls} value={form.targetCurrency} onChange={set('targetCurrency')}>
                                        {['USD', 'EUR', 'GBP', 'AED', 'JPY', 'INR'].map(c => <option key={c}>{c}</option>)}
                                    </select>
                                </div>
                            </div>
                            <div className="bg-brand-50 rounded-xl p-4 text-sm text-brand-700 border border-brand-100">
                                <strong>💡 Tip:</strong> Setting a target price helps sellers understand your budget and submit more competitive quotes.
                            </div>
                            <div className="bg-slate-50 rounded-xl border border-dashed border-slate-300 p-6 text-center cursor-pointer hover:bg-slate-100 transition-colors">
                                <Upload size={24} className="mx-auto text-slate-400 mb-2" />
                                <p className="text-sm text-slate-500">Upload product images, spec sheets (optional)</p>
                                <p className="text-xs text-slate-400 mt-1">Image upload can be added via Cloudinary integration</p>
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
