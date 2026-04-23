import br.com.zenon.fraud.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

class Principal {
    public static void main(String[] args) {


        System.out.println("Iniciando....");
        var transactionIngestor = new TransactionIngestor();
        String filePath = "data/PS_20174392719_1491204439457_log.csv";
        //filePath = "data/paysim_with_bad_data.csv";

        List<Transaction> lista = transactionIngestor.read(filePath);
        Map<String, Transaction> map = transactionIngestor.readMap(filePath);

        //System.out.println("Quantidade de transações lidas: " + lista.size());
        //lista.forEach(System.out::println);

//        var fraudAnalyzer = new FraudAnalyzer(lista);
//        System.out.println("1. Total de Fraudes: " + fraudAnalyzer.countFrauds());
//        System.out.println("2. Top 3 Fraudes de Maior Valor: ");
//        fraudAnalyzer.findTopAmounts(3).stream().forEach(item -> System.out.println(item.amount().toPlainString()));
//        System.out.println("3. Clientes Suspeitos: ");
//        fraudAnalyzer.findTopSuspiciousClients(5).stream().forEach(System.out::println);
//        System.out.println("4. Prejuizo Total: " + fraudAnalyzer.prejuzioTotal(lista).toPlainString());
//        System.out.println("5. Prejuizo Por Tipo: ");
//        Map<EnumTransactionType, Long> map = fraudAnalyzer.quantidadePorTipo();
//        map.forEach((k, v) -> System.out.println(" - " + k + ": " + v));

//__________________________________
//        var repository = new TransactionListRepository(lista);
//        String cliente = "C1868032458";
//        repository.findByName(cliente).ifPresentOrElse(
//                System.out::println,
//                () -> System.out.println("Transação não encontrada para o cliente " + cliente)
//        );
//
//        String cliente2 = "C1231006815";
//        repository.findByName(cliente2).ifPresentOrElse(
//                System.out::println,
//                () -> System.out.println("Transação não encontrada para o cliente " + cliente2)
//        );

        String cliente3 = "C1868032458";

        TransactionRepository repository = new TransactionListRepository(lista);
        long t1 = System.nanoTime();
        repository.findByOriginName(cliente3).ifPresentOrElse(
                System.out::println,
                () -> System.out.println("Transação não encontrada para o cliente " + cliente3)
        );

        long t2 = System.nanoTime();
        System.out.println("Tempo gasto list: " + ((t2 - t1)/1-000-000.0));

        repository = new TransactionMapRepository(lista);
        t1 = System.nanoTime();
        repository.findByOriginName(cliente3).ifPresentOrElse(
                System.out::println,
                () -> System.out.println("Transação não encontrada para o cliente " + cliente3)
        );

        t2 = System.nanoTime();
        System.out.println("Tempo gasto map: " + ((t2 - t1)/1-000-000.0));

    }
}
