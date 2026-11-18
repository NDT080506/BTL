import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import dao.DAO;
import view.auth.LoginView;

/**
 * Entry Point của ứng dụng TutorTech
 */
public class Main {
    public static void main(String[] args) {
        // Thiết lập giao diện hiện đại nếu có FlatLaf, nếu không thì dùng System Look & Feel
        try {
            UIManager.setLookAndFeel("com.formdev.flatlaf.FlatLightLaf");
        } catch (Exception e) {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ignored) {
            }
        }

        // Khởi động kết nối CSDL và mở màn hình Đăng nhập
        SwingUtilities.invokeLater(() -> {
            DAO.openConnection();
            LoginView loginView = new LoginView();
            loginView.setVisible(true);
        });
    }
}
