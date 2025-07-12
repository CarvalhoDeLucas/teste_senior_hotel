CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

CREATE TABLE guest (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    name TEXT NOT NULL,
    document TEXT NOT NULL,
    phone TEXT NOT NULL,
    total_spent NUMERIC(10, 2) DEFAULT 0 NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT now(),
    updated_at TIMESTAMP NOT NULL DEFAULT now(),
    deleted_at TIMESTAMP
);

CREATE TABLE checkin (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    guest_id UUID NOT NULL REFERENCES guest(id),
    checkin_time TIMESTAMP NOT NULL,
    checkout_time TIMESTAMP,
    has_vehicle BOOLEAN NOT NULL,
    total_amount NUMERIC(10, 2),
    lodging_amount NUMERIC(10, 2),
    parking_amount NUMERIC(10, 2)
);
