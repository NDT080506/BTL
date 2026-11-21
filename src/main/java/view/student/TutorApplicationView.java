package view.student;

import dao.ActionLogDAO;
import dao.TutorApplicationDAO;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;
import model.ActionLog;
import model.TutorApplication;
import model.User;

public class TutorApplicationView extends JDialog implements ActionListener {
    private StudentHomeView parent;
    private User student;

    private TutorApplicationDAO appDAO;
    private ActionLogDAO actionLogDAO;

    private TutorApplication existingApp;

    private JPanel cardPanel;
    private CardLayout cardLayout;

    private JTextArea txtBio;
    private JTextField txtSubjects;
    private JTextField txtCvUrl;
    private JTextField txtTestScore;
    private JButton btnSubmit;

    private JLabel lblStatus;
    private JLabel lblSubmittedAt;
    private JTextArea txtAdminNote;
    private JButton btnCancelApp;

    private JButton btnClose;

    public TutorApplicationView(StudentHomeView parent, User student) {
        super(parent, "Ứng tuyển làm Gia sư TutorTech", true);
        this.parent = parent;
        this.student = student;
        this.appDAO = new TutorApplicationDAO();
        this.actionLogDAO = new ActionLogDAO();

        initComponents();
        loadExistingApplication();
    }

    private void initComponents() {
        setSize(550, 480);
        setLocationRelativeTo(parent);

        JPanel mainPanel = new JPanel(new BorderLayout(12, 12));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        JLabel lblTitle = new JLabel("HỒ SƠ ỨNG TUYỂN GIA SƯ", JLabel.CENTER);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblTitle.setForeground(new Color(24, 119, 242));
        mainPanel.add(lblTitle, BorderLayout.NORTH);

        cardLayout = new CardLayout();
        cardPanel = new JPanel(cardLayout);

        JPanel newAppPanel = createNewApplicationPanel();
        cardPanel.add(newAppPanel, "NEW_APP");

        JPanel viewAppPanel = createViewApplicationPanel();
        cardPanel.add(viewAppPanel, "VIEW_APP");

        mainPanel.add(cardPanel, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnClose = new JButton("Đóng");
        btnClose.addActionListener(e -> this.dispose());
        bottomPanel.add(btnClose);
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);

        add(mainPanel);
    }

