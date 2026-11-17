package dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import model.ActionLog;

/**
 * ActionLogDAO: Thao tác nhật ký hệ thống
 * Phục vụ: CN06.07
 */
public class ActionLogDAO extends DAO {

    public ActionLogDAO() {
        super();
    }

    public boolean insert(ActionLog log) {
        String sql = "INSERT INTO tblActionLog (id, user_id, action, target_type, target_id, ip_address, user_agent, created_at) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, CURRENT_TIMESTAMP)";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            if (log.getId() == null) log.setId(UUID.randomUUID());

            ps.setObject(1, log.getId());
            ps.setObject(2, log.getUserId());
            ps.setString(3, log.getAction());
            ps.setString(4, log.getTargetType());
            ps.setObject(5, log.getTargetId());
            ps.setString(6, log.getIpAddress() != null ? log.getIpAddress() : "127.0.0.1");
            ps.setString(7, log.getUserAgent() != null ? log.getUserAgent() : "TutorTech Client");

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<ActionLog> search(String action, String targetType) {
        List<ActionLog> list = new ArrayList<>();
        StringBuilder sql = new StringBuilder("SELECT * FROM tblActionLog WHERE 1=1 ");
        List<Object> params = new ArrayList<>();

        if (action != null && !action.trim().isEmpty() && !"ALL".equalsIgnoreCase(action)) {
            sql.append("AND action = ? ");
            params.add(action.trim());
        }
        if (targetType != null && !targetType.trim().isEmpty() && !"ALL".equalsIgnoreCase(targetType)) {
            sql.append("AND target_type = ? ");
            params.add(targetType.trim());
        }

        sql.append("ORDER BY created_at DESC LIMIT 100");

        try (PreparedStatement ps = con.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetToLog(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public ActionLog findById(UUID id) {
        String sql = "SELECT * FROM tblActionLog WHERE id = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setObject(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToLog(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    private ActionLog mapResultSetToLog(ResultSet rs) throws SQLException {
        return new ActionLog(
            (UUID) rs.getObject("id"),
            (UUID) rs.getObject("user_id"),
            rs.getString("action"),
            rs.getString("target_type"),
            (UUID) rs.getObject("target_id"),
            rs.getString("ip_address"),
            rs.getString("user_agent"),
            rs.getTimestamp("created_at")
        );
    }
}
