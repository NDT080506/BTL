package dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import model.User;

/**
 * UserDAO: Thao tác dữ liệu bảng tblUser
 * Phục vụ: CN01.03, CN01.04, CN02.03, CN06.01, CN06.02
 */
public class UserDAO extends DAO {

    public UserDAO() {
        super();
    }

    /**
     * Kiểm tra username đã tồn tại chưa (CN01.03)
     */
    public boolean isUsernameExists(String username) {
        String sql = "SELECT 1 FROM tblUser WHERE username = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Đăng ký tài khoản mới (CN01.03)
     */
    public boolean insert(User user) {
        String sql = "INSERT INTO tblUser (id, username, password, email, display_name, role, status, phone, created_at) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            if (user.getId() == null) user.setId(UUID.randomUUID());
            if (user.getCreatedAt() == null) user.setCreatedAt(new Timestamp(System.currentTimeMillis()));

            ps.setObject(1, user.getId());
            ps.setString(2, user.getUsername());
            ps.setString(3, user.getPassword());
            ps.setString(4, user.getEmail());
            ps.setString(5, user.getDisplayName());
            ps.setString(6, user.getRole());
            ps.setString(7, user.getStatus() != null ? user.getStatus() : "ACTIVE");
            ps.setString(8, user.getPhone());
            ps.setTimestamp(9, user.getCreatedAt());

            int rows = ps.executeUpdate();
            return rows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Kiểm tra đăng nhập (CN01.04)
     * Trả về đối tượng User nếu đúng mật khẩu và tài khoản không bị khóa, ngược lại trả về null.
     */
    public User checkLogin(String username, String password) {
        String sql = "SELECT * FROM tblUser WHERE username = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    String passwordInDb = rs.getString("password");
                    String status = rs.getString("status");
                    
                    // Nếu tài khoản bị khóa thì không cho đăng nhập
                    if ("LOCKED".equalsIgnoreCase(status)) {
                        return null;
                    }

                    // Đối chiếu mật khẩu
                    if (passwordInDb != null && passwordInDb.equals(password)) {
                        return mapResultSetToUser(rs);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Lấy User theo ID
     */
    public User findById(UUID id) {
        String sql = "SELECT * FROM tblUser WHERE id = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setObject(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToUser(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Lấy thông tin tóm tắt của gia sư theo ID (theo đúng tên phương thức trong Sequence Diagram CN02.03)
     */
    public User findTutorSummary(UUID tutorId) {
        return findById(tutorId);
    }

    /**
     * Lấy thông tin tóm tắt của học viên theo ID (theo đúng tên phương thức trong Sequence Diagram CN04.01)
     */
    public User findStudentSummary(UUID studentId) {
        return findById(studentId);
    }

    /**
     * Tìm kiếm danh sách gia sư theo từ khóa / môn học (CN02.03)
     */
    public List<User> searchTutors(String keyword, String subject) {
        List<User> list = new ArrayList<>();
        StringBuilder sql = new StringBuilder(
            "SELECT DISTINCT u.* FROM tblUser u " +
            "LEFT JOIN tblTutorProfile tp ON u.id = tp.user_id " +
            "WHERE u.role = 'TUTOR' AND u.status = 'ACTIVE' "
        );

        List<String> params = new ArrayList<>();
        if (keyword != null && !keyword.trim().isEmpty()) {
            sql.append("AND (LOWER(u.display_name) LIKE ? OR LOWER(tp.bio) LIKE ?) ");
            params.add("%" + keyword.trim().toLowerCase() + "%");
            params.add("%" + keyword.trim().toLowerCase() + "%");
        }
        if (subject != null && !subject.trim().isEmpty()) {
            sql.append("AND LOWER(tp.subjects_can_teach) LIKE ? ");
            params.add("%" + subject.trim().toLowerCase() + "%");
        }

        try (PreparedStatement ps = con.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) {
                ps.setString(i + 1, params.get(i));
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetToUser(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * Quản trị: Lấy danh sách người dùng kèm lọc (CN06.01)
     */
    public List<User> findAll(String keyword, String role, String status) {
        List<User> list = new ArrayList<>();
        StringBuilder sql = new StringBuilder("SELECT * FROM tblUser WHERE 1=1 ");
        List<Object> params = new ArrayList<>();

        if (keyword != null && !keyword.trim().isEmpty()) {
            sql.append("AND (LOWER(username) LIKE ? OR LOWER(display_name) LIKE ? OR LOWER(email) LIKE ?) ");
            String kw = "%" + keyword.trim().toLowerCase() + "%";
            params.add(kw);
            params.add(kw);
            params.add(kw);
        }
        if (role != null && !role.trim().isEmpty() && !"ALL".equalsIgnoreCase(role)) {
            sql.append("AND role = ? ");
            params.add(role.trim());
        }
        if (status != null && !status.trim().isEmpty() && !"ALL".equalsIgnoreCase(status)) {
            sql.append("AND status = ? ");
            params.add(status.trim());
        }

        sql.append("ORDER BY created_at DESC");

        try (PreparedStatement ps = con.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetToUser(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * Cập nhật trạng thái người dùng (CN06.01: Khóa / Mở khóa)
     */
    public boolean updateStatus(UUID userId, String newStatus) {
        String sql = "UPDATE tblUser SET status = ?, updated_at = CURRENT_TIMESTAMP WHERE id = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, newStatus);
            ps.setObject(2, userId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Cập nhật vai trò người dùng (CN06.02: Nâng cấp thành TUTOR)
     */
    public boolean updateRole(UUID userId, String newRole) {
        String sql = "UPDATE tblUser SET role = ?, updated_at = CURRENT_TIMESTAMP WHERE id = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, newRole);
            ps.setObject(2, userId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    private User mapResultSetToUser(ResultSet rs) throws SQLException {
        return new User(
            (UUID) rs.getObject("id"),
            rs.getString("username"),
            rs.getString("password"),
            rs.getString("email"),
            rs.getString("display_name"),
            rs.getString("role"),
            rs.getString("status"),
            rs.getString("phone"),
            rs.getTimestamp("created_at"),
            rs.getTimestamp("updated_at")
        );
    }
}
