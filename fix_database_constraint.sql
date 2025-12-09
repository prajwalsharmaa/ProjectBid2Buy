-- SQL script to fix the _user_type_check constraint
-- Run this in your PostgreSQL database

-- Connect to your database first:
-- psql -U ProjectBid2Buy -d ProjectBid2Buy

-- Drop the old constraint
ALTER TABLE _user DROP CONSTRAINT IF EXISTS _user_type_check;

-- Add the new constraint with correct enum values
ALTER TABLE _user ADD CONSTRAINT _user_type_check 
    CHECK (type IN ('ADMIN', 'BUYER', 'SELLER'));

