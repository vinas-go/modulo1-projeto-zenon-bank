package br.com.zenon.fraud;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;
import java.util.function.Consumer;
import java.util.stream.Stream;

public class EfficientTransactionIngestor {

    public static final int MAX_SIZE = 10_000;
    public static final int BATCH_MAX_SIZE = 10_000;
    private final Semaphore dbPermits = new Semaphore(100);

    public List<Transaction> read(String filePath) {
        return readFile(filePath, MAX_SIZE);
    }

    public List<Transaction> read(String filePath, Integer maxSize) {
        return readFile(filePath, maxSize);
    }

    private List<Transaction> readFile(String filePath, int maxSize) {
        Objects.requireNonNull(maxSize, "maxSize não pode ser nulo");
        try {
            Stream<String> lines = Files.lines(new File(filePath).toPath());
            return lines
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


    public void readFileAsStream(String filePath, int maxSize, Consumer<Transaction> consumer) {
        Objects.requireNonNull(maxSize, "maxSize não pode ser nulo");
        try {
            Stream<String> lines = Files.lines(new File(filePath).toPath());
            lines
                    .skip(1)
                    .limit(maxSize)
                    .map(this::parseTransaction)
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .forEach(consumer);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void readFileAsBatch(String filePath, int maxSize, Consumer<List<Transaction>> batchConsumer) {
        Objects.requireNonNull(maxSize, "maxSize não pode ser nulo");
        Path path = new File(filePath).toPath();
        try (Stream<String> lines = Files.lines(path).skip(1).limit(maxSize)) {

            var iterator = lines.iterator();
            if (iterator.hasNext()) {
                List<String> lineBatch = new ArrayList<>();
                while (iterator.hasNext()) {
                    String line = iterator.next();
                    lineBatch.add(line);
                    if (lineBatch.size() >= BATCH_MAX_SIZE) {
                        executeBatch(lineBatch, batchConsumer);
                        lineBatch.clear();
                    }
                }

                if (!lineBatch.isEmpty()) {
                    executeBatch(lineBatch, batchConsumer);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void readFileAsBatchThreadPool(String filePath, int maxSize, Consumer<List<Transaction>> batchConsumer) {
        Objects.requireNonNull(maxSize, "maxSize não pode ser nulo");
        Path path = new File(filePath).toPath();
        try (ExecutorService executorService = Executors.newFixedThreadPool(20); Stream<String> lines = Files.lines(path).skip(1).limit(maxSize)) {

            var iterator = lines.iterator();
            if (iterator.hasNext()) {
                List<String> lineBatch = new ArrayList<>();
                while (iterator.hasNext()) {
                    String line = iterator.next();
                    lineBatch.add(line);
                    if (lineBatch.size() >= BATCH_MAX_SIZE) {
                        final List<String> currentLineBatch = List.copyOf(lineBatch);
                        executorService.submit(() -> executeBatch(currentLineBatch, batchConsumer));
                        lineBatch.clear();
                    }
                }

                if (!lineBatch.isEmpty()) {
                    executorService.submit(() -> executeBatch(lineBatch, batchConsumer));
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void readFileAsBatchVirtualThreads(String filePath, int maxSize, Consumer<List<Transaction>> batchConsumer) {
        Objects.requireNonNull(maxSize, "maxSize não pode ser nulo");
        Path path = new File(filePath).toPath();
        try (ExecutorService executorService = Executors.newVirtualThreadPerTaskExecutor(); Stream<String> lines = Files.lines(path).skip(1).limit(maxSize)) {

            var iterator = lines.iterator();
            if (iterator.hasNext()) {
                List<String> lineBatch = new ArrayList<>();
                while (iterator.hasNext()) {
                    String line = iterator.next();
                    lineBatch.add(line);
                    if (lineBatch.size() >= BATCH_MAX_SIZE) {
                        final List<String> currentLineBatch = List.copyOf(lineBatch);
                        executorService.submit(() -> executeBatch(currentLineBatch, batchConsumer));
                        lineBatch.clear();
                    }
                }

                if (!lineBatch.isEmpty()) {
                    executorService.submit(() -> executeBatch(lineBatch, batchConsumer));
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void executeBatch(List<String> lineBatch, Consumer<List<Transaction>> batchConsumer) {
        System.out.println(Thread.currentThread().getName());
        List<Transaction> lista = lineBatch.stream()
                .map(this::parseTransaction)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .toList();

        try {
            dbPermits.acquire();
            try {
                batchConsumer.accept(lista);
            } finally {
                dbPermits.release();
            }
        } catch (Exception e) {
            Thread.currentThread().interrupt();
        } finally {

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
