import { useState, useCallback } from 'react';
import axios from 'axios';
import toast from 'react-hot-toast';

/**
 * Custom hook for Pincode/Postal Code based location auto-fill
 * 
 * @param {Object} options
 * @param {string} options.postalCode - Current postal code value
 * @param {string} options.country - Selected country code (e.g., 'IN', 'US')
 * @param {Function} options.setValue - react-hook-form setValue
 * @param {Function} options.setError - react-hook-form setError
 * @param {Function} options.clearErrors - react-hook-form clearErrors
 * @param {Object} options.fieldNames - Names of the fields to update
 * @param {string} [options.fieldNames.city='city']
 * @param {string} [options.fieldNames.state='state']
 * @param {string} [options.fieldNames.country='country']
 */
const useLocationAutoFill = ({
    postalCode,
    country,
    setValue,
    setError,
    clearErrors,
    fieldNames = { city: 'city', state: 'state', country: 'country' }
}) => {
    const [loading, setLoading] = useState(false);

    const validateFormat = useCallback((code, countryCode) => {
        if (!code) return false;
        if (countryCode === 'IN') {
            return /^\d{6}$/.test(code);
        }
        return /^[a-z0-9\s-]{3,10}$/i.test(code);
    }, []);

    const fetchLocation = useCallback(async (code, countryCode) => {
        if (!countryCode) {
            toast.error("Please select country first");
            return;
        }

        if (!validateFormat(code, countryCode)) {
            if (code && code.length > 0) {
                setError('postalCode', {
                    type: 'manual',
                    message: "Invalid postal code format for selected country"
                });
            }
            return;
        }

        clearErrors('postalCode');
        setLoading(true);

        try {
            if (countryCode === 'IN') {
                const response = await axios.get(`https://api.postalpincode.in/pincode/${code}`);
                const data = response.data[0];
                if (data.Status === "Success" && data.PostOffice && data.PostOffice.length > 0) {
                    const postOffice = data.PostOffice[0];
                    setValue(fieldNames.city, postOffice.District, { shouldDirty: true, shouldValidate: true });
                    setValue(fieldNames.state, postOffice.State, { shouldDirty: true, shouldValidate: true });
                } else {
                    toast.error("Location not found. Please check postal code or enter location manually.");
                }
            } else {
                const response = await axios.get(`https://api.zippopotam.us/${countryCode.toLowerCase()}/${code}`);
                if (response.data && response.data.places && response.data.places.length > 0) {
                    const place = response.data.places[0];
                    setValue(fieldNames.city, place['place name'], { shouldDirty: true, shouldValidate: true });
                    setValue(fieldNames.state, place['state'], { shouldDirty: true, shouldValidate: true });
                } else {
                    toast.error("Location not found. Please check postal code or enter location manually.");
                }
            }
        } catch (error) {
            console.error("Location API fetch failed:", error);
            toast.error("Location not found. Please check postal code or enter location manually.");
        } finally {
            setLoading(false);
        }
    }, [country, fieldNames.city, fieldNames.state, setValue, setError, clearErrors, validateFormat]);

    const handlePostalCodeChange = (e) => {
        const value = e.target.value;
        if (country === 'IN' && value.length === 6) {
            fetchLocation(value, country);
        }
    };

    const handleBlur = () => {
        if (country !== 'IN' && postalCode) {
            fetchLocation(postalCode, country);
        }
    };

    return {
        loading,
        handlePostalCodeChange,
        handleBlur,
        fetchLocation
    };
};

export default useLocationAutoFill;
