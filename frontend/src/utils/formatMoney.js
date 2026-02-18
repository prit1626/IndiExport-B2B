/**
 * Formats a minor unit amount (e.g., paise, cents) into a currency string.
 * @param {number} amountMinor - Amount in minor units (e.g., 1000 paise = 10 INR)
 * @param {string} currency - Currency code (default: 'INR')
 * @returns {string} Formatted string (e.g., "₹10.00")
 */
export const formatMoney = (amountMinor, currency = 'INR') => {
    if (amountMinor === null || amountMinor === undefined) return '-';

    // Default to INR logic (100 paise = 1 INR) if currency is INR
    // Most currencies use 100 minor units
    const majorAmount = amountMinor / 100;

    return new Intl.NumberFormat('en-IN', {
        style: 'currency',
        currency: currency,
        minimumFractionDigits: 2,
        maximumFractionDigits: 2
    }).format(majorAmount);
};
