-- ============================================================================
-- INDIEXPORT - PostgreSQL Schema (Production-Grade)
-- B2B Export Marketplace: Connect Indian Sellers with Global Buyers
-- ============================================================================

-- ============================================================================
-- SECTION 1: ER DIAGRAM EXPLANATION
-- ============================================================================

/*
ENTITY RELATIONSHIP OVERVIEW
============================

1. USER MANAGEMENT LAYER:
   - users (central user table with role assignment)
   - roles (BUYER, SELLER, ADMIN)
   - user_roles (junction table: many-to-many relationship)
   - buyer_profile (buyer-specific data)
   - seller_profile (seller-specific data with IEC verification)
   - seller_plan (plan tracking: BASIC_SELLER, ADVANCED_SELLER)
   - seller_plan_history (audit trail for plan upgrades)

2. PRODUCT CATALOG LAYER:
   - categories (product categories: e.g., Textiles, Electronics)
   - products (seller's products with status, pricing, inventory)
   - product_media (images, videos, documents)
   - product_categories (many-to-many: product belongs to multiple categories)
   - tags (searchable tags)
   - product_tags (many-to-many: product has multiple tags)
   - reported_products (admin moderation: flagged/blocked products)

3. RFQ & NEGOTIATION LAYER:
   - rfq (Request for Quote: buyer creates, seller negotiates)
   - rfq_media (photos/docs for RFQ)
   - rfq_chat (negotiation chat thread tied to RFQ)
   - chat_messages (individual messages in RFQ/Inquiry chats)
   - inquiry_chat (product-specific questions: buyer asks, seller answers)

4. CART & ORDERING LAYER:
   - cart (buyer's shopping cart: temporary)
   - cart_items (items in cart with quantity)
   - orders (checkout: RFQ → Order, or direct product purchase)
   - order_items (items purchased in order)
   - shipping_quotes (quotes from logistics providers)
   - order_tracking_events (delivery tracking states)

5. PAYMENT & FINANCIAL LAYER:
   - payments (payment intent, status, escrow hold)
   - payment_snapshots (currency conversion snapshot per payment)
   - invoices (proforma & final invoices)
   - invoice_versions (versioning: can be amended before payment)
   - seller_discounts (time-limited promotional discounts)
   - disputes (buyer/seller disputes on orders)
   - dispute_evidence (images/documents as evidence)

6. ADMIN & AUDIT LAYER:
   - admin_settings (platform-wide configurations)
   - audit_logs (track user actions: create, update, delete)
   - currency_rates (daily snapshot of exchange rates)

RELATIONSHIP MAPPING:
====================

One-to-Many (1:N):
- user → buyer_profile (1 user has 1 buyer profile)
- user → seller_profile (1 user has 1 seller profile)
- seller_profile → seller_plan (1 seller has 1 active plan)
- seller_profile → products (1 seller has many products)
- seller_profile → rfq (1 seller receives many RFQs)
- seller_profile → seller_discounts (1 seller can have many active discounts)
- product → product_media (1 product has many media files)
- product → rfq (1 product can have multiple RFQs)
- product → reviews (1 product has many reviews)
- category → product_categories (1 category has many products)
- tag → product_tags (1 tag has many products)
- order → order_items (1 order has multiple items)
- order → payments (1 order can have multiple payment attempts)
- order → order_tracking_events (1 order has multiple tracking updates)
- rfq → rfq_media (1 RFQ has multiple media files)
- rfq → rfq_chat (1 RFQ has 1 chat thread)
- inquiry_chat → chat_messages (1 inquiry thread has many messages)
- rfq_chat → chat_messages (1 RFQ chat has many messages)
- user → disputes_raised (1 user can raise multiple disputes)
- order → disputes (1 order can have multiple disputes)
- dispute → dispute_evidence (1 dispute has multiple evidence files)
- product → reviews (1 product has many reviews)
- order → invoices (1 order can have multiple invoice versions)
- user → audit_logs (1 user's actions tracked in audit logs)

Many-to-Many (N:M):
- users ↔ roles (user_roles junction table)
- products ↔ categories (product_categories junction table)
- products ↔ tags (product_tags junction table)

One-to-One (1:1):
- user → buyer_profile (if user is BUYER)
- user → seller_profile (if user is SELLER)
- rfq → rfq_chat (each RFQ has one negotiation chat)
- seller_profile → seller_plan (each seller has one active plan, but history in seller_plan_history)
*/


-- ============================================================================
-- SECTION 2: POSTGRESQL ENUMS (Custom Types)
-- ============================================================================

CREATE TYPE role_type AS ENUM ('BUYER', 'SELLER', 'ADMIN');
CREATE TYPE user_status_enum AS ENUM ('ACTIVE', 'INACTIVE', 'SUSPENDED', 'DELETED');

CREATE TYPE seller_plan_type AS ENUM ('BASIC_SELLER', 'ADVANCED_SELLER');
CREATE TYPE seller_verification_status AS ENUM ('NOT_VERIFIED', 'PENDING', 'VERIFIED', 'REJECTED');

CREATE TYPE product_status_enum AS ENUM ('ACTIVE', 'INACTIVE', 'DRAFT', 'BLOCKED');

CREATE TYPE rfq_status_enum AS ENUM ('OPEN', 'NEGOTIATING', 'CONVERTED_TO_ORDER', 'REJECTED', 'EXPIRED');
CREATE TYPE chat_type_enum AS ENUM ('RFQ_NEGOTIATION', 'PRODUCT_INQUIRY');

