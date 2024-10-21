CREATE ROLE anon;

CREATE TABLE account
(
    id TEXT NOT NULL PRIMARY KEY,
    balance NUMERIC
);

GRANT USAGE ON SCHEMA public TO anon;
GRANT SELECT, INSERT, UPDATE, DELETE ON TABLE account TO anon;

CREATE FUNCTION create_account(
    account_id TEXT,
    balance NUMERIC
) RETURNS TEXT
    LANGUAGE plpgsql
AS
$$
DECLARE
    new_account_id TEXT;
BEGIN
    INSERT INTO account (id, balance)
    VALUES (account_id, balance)
    RETURNING id INTO new_account_id;

    RETURN new_account_id;
END;
$$;

CREATE FUNCTION transfer_balance(
    from_id TEXT,
    to_id TEXT,
    amount NUMERIC
) RETURNS VOID
    LANGUAGE plpgsql
AS
$$
BEGIN
    -- Subtract amount from from_id
    UPDATE account
    SET balance = balance - amount
    WHERE id::TEXT = from_id;

    -- Add amount to to_id
    UPDATE account
    SET balance = balance + amount
    WHERE id::TEXT = to_id;
END;
$$;