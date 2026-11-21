package dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import model.Booking;
import model.PaymentTransaction;
import model.Wallet;

public class PaymentTransactionDAO extends DAO {

    public PaymentTransactionDAO() {
        super();
    }

    public boolean hasSuccessfulPayment(UUID bookingId) {
        String sql = "SELECT 1 FROM tblPaymentTransaction WHERE booking_id = ? AND type = 'TRANSFER' AND status = 'SUCCESS'";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setObject(1, bookingId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public PaymentTransaction createPendingDeposit(Wallet wallet, long amount, String referenceCode) {
        String sql = "INSERT INTO tblPaymentTransaction (id, source_wallet_id, destination_wallet_id, " +
                     "booking_id, type, status, amount, reference_code, created_at) " +
                     "VALUES (?, NULL, ?, NULL, 'DEPOSIT', 'PENDING', ?, ?, CURRENT_TIMESTAMP)";
        UUID txId = UUID.randomUUID();
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setObject(1, txId);
            ps.setObject(2, wallet.getId());
            ps.setLong(3, amount);
            ps.setString(4, referenceCode);

            int rows = ps.executeUpdate();
            if (rows > 0) {
                return new PaymentTransaction(null, wallet.getId(), null, "DEPOSIT", amount, referenceCode);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean confirmDeposit(String referenceCode, String gatewayResult) {
        String checkSql = "SELECT * FROM tblPaymentTransaction WHERE reference_code = ? AND status = 'PENDING'";
        try {
            con.setAutoCommit(false); // Bắt đầu transaction

            UUID destWalletId = null;
            long amount = 0;

            try (PreparedStatement ps = con.prepareStatement(checkSql)) {
                ps.setString(1, referenceCode);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        destWalletId = (UUID) rs.getObject("destination_wallet_id");
                        amount = rs.getLong("amount");
                    } else {
                        con.rollback();
                        return false; // Mã giao dịch không hợp lệ hoặc đã được xử lý trước đó
                    }
                }
            }

            if ("SUCCESS".equalsIgnoreCase(gatewayResult)) {
                // 1. Cộng tiền vào ví
                String updateWalletSql = "UPDATE tblWallet SET available_balance = available_balance + ?, updated_at = CURRENT_TIMESTAMP WHERE id = ?";
                try (PreparedStatement ps = con.prepareStatement(updateWalletSql)) {
                    ps.setLong(1, amount);
                    ps.setObject(2, destWalletId);
                    ps.executeUpdate();
                }

                // 2. Cập nhật transaction SUCCESS
                String updateTxSql = "UPDATE tblPaymentTransaction SET status = 'SUCCESS' WHERE reference_code = ?";
                try (PreparedStatement ps = con.prepareStatement(updateTxSql)) {
                    ps.setString(1, referenceCode);
                    ps.executeUpdate();
                }
            } else {
                // Đánh dấu giao dịch FAILED
                String updateTxSql = "UPDATE tblPaymentTransaction SET status = 'FAILED' WHERE reference_code = ?";
                try (PreparedStatement ps = con.prepareStatement(updateTxSql)) {
                    ps.setString(1, referenceCode);
                    ps.executeUpdate();
                }
            }

            con.commit();
            return "SUCCESS".equalsIgnoreCase(gatewayResult);
        } catch (SQLException e) {
            try { con.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            e.printStackTrace();
            return false;
        } finally {
            try { con.setAutoCommit(true); } catch (SQLException ex) { ex.printStackTrace(); }
        }
    }

    public PaymentTransaction transferBetweenWallets(Booking booking, Wallet studentWallet, Wallet tutorWallet, long amount) {
        String debitSql = "UPDATE tblWallet SET available_balance = available_balance - ?, updated_at = CURRENT_TIMESTAMP WHERE id = ? AND available_balance >= ?";
        String creditSql = "UPDATE tblWallet SET available_balance = available_balance + ?, updated_at = CURRENT_TIMESTAMP WHERE id = ?";
        String txSql = "INSERT INTO tblPaymentTransaction (id, source_wallet_id, destination_wallet_id, booking_id, type, status, amount, reference_code, balance_after, created_at) " +
                       "VALUES (?, ?, ?, ?, 'TRANSFER', 'SUCCESS', ?, ?, ?, CURRENT_TIMESTAMP)";
        String updateBookingSql = "UPDATE tblBooking SET status = 'PAID', updated_at = CURRENT_TIMESTAMP WHERE id = ?";

        String refCode = "PAY-" + System.currentTimeMillis() + "-" + (int)(Math.random() * 1000);
        UUID txId = UUID.randomUUID();

        try {
            con.setAutoCommit(false); // Transaction

            // 1. Trừ tiền ví học viên
            try (PreparedStatement psDebit = con.prepareStatement(debitSql)) {
                psDebit.setLong(1, amount);
                psDebit.setObject(2, studentWallet.getId());
                psDebit.setLong(3, amount);
                int rows = psDebit.executeUpdate();
                if (rows == 0) {
                    con.rollback(); // Không đủ tiền
                    return null;
                }
            }

            // 2. Cộng tiền ví gia sư
            try (PreparedStatement psCredit = con.prepareStatement(creditSql)) {
                psCredit.setLong(1, amount);
                psCredit.setObject(2, tutorWallet.getId());
                psCredit.executeUpdate();
            }

            // 3. Tạo bản ghi giao dịch
            long studentBalanceAfter = studentWallet.getAvailableBalance() - amount;
            try (PreparedStatement psTx = con.prepareStatement(txSql)) {
                psTx.setObject(1, txId);
                psTx.setObject(2, studentWallet.getId());
                psTx.setObject(3, tutorWallet.getId());
                psTx.setObject(4, booking.getId());
                psTx.setLong(5, amount);
                psTx.setString(6, refCode);
                psTx.setLong(7, studentBalanceAfter);
                psTx.executeUpdate();
            }

            // 4. Đổi trạng thái Booking sang PAID
            try (PreparedStatement psBk = con.prepareStatement(updateBookingSql)) {
                psBk.setObject(1, booking.getId());
                psBk.executeUpdate();
            }

            con.commit(); // Hoàn tất thành công
            return new PaymentTransaction(txId, studentWallet.getId(), tutorWallet.getId(), booking.getId(), 
                                          "TRANSFER", "SUCCESS", amount, refCode, studentBalanceAfter, new Timestamp(System.currentTimeMillis()));
        } catch (SQLException e) {
            try { con.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            e.printStackTrace();
            return null;
        } finally {
            try { con.setAutoCommit(true); } catch (SQLException ex) { ex.printStackTrace(); }
        }
    }

    public boolean refundBetweenWallets(UUID disputeId, UUID bookingId, Wallet tutorWallet, Wallet studentWallet, long refundAmount) {
        String debitSql = "UPDATE tblWallet SET available_balance = available_balance - ?, updated_at = CURRENT_TIMESTAMP WHERE id = ? AND available_balance >= ?";
        String creditSql = "UPDATE tblWallet SET available_balance = available_balance + ?, updated_at = CURRENT_TIMESTAMP WHERE id = ?";
        String txSql = "INSERT INTO tblPaymentTransaction (id, source_wallet_id, destination_wallet_id, booking_id, type, status, amount, reference_code, created_at) " +
                       "VALUES (?, ?, ?, ?, 'REFUND', 'SUCCESS', ?, ?, CURRENT_TIMESTAMP)";
        String updateDisputeSql = "UPDATE tblDispute SET status = 'APPROVED', resolved_at = CURRENT_TIMESTAMP WHERE id = ?";

        String refCode = "REFUND-" + System.currentTimeMillis();
        UUID txId = UUID.randomUUID();

        try {
            con.setAutoCommit(false);

            // 1. Trừ tiền ví gia sư
            try (PreparedStatement ps = con.prepareStatement(debitSql)) {
                ps.setLong(1, refundAmount);
                ps.setObject(2, tutorWallet.getId());
                ps.setLong(3, refundAmount);
                if (ps.executeUpdate() == 0) {
                    con.rollback(); // Gia sư không đủ tiền hoàn
                    return false;
                }
            }

            // 2. Cộng tiền ví học viên
            try (PreparedStatement ps = con.prepareStatement(creditSql)) {
                ps.setLong(1, refundAmount);
                ps.setObject(2, studentWallet.getId());
                ps.executeUpdate();
            }

            // 3. Ghi transaction REFUND
            try (PreparedStatement ps = con.prepareStatement(txSql)) {
                ps.setObject(1, txId);
                ps.setObject(2, tutorWallet.getId());
                ps.setObject(3, studentWallet.getId());
                ps.setObject(4, bookingId);
                ps.setLong(5, refundAmount);
                ps.setString(6, refCode);
                ps.executeUpdate();
            }

            // 4. Cập nhật Dispute sang APPROVED
            try (PreparedStatement ps = con.prepareStatement(updateDisputeSql)) {
                ps.setObject(1, disputeId);
                ps.executeUpdate();
            }

            con.commit();
            return true;
        } catch (SQLException e) {
            try { con.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            e.printStackTrace();
            return false;
        } finally {
            try { con.setAutoCommit(true); } catch (SQLException ex) { ex.printStackTrace(); }
        }
    }

    public List<PaymentTransaction> findByBooking(UUID bookingId) {
        List<PaymentTransaction> list = new ArrayList<>();
        String sql = "SELECT * FROM tblPaymentTransaction WHERE booking_id = ? ORDER BY created_at DESC";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setObject(1, bookingId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new PaymentTransaction(
                        (UUID) rs.getObject("id"),
                        (UUID) rs.getObject("source_wallet_id"),
                        (UUID) rs.getObject("destination_wallet_id"),
                        (UUID) rs.getObject("booking_id"),
                        rs.getString("type"),
                        rs.getString("status"),
                        rs.getLong("amount"),
                        rs.getString("reference_code"),
                        rs.getObject("balance_after") != null ? rs.getLong("balance_after") : null,
                        rs.getTimestamp("created_at")
                    ));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
}
