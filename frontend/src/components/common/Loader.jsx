import { motion } from 'framer-motion';

const Loader = ({ className = "h-8 w-8 text-brand-600" }) => {
    return (
        <motion.div
            animate={{ rotate: 360 }}
            transition={{ repeat: Infinity, duration: 1, ease: "linear" }}
            className={`border-4 border-current border-t-transparent rounded-full ${className}`}
        />
    );
};

export default Loader;
