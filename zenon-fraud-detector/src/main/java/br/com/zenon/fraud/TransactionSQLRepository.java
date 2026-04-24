package br.com.zenon.fraud;

import java.sql.*;
import java.util.Optional;

public class TransactionSQLRepository implements TransactionRepository {


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
}
