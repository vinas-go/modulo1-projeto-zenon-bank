import br.com.zenon.fraud.Transaction;
import br.com.zenon.fraud.TransactionIngestor;

import java.util.List;

class Principal {
    public static void main(String[] args) {
        long l1 = System.currentTimeMillis();

        System.out.println("Iniciando....");
        TransactionIngestor transactionIngestor = new TransactionIngestor();
        //List<Transaction> lista = transactionIngestor.readOld("data/PS_20174392719_1491204439457_log.csv");
        List<Transaction> lista = transactionIngestor.read("data/PS_20174392719_1491204439457_log.csv");
        //List<Transaction> lista = transactionIngestor.read("data/paysim_with_bad_data.csv");
        System.out.println("Quantidade de transações lidas: " + lista.size());
        lista.forEach(System.out::println);

        long l2 = System.currentTimeMillis();
        System.out.println("Tempo gasto: " + (l2 - l1));
    }
}
