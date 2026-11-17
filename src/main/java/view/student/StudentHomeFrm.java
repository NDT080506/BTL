package view.student;

import dao.WalletDAO;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;
import model.User;
import model.Wallet;
import view.auth.LoginFrm;

/**
 * StudentHomeFrm: Màn hình chính của Học viên
 */
public class StudentHomeFrm extends JFrame implements ActionListener {
    private User currentUser;
    private Wallet userWallet;
    private WalletDAO walletDAO;

    private JLabel lblWelcome;
    private JLabel lblBalance;
    private JButton btnSearchTutors;
    private JButton btnMyBookings;
    private JButton btnTopUpWallet;
    private JButton btnApplyTutor;
    private JButton btnRefresh;
    private JButton btnLogout;

    public StudentHomeFrm(User user) {
        super("TutorTech - Học Viên: " + user.getDisplayName());
        this.currentUser = user;
        this.walletDAO = new WalletDAO();
        initComponents();
        loadWalletData();
    }

    private void initComponents() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(650, 420);
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new BorderLayout(15, 15));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Header Panel
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(240, 245, 255));
        headerPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 220, 255), 1),
            BorderFactory.createEmptyBorder(12, 15, 12, 15)
        ));

        lblWelcome = new JLabel("Xin chào, " + currentUser.getDisplayName() + " (Học viên)");
        lblWelcome.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblWelcome.setForeground(new Color(24, 119, 242));

        lblBalance = new JLabel("Số dư ví: Đang tải...");
        lblBalance.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblBalance.setForeground(new Color(40, 167, 69));

        JPanel userStatusPanel = new JPanel(new GridLayout(2, 1, 3, 3));
        userStatusPanel.setOpaque(false);
        userStatusPanel.add(lblWelcome);
        userStatusPanel.add(lblBalance);

        btnRefresh = new JButton("Làm mới ví");
        btnRefresh.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        btnRefresh.addActionListener(this);

        headerPanel.add(userStatusPanel, BorderLayout.WEST);
        headerPanel.add(btnRefresh, BorderLayout.EAST);
        mainPanel.add(headerPanel, BorderLayout.NORTH);

        // Menu Actions Panel (Grid)
        JPanel menuPanel = new JPanel(new GridLayout(2, 2, 15, 15));
        menuPanel.setBorder(BorderFactory.createTitledBorder("Chức năng học tập & dịch vụ"));

        btnSearchTutors = createMenuButton("1. Tìm kiếm gia sư & Gói học", "Tra cứu hồ sơ gia sư, đánh giá và các gói kết nối");
        btnMyBookings = createMenuButton("2. Lịch học & Thanh toán học phí", "Theo dõi trạng thái lịch đặt và thanh toán trực tiếp vào ví gia sư");
        btnTopUpWallet = createMenuButton("3. Nạp tiền vào ví", "Nạp số dư khả dụng qua Cổng thanh toán mô phỏng");
        btnApplyTutor = createMenuButton("4. Ứng tuyển làm gia sư", "Nộp hồ sơ chuyên môn để nâng cấp thành gia sư hệ thống");

        menuPanel.add(btnSearchTutors);
        menuPanel.add(btnMyBookings);
        menuPanel.add(btnTopUpWallet);
        menuPanel.add(btnApplyTutor);

        mainPanel.add(menuPanel, BorderLayout.CENTER);

        // Footer / Logout
        JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnLogout = new JButton("Đăng xuất");
        btnLogout.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        btnLogout.setBackground(new Color(220, 53, 69));
        btnLogout.setForeground(Color.WHITE);
        btnLogout.setFocusPainted(false);
        btnLogout.addActionListener(this);
        footerPanel.add(btnLogout);

        mainPanel.add(footerPanel, BorderLayout.SOUTH);

        add(mainPanel);
    }

    private JButton createMenuButton(String title, String desc) {
        JButton btn = new JButton("<html><center><b><font size='+1'>" + title + "</font></b><br><font color='gray' size='-1'>" + desc + "</font></center></html>");
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.addActionListener(this);
        return btn;
    }

    public void loadWalletData() {
        userWallet = walletDAO.findByUserId(currentUser.getId());
        if (userWallet == null) {
            userWallet = new Wallet(currentUser.getId(), 0);
            walletDAO.insert(userWallet);
        }
        lblBalance.setText("Số dư ví: " + String.format("%,d", userWallet.getAvailableBalance()) + " VNĐ");
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == btnRefresh) {
            loadWalletData();
        } else if (e.getSource() == btnSearchTutors) {
            new TutorSearchFrm(this, currentUser).setVisible(true);
        } else if (e.getSource() == btnMyBookings) {
            new PaymentCheckoutFrm(this, currentUser).setVisible(true);
        } else if (e.getSource() == btnTopUpWallet) {
            new WalletTopUpFrm(this, currentUser).setVisible(true);
        } else if (e.getSource() == btnApplyTutor) {
            new TutorApplicationFrm(this, currentUser).setVisible(true);
        } else if (e.getSource() == btnLogout) {
            // Xóa người dùng hiện tại khỏi bộ nhớ
            currentUser = null;
            this.dispose();
            new LoginFrm().setVisible(true);
        }
    }
}
