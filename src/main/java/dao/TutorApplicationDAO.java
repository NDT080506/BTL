package dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import model.TutorApplication;

/**
 * TutorApplicationDAO: Thao tác hồ sơ ứng tuyển gia sư
 * Phục vụ: CN02.13, CN06.02
 */
public class TutorApplicationDAO extends DAO {

    public TutorApplicationDAO() {
        super();
    }

    public TutorApplication findByUserId(UUID userId) {
        String sql = "SELECT * FROM tblTutorApplication WHERE user_id = ? ORDER BY created_at DESC LIMIT 1";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setObject(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToApplication(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<TutorApplication> findPending() {
        List<TutorApplication> list = new ArrayList<>();
        String sql = "SELECT * FROM tblTutorApplication WHERE status = 'PENDING' ORDER BY created_at ASC";
        try (PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(mapResultSetToApplication(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public TutorApplication findById(UUID id) {
        String sql = "SELECT * FROM tblTutorApplication WHERE id = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setObject(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToApplication(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean insert(TutorApplication app) {
        String sql = "INSERT INTO tblTutorApplication (id, user_id, bio, subjects_can_teach, cv_url, test_submission, status, created_at) " +
                     "VALUES (?, ?, ?, ?, ?, ?, 'PENDING', CURRENT_TIMESTAMP)";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            if (app.getId() == null) app.setId(UUID.randomUUID());

            ps.setObject(1, app.getId());
            ps.setObject(2, app.getUserId());
            ps.setString(3, app.getBio());
            ps.setString(4, app.getSubjectsCanTeach());
            ps.setString(5, app.getCvUrl());
            ps.setString(6, app.getTestSubmission());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean deletePending(UUID id) {
        String sql = "DELETE FROM tblTutorApplication WHERE id = ? AND status = 'PENDING'";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setObject(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateStatus(UUID id, String status, String adminNote) {
        String sql = "UPDATE tblTutorApplication SET status = ?, admin_note = ?, updated_at = CURRENT_TIMESTAMP WHERE id = ?";
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

    private TutorApplication mapResultSetToApplication(ResultSet rs) throws SQLException {
        return new TutorApplication(
            (UUID) rs.getObject("id"),
            (UUID) rs.getObject("user_id"),
            rs.getString("bio"),
            rs.getString("subjects_can_teach"),
            rs.getString("cv_url"),
            rs.getString("test_submission"),
            rs.getString("status"),
            rs.getString("admin_note"),
            rs.getTimestamp("created_at"),
            rs.getTimestamp("updated_at")
        );
    }
}
