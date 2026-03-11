import React, { useState, useEffect } from 'react';
import reviewApi from '../../api/reviewApi';
import RatingStars from '../common/RatingStars';
import Badge from '../common/Badge';
import Toggle from '../common/Toggle';
import Pagination from '../common/Pagination';
import ReviewsSkeleton from './ReviewsSkeleton';
import EmptyState from '../analytics/EmptyState';
import { format as formatDate } from 'date-fns';
import { User, CheckCircle, Image as ImageIcon, Star } from 'lucide-react';

const ReviewsSection = ({ productId }) => {
    const [reviews, setReviews] = useState([]);
    const [summary, setSummary] = useState(null);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);

    const [filters, setFilters] = useState({
        page: 0,
        size: 5,
        verifiedOnly: false,
        photosOnly: false
    });

    const [totalPages, setTotalPages] = useState(0);

    const fetchData = async () => {
        setLoading(true);
        try {
            const [reviewsRes, summaryRes] = await Promise.all([
                reviewApi.getProductReviews(productId, filters),
                reviewApi.getProductRatingSummary(productId)
            ]);
            
            setReviews(reviewsRes.data.content || reviewsRes.content || []);
            setTotalPages(reviewsRes.data.totalPages || reviewsRes.totalPages || 0);
            setSummary(summaryRes.data || summaryRes);
            setError(null);
        } catch (err) {
            console.error('Failed to fetch reviews data', err);
            setError('Failed to load reviews');
        } finally {
            setLoading(false);
        }
    };

    useEffect(() => {
        fetchData();
    }, [productId, filters]);

    const handleFilterChange = (key, value) => {
        setFilters(prev => ({ ...prev, [key]: value, page: 0 }));
    };

    const handlePageChange = (page) => {
        setFilters(prev => ({ ...prev, page }));
    };

    if (error) return (
        <div className="text-center py-10">
            <p className="text-red-500 mb-4">{error}</p>
            <button onClick={fetchData} className="px-4 py-2 bg-slate-100 rounded-lg hover:bg-slate-200">Try Again</button>
        </div>
    );

    return (
        <div className="space-y-10">
            {/* Summary Section */}
            {!loading && summary && (
                <div className="grid grid-cols-1 md:grid-cols-3 gap-8 items-center bg-slate-50 p-8 rounded-3xl border border-slate-100">
                    <div className="text-center md:border-r border-slate-200">
                        <h4 className="text-5xl font-black text-slate-900 mb-2">{summary.averageRating?.toFixed(1) || '0.0'}</h4>
                        <div className="flex justify-center mb-2">
                            <RatingStars rating={summary.averageRating || 0} size={24} />
                        </div>
                        <p className="text-sm text-slate-500 font-medium">Based on {summary.totalReviews || 0} reviews</p>
                    </div>

                    <div className="md:col-span-2 space-y-2 px-4">
                        {/* Breakdown could be fetched or estimated; for now showing a simplified view or just bars */}
                        <p className="text-xs font-bold text-slate-400 uppercase tracking-widest mb-4">Rating Breakdown</p>
                        <div className="space-y-2">
                           {/* Simplified breakdown - in a real app, backend would return counts per star */}
                           {[5, 4, 3, 2, 1].map(star => (
                               <div key={star} className="flex items-center gap-3">
                                   <span className="text-xs font-bold text-slate-600 w-3">{star}</span>
                                   <Star size={12} className="text-amber-400 fill-amber-400" />
                                   <div className="flex-1 h-2 bg-slate-200 rounded-full overflow-hidden">
                                       <div className="h-full bg-amber-400 rounded-full" style={{ width: star === 5 ? '70%' : star === 4 ? '15%' : '5%' }}></div>
                                   </div>
                               </div>
                           ))}
                        </div>
                    </div>
                </div>
            )}

            {/* Filter Bar */}
            <div className="flex flex-wrap items-center justify-between gap-4 border-b border-slate-100 pb-6">
                <h3 className="text-xl font-bold text-slate-900">Customer Reviews</h3>
                <div className="flex items-center gap-6">
                    <Toggle
                        label="Verified Purchase"
                        checked={filters.verifiedOnly}
                        onChange={(val) => handleFilterChange('verifiedOnly', val)}
                    />
                    <Toggle
                        label="With Photos"
                        checked={filters.photosOnly}
                        onChange={(val) => handleFilterChange('photosOnly', val)}
                    />
                </div>
            </div>

            {/* List */}
            {loading ? <ReviewsSkeleton /> : (
                <div className="space-y-8">
                    {reviews.length > 0 ? (
                        reviews.map(review => <ReviewCard key={review.id} review={review} />)
                    ) : (
                        <EmptyState message="No reviews found for this selection" />
                    )}
                </div>
            )}

            {/* Pagination */}
            {!loading && totalPages > 1 && (
                <div className="pt-6 border-t border-slate-100">
                    <Pagination
                        currentPage={filters.page}
                        totalPages={totalPages}
                        onPageChange={handlePageChange}
                    />
                </div>
            )}
        </div>
    );
};

const ReviewCard = ({ review }) => {
    return (
        <div className="bg-white group">
            <div className="flex justify-between items-start mb-4">
                <div className="flex items-center gap-3">
                    <div className="w-10 h-10 bg-slate-100 rounded-full flex items-center justify-center text-slate-500 font-bold border border-slate-200">
                        {review.buyerName ? review.buyerName[0] : <User size={20} />}
                    </div>
                    <div>
                        <h4 className="font-bold text-slate-900">{review.buyerName || 'Anonymous Buyer'}</h4>
                        <div className="flex items-center gap-2 mt-0.5">
                            <RatingStars rating={review.rating} size={14} />
                            <span className="text-[10px] text-slate-400">•</span>
                            <span className="text-[11px] text-slate-500 font-medium">
                                {formatDate(new Date(review.createdAt), 'MMM dd, yyyy')}
                            </span>
                        </div>
                    </div>
                </div>
                <div className="flex flex-col items-end gap-2">
                    {review.verifiedPurchase && (
                        <div className="flex items-center gap-1.5 text-[11px] font-bold text-emerald-600 bg-emerald-50 px-2 py-1 rounded-lg">
                            <CheckCircle size={12} /> Verified Purchase
                        </div>
                    )}
                    <span className="text-[10px] text-slate-400 font-medium">{review.buyerCountry}</span>
                </div>
            </div>

            <p className="text-slate-700 leading-relaxed mb-4 text-sm font-medium">{review.reviewText}</p>

            {/* Photos */}
            {review.media && review.media.length > 0 && (
                <div className="flex flex-wrap gap-2 mb-4">
                    {review.media.map((m, idx) => (
                        <div key={idx} className="w-20 h-20 rounded-xl overflow-hidden border border-slate-100 hover:border-brand-300 transition-colors cursor-pointer">
                            <img src={m.url} alt="Review" className="w-full h-full object-cover rounded-xl" />
                        </div>
                    ))}
                </div>
            )}

            {/* Feedback Bar */}
            <div className="flex items-center gap-4 text-xs">
                <button className="text-slate-400 hover:text-brand-600 font-bold transition-colors">Helpful</button>
                <span className="text-slate-200">|</span>
                <button className="text-slate-400 hover:text-red-500 font-bold transition-colors">Report</button>
            </div>
        </div>
    );
};

export default ReviewsSection;
