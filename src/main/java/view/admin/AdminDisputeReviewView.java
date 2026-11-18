package view.admin;

import dao.ActionLogDAO;
import dao.BookingDAO;
import dao.DisputeDAO;
import dao.PaymentTransactionDAO;
import dao.UserDAO;
import dao.WalletDAO;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import model.ActionLog;
import model.Booking;
import model.Dispute;
import model.User;
import model.Wallet;

/**
 * AdminDisputeReviewView: Quản trị xử lý khiếu nại và hoàn tiền (CN06.05)
 */
public class AdminDisputeReviewView extends JDialog implements ActionListener {
    private AdminHomeView parent;
    private User currentAdmin;

    private DisputeDAO disputeDAO;
    private BookingDAO bookingDAO;
    private WalletDAO walletDAO;
    private PaymentTransactionDAO txDAO;
    private UserDAO userDAO;
    private ActionLogDAO actionLogDAO;

    private JTable tblDisputes;
    private DefaultTableModel tableModel;
    private JTextArea txtDetail;
    private JTextField txtAdminNote;
    private JButton btnApproveRefund;
    private JButton btnReject;
    private JButton btnRefresh;
    private JButton btnClose;

    private List<Dispute> disputeList;

    public AdminDisputeReviewView(AdminHomeView parent, User admin) {
        super(parent, "Xử lý khiếu nại & Hoàn tiền học phí", true);
        this.parent = parent;
        this.currentAdmin = admin;

        this.disputeDAO = new DisputeDAO();
        this.bookingDAO = new BookingDAO();
        this.walletDAO = new WalletDAO();
        this.txDAO = new PaymentTransactionDAO();
        this.userDAO = new UserDAO();
        this.actionLogDAO = new ActionLogDAO();

        initComponents();
        loadDisputes();
    }

