package view.tutor;

import dao.WalletDAO;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;
import model.User;
import model.Wallet;
import view.auth.LoginView;

public class TutorHomeView extends JFrame implements ActionListener {
    private User currentTutor;
    private Wallet tutorWallet;
    private WalletDAO walletDAO;

    private JLabel lblWelcome;
    private JLabel lblBalance;
    private JButton btnManageBookings;
    private JButton btnManagePackages;
    private JButton btnWithdrawal;
    private JButton btnRefresh;
    private JButton btnLogout;

    public TutorHomeView(User tutor) {
        super("TutorTech - Gia Sư: " + tutor.getDisplayName());
        this.currentTutor = tutor;
        this.walletDAO = new WalletDAO();

        initComponents();
        loadWallet();
    }

    private void initComponents() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(550, 350);
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new BorderLayout(15, 15));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(240, 250, 245));
        headerPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(180, 230, 200), 1),
            BorderFactory.createEmptyBorder(12, 15, 12, 15)
        ));

        lblWelcome = new JLabel("Xin chào Thầy/Cô, " + currentTutor.getDisplayName());
        lblWelcome.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblWelcome.setForeground(new Color(40, 167, 69));

        lblBalance = new JLabel("Số dư ví gia sư: Đang tải...");
        lblBalance.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblBalance.setForeground(new Color(24, 119, 242));

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

        JPanel actionPanel = new JPanel(new GridLayout(3, 1, 10, 10));
        actionPanel.setBorder(BorderFactory.createTitledBorder("Nghiệp vụ giảng dạy & tài chính"));

        btnManageBookings = new JButton("<html><b>1. Tiếp nhận & Xử lý yêu cầu đặt lịch</b> - <font color='gray'>Chấp nhận hoặc từ chối lịch học từ học viên</font></html>");
        btnManageBookings.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        btnManageBookings.setFocusPainted(false);
        btnManageBookings.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnManageBookings.addActionListener(this);

        btnManagePackages = new JButton("<html><b>2. Quản lý gói dịch vụ giảng dạy</b> - <font color='gray'>Xem và thêm mới các gói học cung cấp</font></html>");
        btnManagePackages.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        btnManagePackages.setFocusPainted(false);
        btnManagePackages.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnManagePackages.addActionListener(this);

        btnWithdrawal = new JButton("<html><b>3. Yêu cầu rút tiền về ngân hàng</b> - <font color='gray'>Tạo đề nghị rút tiền từ ví gia sư</font></html>");
        btnWithdrawal.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        btnWithdrawal.setFocusPainted(false);
        btnWithdrawal.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnWithdrawal.addActionListener(this);

        actionPanel.add(btnManageBookings);
        actionPanel.add(btnManagePackages);
        actionPanel.add(btnWithdrawal);

        mainPanel.add(actionPanel, BorderLayout.CENTER);

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

    public void loadWallet() {
        tutorWallet = walletDAO.findByUserId(currentTutor.getId());
        if (tutorWallet == null) {
            tutorWallet = new Wallet(currentTutor.getId(), 0);
            walletDAO.insert(tutorWallet);
        }
        lblBalance.setText("Số dư ví gia sư: " + String.format("%,d VNĐ", tutorWallet.getAvailableBalance()));
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == btnRefresh) {
            loadWallet();
        } else if (e.getSource() == btnManageBookings) {
            new TutorBookingView(this, currentTutor).setVisible(true);
        } else if (e.getSource() == btnManagePackages) {
            new TutorPackageManagerView(this, currentTutor).setVisible(true);
        } else if (e.getSource() == btnWithdrawal) {
            new CreateWithdrawalView(this, currentTutor, tutorWallet).setVisible(true);
        } else if (e.getSource() == btnLogout) {
            int confirm = JOptionPane.showConfirmDialog(
                this,
                "Bạn có chắc chắn muốn đăng xuất?",
                "Xác nhận",
                JOptionPane.YES_NO_OPTION
            );
            if (confirm == JOptionPane.YES_OPTION) {
                currentTutor = null;
                this.dispose();
                new LoginView().setVisible(true);
            }
        }
    }
}
