import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import dao.DAO;
import view.auth.LoginView;

public class Main {
    public static void main(String[] args) {

        try {
            UIManager.setLookAndFeel("com.formdev.flatlaf.FlatLightLaf");
        } catch (Exception e) {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ignored) {
            }
        }

        SwingUtilities.invokeLater(() -> {
            DAO.openConnection();
            LoginView loginView = new LoginView();
            loginView.setVisible(true);
        });
    }
}
