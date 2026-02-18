import React from 'react';
import { useForm } from 'react-hook-form';
import { useDropzone } from 'react-dropzone';
import { Loader2, Upload, X } from 'lucide-react';
import { useNavigate } from 'react-router-dom';

const ProductForm = ({ defaultValues, onSubmit, submitting, isEditMode, selectedFiles = [], onFilesChange }) => {
    const navigate = useNavigate();
    const { register, handleSubmit, formState: { errors } } = useForm({
        defaultValues: defaultValues || {
            active: true,
            title: '',
            priceINRPaise: '',
            minQty: '',
            leadTimeDays: '',
            weightGrams: '',
            hsCode: '',
            origin: 'India',
            incoterm: 'FOB',
            tags: '',
            unit: 'KG'
        }
    });

    // Dropzone logic for Create Mode
    const onDrop = (acceptedFiles) => {
        if (onFilesChange) {
            onFilesChange([...selectedFiles, ...acceptedFiles]);
        }
    };

    const { getRootProps, getInputProps, isDragActive } = useDropzone({
        onDrop,
        accept: {
            'image/*': ['.jpeg', '.jpg', '.png', '.webp']
        },
        disabled: isEditMode
    });

    const removeFile = (index) => {
        if (onFilesChange) {
            onFilesChange(selectedFiles.filter((_, i) => i !== index));
        }
    };

    const onFormSubmit = (data) => {
        // Convert types and map to backend DTO
        const payload = {
            title: data.title,
            brand: data.brand,
            description: data.description,
            sku: data.sku,
            pricePaise: Math.round(parseFloat(data.priceINRPaise) * 100),
            minQty: parseInt(data.minQty),
            unit: data.unit,
            weightGrams: parseInt(data.weightGrams) || 0,
            leadTimeDays: parseInt(data.leadTimeDays),
            hsCode: data.hsCode,
            incoterm: data.incoterm,
            tagNames: typeof data.tags === 'string' ? data.tags.split(',').map(t => t.trim()).filter(t => t) : data.tags,
            // categoryIds: [], // TODO: Add category selection
            status: data.active ? 'ACTIVE' : 'DRAFT'
        };
        onSubmit(payload);
    };

    const inputClass = "w-full rounded-lg border-slate-300 text-sm focus:border-brand-500 focus:ring-brand-500";
    const labelClass = "block text-sm font-medium text-slate-700 mb-1";
    const errorClass = "text-xs text-red-500 mt-1";

    return (
        <form onSubmit={handleSubmit(onFormSubmit)} className="space-y-8 bg-white p-6 rounded-xl border border-slate-200">
            {/* Basic Info */}
            <div>
                <h3 className="text-lg font-semibold text-slate-800 mb-4 pb-2 border-b border-slate-100">Basic Information</h3>
                <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
                    <div className="col-span-2">
                        <label className={labelClass}>Product Title *</label>
                        <input {...register('title', { required: 'Title is required' })} className={inputClass} placeholder="e.g. Organic Turmeric Powder" />
                        {errors.title && <p className={errorClass}>{errors.title.message}</p>}
                    </div>
                    <div>
                        <label className={labelClass}>SKU *</label>
                        <input {...register('sku', { required: 'SKU is required' })} className={inputClass} placeholder="e.g. PROD-001" />
                        {errors.sku && <p className={errorClass}>{errors.sku.message}</p>}
                    </div>
                    <div>
                        <label className={labelClass}>Brand</label>
                        <input {...register('brand')} className={inputClass} placeholder="e.g. Swara Organics" />
                    </div>
                    <div className="col-span-2">
                        <label className={labelClass}>Description *</label>
                        <textarea {...register('description', { required: 'Description is required' })} rows={4} className={inputClass} placeholder="Detailed product description..." />
                        {errors.description && <p className={errorClass}>{errors.description.message}</p>}
                    </div>
                    <div>
                        <label className={labelClass}>HS Code *</label>
                        <input {...register('hsCode', { required: 'HS Code is required' })} className={inputClass} placeholder="Harmonized System Code" />
                        {errors.hsCode && <p className={errorClass}>{errors.hsCode.message}</p>}
                    </div>
                </div>
            </div>

            {/* Pricing & Logistics */}
            <div>
                <h3 className="text-lg font-semibold text-slate-800 mb-4 pb-2 border-b border-slate-100">Pricing & Logistics</h3>
                <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
                    <div>
                        <label className={labelClass}>Price (INR) *</label>
                        <input type="number" step="0.01" {...register('priceINRPaise', { required: 'Price is required', min: 1 })} className={inputClass} />
                        {errors.priceINRPaise && <p className={errorClass}>{errors.priceINRPaise.message}</p>}
                    </div>
                    <div>
                        <label className={labelClass}>Unit *</label>
                        <select {...register('unit')} className={inputClass}>
                            <option value="KG">KG</option>
                            <option value="TON">TON</option>
                            <option value="PIECE">PIECE</option>
                            <option value="BOX">BOX</option>
                            <option value="LITER">LITER</option>
                        </select>
                    </div>
                    <div>
                        <label className={labelClass}>Minimum Qty *</label>
                        <input type="number" {...register('minQty', { required: 'Min Qty is required', min: 1 })} className={inputClass} />
                        {errors.minQty && <p className={errorClass}>{errors.minQty.message}</p>}
                    </div>
                    <div>
                        <label className={labelClass}>Weight (grams)</label>
                        <input type="number" {...register('weightGrams')} className={inputClass} placeholder="Per unit" />
                    </div>
                    <div>
                        <label className={labelClass}>Lead Time (Days) *</label>
                        <input type="number" {...register('leadTimeDays', { required: true, min: 0 })} className={inputClass} />
                    </div>
                    <div>
                        <label className={labelClass}>Incoterm</label>
                        <select {...register('incoterm')} className={inputClass}>
                            <option value="FOB">FOB (Free on Board)</option>
                            <option value="EXW">EXW (Ex Works)</option>
                            <option value="CIF">CIF (Cost, Insurance, Freight)</option>
                        </select>
                    </div>
                </div>
            </div>

            {/* Tags */}
            <div>
                <h3 className="text-lg font-semibold text-slate-800 mb-4 pb-2 border-b border-slate-100">Categorization</h3>
                <div className="col-span-2">
                    <label className={labelClass}>Tags (comma separated)</label>
                    <input {...register('tags')} className={inputClass} placeholder="spice, organic, bulk, yellow" />
                </div>
            </div>

            {/* Image Upload (Only for Create Mode) */}
            {!isEditMode && (
                <div>
                    <h3 className="text-lg font-semibold text-slate-800 mb-4 pb-2 border-b border-slate-100">Product Images</h3>
                    <div
                        {...getRootProps()}
                        className={`
                            border-2 border-dashed rounded-xl p-8 text-center cursor-pointer transition-colors
                            ${isDragActive ? 'border-brand-500 bg-brand-50' : 'border-slate-300 hover:border-brand-400 hover:bg-slate-50'}
                        `}
                    >
                        <input {...getInputProps()} />
                        <div className="flex flex-col items-center gap-2">
                            <Upload className="h-8 w-8 text-slate-400" />
                            <p className="text-sm text-slate-600 font-medium">Drag & drop images, or click to select</p>
                            <p className="text-xs text-slate-400">Up to 5MB each</p>
                        </div>
                    </div>

                    {/* Preview */}
                    {selectedFiles.length > 0 && (
                        <div className="grid grid-cols-4 md:grid-cols-6 gap-4 mt-4">
                            {selectedFiles.map((file, index) => (
                                <div key={index} className="relative group rounded-lg overflow-hidden border border-slate-200 aspect-square bg-slate-100">
                                    <img src={URL.createObjectURL(file)} alt="preview" className="w-full h-full object-cover" />
                                    <button
                                        type="button"
                                        onClick={() => removeFile(index)}
                                        className="absolute top-1 right-1 bg-black/50 text-white p-1 rounded-full hover:bg-red-500 transition-colors"
                                    >
                                        <X size={12} />
                                    </button>
                                </div>
                            ))}
                        </div>
                    )}
                </div>
            )}

            {/* Actions */}
            <div className="flex justify-end gap-3 pt-4 border-t border-slate-100 mt-8">
                <button type="button" onClick={() => navigate('/seller/products')} className="px-6 py-2 border border-slate-300 text-slate-700 font-medium rounded-lg hover:bg-slate-50">
                    Cancel
                </button>
                <button
                    type="submit"
                    disabled={submitting}
                    className="px-6 py-2 bg-brand-600 hover:bg-brand-700 text-white font-bold rounded-lg shadow-sm flex items-center gap-2 disabled:opacity-70"
                >
                    {submitting && <Loader2 className="animate-spin" size={18} />}
                    {isEditMode ? 'Save Changes' : (selectedFiles.length > 0 ? `Create & Upload (${selectedFiles.length})` : 'Create Product')}
                </button>
            </div>
        </form>
    );
};

export default ProductForm;
