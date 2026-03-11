import { convertPrice, getCurrencySymbol } from './currencyConverter';
import { getBuyerCurrency } from './getBuyerCurrency';

/**
 * Converts a price from INR minor units (paise) to the target currency's major units.
 * @param {number} amountMinor - Amount in INR paise
 * @param {string} targetCurrency - Target currency code (e.g., 'USD', default: 'INR')
 * @returns {number} Converted amount in target currency major units
 */
export const convertFromPaise = (amountMinor, targetCurrency = 'INR') => {
    if (amountMinor === null || amountMinor === undefined) return 0;
    
    // Step 1: Normalize minor units (paise) to major units (INR rupees)
    const amountInINR = amountMinor / 100;
    
    // Step 2: Convert to target currency
    const convertedStr = convertPrice(amountInINR, targetCurrency);
    
    // Step 3: Return as number for calculations/charts
    return parseFloat(convertedStr);
};

/**
 * Formats a major unit value into a localized currency string using Intl.NumberFormat.
 * @param {number} amountMajor - Amount in target currency major units
 * @param {string} targetCurrency - Target currency code
 * @returns {string} Formatted currency string
 */
export const formatCurrencyValue = (amountMajor, targetCurrency = 'INR') => {
    if (amountMajor === null || amountMajor === undefined) return '-';
    
    try {
        return new Intl.NumberFormat('en-US', {
            style: 'currency',
            currency: targetCurrency,
            minimumFractionDigits: 2,
            maximumFractionDigits: 2
        }).format(amountMajor);
    } catch (e) {
        // Fallback if currency is not supported by Intl
        const symbol = getCurrencySymbol(targetCurrency);
        return `${symbol}${amountMajor.toFixed(2)}`;
    }
};

/**
 * Full pipeline: Converts from paise INR to target currency and formats it as a string.
 * This is the primary function to be used for display.
 * @param {number} amountMinor - Amount in INR paise
 * @param {string} targetCurrency - Target currency code
 * @returns {string} Formatted string (e.g., "$10.00")
 */
export const formatDynamicCurrency = (amountMinor, targetCurrency = 'INR') => {
    if (amountMinor === null || amountMinor === undefined) return '-';
    
    const majorAmount = convertFromPaise(amountMinor, targetCurrency);
    return formatCurrencyValue(majorAmount, targetCurrency);
};

/**
 * Helper to get the buyer's preferred currency from the store/localStorage.
 * Re-exports from getBuyerCurrency for convenience.
 */
export { getBuyerCurrency };
