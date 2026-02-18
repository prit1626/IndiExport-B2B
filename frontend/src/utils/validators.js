export const phoneValidator = {
    required: "Phone number is required",
    pattern: {
        value: /^\+?[0-9]{10,15}$/,
        message: "Invalid phone number (10-15 digits)"
    }
};

export const postalCodeValidator = {
    required: "Postal code is required",
    pattern: {
        value: /^[0-9A-Z-\s]{3,10}$/i,
        message: "Invalid postal code"
    }
};

export const urlValidator = {
    pattern: {
        value: /^(https?:\/\/)?([\da-z.-]+)\.([a-z.]{2,6})([/\w .-]*)*\/?$/,
        message: "Invalid URL format"
    }
};

export const requiredValidator = (fieldName) => ({
    required: `${fieldName} is required`
});

export const emailValidator = {
    required: "Email is required",
    pattern: {
        value: /^[A-Z0-9._%+-]+@[A-Z0-9.-]+\.[A-Z]{2,}$/i,
        message: "Invalid email address"
    }
};
