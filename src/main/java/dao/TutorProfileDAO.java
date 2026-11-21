package dao;

import java.sql.*;
import java.util.UUID;
import model.TutorProfile;

public class TutorProfileDAO extends DAO {

    public TutorProfileDAO() {
        super();
    }

    public TutorProfile findByTutorId(UUID tutorId) {
        String sql = "SELECT * FROM tblTutorProfile WHERE user_id = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setObject(1, tutorId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new TutorProfile(
                        (UUID) rs.getObject("user_id"),
                        rs.getString("bio"),
                        rs.getString("subjects_can_teach"),
                        rs.getDouble("rating"),
                        rs.getTimestamp("created_at"),
                        rs.getTimestamp("updated_at")
                    );
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean insert(TutorProfile profile) {
        String sql = "INSERT INTO tblTutorProfile (user_id, bio, subjects_can_teach, rating, created_at) " +
                     "VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setObject(1, profile.getUserId());
            ps.setString(2, profile.getBio());
            ps.setString(3, profile.getSubjectsCanTeach());
            ps.setDouble(4, profile.getRating() > 0 ? profile.getRating() : 5.0);
            ps.setTimestamp(5, profile.getCreatedAt() != null ? profile.getCreatedAt() : new Timestamp(System.currentTimeMillis()));

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
