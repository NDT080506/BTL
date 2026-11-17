package view.student;

import dao.PaymentTransactionDAO;
import dao.WalletDAO;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;
import model.PaymentTransaction;
import model.User;
import model.Wallet;

/**
 * WalletTopUpFrm: Màn hình Nạp tiền vào ví bằng cổng thanh toán mô phỏng (CN02.14)
 */
public class WalletTopUpFrm extends JDialog implements ActionListener {
    private StudentHomeFrm parent;
    private User student;

    private WalletDAO walletDAO;
    private PaymentTransactionDAO txDAO;
    private Wallet currentWallet;

    private JLabel lblCurrentBalance;
    private JTextField txtAmount;
    private JButton btn100k;
    private JButton btn200k;
    private JButton btn500k;
    private JButton btn1000k;
    private JButton btnSubmit;
    private JButton btnCancel;

    public WalletTopUpFrm(StudentHomeFrm parent, User student) {
        super(parent, "Nạp tiền vào ví", true);
        this.parent = parent;
        this.student = student;
        this.walletDAO = new WalletDAO();
        this.txDAO = new PaymentTransactionDAO();

        initComponents();
        loadWallet();
    }

    private void initComponents() {
        setSize(460, 360);
        setLocationRelativeTo(parent);
        setResizable(false);

        JPanel mainPanel = new JPanel(new BorderLayout(15, 15));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        // Header
        JLabel lblTitle = new JLabel("NẠP TIỀN VÀO VÍ HỌC VIÊN", JLabel.CENTER);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblTitle.setForeground(new Color(24, 119, 242));
        mainPanel.add(lblTitle, BorderLayout.NORTH);

        // Center Content
        JPanel centerPanel = new JPanel(new GridLayout(4, 1, 10, 10));

        // Row 1: Số dư hiện tại
        lblCurrentBalance = new JLabel("Số dư hiện tại: Đang tải...");
        lblCurrentBalance.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblCurrentBalance.setForeground(new Color(40, 167, 69));
        centerPanel.add(lblCurrentBalance);

        // Row 2: Chọn nhanh số tiền
        JPanel quickPanel = new JPanel(new GridLayout(1, 4, 8, 0));
        btn100k = new JButton("100k");
        btn200k = new JButton("200k");
        btn500k = new JButton("500k");
        btn1000k = new JButton("1.000k");

        btn100k.addActionListener(this);
        btn200k.addActionListener(this);
        btn500k.addActionListener(this);
        btn1000k.addActionListener(this);

        quickPanel.add(btn100k);
        quickPanel.add(btn200k);
        quickPanel.add(btn500k);
        quickPanel.add(btn1000k);
        centerPanel.add(quickPanel);

        // Row 3: Nhập số tiền tùy chỉnh
        JPanel inputPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        inputPanel.add(new JLabel("Số tiền nạp (VNĐ):"));
        txtAmount = new JTextField("200000", 12);
        txtAmount.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        inputPanel.add(txtAmount);
        centerPanel.add(inputPanel);

        // Row 4: Ghi chú
        JLabel lblNote = new JLabel("<html><i>Lưu ý: Quá trình nạp tiền sử dụng Cổng thanh toán mô phỏng (Mock Payment) để thử nghiệm nghiệp vụ.</i></html>");
        lblNote.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblNote.setForeground(Color.GRAY);
        centerPanel.add(lblNote);

        mainPanel.add(centerPanel, BorderLayout.CENTER);

        // Buttons
        JPanel buttonPanel = new JPanel(new GridLayout(1, 2, 15, 0));
        btnSubmit = new JButton("Tiếp tục thanh toán");
        btnSubmit.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnSubmit.setBackground(new Color(24, 119, 242));
        btnSubmit.setForeground(Color.WHITE);
        btnSubmit.setFocusPainted(false);
        btnSubmit.addActionListener(this);

        btnCancel = new JButton("Đóng");
        btnCancel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        btnCancel.addActionListener(this);

        buttonPanel.add(btnSubmit);
        buttonPanel.add(btnCancel);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(mainPanel);
    }

    private void loadWallet() {
        currentWallet = walletDAO.findByUserId(student.getId());
        if (currentWallet == null) {
            currentWallet = new Wallet(student.getId(), 0);
            walletDAO.insert(currentWallet);
        }
        lblCurrentBalance.setText("Số dư ví hiện tại: " + String.format("%,d VNĐ", currentWallet.getAvailableBalance()));
    }

    public void refreshWallet() {
        loadWallet();
        parent.loadWalletData();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == btn100k) txtAmount.setText("100000");
        else if (e.getSource() == btn200k) txtAmount.setText("200000");
        else if (e.getSource() == btn500k) txtAmount.setText("500000");
        else if (e.getSource() == btn1000k) txtAmount.setText("1000000");
        else if (e.getSource() == btnCancel) {
            this.dispose();
        } else if (e.getSource() == btnSubmit) {
            String amtStr = txtAmount.getText().trim();
            long amount;
            try {
                amount = Long.parseLong(amtStr);
                if (amount <= 0) {
                    JOptionPane.showMessageDialog(this, "Số tiền nạp phải lớn hơn 0!", "Số tiền không hợp lệ", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Vui lòng nhập số tiền hợp lệ dạng số nguyên!", "Lỗi định dạng", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Tạo yêu cầu nạp tiền PENDING kèm referenceCode
            String refCode = "DEP-" + System.currentTimeMillis();
            PaymentTransaction pendingTx = txDAO.createPendingDeposit(currentWallet, amount, refCode);

            if (pendingTx != null) {
                // Chuyển sang màn hình Cổng thanh toán mô phỏng
                new MockPaymentGatewayFrm(this, refCode, amount).setVisible(true);
            } else {
                JOptionPane.showMessageDialog(this, "Không thể tạo yêu cầu nạp tiền!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
