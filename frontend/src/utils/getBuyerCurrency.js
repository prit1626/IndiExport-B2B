export const getBuyerCurrency = () => {
  try {
    const directPref = localStorage.getItem("preferredCurrency");
    if (directPref) return directPref;

    const user = JSON.parse(localStorage.getItem("user"));
    return user?.preferredCurrency || "INR";
  } catch {
    return "INR";
  }
};