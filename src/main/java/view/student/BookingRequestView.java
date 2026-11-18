package view.student;

import dao.BookingDAO;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import javax.swing.*;
import model.Booking;
import model.BrokeragePackage;
import model.User;

/**
 * BookingRequestView: Màn hình Đặt lịch thuê gia sư (CN02.07)
 */
public class BookingRequestView extends JDialog implements ActionListener {
    private TutorDetailView parent;
    private User student;
    private User tutor;
    private BrokeragePackage brokeragePackage;

    private JTextField txtDateTime;
    private JComboBox<Integer> cmbDuration;
    private JTextArea txtNote;
    private JButton btnSubmit;
    private JButton btnCancel;
    private JLabel lblTotalAmount;

    private BookingDAO bookingDAO;
    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");

    public BookingRequestView(TutorDetailView parent, User student, User tutor, BrokeragePackage pkg) {
        super(parent, "Gửi yêu cầu đặt lịch gia sư", true);
        this.parent = parent;
        this.student = student;
        this.tutor = tutor;
        this.brokeragePackage = pkg;
        this.bookingDAO = new BookingDAO();

        initComponents();
    }

    private void initComponents() {
        setSize(480, 450);
        setLocationRelativeTo(parent);
        setResizable(false);

        JPanel mainPanel = new JPanel(new BorderLayout(15, 15));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        JLabel lblTitle = new JLabel("ĐẶT LỊCH THUÊ GIA SƯ", JLabel.CENTER);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblTitle.setForeground(new Color(24, 119, 242));
        mainPanel.add(lblTitle, BorderLayout.NORTH);

        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Gia sư
        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0.35;
        formPanel.add(new JLabel("Gia sư:"), gbc);
        JLabel lblTutorName = new JLabel(tutor.getDisplayName());
        lblTutorName.setFont(new Font("Segoe UI", Font.BOLD, 13));
        gbc.gridx = 1; gbc.gridy = 0; gbc.weightx = 0.65;
        formPanel.add(lblTutorName, gbc);

        // Gói dịch vụ
        gbc.gridx = 0; gbc.gridy = 1;
        formPanel.add(new JLabel("Gói học đã chọn:"), gbc);
        JLabel lblPkg = new JLabel(brokeragePackage.getName());
        lblPkg.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        gbc.gridx = 1; gbc.gridy = 1;
        formPanel.add(lblPkg, gbc);

        // Học phí
        gbc.gridx = 0; gbc.gridy = 2;
        formPanel.add(new JLabel("Học phí dự kiến:"), gbc);
        lblTotalAmount = new JLabel(String.format("%,d VNĐ", brokeragePackage.getPrice()));
        lblTotalAmount.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblTotalAmount.setForeground(new Color(220, 53, 69));
        gbc.gridx = 1; gbc.gridy = 2;
        formPanel.add(lblTotalAmount, gbc);

        // Ngày giờ học
        gbc.gridx = 0; gbc.gridy = 3;
        formPanel.add(new JLabel("Ngày giờ học (*):"), gbc);
        
        // Mặc định là 19:00 ngày mai
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_MONTH, 1);
        cal.set(Calendar.HOUR_OF_DAY, 19);
        cal.set(Calendar.MINUTE, 0);
        txtDateTime = new JTextField(sdf.format(cal.getTime()), 15);
        gbc.gridx = 1; gbc.gridy = 3;
        formPanel.add(txtDateTime, gbc);

        // Thời lượng
        gbc.gridx = 0; gbc.gridy = 4;
        formPanel.add(new JLabel("Thời lượng (phút):"), gbc);
        cmbDuration = new JComboBox<>(new Integer[]{60, 90, 120, 150});
        cmbDuration.setSelectedItem(90);
        gbc.gridx = 1; gbc.gridy = 4;
        formPanel.add(cmbDuration, gbc);

        // Ghi chú
        gbc.gridx = 0; gbc.gridy = 5;
        formPanel.add(new JLabel("Ghi chú cho gia sư:"), gbc);
        txtNote = new JTextArea(3, 15);
        txtNote.setLineWrap(true);
        txtNote.setWrapStyleWord(true);
        txtNote.setText("Nhờ thầy/cô hỗ trợ củng cố lại kiến thức trọng tâm.");
        gbc.gridx = 1; gbc.gridy = 5;
        formPanel.add(new JScrollPane(txtNote), gbc);

        mainPanel.add(formPanel, BorderLayout.CENTER);

        // Buttons
        JPanel buttonPanel = new JPanel(new GridLayout(1, 2, 15, 0));
        btnSubmit = new JButton("Xác nhận gửi yêu cầu");
        btnSubmit.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnSubmit.setBackground(new Color(24, 119, 242));
        btnSubmit.setForeground(Color.WHITE);
        btnSubmit.setFocusPainted(false);
        btnSubmit.addActionListener(this);

        btnCancel = new JButton("Hủy bỏ");
        btnCancel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        btnCancel.addActionListener(this);

        buttonPanel.add(btnSubmit);
        buttonPanel.add(btnCancel);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(mainPanel);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == btnSubmit) {
            String dtStr = txtDateTime.getText().trim();
            int duration = (int) cmbDuration.getSelectedItem();
            String note = txtNote.getText().trim();

            Timestamp scheduledAt;
            try {
                scheduledAt = new Timestamp(sdf.parse(dtStr).getTime());
                if (scheduledAt.before(new Timestamp(System.currentTimeMillis()))) {
                    JOptionPane.showMessageDialog(this, "Thời gian học không thể ở quá khứ!", "Lỗi ngày giờ", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Định dạng ngày giờ không hợp lệ! Vui lòng nhập đúng định dạng: yyyy-MM-dd HH:mm", "Lỗi định dạng", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Kiểm tra trùng lịch gia sư
            boolean available = bookingDAO.isScheduleAvailable(tutor.getId(), scheduledAt, duration);
            if (!available) {
                JOptionPane.showMessageDialog(this, "Gia sư đã có lịch dạy trùng vào khung giờ này!\nVui lòng chọn thời gian khác.", "Trùng lịch", JOptionPane.WARNING_MESSAGE);
                return;
            }

            // Tạo Booking
            Booking booking = new Booking(student.getId(), tutor.getId(), brokeragePackage.getId(), 
                                          scheduledAt, duration, note, brokeragePackage.getPrice());
            boolean success = bookingDAO.insert(booking);

            if (success) {
                JOptionPane.showMessageDialog(this, 
                    "Gửi yêu cầu đặt lịch thành công!\nMã lịch đặt: " + booking.getId() + 
                    "\nTrạng thái: Đang chờ gia sư phản hồi.\n(Chưa trừ tiền ví, bạn chỉ thanh toán sau khi gia sư chấp nhận lịch).", 
                    "Thành công", JOptionPane.INFORMATION_MESSAGE);
                this.dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Có lỗi xảy ra khi tạo đơn đặt lịch!", "Lỗi hệ thống", JOptionPane.ERROR_MESSAGE);
            }
        } else if (e.getSource() == btnCancel) {
            this.dispose();
        }
    }
}
