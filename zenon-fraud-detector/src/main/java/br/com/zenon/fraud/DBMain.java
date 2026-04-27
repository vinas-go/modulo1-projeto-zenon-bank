package br.com.zenon.fraud;

import java.util.List;

public class DBMain {

    public static void main(String[] args) {
        //var transaction = new TransactionIngestor(); //Tempo gasto para salvar: 197319 milissegundos | 3,3 minu
        var transaction = new EfficientTransactionIngestor(); //Tempo gasto para salvar: 175644 milissegundos | 2,9 minu

        String filePath = "data/PS_20174392719_1491204439457_log.csv";
        List<Transaction> lista = transaction.read(filePath, 10_000);

        var repository = new TransactionSQLRepository();
        long t1 = System.currentTimeMillis();
        lista.forEach(repository::save);
        long t2 = System.currentTimeMillis();
        System.out.println("Tempo gasto para salvar: " + (t2 - t1) + " milissegundos");

    }
}
