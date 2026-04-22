package br.com.zenon.fraud;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.util.Objects;
import java.util.stream.Stream;

public class TransactionReport {

    private record ReportTransaction(BigDecimal amount, boolean isFraud){};

    public final int MAX_SIZE = 100_000;
    private final String filePath;

    public TransactionReport(String filePath) {
        Objects.requireNonNull(filePath, "O caminho do arquivo não pode ser nulo");
        this.filePath = filePath;
    }

    private Stream<String> readFile() {
        try {
            return Files.lines(new File(filePath).toPath());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public long countLines() {
        return readFile().skip(1).count();
    }

    public long countFrauds() {
        return readFile()
                .skip(1)
                .map(line -> line.split(","))
                .filter(fields -> fields[9].equals("1"))
                .count();
    }

    public BigDecimal totalOfTransactions() {
        return readFile()
                .skip(1)
                .map(line -> line.split(","))
                .filter(fields -> fields[9].equals("1"))
                .reduce(BigDecimal.ZERO, (total, fields) -> total.add(new BigDecimal(fields[2])), BigDecimal::add);
    }


//    private ReportTransaction parseReportTransaction(String line){
//        return new ReportTransaction();
//    }


}
