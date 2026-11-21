package view.student;

import dao.PaymentTransactionDAO;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;

public class MockPaymentGatewayView extends JDialog implements ActionListener {
    private WalletTopUpView parent;
    private String referenceCode;
    private long amount;

    private JRadioButton rbSuccess;
    private JRadioButton rbFailed;
    private JButton btnConfirm;
    private JButton btnCancel;

    private PaymentTransactionDAO txDAO;

    public MockPaymentGatewayView(WalletTopUpView parent, String referenceCode, long amount) {
        super(parent, "Cổng thanh toán trực tuyến (Mô phỏng)", true);
        this.parent = parent;
        this.referenceCode = referenceCode;
        this.amount = amount;
        this.txDAO = new PaymentTransactionDAO();

        initComponents();
    }

    private void initComponents() {
        setSize(420, 320);
        setLocationRelativeTo(parent);
        setResizable(false);

        JPanel mainPanel = new JPanel(new BorderLayout(15, 15));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 25, 20, 25));

        JPanel header = new JPanel(new GridLayout(2, 1, 4, 4));
        JLabel lblTitle = new JLabel("CỔNG THANH TOÁN MÔ PHỎNG", JLabel.CENTER);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 15));
        lblTitle.setForeground(new Color(24, 119, 242));

        JLabel lblSub = new JLabel("Giả lập phản hồi kết quả giao dịch thanh toán", JLabel.CENTER);
        lblSub.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblSub.setForeground(Color.GRAY);

        header.add(lblTitle);
        header.add(lblSub);
        mainPanel.add(header, BorderLayout.NORTH);

        JPanel contentPanel = new JPanel(new GridLayout(4, 1, 8, 8));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel lblRef = new JLabel("Mã tham chiếu: " + referenceCode);
        lblRef.setFont(new Font("Segoe UI", Font.BOLD, 12));

        JLabel lblAmt = new JLabel("Số tiền giao dịch: " + String.format("%,d VNĐ", amount));
        lblAmt.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblAmt.setForeground(new Color(220, 53, 69));

        rbSuccess = new JRadioButton("Giao dịch THÀNH CÔNG (Cộng tiền vào ví)", true);
        rbFailed = new JRadioButton("Giao dịch THẤT BẠI / BỊ HỦY (Không cộng tiền)");

        ButtonGroup bg = new ButtonGroup();
        bg.add(rbSuccess);
        bg.add(rbFailed);

        contentPanel.add(lblRef);
        contentPanel.add(lblAmt);
        contentPanel.add(rbSuccess);
        contentPanel.add(rbFailed);

        mainPanel.add(contentPanel, BorderLayout.CENTER);

        JPanel btnPanel = new JPanel(new GridLayout(1, 2, 15, 0));
        btnConfirm = new JButton("Xác nhận kết quả");
        btnConfirm.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnConfirm.setBackground(new Color(40, 167, 69));
        btnConfirm.setForeground(Color.WHITE);
        btnConfirm.setFocusPainted(false);
        btnConfirm.addActionListener(this);

        btnCancel = new JButton("Hủy bỏ");
        btnCancel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        btnCancel.addActionListener(this);

        btnPanel.add(btnConfirm);
        btnPanel.add(btnCancel);
        mainPanel.add(btnPanel, BorderLayout.SOUTH);

        add(mainPanel);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == btnConfirm) {
            String result = rbSuccess.isSelected() ? "SUCCESS" : "FAILED";
            boolean ok = txDAO.confirmDeposit(referenceCode, result);

            if (ok) {
                JOptionPane.showMessageDialog(this,
                    "Cổng thanh toán phản hồi: Giao dịch thành công!\nSố dư đã được cộng vào ví của bạn.",
                    "Nạp tiền thành công", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this,
                    "Giao dịch đã kết thúc với trạng thái: " + result + ".\nSố dư ví không thay đổi.",
                    "Kết quả giao dịch", JOptionPane.WARNING_MESSAGE);
            }
            this.dispose();
            parent.refreshWallet();
        } else if (e.getSource() == btnCancel) {
            this.dispose();
        }
    }
}
