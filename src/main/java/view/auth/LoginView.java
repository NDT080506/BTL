package view.auth;

import dao.UserDAO;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;
import model.User;
import view.admin.AdminHomeView;
import view.student.StudentHomeView;
import view.tutor.TutorHomeView;

/**
 * LoginView: Màn hình Đăng nhập (CN01.04)
 * Kế thừa JFrame, implement ActionListener
 */
public class LoginView extends JFrame implements ActionListener {
    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private JButton btnLogin;
    private JButton btnRegister;
    private JLabel lblMessage;

    private UserDAO userDAO;

    public LoginView() {
        super("TutorTech - Đăng Nhập Hệ Thống");
        userDAO = new UserDAO();
        initComponents();
    }

    private void initComponents() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(420, 320);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel mainPanel = new JPanel(new BorderLayout(15, 15));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 25, 20, 25));

        // Header Title
        JLabel lblTitle = new JLabel("TUTORTECH", JLabel.CENTER);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTitle.setForeground(new Color(24, 119, 242));
        JLabel lblSubtitle = new JLabel("Nền tảng kết nối gia sư và học tập trực tuyến", JLabel.CENTER);
        lblSubtitle.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblSubtitle.setForeground(Color.GRAY);

        JPanel headerPanel = new JPanel(new GridLayout(2, 1, 3, 3));
        headerPanel.add(lblTitle);
        headerPanel.add(lblSubtitle);
        mainPanel.add(headerPanel, BorderLayout.NORTH);

        // Form Fields
        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel lblUsername = new JLabel("Tên đăng nhập:");
        lblUsername.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0.3;
        formPanel.add(lblUsername, gbc);

        txtUsername = new JTextField(15);
        txtUsername.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        gbc.gridx = 1; gbc.gridy = 0; gbc.weightx = 0.7;
        formPanel.add(txtUsername, gbc);

        JLabel lblPassword = new JLabel("Mật khẩu:");
        lblPassword.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0.3;
        formPanel.add(lblPassword, gbc);

        txtPassword = new JPasswordField(15);
        txtPassword.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        gbc.gridx = 1; gbc.gridy = 1; gbc.weightx = 0.7;
        formPanel.add(txtPassword, gbc);

        lblMessage = new JLabel("", JLabel.CENTER);
        lblMessage.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        lblMessage.setForeground(Color.RED);
        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2;
        formPanel.add(lblMessage, gbc);

        mainPanel.add(formPanel, BorderLayout.CENTER);

        // Buttons
        JPanel buttonPanel = new JPanel(new GridLayout(1, 2, 15, 0));
        btnLogin = new JButton("Đăng nhập");
        btnLogin.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnLogin.setBackground(new Color(24, 119, 242));
        btnLogin.setForeground(Color.WHITE);
        btnLogin.setFocusPainted(false);
        btnLogin.addActionListener(this);

        btnRegister = new JButton("Đăng ký mới");
        btnRegister.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        btnRegister.setFocusPainted(false);
        btnRegister.addActionListener(this);

        buttonPanel.add(btnLogin);
        buttonPanel.add(btnRegister);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        // Gợi ý tài khoản mẫu
        txtUsername.setText("student01");
        txtPassword.setText("123456");

        add(mainPanel);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == btnLogin) {
            String username = txtUsername.getText().trim();
            String password = new String(txtPassword.getPassword());

            if (username.isEmpty() || password.isEmpty()) {
                lblMessage.setText("Vui lòng nhập đầy đủ tên đăng nhập và mật khẩu!");
                return;
            }

            User user = userDAO.checkLogin(username, password);
            if (user != null) {
                lblMessage.setText("");
                this.dispose(); // Đóng LoginView

                // Điều hướng giao diện theo vai trò (Role-based Navigation)
                String role = user.getRole();
                if ("ADMIN".equalsIgnoreCase(role)) {
                    new AdminHomeView(user).setVisible(true);
                } else if ("TUTOR".equalsIgnoreCase(role)) {
                    new TutorHomeView(user).setVisible(true);
                } else {
                    new StudentHomeView(user).setVisible(true);
                }
            } else {
                lblMessage.setText("Sai tài khoản, mật khẩu hoặc tài khoản đã bị khóa!");
            }
        } else if (e.getSource() == btnRegister) {
            this.dispose();
            new RegisterView().setVisible(true);
        }
    }
}
