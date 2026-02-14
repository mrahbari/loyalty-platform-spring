-- Voucher Service Migration
CREATE TYPE voucher_status AS ENUM ('ACTIVE', 'USED', 'EXPIRED', 'CANCELLED');

CREATE TABLE IF NOT EXISTS vouchers (
    id BIGSERIAL PRIMARY KEY,
    voucher_id VARCHAR(255) UNIQUE NOT NULL,
    user_id VARCHAR(255) NOT NULL,
    voucher_code VARCHAR(255) UNIQUE NOT NULL,
    status voucher_status NOT NULL DEFAULT 'ACTIVE',
    discount_amount DECIMAL(19, 4) NOT NULL,
    expiration_date TIMESTAMP WITH TIME ZONE NOT NULL,
    description TEXT,
    order_id VARCHAR(255),
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    version INTEGER
);

CREATE INDEX idx_vouchers_user_id ON vouchers(user_id);
CREATE INDEX idx_vouchers_voucher_code ON vouchers(voucher_code);
CREATE INDEX idx_vouchers_expiration_date ON vouchers(expiration_date);