package br.com.zenon.fraud;

import java.math.BigDecimal;

public record Transaction(
        String step,
        EnumTransactionType type,
        BigDecimal amount,
        Client clientOrig,
        Client clientDest,
        boolean isFraud,
        boolean isFlaggedFraud
) {

}
