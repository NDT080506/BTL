package dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import model.BrokeragePackage;

/**
 * BrokeragePackageDAO: Thao tác dữ liệu bảng tblBrokeragePackage
 * Phục vụ: CN02.03, CN02.07, CN06.03
 */
public class BrokeragePackageDAO extends DAO {

    public BrokeragePackageDAO() {
        super();
    }

    public List<BrokeragePackage> findActiveByTutorId(UUID tutorId) {
        List<BrokeragePackage> list = new ArrayList<>();
        String sql = "SELECT * FROM tblBrokeragePackage WHERE tutor_id = ? AND active = TRUE ORDER BY price ASC";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setObject(1, tutorId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetToPackage(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public BrokeragePackage findById(UUID id) {
        String sql = "SELECT * FROM tblBrokeragePackage WHERE id = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setObject(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToPackage(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<BrokeragePackage> findAll(String keyword, Boolean activeOnly) {
        List<BrokeragePackage> list = new ArrayList<>();
        StringBuilder sql = new StringBuilder("SELECT * FROM tblBrokeragePackage WHERE 1=1 ");
        List<Object> params = new ArrayList<>();

        if (keyword != null && !keyword.trim().isEmpty()) {
            sql.append("AND LOWER(name) LIKE ? ");
            params.add("%" + keyword.trim().toLowerCase() + "%");
        }
        if (activeOnly != null) {
            sql.append("AND active = ? ");
            params.add(activeOnly);
        }

        sql.append("ORDER BY created_at DESC");

        try (PreparedStatement ps = con.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetToPackage(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public boolean insert(BrokeragePackage pkg) {
        String sql = "INSERT INTO tblBrokeragePackage (id, tutor_id, name, description, price, active, created_at) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            if (pkg.getId() == null) pkg.setId(UUID.randomUUID());
            if (pkg.getCreatedAt() == null) pkg.setCreatedAt(new Timestamp(System.currentTimeMillis()));

            ps.setObject(1, pkg.getId());
            ps.setObject(2, pkg.getTutorId());
            ps.setString(3, pkg.getName());
            ps.setString(4, pkg.getDescription());
            ps.setLong(5, pkg.getPrice());
            ps.setBoolean(6, pkg.isActive());
            ps.setTimestamp(7, pkg.getCreatedAt());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    private BrokeragePackage mapResultSetToPackage(ResultSet rs) throws SQLException {
        return new BrokeragePackage(
            (UUID) rs.getObject("id"),
            (UUID) rs.getObject("tutor_id"),
            rs.getString("name"),
            rs.getString("description"),
            rs.getLong("price"),
            rs.getBoolean("active"),
            rs.getTimestamp("created_at"),
            rs.getTimestamp("updated_at")
        );
    }
}
