package br.com.zenon.fraud;

import java.io.*;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.util.*;
import java.util.stream.Collectors;

public class TransactionIngestor {

    public static final int MAX_SIZE = 10_000;

    public List<Transaction> read(String filePath) {
        return readFile(filePath, MAX_SIZE);
    }

    public List<Transaction> read(String filePath, Integer maxSize) {
        return readFile(filePath, maxSize);
    }

    public List<Transaction> readOld(String filePath) {
        return readFileOld(filePath);
    }

    public Map<String, Transaction> readMap(String filePath) {
        return readFileMap(filePath);
    }

    private List<Transaction> readFile(String filePath, int maxSize) {
        Objects.requireNonNull(maxSize, "maxSize não pode ser nulo");
        try {
            List<String> lines = Files.readAllLines(new File(filePath).toPath());
            return lines.stream()
                    .skip(1)
                    .limit(maxSize)
                    .map(this::parseTransaction)
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .toList();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private Map<String, Transaction> readFileMap(String filePath) {
        try {
            List<String> lines = Files.readAllLines(new File(filePath).toPath());
            return lines.stream()
                    .skip(1)
                    .limit(MAX_SIZE)
                    .map(this::parseTransaction)
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .collect(
                            Collectors.toMap(
                                    transaction -> transaction.origin().name(),
                                    transaction -> transaction
                            )
                    );
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private List<Transaction> readFileOld(String filePath) {
        List<Transaction> listaTransactions = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            br.readLine(); // Skip header
            extractValues(br, listaTransactions);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return listaTransactions;
    }

    private void extractValues(BufferedReader br, List<Transaction> listaTransactions) throws IOException {
        String line;
        int count = 0;
        while ((line = br.readLine()) != null) {
            count++;
            if (count > MAX_SIZE) {
                break;
            }
            listaTransactions.add(this.parseTransaction(line).get());
        }
    }

    private Optional<Transaction> parseTransaction(String line) {
        var values = line.split(",");
        try {

            if (EnumTransactionType.valueOf(values[1]) == null) {
                throw new IllegalArgumentException("Valor de 'type' é inválido: " + values[1]);
            }


            Transaction transaction = new Transaction(
                    Integer.parseInt(values[0]),
                    EnumTransactionType.valueOf(values[1]),
                    new BigDecimal(values[2]),
                    new TransactionCustomer(values[3], new BigDecimal(values[4]), new BigDecimal(values[5])),
                    new TransactionCustomer(values[6], new BigDecimal(values[7]), new BigDecimal(values[8])),
                    values[9].equals("1"),
                    values[10].equals("1")
            );
            return Optional.of(transaction);
        } catch (Exception e) {
            System.err.println("Erro ao processar linha: " + line + " - " + e.getMessage());
            return Optional.empty();
        }

    }

}
