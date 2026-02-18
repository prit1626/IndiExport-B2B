import React from 'react';
import { Star } from 'lucide-react';

const RatingStars = ({ rating, max = 5, size = 16, className = '' }) => {
    return (
        <div className={`flex items-center ${className}`}>
            {[...Array(max)].map((_, i) => {
                const filled = i < Math.round(rating); // Simple rounding for now
                return (
                    <Star
                        key={i}
                        size={size}
                        className={`${filled ? 'text-amber-400 fill-amber-400' : 'text-slate-300'}`}
                    />
                );
            })}
        </div>
    );
};

export default RatingStars;
