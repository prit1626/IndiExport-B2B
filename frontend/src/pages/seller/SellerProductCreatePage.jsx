import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import toast from 'react-hot-toast';
import { ArrowLeft } from 'lucide-react';
import { Link } from 'react-router-dom';
import productApi from '../../api/productApi';
import ProductForm from '../../components/sellerProducts/ProductForm';

const SellerProductCreatePage = () => {
    const navigate = useNavigate();
    const [submitting, setSubmitting] = useState(false);
    const [files, setFiles] = useState([]);

    const handleSubmit = async (data) => {
        setSubmitting(true);
        try {
            // 1. Create Product
            const res = await productApi.sellerCreateProduct(data);
            const newId = res.data.id;

            // 2. Upload Images (if any)
            if (files.length > 0) {
                const formData = new FormData();
                files.forEach(file => {
                    if (file.type.startsWith('image/')) {
                        formData.append('files', file);
                    }
                    // Video support could be added here if needed
                });

                try {
                    await productApi.sellerUploadProductMedia(newId, formData);
                } catch (uploadError) {
                    console.error("Image upload failed", uploadError);
                    toast.error('Product created, but image upload failed. Please try adding images in Edit mode.');
                }
            }

            toast.success('Product created successfully!');
            navigate('/seller/products');
        } catch (error) {
            console.error(error);
            toast.error(error.response?.data?.message || 'Failed to create product');
        } finally {
            setSubmitting(false);
        }
    };

    return (
        <div className="min-h-screen bg-slate-50 pb-20">
            <div className="bg-white border-b border-slate-200 py-4 shadow-sm">
                <div className="container mx-auto px-4 max-w-4xl flex items-center gap-4">
                    <Link to="/seller/products" className="text-slate-500 hover:text-slate-800">
                        <ArrowLeft size={24} />
                    </Link>
                    <h1 className="text-xl font-bold text-slate-800">Add New Product</h1>
                </div>
            </div>

            <div className="container mx-auto px-4 max-w-4xl py-8">
                <ProductForm
                    onSubmit={handleSubmit}
                    submitting={submitting}
                    selectedFiles={files}
                    onFilesChange={setFiles}
                />
            </div>
        </div>
    );
};

export default SellerProductCreatePage;