CREATE TYPE order_status_enum AS ENUM (
    'PENDING_CONFIRMATION',
    'CONFIRMED',
    'SHIPPED',
    'IN_TRANSIT',
    'DELIVERED',
    'CANCELLED',
    'RETURNED'
);

CREATE TYPE payment_status_enum AS ENUM (
    'PENDING',
    'INITIATED',
    'PROCESSING',
    'COMPLETED',
    'FAILED',
    'REFUNDED',
    'DISPUTED'
);

CREATE TYPE dispute_status_enum AS ENUM (
    'OPEN',
    'UNDER_REVIEW',
    'RESOLVED',
    'REJECTED',
    'APPEALED',
    'CLOSED'
);

CREATE TYPE dispute_raise_by AS ENUM ('BUYER', 'SELLER');

CREATE TYPE invoice_type_enum AS ENUM ('PROFORMA', 'FINAL');
CREATE TYPE invoice_status_enum AS ENUM ('DRAFT', 'ISSUED', 'PAID', 'CANCELLED');


-- ============================================================================
-- SECTION 3: CREATE TABLES
-- ============================================================================

-- TABLE: users
-- Core user table for all roles (BUYER, SELLER, ADMIN)
CREATE TABLE users (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    email VARCHAR(255) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    phone_number VARCHAR(20),
    profile_picture_url TEXT,
    status user_status_enum NOT NULL DEFAULT 'ACTIVE',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by UUID REFERENCES users(id) ON DELETE SET NULL,
    updated_by UUID REFERENCES users(id) ON DELETE SET NULL,
    deleted_at TIMESTAMP -- Soft delete support
);

CREATE INDEX idx_users_email ON users(email);
CREATE INDEX idx_users_status ON users(status);
CREATE INDEX idx_users_created_at ON users(created_at);


