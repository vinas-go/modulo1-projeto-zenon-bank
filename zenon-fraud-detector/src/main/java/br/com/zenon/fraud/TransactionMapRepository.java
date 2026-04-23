package br.com.zenon.fraud;

import java.util.*;
import java.util.stream.Collector;
import java.util.stream.Collectors;

public class TransactionMapRepository implements TransactionRepository {

    private final Map<String, Transaction> transactions;

    public TransactionMapRepository(Map<String, Transaction> transactions) {
        Objects.requireNonNull(transactions);
        this.transactions = transactions;
    }

    public TransactionMapRepository(List<Transaction> transactions) {
        Objects.requireNonNull(transactions);
        this.transactions = transactions.stream()
                .collect(
                        Collectors.toMap(
                                transaction -> transaction.origin().name(),
                                transaction -> transaction
                        )
                );
    }

    @Override
    public Optional<Transaction> findByOriginName(String originName) {
        return Optional.ofNullable(transactions.get(originName));
    }

    @Override
    public void save(Transaction transaction) {

    }


}
