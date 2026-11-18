package view.student;

import dao.TutorProfileDAO;
import dao.UserDAO;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import model.TutorProfile;
import model.User;

/**
 * TutorSearchView: Màn hình Tra cứu gia sư & gói dịch vụ (CN02.03)
 */
public class TutorSearchView extends JDialog implements ActionListener {
    private StudentHomeView parent;
    private User currentStudent;
    private UserDAO userDAO;
    private TutorProfileDAO tutorProfileDAO;

    private JTextField txtKeyword;
    private JTextField txtSubject;
    private JButton btnSearch;
    private JButton btnViewDetail;
    private JTable tblTutors;
    private DefaultTableModel tableModel;

    private List<User> currentTutorList;

    public TutorSearchView(StudentHomeView parent, User student) {
        super(parent, "Tra cứu hồ sơ gia sư", true);
        this.parent = parent;
        this.currentStudent = student;
        this.userDAO = new UserDAO();
        this.tutorProfileDAO = new TutorProfileDAO();

        initComponents();
        searchTutors();
    }

    private void initComponents() {
        setSize(750, 480);
        setLocationRelativeTo(parent);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // Filter panel
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        filterPanel.setBorder(BorderFactory.createTitledBorder("Bộ lọc tìm kiếm"));

        filterPanel.add(new JLabel("Tên gia sư / Giới thiệu:"));
        txtKeyword = new JTextField(12);
        filterPanel.add(txtKeyword);

        filterPanel.add(new JLabel("Môn học:"));
        txtSubject = new JTextField(10);
        filterPanel.add(txtSubject);

        btnSearch = new JButton("Tìm kiếm");
        btnSearch.setBackground(new Color(24, 119, 242));
        btnSearch.setForeground(Color.WHITE);
        btnSearch.setFocusPainted(false);
        btnSearch.addActionListener(this);
        filterPanel.add(btnSearch);

        mainPanel.add(filterPanel, BorderLayout.NORTH);

        // Table panel
        String[] columnNames = {"STT", "Họ và tên", "Môn giảng dạy", "Đánh giá", "Email", "ID"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tblTutors = new JTable(tableModel);
        tblTutors.setRowHeight(25);
        tblTutors.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tblTutors.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        tblTutors.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // Ẩn cột ID
        tblTutors.getColumnModel().getColumn(5).setMinWidth(0);
        tblTutors.getColumnModel().getColumn(5).setMaxWidth(0);
        tblTutors.getColumnModel().getColumn(5).setWidth(0);

        JScrollPane scrollPane = new JScrollPane(tblTutors);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        // Bottom panel
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 5));
        btnViewDetail = new JButton("Xem hồ sơ chi tiết & Chọn gói học");
        btnViewDetail.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnViewDetail.setBackground(new Color(40, 167, 69));
        btnViewDetail.setForeground(Color.WHITE);
        btnViewDetail.setFocusPainted(false);
        btnViewDetail.addActionListener(this);

        bottomPanel.add(btnViewDetail);
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);

        add(mainPanel);
    }

    private void searchTutors() {
        String keyword = txtKeyword.getText().trim();
        String subject = txtSubject.getText().trim();

        currentTutorList = userDAO.searchTutors(keyword, subject);
        tableModel.setRowCount(0);

        int stt = 1;
        for (User tutor : currentTutorList) {
            TutorProfile profile = tutorProfileDAO.findByTutorId(tutor.getId());
            String subjects = profile != null && profile.getSubjectsCanTeach() != null ? profile.getSubjectsCanTeach() : "Chưa cập nhật";
            String rating = profile != null ? String.format("%.1f ★", profile.getRating()) : "5.0 ★";

            tableModel.addRow(new Object[]{
                stt++,
                tutor.getDisplayName(),
                subjects,
                rating,
                tutor.getEmail() != null ? tutor.getEmail() : "-",
                tutor.getId()
            });
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == btnSearch) {
            searchTutors();
        } else if (e.getSource() == btnViewDetail) {
            int selectedRow = tblTutors.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn một gia sư trong bảng!", "Thông báo", JOptionPane.WARNING_MESSAGE);
                return;
            }

            User selectedTutor = currentTutorList.get(selectedRow);
            new TutorDetailView(this, currentStudent, selectedTutor).setVisible(true);
        }
    }
}
