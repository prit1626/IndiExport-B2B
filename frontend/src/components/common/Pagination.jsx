import React from 'react';
import { ChevronLeft, ChevronRight } from 'lucide-react';

const Pagination = ({ currentPage, totalPages, onPageChange }) => {
    if (totalPages <= 1) return null;

    const pages = [];
    // Simple pagination logic: show all if <= 7, else show sparse
    // For MVP, just showing start, end, and current window
    // Improved logic:
    const delta = 2;
    const range = [];
    const rangeWithDots = [];

    for (let i = 1; i <= totalPages; i++) {
        if (
            i === 1 ||
            i === totalPages ||
            (i >= currentPage - delta && i <= currentPage + delta)
        ) {
            range.push(i);
        }
    }

    let l;
    for (const i of range) {
        if (l) {
            if (i - l === 2) {
                rangeWithDots.push(l + 1);
            } else if (i - l !== 1) {
                rangeWithDots.push('...');
            }
        }
        rangeWithDots.push(i);
        l = i;
    }

    return (
        <div className="flex items-center justify-center space-x-2 mt-8">
            <button
                onClick={() => onPageChange(currentPage - 1)}
                disabled={currentPage === 0}
                className="p-2 rounded-lg border border-slate-200 hover:bg-slate-50 disabled:opacity-50 disabled:cursor-not-allowed transition-colors"
            >
                <ChevronLeft className="w-5 h-5 text-slate-600" />
            </button>

            {rangeWithDots.map((page, index) => (
                <button
                    key={index}
                    onClick={() => typeof page === 'number' ? onPageChange(page - 1) : null}
                    disabled={page === '...'}
                    className={`min-w-[40px] h-10 px-3 rounded-lg text-sm font-medium transition-all ${page === currentPage + 1
                            ? 'bg-brand-600 text-white shadow-md shadow-brand-200'
                            : page === '...'
                                ? 'cursor-default text-slate-400'
                                : 'bg-white border border-slate-200 text-slate-600 hover:bg-slate-50 hover:border-slate-300'
                        }`}
                >
                    {page}
                </button>
            ))}

            <button
                onClick={() => onPageChange(currentPage + 1)}
                disabled={currentPage === totalPages - 1}
                className="p-2 rounded-lg border border-slate-200 hover:bg-slate-50 disabled:opacity-50 disabled:cursor-not-allowed transition-colors"
            >
                <ChevronRight className="w-5 h-5 text-slate-600" />
            </button>
        </div>
    );
};

export default Pagination;
