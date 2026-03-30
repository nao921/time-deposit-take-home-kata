CREATE TABLE time_deposits (
    id INTEGER PRIMARY KEY,
    plan_type VARCHAR(50) NOT NULL,
    balance NUMERIC(19, 2) NOT NULL,
    days INTEGER NOT NULL
);

CREATE TABLE withdrawals (
    id INTEGER PRIMARY KEY,
    time_deposit_id INTEGER NOT NULL,
    amount DOUBLE PRECISION NOT NULL,
    date DATE NOT NULL,
    FOREIGN KEY (time_deposit_id) REFERENCES time_deposits(id) ON DELETE CASCADE
);

CREATE INDEX idx_withdrawals_time_deposit_id ON withdrawals(time_deposit_id);
