/**
 * Currency conversion utility for IndiExport B2B
 * All prices in backend are stored in INR (paise)
 * This utility converts prices to buyer's preferred currency for display
 */

export const exchangeRates = {
  INR: 1,
  USD: 0.012,
  EUR: 0.011,
  GBP: 0.0095,
  AED: 0.044,
  CAD: 0.016,
  AUD: 0.018,
  SGD: 0.016,
  JPY: 1.29,
  CNY: 0.083,
};

export const currencySymbols = {
  INR: "₹",
  USD: "$",
  EUR: "€",
  GBP: "£",
  AED: "د.إ",
  CAD: "C$",
  AUD: "A$",
  SGD: "S$",
  JPY: "¥",
  CNY: "¥",
};

/**
 * Convert price from INR to target currency
 * @param {number} priceInINR - Price in INR rupees
 * @param {string} targetCurrency - Target currency code (e.g., 'USD', 'EUR')
 * @returns {string} - Converted price formatted to 2 decimal places
 */
export const convertPrice = (priceInINR, targetCurrency = 'INR') => {
  if (!priceInINR) return '0.00';
  
  const rate = exchangeRates[targetCurrency] || exchangeRates['INR'];
  const convertedPrice = priceInINR * rate;
  
  return convertedPrice.toFixed(2);
};

/**
 * Get currency symbol for a given currency code
 * @param {string} currencyCode - Currency code (e.g., 'USD', 'INR')
 * @returns {string} - Currency symbol
 */
export const getCurrencySymbol = (currencyCode = 'INR') => {
  return currencySymbols[currencyCode] || '₹';
};

/**
 * Format a price with currency symbol and proper localization
 * @param {number} amount - Amount to format
 * @param {string} currency - Currency code
 * @returns {string} - Formatted price string
 */
export const formatCurrencyPrice = (amount, currency = 'INR') => {
  try {
    const formatter = new Intl.NumberFormat('en-US', {
      style: 'currency',
      currency: currency,
      minimumFractionDigits: 0,
      maximumFractionDigits: 2,
    });
    return formatter.format(amount);
  } catch {
    // Fallback if currency is not supported by Intl
    const symbol = getCurrencySymbol(currency);
    return `${symbol}${amount.toFixed(2)}`;
  }
};

/**
 * Validate if a currency code is supported
 * @param {string} currencyCode - Currency code to validate
 * @returns {boolean} - True if supported
 */
export const isCurrencySupported = (currencyCode) => {
  return currencyCode in exchangeRates;
};

export default {
  exchangeRates,
  currencySymbols,
  convertPrice,
  getCurrencySymbol,
  formatCurrencyPrice,
  isCurrencySupported,
};
