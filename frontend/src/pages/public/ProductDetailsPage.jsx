import React, { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import productApi from '../../api/productApi';
import ProductMediaCarousel from '../../components/products/ProductMediaCarousel';
import ProductInfoPanel from '../../components/products/ProductInfoPanel';
import ProductTabs from '../../components/products/ProductTabs';
import ReviewsSection from '../../components/products/ReviewsSection';
import ProductDetailsSkeleton from '../../components/products/ProductDetailsSkeleton';
import ErrorState from '../../components/analytics/ErrorState';
import { ArrowLeft } from 'lucide-react';

const ProductDetailsPage = () => {
    const { id } = useParams();
    const navigate = useNavigate();
    const [product, setProduct] = useState(null);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);
    const [activeTab, setActiveTab] = useState('desc');

    useEffect(() => {
        const fetchProduct = async () => {
            setLoading(true);
            try {
                const { data } = await productApi.getProduct(id);
                // Map backend response to component state structure
                const mappedProduct = {
                    ...data,
                    images: data.media?.filter(m => m.type === 'IMAGE').map(m => m.url) || [],
                    thumbnailUrl: data.media?.find(m => m.type === 'IMAGE')?.url || null,
                    videoUrl: data.media?.find(m => m.type === 'VIDEO')?.url || null,
                    sellerCompanyName: data.seller?.companyName || 'Unknown Seller',
                    sellerId: data.seller?.id,
                    weight: data.weightGrams ? (data.weightGrams / 1000).toFixed(2) : null,
                    dimensions: data.lengthMm ? `${data.lengthMm}x${data.widthMm}x${data.heightMm} mm` : null
                };
                setProduct(mappedProduct);
                setError(null);
            } catch (err) {
                console.error('Failed to load product', err);
                setError(err.response?.status === 404 ? 'Product not found' : 'Failed to load product details');
            } finally {
                setLoading(false);
            }
        };

        if (id) {
            fetchProduct();
        }
    }, [id]);

    if (loading) return <ProductDetailsSkeleton />;

    if (error) return (
        <div className="min-h-screen flex items-center justify-center bg-slate-50">
            <ErrorState
                title={error}
                message="We couldn't find the product you're looking for."
                onRetry={() => navigate('/products')}
                actionLabel="Back to Products"
            />
        </div>
    );

    if (!product) return null;

    return (
        <div className="bg-white min-h-screen pb-20">
            <div className="container mx-auto px-4 py-8">
                {/* Back Button */}
                <button
                    onClick={() => navigate(-1)}
                    className="flex items-center gap-2 text-slate-500 hover:text-brand-600 transition-colors mb-6 group"
                >
                    <ArrowLeft size={18} className="group-hover:-translate-x-1 transition-transform" />
                    Back
                </button>

                {/* Top Section: Media & Info */}
                <div className="grid grid-cols-1 md:grid-cols-2 gap-12 mb-16">
                    <ProductMediaCarousel
                        images={product.images || [product.thumbnailUrl]} // Fallback to thumbnail if images array empty
                        videoUrl={product.videoUrl}
                    />
                    <ProductInfoPanel product={product} />
                </div>

                {/* Bottom Section: Tabs */}
                <div className="max-w-4xl">
                    <ProductTabs activeTab={activeTab} onChange={setActiveTab} />

                    <div className="min-h-[400px]">
                        {activeTab === 'desc' && (
                            <div className="prose prose-slate max-w-none">
                                <h3 className="text-xl font-bold text-slate-900 mb-4">Product Description</h3>
                                <p className="text-slate-600 leading-relaxed whitespace-pre-wrap">{product.description}</p>
                            </div>
                        )}

                        {activeTab === 'specs' && (
                            <div>
                                <h3 className="text-xl font-bold text-slate-900 mb-6">Detailed Specifications</h3>
                                <div className="bg-slate-50 rounded-2xl p-6 border border-slate-200">
                                    <dl className="grid grid-cols-1 sm:grid-cols-2 gap-x-8 gap-y-6">
                                        <SpecRow label="HS Code" value={product.hsCode} />
                                        <SpecRow label="Material" value={product.material || 'N/A'} />
                                        <SpecRow label="Weight" value={product.weight ? `${product.weight} kg` : 'N/A'} />
                                        <SpecRow label="Dimensions" value={product.dimensions || 'N/A'} />
                                        <SpecRow label="Origin" value="India" />
                                        <SpecRow label="Manufacturer" value={product.sellerCompanyName} />
                                    </dl>
                                </div>
                            </div>
                        )}

                        {activeTab === 'reviews' && (
                            <ReviewsSection productId={product.id} />
                        )}
                    </div>
                </div>
            </div>
        </div>
    );
};

const SpecRow = ({ label, value }) => (
    <div className="flex flex-col">
        <dt className="text-sm font-medium text-slate-500">{label}</dt>
        <dd className="text-base font-medium text-slate-900 mt-1">{value}</dd>
    </div>
);

export default ProductDetailsPage;
