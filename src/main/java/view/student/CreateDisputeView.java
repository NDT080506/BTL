package view.student;

import dao.ActionLogDAO;
import dao.DisputeDAO;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;
import model.ActionLog;
import model.Booking;
import model.Dispute;
import model.User;

public class CreateDisputeView extends JDialog implements ActionListener {
    private PaymentCheckoutView parent;
    private User student;
    private Booking booking;

    private JLabel lblBookingCode;
    private JTextArea txtReason;
    private JTextField txtEvidenceUrl;
    private JTextField txtRefundAmount;
    private JButton btnSubmit;
    private JButton btnCancel;

    private DisputeDAO disputeDAO;
    private ActionLogDAO actionLogDAO;

    public CreateDisputeView(PaymentCheckoutView parent, User student, Booking booking) {
        super(parent, "Gửi khiếu nại buổi học: " + booking.getId().toString().substring(0, 8), true);
        this.parent = parent;
        this.student = student;
        this.booking = booking;
        this.disputeDAO = new DisputeDAO();
        this.actionLogDAO = new ActionLogDAO();

        initComponents();
    }

    private void initComponents() {
        setSize(480, 400);
        setLocationRelativeTo(parent);
        setResizable(false);

        JPanel mainPanel = new JPanel(new BorderLayout(12, 12));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        JLabel lblTitle = new JLabel("KHIẾU NẠI & ĐỀ NGHỊ HOÀN TIỀN", JLabel.CENTER);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 15));
        lblTitle.setForeground(new Color(220, 53, 69));
        mainPanel.add(lblTitle, BorderLayout.NORTH);

        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Mã booking
        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0.35;
        formPanel.add(new JLabel("Mã lịch học:"), gbc);
        lblBookingCode = new JLabel(booking.getId().toString().substring(0, 8) + "...");
        lblBookingCode.setFont(new Font("Segoe UI", Font.BOLD, 12));
        gbc.gridx = 1; gbc.gridy = 0; gbc.weightx = 0.65;
        formPanel.add(lblBookingCode, gbc);

        // Số tiền đề nghị hoàn
        gbc.gridx = 0; gbc.gridy = 1;
        formPanel.add(new JLabel("Số tiền đề nghị hoàn:"), gbc);
        txtRefundAmount = new JTextField(String.valueOf(booking.getTotalPrice()));
        gbc.gridx = 1; gbc.gridy = 1;
        formPanel.add(txtRefundAmount, gbc);

        // Đường dẫn bằng chứng
        gbc.gridx = 0; gbc.gridy = 2;
        formPanel.add(new JLabel("Đường dẫn bằng chứng:"), gbc);
        txtEvidenceUrl = new JTextField("https://minhchung.tutortech.vn/evidence-01.png");
        gbc.gridx = 1; gbc.gridy = 2;
        formPanel.add(txtEvidenceUrl, gbc);

        // Lý do khiếu nại
        gbc.gridx = 0; gbc.gridy = 3;
        formPanel.add(new JLabel("Lý do khiếu nại (*):"), gbc);
        txtReason = new JTextArea(4, 15);
        txtReason.setLineWrap(true);
        txtReason.setWrapStyleWord(true);
        txtReason.setText("Gia sư không vào lớp đúng giờ và không thực hiện đủ thời lượng buổi học.");
        gbc.gridx = 1; gbc.gridy = 3;
        formPanel.add(new JScrollPane(txtReason), gbc);

        mainPanel.add(formPanel, BorderLayout.CENTER);

        // Buttons
        JPanel btnPanel = new JPanel(new GridLayout(1, 2, 15, 0));
        btnSubmit = new JButton("Gửi khiếu nại");
        btnSubmit.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnSubmit.setBackground(new Color(220, 53, 69));
        btnSubmit.setForeground(Color.WHITE);
        btnSubmit.setFocusPainted(false);
        btnSubmit.addActionListener(this);

        btnCancel = new JButton("Hủy bỏ");
        btnCancel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        btnCancel.addActionListener(e -> this.dispose());

        btnPanel.add(btnSubmit);
        btnPanel.add(btnCancel);
        mainPanel.add(btnPanel, BorderLayout.SOUTH);

        add(mainPanel);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == btnSubmit) {
            String reason = txtReason.getText().trim();
            String evidence = txtEvidenceUrl.getText().trim();
            String amtStr = txtRefundAmount.getText().trim();

            if (reason.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Vui lòng nhập lý do khiếu nại!", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
                return;
            }

            long refundAmt;
            try {
                refundAmt = Long.parseLong(amtStr);
                if (refundAmt <= 0 || refundAmt > booking.getTotalPrice()) {
                    JOptionPane.showMessageDialog(this, "Số tiền hoàn không được lớn hơn tổng học phí của buổi học!", "Số tiền không hợp lệ", JOptionPane.WARNING_MESSAGE);
                    return;
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Số tiền không đúng định dạng số!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }

            Dispute dispute = new Dispute(booking.getId(), student.getId(), reason, evidence, refundAmt);
            boolean ok = disputeDAO.insert(dispute);
            if (ok) {
                actionLogDAO.insert(new ActionLog(student.getId(), "CREATE_DISPUTE", "Dispute", dispute.getId()));
                JOptionPane.showMessageDialog(this, 
                    "Gửi khiếu nại thành công!\nQuản trị viên sẽ thẩm tra và xử lý hoàn tiền cho bạn.", 
                    "Thành công", JOptionPane.INFORMATION_MESSAGE);
                this.dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Không thể lưu khiếu nại do lỗi hệ thống!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
