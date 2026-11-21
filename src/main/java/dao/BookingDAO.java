package dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import model.Booking;

public class BookingDAO extends DAO {

    public BookingDAO() {
        super();
    }

    public boolean isScheduleAvailable(UUID tutorId, Timestamp scheduledAt, int durationMinutes) {

        long startMs = scheduledAt.getTime();
        long endMs = startMs + (long) durationMinutes * 60 * 1000;
        Timestamp scheduledEnd = new Timestamp(endMs);

        String sql = "SELECT 1 FROM tblBooking " +
                "WHERE tutor_id = ? " +
                "AND status IN ('PENDING_TUTOR_CONFIRMATION', 'ACCEPTED', 'PAID') " +
                "AND ((scheduled_at <= ? AND (scheduled_at + (duration_minutes * INTERVAL '1 minute')) > ?) " +
                "  OR (scheduled_at < ? AND scheduled_at >= ?))";

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setObject(1, tutorId);
            ps.setTimestamp(2, scheduledAt);
            ps.setTimestamp(3, scheduledAt);
            ps.setTimestamp(4, scheduledEnd);
            ps.setTimestamp(5, scheduledAt);

            try (ResultSet rs = ps.executeQuery()) {

                return !rs.next();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean insert(Booking booking) {
        String sql = "INSERT INTO tblBooking (id, student_id, tutor_id, package_id, scheduled_at, " +
                "duration_minutes, note, status, total_price, created_at) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            if (booking.getId() == null)
                booking.setId(UUID.randomUUID());
            if (booking.getCreatedAt() == null)
                booking.setCreatedAt(new Timestamp(System.currentTimeMillis()));
            if (booking.getStatus() == null)
                booking.setStatus("PENDING_TUTOR_CONFIRMATION");

            ps.setObject(1, booking.getId());
            ps.setObject(2, booking.getStudentId());
            ps.setObject(3, booking.getTutorId());
            ps.setObject(4, booking.getPackageId());
            ps.setTimestamp(5, booking.getScheduledAt());
            ps.setInt(6, booking.getDurationMinutes());
            ps.setString(7, booking.getNote());
            ps.setString(8, booking.getStatus());
            ps.setLong(9, booking.getTotalPrice());
            ps.setTimestamp(10, booking.getCreatedAt());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<Booking> findPendingByTutor(UUID tutorId) {
        List<Booking> list = new ArrayList<>();
        String sql = "SELECT * FROM tblBooking WHERE tutor_id = ? AND status = 'PENDING_TUTOR_CONFIRMATION' " +
                "ORDER BY scheduled_at ASC";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setObject(1, tutorId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetToBooking(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<Booking> findByStudent(UUID studentId) {
        List<Booking> list = new ArrayList<>();
        String sql = "SELECT * FROM tblBooking WHERE student_id = ? ORDER BY scheduled_at DESC";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setObject(1, studentId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetToBooking(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public Booking findPayable(UUID bookingId) {
        String sql = "SELECT * FROM tblBooking WHERE id = ? AND status = 'ACCEPTED'";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setObject(1, bookingId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToBooking(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Booking findById(UUID id) {
        String sql = "SELECT * FROM tblBooking WHERE id = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setObject(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToBooking(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean updateResponse(UUID bookingId, String newStatus) {
        String sql = "UPDATE tblBooking SET status = ?, updated_at = CURRENT_TIMESTAMP " +
                "WHERE id = ? AND status = 'PENDING_TUTOR_CONFIRMATION'";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, newStatus);
            ps.setObject(2, bookingId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateStatus(UUID bookingId, String newStatus) {
        String sql = "UPDATE tblBooking SET status = ?, updated_at = CURRENT_TIMESTAMP WHERE id = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, newStatus);
            ps.setObject(2, bookingId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    private Booking mapResultSetToBooking(ResultSet rs) throws SQLException {
        return new Booking(
                (UUID) rs.getObject("id"),
                (UUID) rs.getObject("student_id"),
                (UUID) rs.getObject("tutor_id"),
                (UUID) rs.getObject("package_id"),
                rs.getTimestamp("scheduled_at"),
                rs.getInt("duration_minutes"),
                rs.getString("note"),
                rs.getString("status"),
                rs.getLong("total_price"),
                rs.getTimestamp("created_at"),
                rs.getTimestamp("updated_at"));
    }
}
