package br.com.zenon.fraud;

import java.sql.*;
import java.util.List;
import java.util.Optional;

public class TransactionSQLRepository implements TransactionRepository {

    public static final int JDBC_BATCH_SIZE = 5_000;

    private Connection getConnection() {
        String url = "jdbc:postgresql://localhost:5432/zenon";
        String usuario = "postgres";
        String senha = "123";
        try {
            Connection conexao = DriverManager.getConnection(url, usuario, senha);
            Class.forName("org.postgresql.Driver");
            return conexao;
        } catch (Exception e) {
            throw new RuntimeException("Failed to load PostgreSQL driver", e);
        }
    }

    @Override
    public Optional<Transaction> findByOriginName(String originName) {

        String sql = "SELECT step, type, amount, nameOrig, oldbalanceOrg, newbalanceOrig, nameDest, oldbalanceDest, newbalanceDest, isFraud, isFlaggedFraud FROM  transactions WHERE nameOrig = ? LIMIT 1";
        try (
                var conexao = getConnection();
                var statement = conexao.prepareStatement(sql);
        ) {
            statement.setString(1, originName);
            try (ResultSet rs = statement.executeQuery();) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToTransaction(rs));
                }
            } catch (Exception e) {
                throw new RuntimeException("Failed to execute query", e);
            }

        } catch (Exception e) {
            throw new RuntimeException("Failed to save transaction", e);
        }
        return Optional.empty();
    }

    private static Transaction mapResultSetToTransaction(ResultSet rs) throws SQLException {
        return new Transaction(
                rs.getInt("step"),
                EnumTransactionType.valueOf(rs.getString("type")),
                rs.getBigDecimal("amount"),
                new TransactionCustomer(
                        rs.getString("nameOrig"),
                        rs.getBigDecimal("oldbalanceOrg"),
                        rs.getBigDecimal("newbalanceOrig")
                ),
                new TransactionCustomer(
                        rs.getString("nameDest"),
                        rs.getBigDecimal("oldbalanceDest"),
                        rs.getBigDecimal("newbalanceDest")
                ),
                rs.getBoolean("isFraud"),
                rs.getBoolean("isFlaggedFraud")
        );
    }

    @Override
    public void save(Transaction transaction) {
        String sql = "INSERT INTO transactions (step, type, amount, nameOrig, oldbalanceOrg, newbalanceOrig, nameDest, oldbalanceDest, newbalanceDest, isFraud, isFlaggedFraud) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?,?)";
        try (
                var conexao = getConnection();
                var statement = conexao.prepareStatement(sql);
        ) {

            statement.setInt(1, transaction.step());
            statement.setString(2, transaction.type().name());
            statement.setBigDecimal(3, transaction.amount());
            statement.setString(4, transaction.origin().name());
            statement.setBigDecimal(5, transaction.origin().oldBalance());
            statement.setBigDecimal(6, transaction.origin().newBalance());
            statement.setString(7, transaction.recipient().name());
            statement.setBigDecimal(8, transaction.recipient().oldBalance());
            statement.setBigDecimal(9, transaction.recipient().newBalance());
            statement.setBoolean(10, transaction.isFraud());
            statement.setBoolean(11, transaction.isFlaggedFraud());
            statement.executeUpdate();
        } catch (Exception e) {
            throw new RuntimeException("Failed to save transaction", e);
        }
    }

    public void saveAll(List<Transaction> transactions) {
        String sql = "INSERT INTO transactions (step, type, amount, nameOrig, oldbalanceOrg, newbalanceOrig, nameDest, oldbalanceDest, newbalanceDest, isFraud, isFlaggedFraud) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?,?)";
        try (var conn = getConnection()) {
            conn.setAutoCommit(false);
            int count = 0;
            try (var ps = conn.prepareStatement(sql)) {
                for (Transaction transaction : transactions) {
                    ps.setInt(1, transaction.step());
                    ps.setString(2, transaction.type().name());
                    ps.setBigDecimal(3, transaction.amount());
                    ps.setString(4, transaction.origin().name());
                    ps.setBigDecimal(5, transaction.origin().oldBalance());
                    ps.setBigDecimal(6, transaction.origin().newBalance());
                    ps.setString(7, transaction.recipient().name());
                    ps.setBigDecimal(8, transaction.recipient().oldBalance());
                    ps.setBigDecimal(9, transaction.recipient().newBalance());
                    ps.setBoolean(10, transaction.isFraud());
                    ps.setBoolean(11, transaction.isFlaggedFraud());
                    //System.out.println("Adicionando nova transacao no batch...");
                    ps.addBatch();
                    count++;
                    if (count % JDBC_BATCH_SIZE == 0) {
                        System.out.println("Executando batch...");
                        ps.executeBatch();
                        conn.commit();
                    }
                }
                System.out.println("Executando o batch final...");
                ps.executeBatch();
                conn.commit();
                conn.setAutoCommit(true);
            } catch (Exception e) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    throw new RuntimeException(ex);
                }
                throw new RuntimeException("Falha ao salvar a transaction", e);
            }
        } catch (Exception e) {
            throw new RuntimeException("Erro na conexão com o BD...", e);
        }
    }
}
