-- Add weightKg column to products table
ALTER TABLE products ADD COLUMN weight_kg DECIMAL(10,3) NOT NULL DEFAULT 0.001;

-- Update existing products to have weightKg based on weightGrams / 1000
UPDATE products SET weight_kg = weight_grams / 1000.0 WHERE weight_grams > 0;

-- Optional: Drop weightGrams column if no longer needed
-- ALTER TABLE products DROP COLUMN weight_grams;