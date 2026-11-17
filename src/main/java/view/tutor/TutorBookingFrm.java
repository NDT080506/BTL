package view.tutor;

import dao.BookingDAO;
import dao.UserDAO;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import model.Booking;
import model.User;

/**
 * TutorBookingFrm: Gia sư tiếp nhận và chấp nhận lịch đặt (CN04.01)
 */
public class TutorBookingFrm extends JDialog implements ActionListener {
    private TutorHomeFrm parent;
    private User currentTutor;

    private BookingDAO bookingDAO;
    private UserDAO userDAO;

    private JTable tblBookings;
    private DefaultTableModel tableModel;
    private JButton btnAccept;
    private JButton btnReject;
    private JButton btnRefresh;
    private JButton btnClose;

    private List<Booking> pendingBookings;
    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");

    public TutorBookingFrm(TutorHomeFrm parent, User tutor) {
        super(parent, "Tiếp nhận & Xử lý yêu cầu đặt lịch", true);
        this.parent = parent;
        this.currentTutor = tutor;
        this.bookingDAO = new BookingDAO();
        this.userDAO = new UserDAO();

        initComponents();
        loadPendingBookings();
    }

    private void initComponents() {
        setSize(780, 450);
        setLocationRelativeTo(parent);

        JPanel mainPanel = new JPanel(new BorderLayout(12, 12));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // Header
        JLabel lblTitle = new JLabel("CÁC YÊU CẦU ĐẶT LỊCH ĐANG CHỜ PHẢN HỒI", JLabel.LEFT);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 15));
        lblTitle.setForeground(new Color(24, 119, 242));
        mainPanel.add(lblTitle, BorderLayout.NORTH);

        // Table
        String[] cols = {"STT", "Học viên", "Thời gian học", "Thời lượng", "Học phí", "Ghi chú của học viên"};
        tableModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int col) { return false; }
        };
        tblBookings = new JTable(tableModel);
        tblBookings.setRowHeight(25);
        tblBookings.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        mainPanel.add(new JScrollPane(tblBookings), BorderLayout.CENTER);

        // Buttons
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 5));
        btnRefresh = new JButton("Làm mới danh sách");
        btnRefresh.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        btnRefresh.addActionListener(this);

        btnAccept = new JButton("Chấp nhận lịch đặt");
        btnAccept.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnAccept.setBackground(new Color(40, 167, 69));
        btnAccept.setForeground(Color.WHITE);
        btnAccept.setFocusPainted(false);
        btnAccept.addActionListener(this);

        btnReject = new JButton("Từ chối lịch đặt");
        btnReject.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        btnReject.setBackground(new Color(220, 53, 69));
        btnReject.setForeground(Color.WHITE);
        btnReject.setFocusPainted(false);
        btnReject.addActionListener(this);

        btnClose = new JButton("Đóng");
        btnClose.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        btnClose.addActionListener(this);

        bottomPanel.add(btnRefresh);
        bottomPanel.add(btnAccept);
        bottomPanel.add(btnReject);
        bottomPanel.add(btnClose);
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);

        add(mainPanel);
    }

    private void loadPendingBookings() {
        pendingBookings = bookingDAO.findPendingByTutor(currentTutor.getId());
        tableModel.setRowCount(0);

        int stt = 1;
        for (Booking b : pendingBookings) {
            User student = userDAO.findById(b.getStudentId());
            String studentName = student != null ? student.getDisplayName() : "Học viên";
            String dtStr = b.getScheduledAt() != null ? sdf.format(b.getScheduledAt()) : "-";

            tableModel.addRow(new Object[]{
                stt++,
                studentName,
                dtStr,
                b.getDurationMinutes() + " phút",
                String.format("%,d VNĐ", b.getTotalPrice()),
                b.getNote() != null ? b.getNote() : ""
            });
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == btnRefresh) {
            loadPendingBookings();
        } else if (e.getSource() == btnClose) {
            this.dispose();
        } else if (e.getSource() == btnAccept || e.getSource() == btnReject) {
            int selectedRow = tblBookings.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn một yêu cầu đặt lịch trong bảng!", "Thông báo", JOptionPane.WARNING_MESSAGE);
                return;
            }

            Booking booking = pendingBookings.get(selectedRow);
            boolean isAccept = (e.getSource() == btnAccept);
            String actionName = isAccept ? "Chấp nhận" : "Từ chối";
            String newStatus = isAccept ? "ACCEPTED" : "REJECTED";

            int confirm = JOptionPane.showConfirmDialog(this, 
                "Bạn có chắc chắn muốn " + actionName + " yêu cầu đặt lịch này không?", 
                "Xác nhận", JOptionPane.YES_NO_OPTION);

            if (confirm == JOptionPane.YES_OPTION) {
                boolean ok = bookingDAO.updateResponse(booking.getId(), newStatus);
                if (ok) {
                    JOptionPane.showMessageDialog(this, 
                        "Đã " + actionName.toLowerCase() + " yêu cầu đặt lịch thành công!" +
                        (isAccept ? "\nHọc viên có thể thanh toán trực tiếp vào ví của bạn." : ""), 
                        "Thành công", JOptionPane.INFORMATION_MESSAGE);
                    loadPendingBookings();
                } else {
                    JOptionPane.showMessageDialog(this, "Không thể cập nhật yêu cầu (có thể trạng thái đã thay đổi)!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }
}
