package br.com.zenon.fraud;

import java.util.List;
import java.util.Optional;

public class DBMain {

    public static void main(String[] args) {
        //TransactionIngestor transactionIngestor = new TransactionIngestor();

        //String filePath = "data/PS_20174392719_1491204439457_log.csv";
        //List<Transaction> lista = transactionIngestor.read(filePath, 10_000);

        TransactionRepository repository = new TransactionSQLRepository();
        //lista.forEach(repository::save);
        repository.findByOriginName("C1231006815X").ifPresentOrElse(System.out::println, () -> System.out.println("Transação não encontrada"));
        repository.findByOriginName("C1231006815").ifPresentOrElse(System.out::println, () -> System.out.println("Transação não encontrada"));
    }
}
