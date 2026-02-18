import React, { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import toast from 'react-hot-toast';
import { ArrowLeft, Loader2 } from 'lucide-react';
import { Link } from 'react-router-dom';
import productApi from '../../api/productApi';
import ProductForm from '../../components/sellerProducts/ProductForm';
import ProductMediaUploader from '../../components/sellerProducts/ProductMediaUploader';

const SellerProductEditPage = () => {
    const { id } = useParams();
    const navigate = useNavigate();
    const [product, setProduct] = useState(null);
    const [loading, setLoading] = useState(true);
    const [submitting, setSubmitting] = useState(false);

    useEffect(() => {
        if (id) fetchProduct();
    }, [id]);

    const fetchProduct = async () => {
        try {
            const { data } = await productApi.sellerGetProductById(id);
            setProduct({
                ...data,
                priceINRPaise: data.priceINRPaise / 100 // Convert for form display
            });
        } catch (error) {
            toast.error('Failed to load product');
            navigate('/seller/products');
        } finally {
            setLoading(false);
        }
    };

    const handleSubmit = async (data) => {
        setSubmitting(true);
        try {
            await productApi.sellerUpdateProduct(id, data);
            toast.success('Product updated successfully');
            fetchProduct(); // Refresh data
        } catch (error) {
            toast.error('Failed to update product');
        } finally {
            setSubmitting(false);
        }
    };

    if (loading) return <div className="min-h-screen flex justify-center items-center"><Loader2 className="animate-spin" /></div>;

    return (
        <div className="min-h-screen bg-slate-50 pb-20">
            <div className="bg-white border-b border-slate-200 py-4 shadow-sm">
                <div className="container mx-auto px-4 max-w-4xl flex items-center gap-4">
                    <Link to="/seller/products" className="text-slate-500 hover:text-slate-800">
                        <ArrowLeft size={24} />
                    </Link>
                    <div>
                        <h1 className="text-xl font-bold text-slate-800">Edit Product</h1>
                        <p className="text-xs text-slate-500">{product?.title}</p>
                    </div>
                </div>
            </div>

            <div className="container mx-auto px-4 max-w-4xl py-8 grid grid-cols-1 lg:grid-cols-3 gap-8">
                {/* Left Column: Form */}
                <div className="lg:col-span-2">
                    <ProductForm
                        defaultValues={product}
                        onSubmit={handleSubmit}
                        submitting={submitting}
                        isEditMode
                    />
                </div>

                {/* Right Column: Media */}
                <div className="space-y-6">
                    <div className="bg-white p-6 rounded-xl border border-slate-200">
                        <h3 className="font-bold text-slate-800 mb-4">Product Images</h3>
                        {/* Current Gallery */}
                        <div className="grid grid-cols-3 gap-2 mb-4">
                            {product?.images?.map((url, i) => (
                                <img key={i} src={url} alt="product" className="w-full h-20 object-cover rounded-lg border border-slate-100" />
                            ))}
                        </div>

                        <div className="border-t border-slate-100 pt-4">
                            <ProductMediaUploader productId={id} onUploadSuccess={fetchProduct} />
                        </div>
                    </div>
                </div>
            </div>
        </div>
    );
};

export default SellerProductEditPage;
