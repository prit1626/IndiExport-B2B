import React, { useState, useEffect } from 'react';
import productApi from '../../api/productApi';
import RatingStars from '../common/RatingStars';
import Badge from '../common/Badge';
import Toggle from '../common/Toggle';
import Pagination from '../common/Pagination';
import ReviewsSkeleton from './ReviewsSkeleton';
import EmptyState from '../analytics/EmptyState';
import { format as formatDate } from 'date-fns';
import { User, CheckCircle, ThumbsUp } from 'lucide-react';

const ReviewsSection = ({ productId }) => {
    const [reviews, setReviews] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);

    // Filters & Pagination
    const [filters, setFilters] = useState({
        page: 0,
        size: 5,
        rating: '',
        verifiedOnly: false,
        photosOnly: false,
        sort: 'latest' // latest, highest_rating, lowest_rating
    });

    const [totalPages, setTotalPages] = useState(0);

    const fetchReviews = async () => {
        setLoading(true);
        try {
            const { data } = await productApi.getProductReviews(productId, filters);
            setReviews(data.content);
            setTotalPages(data.totalPages);
            setError(null);
        } catch (err) {
            console.error('Failed to fetch reviews', err);
            setError('Failed to load reviews');
        } finally {
            setLoading(false);
        }
    };

    useEffect(() => {
        fetchReviews();
    }, [productId, filters]);

    const handleFilterChange = (key, value) => {
        setFilters(prev => ({ ...prev, [key]: value, page: 0 })); // Reset page on filter change
    };

    const handlePageChange = (page) => {
        setFilters(prev => ({ ...prev, page }));
    };

    if (error) return (
        <div className="text-center py-10">
            <p className="text-red-500 mb-4">{error}</p>
            <button onClick={fetchReviews} className="text-brand-600 hover:underline">Try Again</button>
        </div>
    );

    return (
        <div className="space-y-8">
            {/* Filter Bar */}
            <div className="bg-white p-4 rounded-xl border border-slate-200 flex flex-col md:flex-row gap-4 items-center justify-between">
                <div className="flex flex-wrap items-center gap-4">
                    <select
                        value={filters.rating}
                        onChange={(e) => handleFilterChange('rating', e.target.value)}
                        className="px-3 py-2 bg-slate-50 border border-slate-200 rounded-lg text-sm focus:ring-2 focus:ring-brand-500 outline-none"
                    >
                        <option value="">All Ratings</option>
                        <option value="5">5 Stars</option>
                        <option value="4">4 Stars & Up</option>
                        <option value="3">3 Stars & Up</option>
                    </select>

                    <select
                        value={filters.sort}
                        onChange={(e) => handleFilterChange('sort', e.target.value)}
                        className="px-3 py-2 bg-slate-50 border border-slate-200 rounded-lg text-sm focus:ring-2 focus:ring-brand-500 outline-none"
                    >
                        <option value="latest">Latest</option>
                        <option value="highest_rating">Highest Rating</option>
                        <option value="lowest_rating">Lowest Rating</option>
                    </select>
                </div>

                <div className="flex items-center gap-6">
                    <Toggle
                        label="Verified Purchase"
                        checked={filters.verifiedOnly}
                        onChange={(val) => handleFilterChange('verifiedOnly', val)}
                    />
                    {/* <Toggle 
                        label="With Photos"
                        checked={filters.photosOnly}
                        onChange={(val) => handleFilterChange('photosOnly', val)}
                    /> */}
                </div>
            </div>

            {/* List */}
            {loading ? <ReviewsSkeleton /> : (
                <div className="space-y-6">
                    {reviews.length > 0 ? (
                        reviews.map(review => <ReviewCard key={review.id} review={review} />)
                    ) : (
                        <EmptyState message="No reviews match your filters" />
                    )}
                </div>
            )}

            {/* Pagination */}
            {!loading && totalPages > 1 && (
                <Pagination
                    currentPage={filters.page}
                    totalPages={totalPages}
                    onPageChange={handlePageChange}
                />
            )}
        </div>
    );
};

const ReviewCard = ({ review }) => {
    return (
        <div className="bg-white p-6 rounded-2xl border border-slate-100 shadow-sm hover:shadow-md transition-shadow">
            <div className="flex justify-between items-start mb-4">
                <div className="flex items-center gap-3">
                    <div className="w-10 h-10 bg-brand-100 rounded-full flex items-center justify-center text-brand-700 font-bold">
                        {review.buyerName ? review.buyerName[0] : <User size={20} />}
                    </div>
                    <div>
                        <h4 className="font-semibold text-slate-900">{review.buyerName || 'Anonymous Buyer'}</h4>
                        <p className="text-xs text-slate-500">{formatDate(new Date(review.createdAt), 'MMM dd, yyyy')}</p>
                    </div>
                </div>
                {review.verifiedPurchase && (
                    <Badge variant="success" size="sm" className="gap-1">
                        <CheckCircle size={12} /> Verified Purchase
                    </Badge>
                )}
            </div>

            <div className="mb-3">
                <RatingStars rating={review.rating} />
            </div>

            <p className="text-slate-700 leading-relaxed mb-4">{review.comment}</p>

            {/* Photos would go here if backend supported returning them */}

            {/* Helpful Button (Mock interaction) */}
            <button className="flex items-center gap-2 text-sm text-slate-500 hover:text-slate-800 transition-colors">
                <ThumbsUp size={14} /> Helpful
            </button>
        </div>
    );
};

export default ReviewsSection;
