package dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import model.Withdrawal;

public class WithdrawalDAO extends DAO {

    public WithdrawalDAO() {
        super();
    }

    public List<Withdrawal> findAll(String keyword, String status) {
        List<Withdrawal> list = new ArrayList<>();
        StringBuilder sql = new StringBuilder("SELECT * FROM tblWithdrawal WHERE 1=1 ");
        List<Object> params = new ArrayList<>();

        if (status != null && !status.trim().isEmpty() && !"ALL".equalsIgnoreCase(status)) {
            sql.append("AND status = ? ");
            params.add(status.trim());
        }
        if (keyword != null && !keyword.trim().isEmpty()) {
            sql.append("AND (LOWER(bank_name) LIKE ? OR LOWER(account_holder) LIKE ?) ");
            String kw = "%" + keyword.trim().toLowerCase() + "%";
            params.add(kw);
            params.add(kw);
        }

        sql.append("ORDER BY created_at DESC");

        try (PreparedStatement ps = con.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetToWithdrawal(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public Withdrawal findById(UUID id) {
        String sql = "SELECT * FROM tblWithdrawal WHERE id = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setObject(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToWithdrawal(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean insert(Withdrawal wd) {
        String sql = "INSERT INTO tblWithdrawal (id, user_id, wallet_id, amount, bank_name, account_number, account_holder, status, created_at) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, 'PENDING', CURRENT_TIMESTAMP)";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            if (wd.getId() == null) wd.setId(UUID.randomUUID());

            ps.setObject(1, wd.getId());
            ps.setObject(2, wd.getUserId());
            ps.setObject(3, wd.getWalletId());
            ps.setLong(4, wd.getAmount());
            ps.setString(5, wd.getBankName());
            ps.setString(6, wd.getAccountNumber());
            ps.setString(7, wd.getAccountHolder());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    private Withdrawal mapResultSetToWithdrawal(ResultSet rs) throws SQLException {
        return new Withdrawal(
            (UUID) rs.getObject("id"),
            (UUID) rs.getObject("user_id"),
            (UUID) rs.getObject("wallet_id"),
            rs.getLong("amount"),
            rs.getString("bank_name"),
            rs.getString("account_number"),
            rs.getString("account_holder"),
            rs.getString("status"),
            rs.getTimestamp("created_at"),
            rs.getTimestamp("processed_at")
        );
    }
}