    private JPanel createNewApplicationPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Điền thông tin hồ sơ chuyên môn"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0.3;
        panel.add(new JLabel("Môn có thể dạy (*):"), gbc);
        txtSubjects = new JTextField("Toán, Tiếng Anh");
        gbc.gridx = 1; gbc.gridy = 0; gbc.weightx = 0.7;
        panel.add(txtSubjects, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(new JLabel("Đường dẫn CV (*):"), gbc);
        txtCvUrl = new JTextField("https://drive.google.com/cv-minh-chung.pdf");
        gbc.gridx = 1; gbc.gridy = 1;
        panel.add(txtCvUrl, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        panel.add(new JLabel("Điểm/Minh chứng bài test:"), gbc);
        txtTestScore = new JTextField("Đạt 9.0/10 bài kiểm tra đầu vào");
        gbc.gridx = 1; gbc.gridy = 2;
        panel.add(txtTestScore, gbc);

        gbc.gridx = 0; gbc.gridy = 3;
        panel.add(new JLabel("Giới thiệu bản thân (*):"), gbc);
        txtBio = new JTextArea(4, 20);
        txtBio.setLineWrap(true);
        txtBio.setWrapStyleWord(true);
        txtBio.setText("Có 3 năm kinh nghiệm trợ giảng và dạy kèm học sinh ôn thi vào lớp 10.");
        gbc.gridx = 1; gbc.gridy = 3;
        panel.add(new JScrollPane(txtBio), gbc);

        btnSubmit = new JButton("Gửi hồ sơ ứng tuyển");
        btnSubmit.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnSubmit.setBackground(new Color(40, 167, 69));
        btnSubmit.setForeground(Color.WHITE);
        btnSubmit.setFocusPainted(false);
        btnSubmit.addActionListener(this);
        gbc.gridx = 1; gbc.gridy = 4;
        panel.add(btnSubmit, gbc);

        return panel;
    }

    private JPanel createViewApplicationPanel() {
        JPanel panel = new JPanel(new GridLayout(5, 1, 8, 8));
        panel.setBorder(BorderFactory.createTitledBorder("Trạng thái hồ sơ đã gửi"));

        lblStatus = new JLabel("Trạng thái: Đang chờ xem xét");
        lblStatus.setFont(new Font("Segoe UI", Font.BOLD, 14));

        lblSubmittedAt = new JLabel("Ngày gửi: -");
        lblSubmittedAt.setFont(new Font("Segoe UI", Font.PLAIN, 12));

        txtAdminNote = new JTextArea(2, 20);
        txtAdminNote.setEditable(false);
        txtAdminNote.setBackground(new Color(245, 245, 245));
        txtAdminNote.setBorder(BorderFactory.createTitledBorder("Nhận xét từ Quản trị viên"));

        btnCancelApp = new JButton("Hủy hồ sơ ứng tuyển này");
        btnCancelApp.setBackground(new Color(220, 53, 69));
        btnCancelApp.setForeground(Color.WHITE);
        btnCancelApp.setFocusPainted(false);
        btnCancelApp.addActionListener(this);

        panel.add(lblStatus);
        panel.add(lblSubmittedAt);
        panel.add(new JScrollPane(txtAdminNote));
        panel.add(btnCancelApp);

        return panel;
    }

    private void loadExistingApplication() {
        existingApp = appDAO.findByUserId(student.getId());
        if (existingApp != null) {
            cardLayout.show(cardPanel, "VIEW_APP");
            String status = existingApp.getStatus();
            if ("PENDING".equalsIgnoreCase(status)) {
                lblStatus.setText("Trạng thái: ĐANG CHỜ XEM XÉT (PENDING)");
                lblStatus.setForeground(new Color(255, 153, 0));
                btnCancelApp.setEnabled(true);
            } else if ("APPROVED".equalsIgnoreCase(status)) {
                lblStatus.setText("Trạng thái: ĐÃ ĐƯỢC PHÊ DUYỆT (APPROVED) - BẠN ĐÃ LÀ GIA SƯ!");
                lblStatus.setForeground(new Color(40, 167, 69));
                btnCancelApp.setEnabled(false);
            } else {
                lblStatus.setText("Trạng thái: BỊ TỪ CHỐI (REJECTED)");
                lblStatus.setForeground(Color.RED);
                btnCancelApp.setEnabled(false);
            }
            lblSubmittedAt.setText("Ngày nộp: " + existingApp.getCreatedAt());
            txtAdminNote.setText(existingApp.getAdminNote() != null ? existingApp.getAdminNote() : "Chưa có nhận xét.");
        } else {
            cardLayout.show(cardPanel, "NEW_APP");
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == btnSubmit) {
            String subjects = txtSubjects.getText().trim();
            String cvUrl = txtCvUrl.getText().trim();
            String bio = txtBio.getText().trim();
            String testScore = txtTestScore.getText().trim();

            if (subjects.isEmpty() || cvUrl.isEmpty() || bio.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Vui lòng điền đầy đủ các thông tin có dấu (*)", "Thiếu thông tin", JOptionPane.WARNING_MESSAGE);
                return;
            }

            TutorApplication app = new TutorApplication(student.getId(), bio, subjects, cvUrl, testScore);
            boolean success = appDAO.insert(app);
            if (success) {

                actionLogDAO.insert(new ActionLog(student.getId(), "APPLY_TUTOR", "TutorApplication", app.getId()));

                JOptionPane.showMessageDialog(this, "Nộp hồ sơ ứng tuyển gia sư thành công!\nHồ sơ đang được chuyển đến Quản trị viên để xét duyệt.", "Thành công", JOptionPane.INFORMATION_MESSAGE);
                loadExistingApplication();
            } else {
                JOptionPane.showMessageDialog(this, "Lỗi khi nộp hồ sơ!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        } else if (e.getSource() == btnCancelApp) {
            if (existingApp != null && "PENDING".equalsIgnoreCase(existingApp.getStatus())) {
                int confirm = JOptionPane.showConfirmDialog(this, "Bạn có chắc chắn muốn hủy hồ sơ ứng tuyển này không?", "Xác nhận hủy", JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    boolean ok = appDAO.deletePending(existingApp.getId());
                    if (ok) {
                        JOptionPane.showMessageDialog(this, "Đã hủy hồ sơ thành công!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                        loadExistingApplication();
                    }
                }
            }
        }
    }
}
