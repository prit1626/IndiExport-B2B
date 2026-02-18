import React from 'react';
import QuoteCard from './QuoteCard';

const QuotesList = ({ quotes, selectedQuoteId, onSelect }) => {
    if (!quotes || quotes.length === 0) return (
        <div className="text-center py-12 bg-slate-50 rounded-xl border border-dashed border-slate-300">
            <p className="text-slate-500">No quotes received yet.</p>
        </div>
    );

    return (
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
            {quotes.map(quote => (
                <QuoteCard
                    key={quote.id}
                    quote={quote}
                    isSelected={selectedQuoteId === quote.id}
                    onSelect={onSelect}
                />
            ))}
        </div>
    );
};

export default QuotesList;
