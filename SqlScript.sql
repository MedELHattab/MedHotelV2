-- Drop the database if it already exists
DROP DATABASE IF EXISTS medhotel;

-- Create the medhotel database
CREATE DATABASE medhotel;

-- Switch to medhotel database
\c medhotel;

-- Drop tables if they already exist
DROP TABLE IF EXISTS cancellation_policies;
DROP TABLE IF EXISTS pricing_rules;
DROP TABLE IF EXISTS reservations;
DROP TABLE IF EXISTS rooms;
DROP TABLE IF EXISTS room_types;
DROP TABLE IF EXISTS customers;

-- Create Customer table
CREATE TABLE customers (
    customer_id SERIAL PRIMARY KEY,
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    phone VARCHAR(20),
    address TEXT
);

-- Create RoomType table (Enum can be stored as VARCHAR or a separate table)
CREATE TABLE room_types (
    room_type_id SERIAL PRIMARY KEY,
    type_name VARCHAR(50) UNIQUE NOT NULL,  -- e.g., Single, Double, Suite
    description TEXT
);

-- Create Room table
CREATE TABLE rooms (
    room_id SERIAL PRIMARY KEY,
    room_number VARCHAR(10) UNIQUE NOT NULL,
    room_type_id INT REFERENCES room_types(room_type_id) ON DELETE CASCADE,
    price_per_night NUMERIC(10, 2) NOT NULL,
    availability BOOLEAN DEFAULT TRUE
);

-- Create Reservation table
CREATE TABLE reservations (
    reservation_id SERIAL PRIMARY KEY,
    customer_id INT REFERENCES customers(customer_id) ON DELETE CASCADE,
    room_id INT REFERENCES rooms(room_id) ON DELETE CASCADE,
    check_in_date DATE NOT NULL,
    check_out_date DATE NOT NULL,
    total_price NUMERIC(10, 2) NOT NULL,
    reservation_status VARCHAR(50) DEFAULT 'pending', -- e.g., pending, confirmed, cancelled
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Create a table for dynamic pricing rules (optional, if you want seasonal pricing)
CREATE TABLE pricing_rules (
    rule_id SERIAL PRIMARY KEY,
    room_type_id INT REFERENCES room_types(room_type_id) ON DELETE CASCADE,
    season VARCHAR(50), -- e.g., high-season, low-season
    day_of_week VARCHAR(20), -- e.g., Monday, Tuesday
    event VARCHAR(100), -- Optional, for special events
    price_multiplier NUMERIC(3, 2) NOT NULL DEFAULT 1.00 -- e.g., 1.5 for 50% increase in price
);

-- Create a table for storing cancellation policies
CREATE TABLE cancellation_policies (
    policy_id SERIAL PRIMARY KEY,
    reservation_id INT REFERENCES reservations(reservation_id) ON DELETE CASCADE,
    refund_percentage NUMERIC(3, 2) NOT NULL, -- e.g., 0.50 for 50% refund
    deadline_days_before_checkin INT NOT NULL -- Deadline in days before check-in
);

INSERT INTO room_types (type_name, description)
VALUES
('Single', 'A room assigned to one person.'),
('Double', 'A room assigned to two people.');

ALTER TABLE public.rooms ALTER COLUMN availability SET NOT NULL;



ALTER TABLE reservations
    DROP COLUMN reservation_status;
