
import React from 'react';
import { AlertCircle, CheckCircle2 } from 'lucide-react';

const AcceptTermsBar = ({ onAccept, isAccepting }) => {
    return (
        <div className="fixed bottom-0 left-0 right-0 z-50 p-4 animate-in slide-in-from-bottom-5 duration-300">
            <div className="container mx-auto max-w-4xl bg-white rounded-xl shadow-2xl border border-brand-100 p-4 md:p-6 flex flex-col md:flex-row items-center justify-between gap-4 ring-1 ring-black/5">
                <div className="flex items-start gap-4">
                    <div className="bg-amber-100 p-2 rounded-full hidden md:block">
                        <AlertCircle className="text-amber-600" size={24} />
                    </div>
                    <div>
                        <h3 className="font-bold text-slate-900">Action Required: Update to Terms & Conditions</h3>
                        <p className="text-sm text-slate-600 mt-1">
                            We have updated our terms. You must review and accept the latest version to continue using the platform.
                        </p>
                    </div>
                </div>
                <button
                    onClick={onAccept}
                    disabled={isAccepting}
                    className="w-full md:w-auto px-8 py-3 bg-brand-600 hover:bg-brand-700 text-white font-bold rounded-lg shadow-lg flex items-center justify-center gap-2 transition-all active:scale-95 disabled:opacity-70 whitespace-nowrap"
                >
                    {isAccepting ? (
                        <>Processing...</>
                    ) : (
                        <>
                            <CheckCircle2 size={20} /> I Accept Terms
                        </>
                    )}
                </button>
            </div>
        </div>
    );
};

export default AcceptTermsBar;
