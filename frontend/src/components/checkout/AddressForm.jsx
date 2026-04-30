import React from 'react';
import { useForm } from 'react-hook-form';
import { MapPin, Phone, User, Globe, Building, Loader2 } from 'lucide-react';
import useLocationAutoFill from '../../hooks/useLocationAutoFill';
import { postalCodeValidator } from '../../utils/validators';

const commonCountries = [
    "US", "CA", "GB", "DE", "FR", "AU", "IN", "JP", "SG", "AE"
];

const AddressForm = ({ onSubmit, disabled }) => {
    const { register, handleSubmit, watch, setValue, setError, clearErrors, formState: { errors } } = useForm();

    const postalCodeValue = watch('postalCode');
    const selectedCountry = watch('country');

    const { loading: locationLoading, handlePostalCodeChange, handleBlur } = useLocationAutoFill({
        postalCode: postalCodeValue,
        country: selectedCountry,
        setValue,
        setError,
        clearErrors
    });

    const { onChange: pcOnChange, onBlur: pcOnBlur, ...pcRegister } = register("postalCode", {
        ...postalCodeValidator,
        required: "Postal code is required"
    });

    return (
        <form id="address-form" onSubmit={handleSubmit(onSubmit)} className="space-y-6">
            <h3 className="text-xl font-bold text-slate-900 border-b border-slate-100 pb-4">Shipping Address</h3>

            <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
                <InputField
                    label="Full Name"
                    icon={User}
                    {...register("fullName", { required: "Full name is required" })}
                    error={errors.fullName}
                    disabled={disabled}
                    placeholder="John Doe"
                />

                <InputField
                    label="Phone Number"
                    icon={Phone}
                    {...register("phone", {
                        required: "Phone is required",
                        pattern: { value: /^\+?[0-9\s-]{10,}$/, message: "Invalid phone number" }
                    })}
                    error={errors.phone}
                    disabled={disabled}
                    placeholder="+1 234 567 8900"
                />
            </div>

            <InputField
                label="Address Line 1"
                icon={MapPin}
                {...register("line1", { required: "Address is required" })}
                error={errors.line1}
                disabled={disabled}
                placeholder="123 Export Lane"
            />

            <InputField
                label="Address Line 2 (Optional)"
                icon={Building}
                {...register("line2")}
                disabled={disabled}
                placeholder="Suite, Unit, Floor"
            />

            <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
                <InputField
                    label="City"
                    {...register("city", { required: "City is required" })}
                    error={errors.city}
                    disabled={disabled}
                    placeholder="New York"
                />

                <InputField
                    label="State / Province"
                    {...register("state", { required: "State is required" })}
                    error={errors.state}
                    disabled={disabled}
                    placeholder="NY"
                />
            </div>

            <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
                <InputField
                    label="Postal Code"
                    {...pcRegister}
                    onChange={(e) => {
                        pcOnChange(e);
                        handlePostalCodeChange(e);
                    }}
                    onBlur={(e) => {
                        pcOnBlur(e);
                        handleBlur();
                    }}
                    error={errors.postalCode}
                    disabled={disabled || locationLoading}
                    loading={locationLoading}
                    placeholder="10001"
                />

                <div className="space-y-1">
                    <label className="block text-sm font-medium text-slate-700">Country</label>
                    <div className="relative">
                        <div className="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none text-slate-400">
                            <Globe size={18} />
                        </div>
                        <div className="flex">
                            <select
                                {...register("country", { required: "Country is required" })}
                                disabled={disabled}
                                className={`block w-full pl-10 pr-3 py-2.5 bg-slate-50 border rounded-lg text-sm transition-all outline-none focus:ring-2 focus:ring-brand-500 ${errors.country ? 'border-red-300 focus:ring-red-200' : 'border-slate-200'}`}
                            >
                                <option value="">Select Country</option>
                                {commonCountries.map(c => <option key={c} value={c}>{c}</option>)}
                                <option value="OTHER">Other</option>
                            </select>
                            {/* Ideally handled better than just "Other", but satisfies requirements */}
                        </div>
                        {errors.country && <span className="text-xs text-red-500 mt-1">{errors.country.message}</span>}
                    </div>
                </div>
            </div>
        </form>
    );
};

// Reusable Input Component restricted to this file for now
const InputField = React.forwardRef(({ label, icon: Icon, error, disabled, loading, ...props }, ref) => (
    <div className="space-y-1 w-full">
        <label className="text-sm font-medium text-slate-700 flex items-center gap-2">
            {label}
            {loading && <Loader2 size={14} className="animate-spin text-brand-500" />}
        </label>
        <div className="relative">
            {Icon && (
                <div className="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none text-slate-400">
                    <Icon size={18} />
                </div>
            )}
            <input
                ref={ref}
                disabled={disabled}
                className={`block w-full ${Icon ? 'pl-10' : 'pl-3'} pr-3 py-2.5 bg-slate-50 border rounded-lg text-sm transition-all outline-none focus:bg-white focus:ring-2 focus:ring-brand-500 disabled:opacity-60 disabled:cursor-not-allowed ${error ? 'border-red-300 focus:ring-red-200' : 'border-slate-200'}`}
                {...props}
            />
        </div>
        {error && <span className="text-xs text-red-500 mt-1">{error.message}</span>}
    </div>
));

export default AddressForm;
