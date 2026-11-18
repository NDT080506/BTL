package view.admin;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;
import model.User;
import view.auth.LoginFrm;

/**
 * AdminHomeFrm: Cổng quản trị hệ thống TutorTech (Admin Portal)
 */
public class AdminHomeFrm extends JFrame implements ActionListener {
    private User currentAdmin;

    private JButton btnUsers;
    private JButton btnApplications;
    private JButton btnPackages;
    private JButton btnDisputes;
    private JButton btnWithdrawals;
    private JButton btnAuditLogs;
    private JButton btnLogout;

    public AdminHomeFrm(User admin) {
        super("TutorTech - Cổng Quản Trị Hệ Thống (Administrator: " + admin.getDisplayName() + ")");
        this.currentAdmin = admin;

        initComponents();
    }

    private void initComponents() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(720, 500);
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new BorderLayout(15, 15));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(230, 240, 255));
        headerPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(180, 210, 255), 1),
            BorderFactory.createEmptyBorder(12, 15, 12, 15)
        ));

        JLabel lblTitle = new JLabel("BẢNG ĐIỀU KHIỂN QUẢN TRỊ VIÊN");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 17));
        lblTitle.setForeground(new Color(24, 119, 242));

        JLabel lblAdminInfo = new JLabel("Admin: " + currentAdmin.getDisplayName() + " (" + currentAdmin.getUsername() + ")");
        lblAdminInfo.setFont(new Font("Segoe UI", Font.PLAIN, 13));

        headerPanel.add(lblTitle, BorderLayout.WEST);
        headerPanel.add(lblAdminInfo, BorderLayout.EAST);
        mainPanel.add(headerPanel, BorderLayout.NORTH);

        // Grid Menu (6 Chức năng quản trị)
        JPanel menuPanel = new JPanel(new GridLayout(3, 2, 15, 15));
        menuPanel.setBorder(BorderFactory.createTitledBorder("Các phân hệ quản trị nghiệp vụ"));

        btnUsers = createMenuButton("1. Quản lý người dùng & Trạng thái tài khoản", "Tra cứu tài khoản, Khóa hoặc Mở khóa tài khoản người dùng");
        btnApplications = createMenuButton("2. Xét duyệt hồ sơ ứng tuyển gia sư", "Duyệt hồ sơ PENDING, thăng cấp TUTOR và tạo TutorProfile");
        btnPackages = createMenuButton("3. Tra cứu danh sách gói dịch vụ gia sư", "Xem danh sách và chi tiết các gói kết nối gia sư đang cung cấp");
        btnDisputes = createMenuButton("4. Xử lý khiếu nại & Hoàn tiền học phí", "Thẩm tra bằng chứng, chuyển hoàn tiền trực tiếp từ ví gia sư");
        btnWithdrawals = createMenuButton("5. Tra cứu yêu cầu rút tiền", "Theo dõi thông tin rút tiền, tài khoản ngân hàng đã che số");
        btnAuditLogs = createMenuButton("6. Tra cứu nhật ký thao tác kiểm toán", "Xem lịch sử thay đổi dữ liệu, người thao tác và thời điểm");

        menuPanel.add(btnUsers);
        menuPanel.add(btnApplications);
        menuPanel.add(btnPackages);
        menuPanel.add(btnDisputes);
        menuPanel.add(btnWithdrawals);
        menuPanel.add(btnAuditLogs);

        mainPanel.add(menuPanel, BorderLayout.CENTER);

        // Footer
        JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnLogout = new JButton("Đăng xuất");
        btnLogout.setBackground(new Color(220, 53, 69));
        btnLogout.setForeground(Color.WHITE);
        btnLogout.setFocusPainted(false);
        btnLogout.addActionListener(this);
        footerPanel.add(btnLogout);

        mainPanel.add(footerPanel, BorderLayout.SOUTH);

        add(mainPanel);
    }

    private JButton createMenuButton(String title, String desc) {
        JButton btn = new JButton("<html><center><b>" + title + "</b><br><font color='gray' size='-1'>" + desc + "</font></center></html>");
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.addActionListener(this);
        return btn;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == btnUsers) {
            new AdminUserManagementFrm(this, currentAdmin).setVisible(true);
        } else if (e.getSource() == btnApplications) {
            new AdminApplicationReviewFrm(this, currentAdmin).setVisible(true);
        } else if (e.getSource() == btnPackages) {
            new AdminPackageInquiryFrm(this).setVisible(true);
        } else if (e.getSource() == btnDisputes) {
            new AdminDisputeReviewFrm(this, currentAdmin).setVisible(true);
        } else if (e.getSource() == btnWithdrawals) {
            new AdminWithdrawalInquiryFrm(this).setVisible(true);
        } else if (e.getSource() == btnAuditLogs) {
            new AdminActionLogInquiryFrm(this).setVisible(true);
        } else if (e.getSource() == btnLogout) {
            int confirm = JOptionPane.showConfirmDialog(
                this,
                "Bạn có chắc chắn muốn đăng xuất?",
                "Xác nhận",
                JOptionPane.YES_NO_OPTION
            );
            if (confirm == JOptionPane.YES_OPTION) {
                currentAdmin = null;
                this.dispose();
                new LoginFrm().setVisible(true);
            }
        }
    }
}
