-- Loyalty Service Migrations
CREATE TABLE IF NOT EXISTS loyalty_accounts (
    id BIGSERIAL PRIMARY KEY,
    user_id VARCHAR(255) UNIQUE NOT NULL,
    total_points DECIMAL(19, 4) NOT NULL DEFAULT 0.0000,
    available_points DECIMAL(19, 4) NOT NULL DEFAULT 0.0000,
    spent_points DECIMAL(19, 4) NOT NULL DEFAULT 0.0000,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    version INTEGER
);

CREATE INDEX idx_loyalty_accounts_user_id ON loyalty_accounts(user_id);

CREATE TYPE transaction_type AS ENUM ('EARN', 'REDEEM');

CREATE TABLE IF NOT EXISTS points_transactions (
    id BIGSERIAL PRIMARY KEY,
    user_id VARCHAR(255) NOT NULL,
    amount DECIMAL(19, 4) NOT NULL,
    type transaction_type NOT NULL,
    reference_id VARCHAR(255) NOT NULL,
    description TEXT,
    event_id VARCHAR(255) UNIQUE, -- For idempotency
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    version INTEGER
);

CREATE INDEX idx_points_transactions_user_id ON points_transactions(user_id);
CREATE INDEX idx_points_transactions_reference_id ON points_transactions(reference_id);
CREATE INDEX idx_points_transactions_event_id ON points_transactions(event_id);