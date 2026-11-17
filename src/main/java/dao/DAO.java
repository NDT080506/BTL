package dao;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Lớp DAO cơ sở (Base DAO)
 * Cung cấp kết nối cơ sở dữ liệu dùng chung (PostgreSQL) theo chuẩn thiết kế PTIT / Blog Software Design.
 * Các DAO nghiệp vụ kế thừa lớp này để dùng chung thuộc tính Connection con.
 */
public class DAO {
    public static Connection con;

    private static String dbUrl = "jdbc:postgresql://localhost:5432/tutortech";
    private static String dbUser = "postgres";
    private static String dbPass = "postgres";

    static {
        loadConfig();
    }

    public DAO() {
        if (con == null) {
            openConnection();
        }
    }

    private static void loadConfig() {
        try (InputStream input = DAO.class.getClassLoader().getResourceAsStream("db.properties")) {
            if (input != null) {
                Properties prop = new Properties();
                prop.load(input);
                if (prop.getProperty("db.url") != null) dbUrl = prop.getProperty("db.url");
                if (prop.getProperty("db.user") != null) dbUser = prop.getProperty("db.user");
                if (prop.getProperty("db.password") != null) dbPass = prop.getProperty("db.password");
            }
        } catch (Exception e) {
            System.out.println("Không tìm thấy db.properties, sử dụng cấu hình mặc định: " + dbUrl);
        }
    }

    public static Connection openConnection() {
        try {
            if (con == null || con.isClosed()) {
                Class.forName("org.postgresql.Driver");
                con = DriverManager.getConnection(dbUrl, dbUser, dbPass);
                System.out.println("Kết nối CSDL PostgreSQL thành công!");
            }
        } catch (ClassNotFoundException e) {
            System.err.println("Chưa tìm thấy Driver PostgreSQL: " + e.getMessage());
        } catch (SQLException e) {
            System.err.println("Lỗi kết nối CSDL PostgreSQL: " + e.getMessage());
        }
        return con;
    }

    public static void closeConnection() {
        try {
            if (con != null && !con.isClosed()) {
                con.close();
                con = null;
                System.out.println("Đã đóng kết nối CSDL.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Cho phép cấu hình lại thông số kết nối khi cần thiết
     */
    public static void setDbConfig(String url, String user, String password) {
        closeConnection();
        dbUrl = url;
        dbUser = user;
        dbPass = password;
        openConnection();
    }
}