    private void initComponents() {
        setSize(880, 530);
        setLocationRelativeTo(parent);

        JPanel mainPanel = new JPanel(new BorderLayout(12, 12));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // Header
        JLabel lblTitle = new JLabel("DANH SÁCH KHIẾU NẠI ĐANG CHỜ XỬ LÝ (PENDING)");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 15));
        lblTitle.setForeground(new Color(24, 119, 242));
        mainPanel.add(lblTitle, BorderLayout.NORTH);

        // Center Split
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        splitPane.setResizeWeight(0.5);

        String[] cols = {"STT", "Mã khiếu nại", "Người gửi", "Mã Booking", "Số tiền yêu cầu hoàn", "Ngày gửi"};
        tableModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };
        tblDisputes = new JTable(tableModel);
        tblDisputes.setRowHeight(25);
        tblDisputes.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tblDisputes.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) showDisputeDetail();
        });

        splitPane.setTopComponent(new JScrollPane(tblDisputes));

        // Detail Panel
        JPanel detailPanel = new JPanel(new BorderLayout(8, 8));
        detailPanel.setBorder(BorderFactory.createTitledBorder("Chi tiết khiếu nại & Hồ sơ hoàn tiền"));

        txtDetail = new JTextArea(4, 40);
        txtDetail.setEditable(false);
        txtDetail.setBackground(new Color(245, 245, 245));
        detailPanel.add(new JScrollPane(txtDetail), BorderLayout.CENTER);

        JPanel notePanel = new JPanel(new BorderLayout(5, 5));
        notePanel.add(new JLabel("Ghi chú quyết định:"), BorderLayout.WEST);
        txtAdminNote = new JTextField();
        notePanel.add(txtAdminNote, BorderLayout.CENTER);
        detailPanel.add(notePanel, BorderLayout.SOUTH);

        splitPane.setBottomComponent(detailPanel);
        mainPanel.add(splitPane, BorderLayout.CENTER);

        // Bottom Buttons
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 5));
        btnRefresh = new JButton("Làm mới");
        btnRefresh.addActionListener(this);

        btnApproveRefund = new JButton("Chấp thuận & Hoàn tiền về ví học viên");
        btnApproveRefund.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnApproveRefund.setBackground(new Color(40, 167, 69));
        btnApproveRefund.setForeground(Color.WHITE);
        btnApproveRefund.setFocusPainted(false);
        btnApproveRefund.addActionListener(this);

        btnReject = new JButton("Từ chối khiếu nại");
        btnReject.setBackground(new Color(220, 53, 69));
        btnReject.setForeground(Color.WHITE);
        btnReject.setFocusPainted(false);
        btnReject.addActionListener(this);

        btnClose = new JButton("Đóng");
        btnClose.addActionListener(this);

        bottomPanel.add(btnRefresh);
        bottomPanel.add(btnApproveRefund);
        bottomPanel.add(btnReject);
        bottomPanel.add(btnClose);
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);

        add(mainPanel);
    }

    private void loadDisputes() {
        disputeList = disputeDAO.findPending();
        tableModel.setRowCount(0);

        int stt = 1;
        for (Dispute d : disputeList) {
            User sender = userDAO.findById(d.getCreatedById());
            String senderName = sender != null ? sender.getDisplayName() : "N/A";

            tableModel.addRow(new Object[]{
                stt++,
                d.getId().toString().substring(0, 8) + "...",
                senderName,
                d.getBookingId().toString().substring(0, 8) + "...",
                String.format("%,d VNĐ", d.getRefundAmount()),
                d.getCreatedAt()
            });
        }
        txtDetail.setText("");
    }

    private void showDisputeDetail() {
        int row = tblDisputes.getSelectedRow();
        if (row >= 0 && row < disputeList.size()) {
            Dispute d = disputeList.get(row);
            Booking bk = bookingDAO.findById(d.getBookingId());
            User sender = userDAO.findById(d.getCreatedById());
            User tutor = bk != null ? userDAO.findById(bk.getTutorId()) : null;
            Wallet tutorWallet = tutor != null ? walletDAO.findByUserId(tutor.getId()) : null;

            StringBuilder sb = new StringBuilder();
            sb.append("Người khiếu nại: ").append(sender != null ? sender.getDisplayName() : "N/A").append("\n");
            sb.append("Lý do khiếu nại: ").append(d.getReason()).append("\n");
            sb.append("Đường dẫn bằng chứng: ").append(d.getEvidenceUrl() != null ? d.getEvidenceUrl() : "Không có").append("\n");
            sb.append("Gia sư liên quan: ").append(tutor != null ? tutor.getDisplayName() : "N/A").append("\n");
            sb.append("Số dư khả dụng ví gia sư hiện tại: ")
              .append(tutorWallet != null ? String.format("%,d VNĐ", tutorWallet.getAvailableBalance()) : "0 VNĐ").append("\n");
            sb.append("Số tiền cần hoàn trả: ").append(String.format("%,d VNĐ", d.getRefundAmount()));

            txtDetail.setText(sb.toString());
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == btnRefresh) {
            loadDisputes();
        } else if (e.getSource() == btnClose) {
            this.dispose();
        } else if (e.getSource() == btnApproveRefund || e.getSource() == btnReject) {
            int row = tblDisputes.getSelectedRow();
            if (row == -1) {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn một khiếu nại trong bảng!", "Thông báo", JOptionPane.WARNING_MESSAGE);
                return;
            }

            Dispute d = disputeList.get(row);
            Booking bk = bookingDAO.findById(d.getBookingId());
            if (bk == null) {
                JOptionPane.showMessageDialog(this, "Không tìm thấy booking liên quan!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }

            boolean isApprove = (e.getSource() == btnApproveRefund);
            String note = txtAdminNote.getText().trim();

            if (isApprove) {
                // Kiểm tra ví gia sư có đủ số dư hoàn tiền không
                Wallet tutorWallet = walletDAO.findByUserId(bk.getTutorId());
                Wallet studentWallet = walletDAO.findByUserId(bk.getStudentId());

                if (tutorWallet == null || tutorWallet.getAvailableBalance() < d.getRefundAmount()) {
                    long currentBal = tutorWallet != null ? tutorWallet.getAvailableBalance() : 0;
                    JOptionPane.showMessageDialog(this, 
                        "Ví gia sư không đủ số dư để hoàn tiền!\nSố dư ví gia sư: " + String.format("%,d VNĐ", currentBal) +
                        "\nSố tiền cần hoàn: " + String.format("%,d VNĐ", d.getRefundAmount()), 
                        "Không đủ số dư", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                int confirm = JOptionPane.showConfirmDialog(this, 
                    "Xác nhận CHẤP THUẬN khiếu nại?\nSố tiền " + String.format("%,d VNĐ", d.getRefundAmount()) + 
                    " sẽ được chuyển trực tiếp từ ví gia sư về ví học viên.", 
                    "Xác nhận hoàn tiền", JOptionPane.YES_NO_OPTION);

                if (confirm == JOptionPane.YES_OPTION) {
                    boolean ok = txDAO.refundBetweenWallets(d.getId(), bk.getId(), tutorWallet, studentWallet, d.getRefundAmount());
                    if (ok) {
                        actionLogDAO.insert(new ActionLog(currentAdmin.getId(), "RESOLVE_DISPUTE", "Dispute", d.getId()));
                        JOptionPane.showMessageDialog(this, "Đã hoàn tiền thành công về ví học viên!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
                        loadDisputes();
                    } else {
                        JOptionPane.showMessageDialog(this, "Lỗi trong quá trình chuyển hoàn tiền ví!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                    }
                }
            } else {
                if (note.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Vui lòng nhập lý do từ chối vào ô Ghi chú!", "Thiếu lý do", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                int confirm = JOptionPane.showConfirmDialog(this, "Xác nhận TỪ CHỐI khiếu nại này?", "Xác nhận", JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    disputeDAO.updateStatus(d.getId(), "REJECTED", note);
                    actionLogDAO.insert(new ActionLog(currentAdmin.getId(), "RESOLVE_DISPUTE", "Dispute", d.getId()));
                    JOptionPane.showMessageDialog(this, "Đã từ chối khiếu nại.", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                    loadDisputes();
                }
            }
        }
    }
}
