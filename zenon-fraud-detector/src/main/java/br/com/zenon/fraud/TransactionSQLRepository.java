package br.com.zenon.fraud;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

public class TransactionSQLRepository implements TransactionRepository {


    @Override
    public Optional<Transaction> findByOriginName(String originName) {
        var statement = (java.sql.PreparedStatement) null;
        try (Connection conexao = connect()) {
            String sql = "SELECT step, type, amount, nameOrig, oldbalanceOrg, newbalanceOrig, nameDest, oldbalanceDest, newbalanceDest, isFraud, isFlaggedFraud FROM  transactions WHERE nameOrig = ?";
            statement = conexao.prepareStatement(sql);
            statement.setString(1, originName);
            ResultSet rs = statement.executeQuery();
            if (rs.next()) {
                return Optional.of(getValue(rs));
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to save transaction", e);
        } finally {
            try {
                statement.close();
            } catch (Exception e) {
                throw new RuntimeException("Failed to close statement", e);
            }

        }
        return Optional.empty();
    }

    private static Transaction getValue(ResultSet rs) throws SQLException {
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
        var statement = (java.sql.PreparedStatement) null;
        try (Connection conexao = connect()) {
            String sql = "INSERT INTO transactions (step, type, amount, nameOrig, oldbalanceOrg, newbalanceOrig, nameDest, oldbalanceDest, newbalanceDest, isFraud, isFlaggedFraud) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?,?)";
            statement = conexao.prepareStatement(sql);
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
        } finally {
            try {
                statement.close();
            } catch (Exception e) {
                throw new RuntimeException("Failed to close statement", e);
            }

        }
    }

    private Connection connect() {
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