-- TABLE: roles
-- Predefined roles for the platform
CREATE TABLE roles (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name role_type NOT NULL UNIQUE,
    description TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

INSERT INTO roles (name, description) VALUES
    ('BUYER', 'Buyer role: can browse, purchase, create RFQ'),
    ('SELLER', 'Seller role: can list products, negotiate RFQ, fulfill orders'),
    ('ADMIN', 'Admin role: manage users, sellers, payments, disputes');


-- TABLE: user_roles
-- Junction table for many-to-many user-role relationship
CREATE TABLE user_roles (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    role_id UUID NOT NULL REFERENCES roles(id) ON DELETE CASCADE,
    assigned_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(user_id, role_id)
);

CREATE INDEX idx_user_roles_user_id ON user_roles(user_id);
CREATE INDEX idx_user_roles_role_id ON user_roles(role_id);


-- TABLE: buyer_profile
-- Additional buyer-specific information
CREATE TABLE buyer_profile (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL UNIQUE REFERENCES users(id) ON DELETE CASCADE,
    country VARCHAR(2) NOT NULL, -- ISO-2 country code (e.g., 'IN', 'US', 'DE')
    company_name VARCHAR(255),
    business_registration_number VARCHAR(100),
    buyer_type VARCHAR(50), -- e.g., 'INDIVIDUAL', 'CORPORATE', 'GOVERNMENT'
    preferred_currency VARCHAR(3) DEFAULT 'INR',
    address TEXT,
    city VARCHAR(100),
    state VARCHAR(100),
    postal_code VARCHAR(20),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by UUID REFERENCES users(id) ON DELETE SET NULL
);

CREATE INDEX idx_buyer_profile_user_id ON buyer_profile(user_id);
CREATE INDEX idx_buyer_profile_country ON buyer_profile(country);


-- TABLE: seller_profile
-- Seller-specific information including IEC verification workflow
CREATE TABLE seller_profile (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL UNIQUE REFERENCES users(id) ON DELETE CASCADE,
    iec_number VARCHAR(50) UNIQUE, -- Import-Export Code: Indian seller identifier
    iec_verification_status seller_verification_status NOT NULL DEFAULT 'NOT_VERIFIED',
    iec_document_url TEXT,
    iec_verified_at TIMESTAMP,
    kyc_verification_status seller_verification_status NOT NULL DEFAULT 'NOT_VERIFIED',
    kyc_document_url TEXT,
    kyc_verified_at TIMESTAMP,
    company_name VARCHAR(255) NOT NULL,
    company_registration_number VARCHAR(100),
    bank_account_number VARCHAR(50) NOT NULL,
    bank_ifsc_code VARCHAR(20) NOT NULL,
    bank_account_holder_name VARCHAR(100) NOT NULL,
    address TEXT NOT NULL,
    city VARCHAR(100) NOT NULL,
    state VARCHAR(100) NOT NULL,
    postal_code VARCHAR(20) NOT NULL,
    country VARCHAR(2) NOT NULL DEFAULT 'IN', -- Sellers must be from India
    CHECK (country = 'IN'),
    average_rating DECIMAL(3, 2) DEFAULT 0,
    total_products INT DEFAULT 0,
    active_products INT DEFAULT 0,
    total_orders INT DEFAULT 0,
    total_sales DECIMAL(15, 2) DEFAULT 0,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by UUID REFERENCES users(id) ON DELETE SET NULL,
    deleted_at TIMESTAMP -- Soft delete support
);

CREATE INDEX idx_seller_profile_user_id ON seller_profile(user_id);
CREATE INDEX idx_seller_profile_iec_number ON seller_profile(iec_number);
CREATE INDEX idx_seller_profile_verification_status ON seller_profile(iec_verification_status);
CREATE INDEX idx_seller_profile_active_products ON seller_profile(active_products);


-- TABLE: seller_plan
-- Current active plan for each seller
CREATE TABLE seller_plan (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    seller_id UUID NOT NULL UNIQUE REFERENCES seller_profile(id) ON DELETE CASCADE,
    plan_type seller_plan_type NOT NULL DEFAULT 'BASIC_SELLER',
    max_active_products INT NOT NULL,
    max_monthly_revenue DECIMAL(15, 2), -- NULL if unlimited
    active_since TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    valid_until TIMESTAMP,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_seller_plan_seller_id ON seller_plan(seller_id);
CREATE INDEX idx_seller_plan_active ON seller_plan(is_active);


-- TABLE: seller_plan_history
-- Audit trail of plan changes and upgrades
CREATE TABLE seller_plan_history (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    seller_id UUID NOT NULL REFERENCES seller_profile(id) ON DELETE CASCADE,
    previous_plan seller_plan_type,
    new_plan seller_plan_type NOT NULL,
    changed_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    changed_by UUID REFERENCES users(id) ON DELETE SET NULL,
    reason VARCHAR(255)
);

CREATE INDEX idx_seller_plan_history_seller_id ON seller_plan_history(seller_id);
CREATE INDEX idx_seller_plan_history_changed_at ON seller_plan_history(changed_at);


-- TABLE: categories
-- Product categories: Textiles, Electronics, Machinery, etc.
CREATE TABLE categories (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(100) NOT NULL UNIQUE,
    description TEXT,
    parent_category_id UUID REFERENCES categories(id) ON DELETE SET NULL,
    icon_url TEXT,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    sort_order INT DEFAULT 0,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_categories_parent_id ON categories(parent_category_id);
CREATE INDEX idx_categories_active ON categories(is_active);


-- TABLE: tags
-- Searchable product tags: "Organic", "Export-ready", "Wholesale"
CREATE TABLE tags (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(50) NOT NULL UNIQUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_tags_name ON tags(name);


-- TABLE: products
-- Core product table with pricing in INR, multi-currency conversion at order level
CREATE TABLE products (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    seller_id UUID NOT NULL REFERENCES seller_profile(id) ON DELETE CASCADE,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    sku VARCHAR(100) NOT NULL,
    CONSTRAINT unique_sku_per_seller UNIQUE(seller_id, sku),
    status product_status_enum NOT NULL DEFAULT 'DRAFT',
    price_inr DECIMAL(12, 2) NOT NULL, -- Price in INR, base currency
    CHECK (price_inr > 0),
    minimum_order_quantity INT NOT NULL DEFAULT 1,
    maximum_order_quantity INT,
    quantity_unit VARCHAR(50) NOT NULL, -- 'KG', 'PIECE', 'LITER', 'METER'
    stock_quantity INT NOT NULL DEFAULT 0,
    lead_time_days INT DEFAULT 7,
    gst_percentage DECIMAL(5, 2) DEFAULT 0, -- GST rate
    hs_code VARCHAR(20), -- Harmonized System Code for export
    average_rating DECIMAL(3, 2) DEFAULT 0,
    total_reviews INT DEFAULT 0,
    total_orders INT DEFAULT 0,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by UUID REFERENCES users(id) ON DELETE SET NULL,
    deleted_at TIMESTAMP -- Soft delete support
);

CREATE INDEX idx_products_seller_id ON products(seller_id);
CREATE INDEX idx_products_status ON products(status);
CREATE INDEX idx_products_name ON products USING GIN (to_tsvector('english', name)); -- Full-text search
CREATE INDEX idx_products_sku ON products(sku);
CREATE INDEX idx_products_created_at ON products(created_at);


-- TABLE: product_media
-- Product images, videos, documents
CREATE TABLE product_media (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    product_id UUID NOT NULL REFERENCES products(id) ON DELETE CASCADE,
    media_url TEXT NOT NULL,
    media_type VARCHAR(20) NOT NULL, -- 'IMAGE', 'VIDEO', 'DOCUMENT'
    display_order INT NOT NULL DEFAULT 0,
    uploaded_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    uploaded_by UUID NOT NULL REFERENCES users(id) ON DELETE SET NULL
);

CREATE INDEX idx_product_media_product_id ON product_media(product_id);
CREATE INDEX idx_product_media_media_type ON product_media(media_type);


-- TABLE: product_categories
-- Many-to-many: products belong to multiple categories
CREATE TABLE product_categories (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    product_id UUID NOT NULL REFERENCES products(id) ON DELETE CASCADE,
    category_id UUID NOT NULL REFERENCES categories(id) ON DELETE CASCADE,
    UNIQUE(product_id, category_id)
);

CREATE INDEX idx_product_categories_product_id ON product_categories(product_id);
CREATE INDEX idx_product_categories_category_id ON product_categories(category_id);


-- TABLE: product_tags
-- Many-to-many: products have multiple tags
CREATE TABLE product_tags (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    product_id UUID NOT NULL REFERENCES products(id) ON DELETE CASCADE,
    tag_id UUID NOT NULL REFERENCES tags(id) ON DELETE CASCADE,
    UNIQUE(product_id, tag_id)
);

CREATE INDEX idx_product_tags_product_id ON product_tags(product_id);
CREATE INDEX idx_product_tags_tag_id ON product_tags(tag_id);


-- TABLE: reported_products
-- Admin moderation: flagged/blocked products
CREATE TABLE reported_products (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    product_id UUID NOT NULL REFERENCES products(id) ON DELETE CASCADE,
    reported_by UUID NOT NULL REFERENCES users(id) ON DELETE RESTRICT,
    reason VARCHAR(255) NOT NULL, -- 'INAPPROPRIATE', 'DUPLICATE', 'MISLEADING'
    description TEXT,
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING', -- 'PENDING', 'REVIEWED', 'ACTION_TAKEN'
    reviewed_by UUID REFERENCES users(id) ON DELETE SET NULL,
    reviewed_at TIMESTAMP,
    action_taken VARCHAR(50), -- 'BLOCKED', 'REMOVED', 'WARNING'
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_reported_products_product_id ON reported_products(product_id);
CREATE INDEX idx_reported_products_status ON reported_products(status);


-- TABLE: cart
-- Temporary shopping cart for buyers
CREATE TABLE cart (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    buyer_id UUID NOT NULL UNIQUE REFERENCES buyer_profile(id) ON DELETE CASCADE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_cart_buyer_id ON cart(buyer_id);


-- TABLE: cart_items
-- Items in cart with quantity
CREATE TABLE cart_items (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    cart_id UUID NOT NULL REFERENCES cart(id) ON DELETE CASCADE,
    product_id UUID NOT NULL REFERENCES products(id) ON DELETE CASCADE,
    quantity INT NOT NULL DEFAULT 1,
    CHECK (quantity > 0),
    added_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(cart_id, product_id)
);

CREATE INDEX idx_cart_items_cart_id ON cart_items(cart_id);
CREATE INDEX idx_cart_items_product_id ON cart_items(product_id);


-- TABLE: rfq (Request for Quote)
-- Buyer initiates negotiation for a product
CREATE TABLE rfq (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    buyer_id UUID NOT NULL REFERENCES buyer_profile(id) ON DELETE RESTRICT,
    seller_id UUID NOT NULL REFERENCES seller_profile(id) ON DELETE RESTRICT,
    product_id UUID NOT NULL REFERENCES products(id) ON DELETE RESTRICT,
    requested_quantity INT NOT NULL,
    CHECK (requested_quantity > 0),
    status rfq_status_enum NOT NULL DEFAULT 'OPEN',
    seller_quoted_price DECIMAL(12, 2), -- Seller's quote in INR
    buyer_response_text TEXT, -- Buyer's initial inquiry
    buyer_expected_delivery_date DATE,
    converted_to_order_id UUID, -- When RFQ converts to order
    expires_at TIMESTAMP DEFAULT (CURRENT_TIMESTAMP + INTERVAL '30 days'),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by UUID REFERENCES users(id) ON DELETE SET NULL
);

CREATE INDEX idx_rfq_buyer_id ON rfq(buyer_id);
CREATE INDEX idx_rfq_seller_id ON rfq(seller_id);
CREATE INDEX idx_rfq_product_id ON rfq(product_id);
CREATE INDEX idx_rfq_status ON rfq(status);
CREATE INDEX idx_rfq_created_at ON rfq(created_at);


-- TABLE: rfq_media
-- Documents/images attached to RFQ
CREATE TABLE rfq_media (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    rfq_id UUID NOT NULL REFERENCES rfq(id) ON DELETE CASCADE,
    media_url TEXT NOT NULL,
    media_type VARCHAR(20) NOT NULL, -- 'IMAGE', 'DOCUMENT'
    uploaded_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    uploaded_by UUID NOT NULL REFERENCES users(id) ON DELETE SET NULL
);

CREATE INDEX idx_rfq_media_rfq_id ON rfq_media(rfq_id);


-- TABLE: rfq_chat
-- Negotiation chat thread for each RFQ (one-to-one)
CREATE TABLE rfq_chat (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    rfq_id UUID NOT NULL UNIQUE REFERENCES rfq(id) ON DELETE CASCADE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    last_message_at TIMESTAMP
);

CREATE INDEX idx_rfq_chat_rfq_id ON rfq_chat(rfq_id);


-- TABLE: inquiry_chat
-- Product-specific inquiry chat (buyer asks, seller answers)
CREATE TABLE inquiry_chat (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    product_id UUID NOT NULL REFERENCES products(id) ON DELETE CASCADE,
    buyer_id UUID NOT NULL REFERENCES buyer_profile(id) ON DELETE CASCADE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(product_id, buyer_id) -- One chat per buyer per product
);

CREATE INDEX idx_inquiry_chat_product_id ON inquiry_chat(product_id);
CREATE INDEX idx_inquiry_chat_buyer_id ON inquiry_chat(buyer_id);


-- TABLE: chat_messages
-- Messages for both RFQ and Inquiry chats
CREATE TABLE chat_messages (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    rfq_chat_id UUID REFERENCES rfq_chat(id) ON DELETE CASCADE,
    inquiry_chat_id UUID REFERENCES inquiry_chat(id) ON DELETE CASCADE,
    sender_id UUID NOT NULL REFERENCES users(id) ON DELETE RESTRICT,
    message_text TEXT NOT NULL,
    attachment_url TEXT,
    is_read BOOLEAN NOT NULL DEFAULT FALSE,
    read_at TIMESTAMP,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CHECK (
        (rfq_chat_id IS NOT NULL AND inquiry_chat_id IS NULL) OR
        (rfq_chat_id IS NULL AND inquiry_chat_id IS NOT NULL)
    )
);

CREATE INDEX idx_chat_messages_rfq_chat_id ON chat_messages(rfq_chat_id);
CREATE INDEX idx_chat_messages_inquiry_chat_id ON chat_messages(inquiry_chat_id);
CREATE INDEX idx_chat_messages_sender_id ON chat_messages(sender_id);
CREATE INDEX idx_chat_messages_created_at ON chat_messages(created_at);


-- TABLE: seller_discounts
-- Time-limited promotional discounts offered by seller
CREATE TABLE seller_discounts (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    seller_id UUID NOT NULL REFERENCES seller_profile(id) ON DELETE CASCADE,
    discount_percentage DECIMAL(5, 2) NOT NULL,
    CHECK (discount_percentage > 0 AND discount_percentage <= 100),
    applicable_product_id UUID REFERENCES products(id) ON DELETE CASCADE, -- NULL if sitewide
    valid_from TIMESTAMP NOT NULL,
    valid_until TIMESTAMP NOT NULL,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by UUID REFERENCES users(id) ON DELETE SET NULL
);

CREATE INDEX idx_seller_discounts_seller_id ON seller_discounts(seller_id);
CREATE INDEX idx_seller_discounts_product_id ON seller_discounts(applicable_product_id);
CREATE INDEX idx_seller_discounts_active ON seller_discounts(is_active);


-- TABLE: orders
-- Checkout/Order from buyer (can originate from RFQ or direct product purchase)
CREATE TABLE orders (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    order_number VARCHAR(50) NOT NULL UNIQUE,
    buyer_id UUID NOT NULL REFERENCES buyer_profile(id) ON DELETE RESTRICT,
    seller_id UUID NOT NULL REFERENCES seller_profile(id) ON DELETE RESTRICT,
    rfq_id UUID REFERENCES rfq(id) ON DELETE SET NULL,
    status order_status_enum NOT NULL DEFAULT 'PENDING_CONFIRMATION',
    total_amount_inr DECIMAL(15, 2) NOT NULL,
    currency_code VARCHAR(3) NOT NULL DEFAULT 'INR', -- Order currency context
    buyer_country VARCHAR(2) NOT NULL, -- For audit
    shipping_address TEXT NOT NULL,
    shipping_city VARCHAR(100),
    shipping_state VARCHAR(100),
    shipping_postal_code VARCHAR(20),
    shipping_country VARCHAR(2),
    special_instructions TEXT,
    estimated_delivery_date DATE,
    actual_delivery_date DATE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by UUID REFERENCES users(id) ON DELETE SET NULL
);

CREATE INDEX idx_orders_buyer_id ON orders(buyer_id);
CREATE INDEX idx_orders_seller_id ON orders(seller_id);
CREATE INDEX idx_orders_status ON orders(status);
CREATE INDEX idx_orders_rfq_id ON orders(rfq_id);
CREATE INDEX idx_orders_created_at ON orders(created_at);


-- TABLE: order_items
-- Line items in an order
CREATE TABLE order_items (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    order_id UUID NOT NULL REFERENCES orders(id) ON DELETE CASCADE,
    product_id UUID NOT NULL REFERENCES products(id) ON DELETE RESTRICT,
    quantity INT NOT NULL,
    CHECK (quantity > 0),
    unit_price_inr DECIMAL(12, 2) NOT NULL, -- Locked-in price at order time
    discount_percentage DECIMAL(5, 2) DEFAULT 0,
    gst_percentage DECIMAL(5, 2) DEFAULT 0,
    line_total_inr DECIMAL(15, 2) NOT NULL, -- quantity * unit_price_inr - discount + gst
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_order_items_order_id ON order_items(order_id);
CREATE INDEX idx_order_items_product_id ON order_items(product_id);


-- TABLE: shipping_quotes
-- Logistics provider quotes for orders
CREATE TABLE shipping_quotes (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    order_id UUID NOT NULL REFERENCES orders(id) ON DELETE CASCADE,
    provider_name VARCHAR(100) NOT NULL,
    estimated_days INT NOT NULL,
    shipping_cost DECIMAL(12, 2) NOT NULL,
    quoted_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    accepted_at TIMESTAMP
);

CREATE INDEX idx_shipping_quotes_order_id ON shipping_quotes(order_id);


-- TABLE: order_tracking_events
-- Delivery tracking: "Order Confirmed", "Shipped", "In Transit", "Delivered"
CREATE TABLE order_tracking_events (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    order_id UUID NOT NULL REFERENCES orders(id) ON DELETE CASCADE,
    event_type VARCHAR(50) NOT NULL, -- 'CONFIRMED', 'SHIPPED', 'IN_TRANSIT', 'DELIVERED', 'DELAYED'
    event_status order_status_enum NOT NULL,
    description TEXT,
    location VARCHAR(100),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_order_tracking_events_order_id ON order_tracking_events(order_id);
CREATE INDEX idx_order_tracking_events_created_at ON order_tracking_events(created_at);


-- TABLE: currency_rates
-- Daily snapshot of exchange rates against INR
CREATE TABLE currency_rates (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    currency_code VARCHAR(3) NOT NULL,
    exchange_rate_to_inr DECIMAL(12, 4) NOT NULL, -- How much INR per 1 unit of currency
    valid_date DATE NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(currency_code, valid_date)
);

CREATE INDEX idx_currency_rates_currency ON currency_rates(currency_code);
CREATE INDEX idx_currency_rates_date ON currency_rates(valid_date);


-- TABLE: payment_snapshots
-- Currency conversion snapshot per payment (immutable record)
CREATE TABLE payment_snapshots (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    order_id UUID NOT NULL REFERENCES orders(id) ON DELETE RESTRICT,
    amount_inr DECIMAL(15, 2) NOT NULL,
    buyer_currency VARCHAR(3) NOT NULL,
    exchange_rate DECIMAL(12, 4) NOT NULL, -- Exchange rate used for conversion
    amount_in_buyer_currency DECIMAL(15, 2) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_payment_snapshots_order_id ON payment_snapshots(order_id);


-- TABLE: payments
-- Payment intent tracking with escrow-like hold mechanism
CREATE TABLE payments (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    payment_reference VARCHAR(100) NOT NULL UNIQUE,
    order_id UUID NOT NULL REFERENCES orders(id) ON DELETE RESTRICT,
    amount_inr DECIMAL(15, 2) NOT NULL,
    currency_code VARCHAR(3) NOT NULL DEFAULT 'INR',
    status payment_status_enum NOT NULL DEFAULT 'PENDING',
    payment_method VARCHAR(50), -- 'CREDIT_CARD', 'UPI', 'BANK_TRANSFER'
    payment_gateway_id VARCHAR(100), -- Payment gateway transaction ID
    payment_initiated_at TIMESTAMP,
    payment_completed_at TIMESTAMP,
    escrow_released_at TIMESTAMP, -- When funds released to seller
    refund_initiated_at TIMESTAMP,
    refund_completed_at TIMESTAMP,
    refund_amount DECIMAL(15, 2),
    payment_gateway_response TEXT, -- JSON or raw response
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by UUID REFERENCES users(id) ON DELETE SET NULL
);

CREATE INDEX idx_payments_order_id ON payments(order_id);
CREATE INDEX idx_payments_status ON payments(status);
CREATE INDEX idx_payments_payment_reference ON payments(payment_reference);
CREATE INDEX idx_payments_created_at ON payments(created_at);


-- TABLE: invoices
-- Proforma and final invoices (versioned)
CREATE TABLE invoices (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    invoice_number VARCHAR(50) NOT NULL UNIQUE,
    order_id UUID NOT NULL REFERENCES orders(id) ON DELETE RESTRICT,
    invoice_type invoice_type_enum NOT NULL, -- 'PROFORMA' or 'FINAL'
    status invoice_status_enum NOT NULL DEFAULT 'DRAFT',
    issued_at TIMESTAMP,
    due_date DATE,
    paid_at TIMESTAMP,
    subtotal_inr DECIMAL(15, 2) NOT NULL,
    tax_amount_inr DECIMAL(15, 2) DEFAULT 0,
    shipping_cost_inr DECIMAL(15, 2) DEFAULT 0,
    total_amount_inr DECIMAL(15, 2) NOT NULL,
    payment_terms VARCHAR(255),
    notes TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by UUID REFERENCES users(id) ON DELETE SET NULL
);

CREATE INDEX idx_invoices_order_id ON invoices(order_id);
CREATE INDEX idx_invoices_status ON invoices(status);
CREATE INDEX idx_invoices_invoice_number ON invoices(invoice_number);


-- TABLE: invoice_versions
-- Versioning for amended invoices (proforma→final)
CREATE TABLE invoice_versions (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    invoice_id UUID NOT NULL REFERENCES invoices(id) ON DELETE CASCADE,
    version_number INT NOT NULL,
    amended_reason TEXT,
    version_created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    amended_by UUID REFERENCES users(id) ON DELETE SET NULL
);

CREATE INDEX idx_invoice_versions_invoice_id ON invoice_versions(invoice_id);


-- TABLE: reviews
-- Buyer reviews for products after delivery
CREATE TABLE reviews (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    product_id UUID NOT NULL REFERENCES products(id) ON DELETE CASCADE,
    order_id UUID NOT NULL REFERENCES orders(id) ON DELETE CASCADE,
    buyer_id UUID NOT NULL REFERENCES buyer_profile(id) ON DELETE CASCADE,
    rating INT NOT NULL,
    CHECK (rating >= 1 AND rating <= 5),
    review_text TEXT,
    helpful_count INT DEFAULT 0,
    unhelpful_count INT DEFAULT 0,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(product_id, order_id) -- One review per product per order
);

CREATE INDEX idx_reviews_product_id ON reviews(product_id);
CREATE INDEX idx_reviews_buyer_id ON reviews(buyer_id);
CREATE INDEX idx_reviews_created_at ON reviews(created_at);


-- TABLE: review_media
-- Photos/videos attached to reviews
CREATE TABLE review_media (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    review_id UUID NOT NULL REFERENCES reviews(id) ON DELETE CASCADE,
    media_url TEXT NOT NULL,
    media_type VARCHAR(20) NOT NULL, -- 'IMAGE', 'VIDEO'
    uploaded_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_review_media_review_id ON review_media(review_id);


-- TABLE: disputes
-- Buyer/Seller disputes on orders
CREATE TABLE disputes (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    dispute_number VARCHAR(50) NOT NULL UNIQUE,
    order_id UUID NOT NULL REFERENCES orders(id) ON DELETE RESTRICT,
    raised_by raised_by_enum NOT NULL, -- BUYER or SELLER
    raised_by_user_id UUID NOT NULL REFERENCES users(id) ON DELETE RESTRICT,
    reason VARCHAR(255) NOT NULL, -- 'PRODUCT_QUALITY', 'NON_DELIVERY', 'PAYMENT_ISSUE'
    description TEXT NOT NULL,
    status dispute_status_enum NOT NULL DEFAULT 'OPEN',
    resolution_notes TEXT,
    resolved_at TIMESTAMP,
    resolved_by UUID REFERENCES users(id) ON DELETE SET NULL,
    payout_frozen BOOLEAN NOT NULL DEFAULT TRUE, -- Prevents seller payout until dispute resolved
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_disputes_order_id ON disputes(order_id);
CREATE INDEX idx_disputes_status ON disputes(status);
CREATE INDEX idx_disputes_raised_by_user_id ON disputes(raised_by_user_id);
CREATE INDEX idx_disputes_payout_frozen ON disputes(payout_frozen);


-- TABLE: dispute_evidence
-- Evidence (images/documents) attached to disputes
CREATE TABLE dispute_evidence (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    dispute_id UUID NOT NULL REFERENCES disputes(id) ON DELETE CASCADE,
    evidence_url TEXT NOT NULL,
    evidence_type VARCHAR(20) NOT NULL, -- 'IMAGE', 'DOCUMENT', 'VIDEO'
    uploaded_by UUID NOT NULL REFERENCES users(id) ON DELETE SET NULL,
    uploaded_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    description TEXT
);

CREATE INDEX idx_dispute_evidence_dispute_id ON dispute_evidence(dispute_id);


-- TABLE: admin_settings
-- Platform-wide admin configuration
CREATE TABLE admin_settings (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    setting_key VARCHAR(100) NOT NULL UNIQUE,
    setting_value TEXT NOT NULL,
    data_type VARCHAR(20) DEFAULT 'STRING', -- 'STRING', 'BOOLEAN', 'NUMBER', 'JSON'
    description TEXT,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_by UUID REFERENCES users(id) ON DELETE SET NULL
);

INSERT INTO admin_settings (setting_key, setting_value, data_type, description) VALUES
    ('BASIC_SELLER_MAX_PRODUCTS', '5', 'NUMBER', 'Max active products for BASIC_SELLER plan'),
    ('ADVANCED_SELLER_MAX_PRODUCTS', '999999', 'NUMBER', 'Max active products for ADVANCED_SELLER plan'),
    ('RFQ_EXPIRATION_DAYS', '30', 'NUMBER', 'Default RFQ validity in days'),
    ('DISPUTE_RESOLUTION_SLA_DAYS', '7', 'NUMBER', 'SLA for dispute resolution'),
    ('PAYMENT_ESCROW_RELEASE_DAYS', '3', 'NUMBER', 'Days after delivery confirmation to release escrow'),
    ('GST_PERCENTAGE', '18', 'NUMBER', 'Default GST percentage'),
    ('PLATFORM_COMMISSION_PERCENTAGE', '5', 'NUMBER', 'Platform commission on orders');

CREATE INDEX idx_admin_settings_key ON admin_settings(setting_key);


-- TABLE: audit_logs
-- Comprehensive audit trail for compliance and debugging
CREATE TABLE audit_logs (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID REFERENCES users(id) ON DELETE SET NULL,
    entity_type VARCHAR(100) NOT NULL, -- 'USER', 'PRODUCT', 'ORDER', 'PAYMENT', 'DISPUTE'
    entity_id UUID NOT NULL,
    action VARCHAR(50) NOT NULL, -- 'CREATE', 'UPDATE', 'DELETE', 'VERIFY', 'BLOCK'
    old_values JSONB,
    new_values JSONB,
    ip_address VARCHAR(45),
    user_agent TEXT,
    status VARCHAR(20) DEFAULT 'SUCCESS', -- 'SUCCESS', 'FAILED'
    error_message TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_audit_logs_user_id ON audit_logs(user_id);
CREATE INDEX idx_audit_logs_entity_type ON audit_logs(entity_type);
CREATE INDEX idx_audit_logs_entity_id ON audit_logs(entity_id);
CREATE INDEX idx_audit_logs_action ON audit_logs(action);
CREATE INDEX idx_audit_logs_created_at ON audit_logs(created_at);


-- ============================================================================
-- SECTION 4: INDEXING STRATEGY
-- ============================================================================

/*
COMPREHENSIVE INDEX STRATEGY
=============================

1. PRODUCT SEARCH & FILTERING INDEXES
   - Full-text search on product name: idx_products_name (USING GIN)
   - Status filtering: idx_products_status
   - Seller filtering: idx_products_seller_id
   - Category filtering: idx_product_categories_category_id
   - Tag filtering: idx_product_tags_tag_id
   - Creation date: idx_products_created_at (for sorting)

2. RFQ LISTING & NEGOTIATION INDEXES
   - Buyer RFQs: idx_rfq_buyer_id
   - Seller RFQs: idx_rfq_seller_id
   - Product-level RFQs: idx_rfq_product_id
   - RFQ status: idx_rfq_status (for workflow queries)
   - RFQ creation date: idx_rfq_created_at (for pagination)
   - RFQ chat messages: idx_chat_messages_rfq_chat_id, idx_chat_messages_created_at

3. ORDER & PAYMENT INDEXES
   - Buyer orders: idx_orders_buyer_id
   - Seller orders: idx_orders_seller_id
   - Order status: idx_orders_status (for order tracking)
   - Payment status: idx_payments_status (for reconciliation)
   - Payment creation: idx_payments_created_at (for pagination)
   - Order tracking events: idx_order_tracking_events_order_id, idx_order_tracking_events_created_at

4. DISPUTE TRACKING INDEXES
   - Order disputes: idx_disputes_order_id
   - Dispute status: idx_disputes_status
   - Payout freeze: idx_disputes_payout_frozen (for accounting queries)
   - Dispute evidence: idx_dispute_evidence_dispute_id

5. SELLER PROFILE INDEXES
   - Seller verification: idx_seller_profile_verification_status
   - Active products count: idx_seller_profile_active_products
   - Seller plan active: idx_seller_plan_active

6. AUDIT TRAIL INDEXES
   - Audit by user: idx_audit_logs_user_id
   - Audit by entity: idx_audit_logs_entity_type, idx_audit_logs_entity_id
   - Audit by action: idx_audit_logs_action
   - Audit by timestamp: idx_audit_logs_created_at

ALL INDEXES CREATED IN "SECTION 3: CREATE TABLES" ABOVE
*/


-- ============================================================================
-- SECTION 5: CRITICAL BUSINESS LOGIC CONSTRAINTS & NOTES
-- ============================================================================

/*
BUSINESS RULE ENFORCEMENT (Schema Level)
==========================================

1. SELLER PLAN CONSTRAINTS
   - Sellers CANNOT have more ACTIVE products than their plan allows
   - CHECK: seller_profile.active_products ≤ seller_plan.max_active_products
   - ENFORCEMENT: Backend service must validate before INSERT/UPDATE on products
   - QUERY: COUNT(products WHERE seller_id = X AND status = 'ACTIVE') must be ≤ seller_plan.max_active_products

2. SELLER LOCATION CONSTRAINT
   - Only Indian sellers (country = 'IN')
   - CHECK constraint on seller_profile.country ensures this

3. IEC VERIFICATION WORKFLOW
   - States: NOT_VERIFIED → PENDING → VERIFIED → REJECTED
   - VERIFIED sellers can list products
   - REJECTED sellers need re-verification
   - Backend must enforce this state machine

4. RFQ TO ORDER CONVERSION
   - When RFQ converts to order, rfq.status = 'CONVERTED_TO_ORDER'
   - Order stores rfq_id reference
   - Backend must ensure: rfq.converted_to_order_id IS NOT NULL

5. PAYMENT ESCROW HOLD
   - New orders create PENDING payments with escrow_released_at = NULL
   - On delivery_confirmed → payment status = COMPLETED, escrow_released_at = CURRENT_TIMESTAMP
   - Prevents seller payout until delivery confirmed

6. DISPUTE PAYOUT FREEZE
   - Any OPEN dispute on order → disputes.payout_frozen = TRUE
   - Seller cannot withdraw funds while payout_frozen = TRUE
   - Backend must check: SELECT * FROM disputes WHERE order_id = X AND status IN ('OPEN', 'UNDER_REVIEW') AND payout_frozen = TRUE

7. CURRENCY SNAPSHOT
   - Every order captures buyer_currency at order creation
   - payment_snapshots table stores immutable exchange_rate used
   - Prevents disputes over currency fluctuations

8. CHAT TYPE SEPARATION
   - rfq_chat_id XOR inquiry_chat_id (one must be NULL, other NOT NULL)
   - CHECK constraint ensures this in chat_messages

9. SOFT DELETES
   - users.deleted_at, products.deleted_at, seller_profile.deleted_at
   - Backend queries must include: WHERE deleted_at IS NULL
   - Preserves data for audit/compliance

10. AUDIT TRAIL
    - All user actions logged in audit_logs
    - JSONB columns store before/after values for debugging
    - Immutable records (no updates to audit_logs)

PERFORMANCE OPTIMIZATION NOTES
==============================

1. PAGINATION
   - Always use OFFSET LIMIT with created_at index for pagination
   - Avoid OFFSET for large datasets; use keyset pagination: WHERE created_at > LAST_TIMESTAMP

2. PRODUCT SEARCH
   - Use full-text search (GIN index) for name searches
   - Combine with category/tag/seller filters via indexed lookups
   - Expected query time: <100ms for most queries

3. ORDER QUERIES
   - Always filter by buyer_id or seller_id first (indexed)
   - Then filter by status (indexed)
   - Join with order_items only when needed

4. CHAT MESSAGES
   - Pagination by created_at
   - Mark messages as read async (avoid locking)
   - Consider archiving old messages (>1 year) for performance

5. DISPUTE QUERIES
   - Critical query: payout_frozen = TRUE for accounting (indexed)
   - Admin queries: status IN ('OPEN', 'UNDER_REVIEW') for SLA monitoring

6. AUDIT LOGS
   - Consider partitioning audit_logs by date (PARTITION BY RANGE (created_at))
   - Archive old logs (>1 year) to separate table
   - Query performance: Indexed lookups by user_id, entity_id should be <50ms

FUTURE SCALABILITY CONSIDERATIONS
===================================

1. MULTI-WAREHOUSE
   - Add warehouse_id FK to inventory_locations table
   - Partition orders by warehouse
   - Add warehouse_id to shipping_quotes for better fulfillment routing

2. MULTI-LANGUAGE
   - Add language column to product descriptions (default 'en')
   - Create translations table: product_id, language_code, translated_name, translated_description
   - Support for category names, tags in multiple languages

3. SELLER CATALOG DOWNLOAD
   - Add bulk_export_jobs table: seller_id, export_format, status, download_url, created_at
   - Schedule exports nightly via background job
   - Store compressed CSV/XML in object storage (S3/Blob Storage)

4. DATABASE SHARDING
   - Shard by buyer_country or seller_id for horizontal scaling
   - Orders/RFQs heavily sharded by buyer_id and seller_id
   - Maintain global users table (replicated across shards)

5. CACHING STRATEGY
   - Cache seller_plan data (changes rarely)
   - Cache currency_rates (updated daily)
   - Cache product categories/tags (static)
   - Invalidate on update, TTL 1 hour

*/

