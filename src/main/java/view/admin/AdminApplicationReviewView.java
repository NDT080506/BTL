package view.admin;

import dao.ActionLogDAO;
import dao.TutorApplicationDAO;
import dao.TutorProfileDAO;
import dao.UserDAO;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import model.ActionLog;
import model.TutorApplication;
import model.TutorProfile;
import model.User;

public class AdminApplicationReviewView extends JDialog implements ActionListener {
    private AdminHomeView parent;
    private User currentAdmin;

    private TutorApplicationDAO appDAO;
    private UserDAO userDAO;
    private TutorProfileDAO tutorProfileDAO;
    private ActionLogDAO actionLogDAO;

    private JTable tblApps;
    private DefaultTableModel tableModel;
    private JTextArea txtDetail;
    private JTextField txtAdminNote;
    private JButton btnApprove;
    private JButton btnReject;
    private JButton btnRefresh;
    private JButton btnClose;

    private List<TutorApplication> pendingList;

    public AdminApplicationReviewView(AdminHomeView parent, User admin) {
        super(parent, "Xét duyệt hồ sơ ứng tuyển gia sư", true);
        this.parent = parent;
        this.currentAdmin = admin;

        this.appDAO = new TutorApplicationDAO();
        this.userDAO = new UserDAO();
        this.tutorProfileDAO = new TutorProfileDAO();
        this.actionLogDAO = new ActionLogDAO();

        initComponents();
        loadApplications();
    }

