package br.com.zenon.fraud;

public class ReportMain {
    public static void main(String[] args) {
        System.out.println("Iniciando....");
        var transactionReport = new TransactionReport("data/PS_20174392719_1491204439457_log.csv");
        System.out.println("Total de linhas: " + transactionReport.countLines());
        System.out.println("Total de fraudes: " + transactionReport.countFrauds());
        System.out.println("Valor total transacionado: " + transactionReport.totalOfTransactions());
    }
}
