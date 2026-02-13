-- Invoice Module Schema

-- Invoice Number Sequence Table
CREATE TABLE invoice_sequences (
    sequence_key VARCHAR(50) PRIMARY KEY, -- e.g., 'INV-2026', 'PRF-2026'
    current_val BIGINT NOT NULL DEFAULT 0,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Invoices Table
CREATE TABLE invoices (
    id UUID PRIMARY KEY,
    order_id UUID NOT NULL,
    buyer_id UUID NOT NULL,
    seller_id UUID NOT NULL,
    
    invoice_number VARCHAR(50) NOT NULL, -- e.g., INV-2026-0042
    invoice_type VARCHAR(20) NOT NULL,   -- PROFORMA, FINAL
    invoice_side VARCHAR(20) NOT NULL,   -- BUYER_COPY, SELLER_COPY
    status VARCHAR(20) NOT NULL,         -- GENERATED, FAILED
    
    -- Monetary values (snapshot at creation)
    currency_base VARCHAR(3) NOT NULL DEFAULT 'INR',
    currency_buyer VARCHAR(3) NOT NULL,
    exchange_rate_used BIGINT NOT NULL, -- multiplied by 10^6 (micros) or just long representation? 
                                       -- OrderCurrencySnapshot uses exchangeRateMicros. We'll use the same.
    
    subtotal_inr_paise BIGINT NOT NULL,
    shipping_inr_paise BIGINT NOT NULL,
    total_inr_paise BIGINT NOT NULL,
    
    total_buyer_minor BIGINT NOT NULL, -- Amount in buyer currency
    
    pdf_url TEXT,
    
    -- Snapshots for immutable audit
    iec_number VARCHAR(50),
    gstin_number VARCHAR(50),
    incoterm VARCHAR(20),
    
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    CONSTRAINT uq_invoice_number UNIQUE (invoice_number),
    CONSTRAINT fk_invoices_order FOREIGN KEY (order_id) REFERENCES orders(id),
    CONSTRAINT fk_invoices_buyer FOREIGN KEY (buyer_id) REFERENCES buyer_profile(id),
    CONSTRAINT fk_invoices_seller FOREIGN KEY (seller_id) REFERENCES seller_profile(id)
);

CREATE INDEX idx_invoices_order_id ON invoices(order_id);
CREATE INDEX idx_invoices_buyer_id ON invoices(buyer_id);
CREATE INDEX idx_invoices_seller_id ON invoices(seller_id);
CREATE INDEX idx_invoices_number ON invoices(invoice_number);
