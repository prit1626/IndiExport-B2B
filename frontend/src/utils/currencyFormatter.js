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

export const formatCurrency = (priceInINR, currency = "INR") => {
  if (priceInINR === null || priceInINR === undefined || priceInINR === '') return "";

  const rate = exchangeRates[currency] || 1;
  const converted = Number(priceInINR) * rate;

  try {
    return new Intl.NumberFormat('en-US', {
      style: 'currency',
      currency: currency
    }).format(converted);
  } catch (e) {
    return `${currencySymbols[currency] || currency + " "}${converted.toFixed(2)}`;
  }
};