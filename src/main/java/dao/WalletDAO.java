package dao;

import java.sql.*;
import java.util.UUID;
import model.Wallet;

public class WalletDAO extends DAO {

    public WalletDAO() {
        super();
    }

    public Wallet findByUserId(UUID userId) {
        String sql = "SELECT * FROM tblWallet WHERE user_id = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setObject(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToWallet(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Wallet findById(UUID id) {
        String sql = "SELECT * FROM tblWallet WHERE id = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setObject(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToWallet(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean insert(Wallet wallet) {
        String sql = "INSERT INTO tblWallet (id, user_id, available_balance, created_at) " +
                     "VALUES (?, ?, ?, ?)";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            if (wallet.getId() == null) wallet.setId(UUID.randomUUID());
            if (wallet.getCreatedAt() == null) wallet.setCreatedAt(new Timestamp(System.currentTimeMillis()));

            ps.setObject(1, wallet.getId());
            ps.setObject(2, wallet.getUserId());
            ps.setLong(3, wallet.getAvailableBalance());
            ps.setTimestamp(4, wallet.getCreatedAt());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Cộng tiền vào ví
     */
    public boolean credit(UUID walletId, long amount) {
        String sql = "UPDATE tblWallet SET available_balance = available_balance + ?, updated_at = CURRENT_TIMESTAMP WHERE id = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setLong(1, amount);
            ps.setObject(2, walletId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Trừ tiền từ ví (với điều kiện số dư đủ)
     */
    public boolean debit(UUID walletId, long amount) {
        String sql = "UPDATE tblWallet SET available_balance = available_balance - ?, updated_at = CURRENT_TIMESTAMP " +
                     "WHERE id = ? AND available_balance >= ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setLong(1, amount);
            ps.setObject(2, walletId);
            ps.setLong(3, amount);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    private Wallet mapResultSetToWallet(ResultSet rs) throws SQLException {
        return new Wallet(
            (UUID) rs.getObject("id"),
            (UUID) rs.getObject("user_id"),
            rs.getLong("available_balance"),
            rs.getTimestamp("created_at"),
            rs.getTimestamp("updated_at")
        );
    }
}
