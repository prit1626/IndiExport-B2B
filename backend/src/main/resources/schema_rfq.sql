-- RFQ Module Schema

CREATE TABLE rfqs (
    id UUID PRIMARY KEY,
    buyer_id UUID NOT NULL REFERENCES buyer_profiles(id),
    category_id UUID REFERENCES categories(id), -- Optional constraint depending on Category entity
    title VARCHAR(255) NOT NULL,
    details TEXT,
    quantity INTEGER NOT NULL,
    unit VARCHAR(50) NOT NULL, -- PCS, KG, TON
    destination_country VARCHAR(2) NOT NULL, -- ISO-2
    destination_address_json TEXT, -- JSON structure for address
    shipping_mode VARCHAR(20), -- AIR, SEA, ROAD, COURIER
    incoterm VARCHAR(20) NOT NULL, -- EXW, FOB, etc.
    target_price_minor BIGINT,
    target_currency VARCHAR(3),
    status VARCHAR(30) NOT NULL, -- OPEN, UNDER_NEGOTIATION, FINALIZED, CONVERTED_TO_ORDER, CANCELLED, EXPIRED
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL
);

CREATE INDEX idx_rfqs_buyer_id ON rfqs(buyer_id);
CREATE INDEX idx_rfqs_category_id ON rfqs(category_id);
CREATE INDEX idx_rfqs_status ON rfqs(status);
CREATE INDEX idx_rfqs_destination_country ON rfqs(destination_country);
CREATE INDEX idx_rfqs_created_at ON rfqs(created_at);

CREATE TABLE rfq_media (
    id UUID PRIMARY KEY,
    rfq_id UUID NOT NULL REFERENCES rfqs(id),
    url TEXT NOT NULL,
    media_type VARCHAR(20) DEFAULT 'IMAGE',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL
);

CREATE INDEX idx_rfq_media_rfq_id ON rfq_media(rfq_id);

CREATE TABLE rfq_quotes (
    id UUID PRIMARY KEY,
    rfq_id UUID NOT NULL REFERENCES rfqs(id),
    seller_id UUID NOT NULL REFERENCES seller_profiles(id),
    quoted_price_inr_paise BIGINT NOT NULL,
    shipping_estimate_inr_paise BIGINT,
    lead_time_days INTEGER,
    notes TEXT,
    validity_until TIMESTAMP,
    status VARCHAR(20) NOT NULL, -- ACTIVE, REJECTED, ACCEPTED
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL
);

CREATE INDEX idx_rfq_quotes_rfq_id ON rfq_quotes(rfq_id);
CREATE INDEX idx_rfq_quotes_seller_id ON rfq_quotes(seller_id);