    private void initComponents() {
        setSize(850, 520);
        setLocationRelativeTo(parent);

        JPanel mainPanel = new JPanel(new BorderLayout(12, 12));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JLabel lblTitle = new JLabel("DANH SÁCH HỒ SƠ ỨNG TUYỂN ĐANG CHỜ XÉT DUYỆT (PENDING)");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 15));
        lblTitle.setForeground(new Color(24, 119, 242));
        mainPanel.add(lblTitle, BorderLayout.NORTH);

        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        splitPane.setResizeWeight(0.5);

        String[] cols = {"STT", "Ứng viên", "Môn đăng ký dạy", "Đường dẫn CV", "Điểm test", "Ngày nộp"};
        tableModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int col) { return false; }
        };
        tblApps = new JTable(tableModel);
        tblApps.setRowHeight(25);
        tblApps.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tblApps.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) showApplicationDetail();
        });

        splitPane.setTopComponent(new JScrollPane(tblApps));

        JPanel detailPanel = new JPanel(new BorderLayout(8, 8));
        detailPanel.setBorder(BorderFactory.createTitledBorder("Chi tiết hồ sơ & Ghi chú quản trị"));

        txtDetail = new JTextArea(4, 40);
        txtDetail.setEditable(false);
        txtDetail.setBackground(new Color(245, 245, 245));
        txtDetail.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        detailPanel.add(new JScrollPane(txtDetail), BorderLayout.CENTER);

        JPanel notePanel = new JPanel(new BorderLayout(5, 5));
        notePanel.add(new JLabel("Lý do / Ghi chú phản hồi:"), BorderLayout.WEST);
        txtAdminNote = new JTextField();
        notePanel.add(txtAdminNote, BorderLayout.CENTER);
        detailPanel.add(notePanel, BorderLayout.SOUTH);

        splitPane.setBottomComponent(detailPanel);
        mainPanel.add(splitPane, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 5));
        btnRefresh = new JButton("Làm mới");
        btnRefresh.addActionListener(this);

        btnApprove = new JButton("Chấp thuận & Cấp quyền Gia sư");
        btnApprove.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnApprove.setBackground(new Color(40, 167, 69));
        btnApprove.setForeground(Color.WHITE);
        btnApprove.setFocusPainted(false);
        btnApprove.addActionListener(this);

        btnReject = new JButton("Từ chối hồ sơ");
        btnReject.setBackground(new Color(220, 53, 69));
        btnReject.setForeground(Color.WHITE);
        btnReject.setFocusPainted(false);
        btnReject.addActionListener(this);

        btnClose = new JButton("Đóng");
        btnClose.addActionListener(this);

        bottomPanel.add(btnRefresh);
        bottomPanel.add(btnApprove);
        bottomPanel.add(btnReject);
        bottomPanel.add(btnClose);
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);

        add(mainPanel);
    }

    private void loadApplications() {
        pendingList = appDAO.findPending();
        tableModel.setRowCount(0);

        int stt = 1;
        for (TutorApplication app : pendingList) {
            User applicant = userDAO.findById(app.getUserId());
            String name = applicant != null ? applicant.getDisplayName() : "N/A";

            tableModel.addRow(new Object[]{
                stt++,
                name,
                app.getSubjectsCanTeach(),
                app.getCvUrl(),
                app.getTestSubmission() != null ? app.getTestSubmission() : "-",
                app.getCreatedAt()
            });
        }
        txtDetail.setText("");
    }

    private void showApplicationDetail() {
        int selectedRow = tblApps.getSelectedRow();
        if (selectedRow >= 0 && selectedRow < pendingList.size()) {
            TutorApplication app = pendingList.get(selectedRow);
            User applicant = userDAO.findById(app.getUserId());
            StringBuilder sb = new StringBuilder();
            sb.append("Ứng viên: ").append(applicant != null ? applicant.getDisplayName() : "N/A")
              .append(" (Username: ").append(applicant != null ? applicant.getUsername() : "").append(")\n");
            sb.append("Môn đăng ký: ").append(app.getSubjectsCanTeach()).append("\n");
            sb.append("Đường dẫn CV: ").append(app.getCvUrl()).append("\n");
            sb.append("Bài test năng lực: ").append(app.getTestSubmission() != null ? app.getTestSubmission() : "Không có").append("\n");
            sb.append("Tự giới thiệu kinh nghiệm: ").append(app.getBio()).append("\n");
            txtDetail.setText(sb.toString());
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == btnRefresh) {
            loadApplications();
        } else if (e.getSource() == btnClose) {
            this.dispose();
        } else if (e.getSource() == btnApprove || e.getSource() == btnReject) {
            int selectedRow = tblApps.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn một hồ sơ cần xét duyệt!", "Thông báo", JOptionPane.WARNING_MESSAGE);
                return;
            }

            TutorApplication app = pendingList.get(selectedRow);
            boolean isApprove = (e.getSource() == btnApprove);
            String note = txtAdminNote.getText().trim();

            if (isApprove) {
                int confirm = JOptionPane.showConfirmDialog(this,
                    "Xác nhận CHẤP THUẬN hồ sơ này?\nNgười dùng sẽ được nâng cấp lên vai trò TUTOR và tạo hồ sơ chuyên môn.",
                    "Xác nhận phê duyệt", JOptionPane.YES_NO_OPTION);

                if (confirm == JOptionPane.YES_OPTION) {

                    appDAO.updateStatus(app.getId(), "APPROVED", note.isEmpty() ? "Đã duyệt hồ sơ hợp lệ" : note);

                    userDAO.updateRole(app.getUserId(), "TUTOR");

                    TutorProfile profile = new TutorProfile(app.getUserId(), app.getBio(), app.getSubjectsCanTeach(), 5.0);
                    tutorProfileDAO.insert(profile);

                    actionLogDAO.insert(new ActionLog(currentAdmin.getId(), "REVIEW_TUTOR_APPLICATION", "TutorApplication", app.getId()));

                    JOptionPane.showMessageDialog(this, "Phê duyệt hồ sơ thành công! Đã cấp quyền Gia sư.", "Thành công", JOptionPane.INFORMATION_MESSAGE);
                    loadApplications();
                }
            } else {
                if (note.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Vui lòng nhập lý do từ chối vào ô Ghi chú!", "Thiếu lý do", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                int confirm = JOptionPane.showConfirmDialog(this, "Xác nhận TỪ CHỐI hồ sơ này?", "Xác nhận từ chối", JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    appDAO.updateStatus(app.getId(), "REJECTED", note);
                    actionLogDAO.insert(new ActionLog(currentAdmin.getId(), "REVIEW_TUTOR_APPLICATION", "TutorApplication", app.getId()));

                    JOptionPane.showMessageDialog(this, "Đã từ chối hồ sơ ứng tuyển.", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                    loadApplications();
                }
            }
        }
    }
}
