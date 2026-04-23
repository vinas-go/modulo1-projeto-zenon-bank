CREATE TABLE transactions
(
    step           INTEGER        NOT NULL,
    type           VARCHAR(255)   NOT NULL,
    amount         DECIMAL(18, 2) NOT NULL,
    nameOrig       VARCHAR(255)   NOT NULL,
    oldbalanceOrg  DECIMAL(18, 2) NOT NULL,
    newbalanceOrig DECIMAL(18, 2) NOT NULL,
    nameDest       VARCHAR(255)   NOT NULL,
    oldbalanceDest DECIMAL(18, 2) NOT NULL,
    newbalanceDest DECIMAL(18, 2) NOT NULL,
    isFraud        BOOLEAN        NOT NULL,
    isFlaggedFraud BOOLEAN        NOT NULL
);