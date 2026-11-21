package view.student;

import dao.BookingDAO;
import dao.PaymentTransactionDAO;
import dao.UserDAO;
import dao.WalletDAO;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import model.Booking;
import model.PaymentTransaction;
import model.User;
import model.Wallet;

public class PaymentCheckoutView extends JDialog implements ActionListener {
    private StudentHomeView parent;
    private User student;

    private BookingDAO bookingDAO;
    private WalletDAO walletDAO;
    private PaymentTransactionDAO txDAO;
    private UserDAO userDAO;

    private JTable tblBookings;
    private DefaultTableModel tableModel;
    private JButton btnPay;
    private JButton btnRefresh;
    private JButton btnClose;
    private JLabel lblWalletInfo;

    private List<Booking> bookingList;
    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");

    public PaymentCheckoutView(StudentHomeView parent, User student) {
        super(parent, "Lịch học & Thanh toán học phí trực tiếp", true);
        this.parent = parent;
        this.student = student;

        this.bookingDAO = new BookingDAO();
        this.walletDAO = new WalletDAO();
        this.txDAO = new PaymentTransactionDAO();
        this.userDAO = new UserDAO();

        initComponents();
        loadBookings();
    }

    private void initComponents() {
        setSize(800, 480);
        setLocationRelativeTo(parent);

        JPanel mainPanel = new JPanel(new BorderLayout(12, 12));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JPanel headerPanel = new JPanel(new BorderLayout());
        JLabel lblTitle = new JLabel("LỊCH ĐẶT CỦA BẠN & THANH TOÁN HỌC PHÍ");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 15));
        lblTitle.setForeground(new Color(24, 119, 242));

        lblWalletInfo = new JLabel("Số dư ví: Đang tải...");
        lblWalletInfo.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblWalletInfo.setForeground(new Color(40, 167, 69));

        headerPanel.add(lblTitle, BorderLayout.WEST);
        headerPanel.add(lblWalletInfo, BorderLayout.EAST);
        mainPanel.add(headerPanel, BorderLayout.NORTH);

        String[] cols = {"Mã lịch", "Gia sư", "Thời gian học", "Thời lượng", "Học phí", "Trạng thái", "Ghi chú"};
        tableModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int col) { return false; }
        };
        tblBookings = new JTable(tableModel);
        tblBookings.setRowHeight(25);
        tblBookings.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        mainPanel.add(new JScrollPane(tblBookings), BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 5));
        btnRefresh = new JButton("Làm mới");
        btnRefresh.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        btnRefresh.addActionListener(this);

        btnPay = new JButton("Thanh toán tiền học phí bằng ví");
        btnPay.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnPay.setBackground(new Color(40, 167, 69));
        btnPay.setForeground(Color.WHITE);
        btnPay.setFocusPainted(false);
        btnPay.addActionListener(this);

        btnClose = new JButton("Đóng");
        btnClose.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        btnClose.addActionListener(this);

        bottomPanel.add(btnRefresh);
        bottomPanel.add(btnPay);
        bottomPanel.add(btnClose);
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);

        add(mainPanel);
    }

    private void loadBookings() {

        Wallet wallet = walletDAO.findByUserId(student.getId());
        long balance = wallet != null ? wallet.getAvailableBalance() : 0;
        lblWalletInfo.setText("Số dư ví học viên: " + String.format("%,d VNĐ", balance));

        bookingList = bookingDAO.findByStudent(student.getId());
        tableModel.setRowCount(0);

        for (Booking b : bookingList) {
            User tutor = userDAO.findById(b.getTutorId());
            String tutorName = tutor != null ? tutor.getDisplayName() : "N/A";
            String dtStr = b.getScheduledAt() != null ? sdf.format(b.getScheduledAt()) : "-";

            String statusDisplay = b.getStatus();
            if ("PENDING_TUTOR_CONFIRMATION".equals(b.getStatus())) {
                statusDisplay = "Chờ gia sư xác nhận";
            } else if ("ACCEPTED".equals(b.getStatus())) {
                statusDisplay = "Gia sư đã chấp nhận (Chờ thanh toán)";
            } else if ("PAID".equals(b.getStatus())) {
                statusDisplay = "Đã thanh toán";
            } else if ("REJECTED".equals(b.getStatus())) {
                statusDisplay = "Gia sư từ chối";
            }

            tableModel.addRow(new Object[]{
                b.getId().toString().substring(0, 8) + "...",
                tutorName,
                dtStr,
                b.getDurationMinutes() + " phút",
                String.format("%,d VNĐ", b.getTotalPrice()),
                statusDisplay,
                b.getNote() != null ? b.getNote() : ""
            });
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == btnRefresh) {
            loadBookings();
        } else if (e.getSource() == btnClose) {
            this.dispose();
        } else if (e.getSource() == btnPay) {
            int selectedRow = tblBookings.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn một lịch học trong bảng để thanh toán!", "Thông báo", JOptionPane.WARNING_MESSAGE);
                return;
            }

            Booking selectedBooking = bookingList.get(selectedRow);

            if (!"ACCEPTED".equalsIgnoreCase(selectedBooking.getStatus())) {
                if ("PAID".equalsIgnoreCase(selectedBooking.getStatus())) {
                    JOptionPane.showMessageDialog(this, "Lịch học này đã được thanh toán trước đó!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(this, "Chỉ có thể thanh toán khi gia sư đã CHẤP NHẬN lịch học!\nTrạng thái hiện tại: " + selectedBooking.getStatus(), "Chưa thể thanh toán", JOptionPane.WARNING_MESSAGE);
                }
                return;
            }

            Wallet studentWallet = walletDAO.findByUserId(student.getId());
            if (studentWallet == null || studentWallet.getAvailableBalance() < selectedBooking.getTotalPrice()) {
                long currentBal = studentWallet != null ? studentWallet.getAvailableBalance() : 0;
                long need = selectedBooking.getTotalPrice() - currentBal;
                JOptionPane.showMessageDialog(this,
                    "Số dư ví không đủ để thanh toán!\nSố dư hiện có: " + String.format("%,d VNĐ", currentBal) +
                    "\nCần thêm: " + String.format("%,d VNĐ", need) +
                    "\nVui lòng nạp thêm tiền vào ví.",
                    "Số dư không đủ", JOptionPane.WARNING_MESSAGE);
                return;
            }

            Wallet tutorWallet = walletDAO.findByUserId(selectedBooking.getTutorId());
            if (tutorWallet == null) {
                tutorWallet = new Wallet(selectedBooking.getTutorId(), 0);
                walletDAO.insert(tutorWallet);
            }

            int confirm = JOptionPane.showConfirmDialog(this,
                "Xác nhận thanh toán trực tiếp " + String.format("%,d VNĐ", selectedBooking.getTotalPrice()) +
                " từ ví của bạn sang ví gia sư?",
                "Xác nhận thanh toán", JOptionPane.YES_NO_OPTION);

            if (confirm == JOptionPane.YES_OPTION) {
                PaymentTransaction tx = txDAO.transferBetweenWallets(selectedBooking, studentWallet, tutorWallet, selectedBooking.getTotalPrice());
                if (tx != null) {
                    JOptionPane.showMessageDialog(this,
                        "Thanh toán thành công!\n" +
                        "Mã giao dịch: " + tx.getReferenceCode() + "\n" +
                        "Số tiền: " + String.format("%,d VNĐ", tx.getAmount()) + "\n" +
                        "Học phí đã được chuyển trực tiếp vào ví gia sư.",
                        "Giao dịch thành công", JOptionPane.INFORMATION_MESSAGE);
                    loadBookings();
                    parent.loadWalletData();
                } else {
                    JOptionPane.showMessageDialog(this, "Giao dịch thất bại! Lỗi chuyển khoản ví.", "Lỗi", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }
}
