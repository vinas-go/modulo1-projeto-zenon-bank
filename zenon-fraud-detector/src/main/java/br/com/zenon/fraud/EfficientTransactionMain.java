package br.com.zenon.fraud;

public class EfficientTransactionMain {

    public static void main(String[] args) {
        //var transaction = new TransactionIngestor(); //Tempo gasto para salvar: 197319 milissegundos | 3,3 minu
        var transaction = new EfficientTransactionIngestor();

        String filePath = "data/PS_20174392719_1491204439457_log.csv";

//        List<Transaction> lista = transaction.read(filePath, 10_000);
//        var repository = new TransactionSQLRepository();
//        long t1 = System.currentTimeMillis();
//        repository.saveAll(lista); //Tempo gasto para salvar: 175644 milissegundos | 2,9 minu
//        long t2 = System.currentTimeMillis();
//        System.out.println("Tempo gasto para salvar: " + (t2 - t1) + " milissegundos");


        var repository = new TransactionSQLRepository();

        long t1 = System.currentTimeMillis();
        //transaction.readFileAsStream(filePath, 10_000, repository::save); //Tempo gasto para salvar: 188872 milissegundos | 3,14 minu
        //transaction.readFileAsBatch(filePath, 10_000, repository::saveAll); //Tempo gasto para salvar: 1243 milissegundos | 0.02 minu
        //transaction.readFileAsBatchThreadPool(filePath, 10_000, repository::saveAll); //Tempo gasto para salvar: 821 milissegundos | 0.01 minu
        //transaction.readFileAsBatchThreadPool(filePath, 10_000_000, repository::saveAll);
        transaction.readFileAsBatchVirtualThreads(filePath, 1_000_000, repository::saveAll);
        long t2 = System.currentTimeMillis();
        System.out.println("Tempo gasto para salvar: " + (t2 - t1)/1_000 + " segundos");

    }
}
