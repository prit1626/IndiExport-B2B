
import React from 'react';
import { Check } from 'lucide-react';

const steps = [
    { id: 1, label: 'Info' },
    { id: 2, label: 'Uploads' },
    { id: 3, label: 'Review' },
    { id: 4, label: 'Verified' }
];

const SellerVerificationStepper = ({ currentStep, status }) => {
    // Logic to determine active step based on status/page
    // 1: Info (Not Verified)
    // 2: Uploads (Page)
    // 3: Review (Submitted)
    // 4: Verified (Verified)

    // For simplicity, we just passed currentStep prop, but we can infer from status too using a helper

    return (
        <div className="w-full py-4">
            <div className="flex items-center justify-between relative max-w-2xl mx-auto">
                {/* Connecting Line */}
                <div className="absolute top-1/2 left-0 w-full h-1 bg-slate-200 -z-10 -translate-y-1/2 rounded-full"></div>
                <div
                    className="absolute top-1/2 left-0 h-1 bg-brand-600 -z-10 -translate-y-1/2 rounded-full transition-all duration-500"
                    style={{ width: `${((currentStep - 1) / (steps.length - 1)) * 100}%` }}
                ></div>

                {steps.map((step) => {
                    const isCompleted = step.id < currentStep;
                    const isCurrent = step.id === currentStep;

                    return (
                        <div key={step.id} className="flex flex-col items-center gap-2 bg-slate-50 px-2">
                            <div
                                className={`w-10 h-10 rounded-full flex items-center justify-center border-2 transition-all duration-300 z-10
                                    ${isCompleted || isCurrent
                                        ? 'bg-brand-600 border-brand-600 text-white'
                                        : 'bg-white border-slate-300 text-slate-400'
                                    }`}
                            >
                                {isCompleted ? <Check size={20} /> : <span className="font-bold">{step.id}</span>}
                            </div>
                            <span className={`text-xs font-semibold ${isCurrent ? 'text-brand-700' : 'text-slate-500'}`}>
                                {step.label}
                            </span>
                        </div>
                    );
                })}
            </div>
        </div>
    );
};

export default SellerVerificationStepper;
