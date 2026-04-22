package br.com.zenon.fraud;

import java.text.NumberFormat;
import java.util.Currency;
import java.util.Locale;
import java.util.ResourceBundle;

public class ReportMain {
    public static void main(String[] args) {
        Locale locale = Locale.of("pt", "BR");

        var integerFormatter = NumberFormat.getIntegerInstance(locale);
        var currencyFormatter = NumberFormat.getCurrencyInstance(locale);
        currencyFormatter.setCurrency(Currency.getInstance(Locale.US));

        ResourceBundle report = ResourceBundle.getBundle("report", locale);


        System.out.println("Iniciando....");
        var transactionReport = new TransactionReport("data/PS_20174392719_1491204439457_log.csv");
        System.out.println(report.getString("total_lines") + ": " + integerFormatter.format(transactionReport.countLines()));
        System.out.println(report.getString("total_frauds") + ": " + integerFormatter.format(transactionReport.countFrauds()));
        System.out.println(report.getString("total_amount") + ": " + currencyFormatter.format(transactionReport.totalOfTransactions()));
    }
}
