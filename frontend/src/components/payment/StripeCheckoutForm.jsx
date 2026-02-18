import React, { useState } from 'react';
import { useStripe, useElements, PaymentElement } from '@stripe/react-stripe-js';
import { toast } from 'react-hot-toast';
import { Lock, Loader2 } from 'lucide-react';

const StripeCheckoutForm = ({ orderId, returnUrl }) => {
    const stripe = useStripe();
    const elements = useElements();
    const [message, setMessage] = useState(null);
    const [isLoading, setIsLoading] = useState(false);

    const handleSubmit = async (e) => {
        e.preventDefault();

        if (!stripe || !elements) {
            return;
        }

        setIsLoading(true);

        const { error } = await stripe.confirmPayment({
            elements,
            confirmParams: {
                return_url: returnUrl,
            },
        });

        if (error) {
            if (error.type === "card_error" || error.type === "validation_error") {
                setMessage(error.message);
                toast.error(error.message);
            } else {
                setMessage("An unexpected error occurred.");
                toast.error("An unexpected error occurred.");
            }
        } else {
            // Unexpected state
        }

        setIsLoading(false);
    };

    return (
        <form onSubmit={handleSubmit} className="space-y-6">
            <PaymentElement id="payment-element" />

            {message && <div className="text-red-500 text-sm bg-red-50 p-2 rounded">{message}</div>}

            <button
                disabled={isLoading || !stripe || !elements}
                className="w-full flex items-center justify-center gap-2 bg-brand-600 text-white font-bold py-3.5 rounded-xl hover:bg-brand-700 transition-all shadow-brand-md disabled:opacity-50 disabled:cursor-not-allowed"
            >
                {isLoading ? (
                    <>
                        <Loader2 size={20} className="animate-spin" />
                        Processing...
                    </>
                ) : (
                    <>
                        <Lock size={18} />
                        Pay Now
                    </>
                )}
            </button>

            <p className="text-center text-xs text-slate-400 flex items-center justify-center gap-1">
                <Lock size={12} />
                Payments are secure and encrypted.
            </p>
        </form>
    );
};

export default StripeCheckoutForm;
