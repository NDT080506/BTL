package dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import model.Dispute;

public class DisputeDAO extends DAO {

    public DisputeDAO() {
        super();
    }

    public List<Dispute> findPending() {
        List<Dispute> list = new ArrayList<>();
        String sql = "SELECT * FROM tblDispute WHERE status = 'PENDING' ORDER BY created_at ASC";
        try (PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(mapResultSetToDispute(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public Dispute findById(UUID id) {
        String sql = "SELECT * FROM tblDispute WHERE id = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setObject(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToDispute(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean insert(Dispute d) {
        String sql = "INSERT INTO tblDispute (id, booking_id, created_by_id, reason, evidence_url, status, refund_amount, created_at) " +
                     "VALUES (?, ?, ?, ?, ?, 'PENDING', ?, CURRENT_TIMESTAMP)";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            if (d.getId() == null) d.setId(UUID.randomUUID());

            ps.setObject(1, d.getId());
            ps.setObject(2, d.getBookingId());
            ps.setObject(3, d.getCreatedById());
            ps.setString(4, d.getReason());
            ps.setString(5, d.getEvidenceUrl());
            ps.setLong(6, d.getRefundAmount());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateStatus(UUID id, String status, String adminNote) {
        String sql = "UPDATE tblDispute SET status = ?, admin_note = ?, resolved_at = CURRENT_TIMESTAMP WHERE id = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, status);
            ps.setString(2, adminNote);
            ps.setObject(3, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    private Dispute mapResultSetToDispute(ResultSet rs) throws SQLException {
        return new Dispute(
            (UUID) rs.getObject("id"),
            (UUID) rs.getObject("booking_id"),
            (UUID) rs.getObject("created_by_id"),
            rs.getString("reason"),
            rs.getString("evidence_url"),
            rs.getString("status"),
            rs.getLong("refund_amount"),
            rs.getString("admin_note"),
            rs.getTimestamp("created_at"),
            rs.getTimestamp("resolved_at")
        );
    }
}
