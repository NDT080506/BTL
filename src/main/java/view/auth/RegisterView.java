package view.auth;

import dao.UserDAO;
import dao.WalletDAO;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;
import model.User;
import model.Wallet;

public class RegisterView extends JFrame implements ActionListener {
    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private JTextField txtDisplayName;
    private JTextField txtEmail;
    private JComboBox<String> cmbRole;
    private JButton btnSubmit;
    private JButton btnBack;
    private JLabel lblMessage;

    private UserDAO userDAO;
    private WalletDAO walletDAO;

    public RegisterView() {
        super("TutorTech - Đăng Ký Tài Khoản");
        userDAO = new UserDAO();
        walletDAO = new WalletDAO();
        initComponents();
    }

    private void initComponents() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(460, 420);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel mainPanel = new JPanel(new BorderLayout(15, 15));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 25, 20, 25));

        // Header
        JLabel lblTitle = new JLabel("ĐĂNG KÝ TÀI KHOẢN MỚI", JLabel.CENTER);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblTitle.setForeground(new Color(24, 119, 242));
        mainPanel.add(lblTitle, BorderLayout.NORTH);

        // Form
        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Tên đăng nhập
        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0.35;
        formPanel.add(new JLabel("Tên đăng nhập (*):"), gbc);
        txtUsername = new JTextField(15);
        gbc.gridx = 1; gbc.gridy = 0; gbc.weightx = 0.65;
        formPanel.add(txtUsername, gbc);

        // Mật khẩu
        gbc.gridx = 0; gbc.gridy = 1;
        formPanel.add(new JLabel("Mật khẩu (*):"), gbc);
        txtPassword = new JPasswordField(15);
        gbc.gridx = 1; gbc.gridy = 1;
        formPanel.add(txtPassword, gbc);

        // Họ và tên
        gbc.gridx = 0; gbc.gridy = 2;
        formPanel.add(new JLabel("Tên hiển thị (*):"), gbc);
        txtDisplayName = new JTextField(15);
        gbc.gridx = 1; gbc.gridy = 2;
        formPanel.add(txtDisplayName, gbc);

        // Email
        gbc.gridx = 0; gbc.gridy = 3;
        formPanel.add(new JLabel("Email liên hệ:"), gbc);
        txtEmail = new JTextField(15);
        gbc.gridx = 1; gbc.gridy = 3;
        formPanel.add(txtEmail, gbc);

        // Vai trò
        gbc.gridx = 0; gbc.gridy = 4;
        formPanel.add(new JLabel("Vai trò tham gia:"), gbc);
        cmbRole = new JComboBox<>(new String[]{"Học viên (STUDENT)", "Gia sư (TUTOR)"});
        gbc.gridx = 1; gbc.gridy = 4;
        formPanel.add(cmbRole, gbc);

        // Thông báo lỗi
        lblMessage = new JLabel("", JLabel.CENTER);
        lblMessage.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        lblMessage.setForeground(Color.RED);
        gbc.gridx = 0; gbc.gridy = 5; gbc.gridwidth = 2;
        formPanel.add(lblMessage, gbc);

        mainPanel.add(formPanel, BorderLayout.CENTER);

        // Buttons
        JPanel buttonPanel = new JPanel(new GridLayout(1, 2, 15, 0));
        btnSubmit = new JButton("Tạo tài khoản");
        btnSubmit.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnSubmit.setBackground(new Color(40, 167, 69));
        btnSubmit.setForeground(Color.WHITE);
        btnSubmit.setFocusPainted(false);
        btnSubmit.addActionListener(this);

        btnBack = new JButton("Quay lại đăng nhập");
        btnBack.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        btnBack.setFocusPainted(false);
        btnBack.addActionListener(this);

        buttonPanel.add(btnSubmit);
        buttonPanel.add(btnBack);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(mainPanel);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == btnSubmit) {
            String username = txtUsername.getText().trim();
            String password = new String(txtPassword.getPassword()).trim();
            String displayName = txtDisplayName.getText().trim();
            String email = txtEmail.getText().trim();
            String selectedRole = cmbRole.getSelectedIndex() == 0 ? "STUDENT" : "TUTOR";

            // Kiểm tra dữ liệu bắt buộc
            if (username.isEmpty() || password.isEmpty() || displayName.isEmpty()) {
                lblMessage.setText("Vui lòng điền đầy đủ các thông tin có dấu (*)");
                return;
            }

            // Kiểm tra username đã tồn tại chưa
            if (userDAO.isUsernameExists(username)) {
                lblMessage.setText("Tên đăng nhập đã được sử dụng! Vui lòng chọn tên khác.");
                return;
            }

            // Tạo đối tượng User
            User newUser = new User(username, password, displayName, email.isEmpty() ? null : email, selectedRole);
            boolean success = userDAO.insert(newUser);

            if (success) {
                // Tự động khởi tạo ví rỗng cho người dùng mới
                walletDAO.insert(new Wallet(newUser.getId(), 0));

                JOptionPane.showMessageDialog(this, 
                    "Đăng ký tài khoản thành công!\nBạn có thể đăng nhập ngay bây giờ.", 
                    "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                this.dispose();
                new LoginView().setVisible(true);
            } else {
                lblMessage.setText("Đăng ký không thành công do lỗi hệ thống!");
            }
        } else if (e.getSource() == btnBack) {
            this.dispose();
            new LoginView().setVisible(true);
        }
    }
}
