import React from 'react';
import { Package } from 'lucide-react';

const ProductPerformanceList = ({ title, products, metricLabel }) => {
    return (
        <div className="bg-white rounded-xl shadow-sm border border-slate-200 overflow-hidden">
            <div className="p-4 border-b border-slate-100 flex items-center justify-between">
                <h3 className="font-semibold text-slate-800 flex items-center gap-2">
                    <Package className="w-4 h-4 text-brand-600" />
                    {title}
                </h3>
            </div>
            <div className="divide-y divide-slate-100">
                {products && products.length > 0 ? (
                    products.map((product, idx) => (
                        <div key={product.productId} className="p-4 hover:bg-slate-50 transition-colors flex items-center justify-between">
                            <div className="flex items-center gap-3">
                                <span className="text-sm font-medium text-slate-400 w-4">#{idx + 1}</span>
                                <span className="text-sm font-medium text-slate-700 truncate max-w-[200px]" title={product.title}>
                                    {product.title}
                                </span>
                            </div>
                            <div className="text-right">
                                <span className="text-sm font-bold text-slate-900">{product.count}</span>
                                <span className="text-xs text-slate-500 ml-1">{metricLabel}</span>
                            </div>
                        </div>
                    ))
                ) : (
                    <div className="p-8 text-center text-slate-400 text-sm">
                        No performance data available
                    </div>
                )}
            </div>
        </div>
    );
};

export default ProductPerformanceList;
