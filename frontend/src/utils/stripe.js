import { loadStripe } from '@stripe/stripe-js';

// Publishable key from environment variables
// Make sure VITE_STRIPE_PUBLISHABLE_KEY is set in .env
const stripePromise = loadStripe(import.meta.env.VITE_STRIPE_PUBLISHABLE_KEY);

export default stripePromise;
