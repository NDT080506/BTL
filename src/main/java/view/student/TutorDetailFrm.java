package view.student;

import dao.BrokeragePackageDAO;
import dao.TutorProfileDAO;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import model.BrokeragePackage;
import model.TutorProfile;
import model.User;

/**
 * TutorDetailFrm: Màn hình Xem hồ sơ gia sư & Danh sách gói học (CN02.03)
 */
public class TutorDetailFrm extends JDialog implements ActionListener {
    private TutorSearchFrm parent;
    private User student;
    private User tutor;

    private TutorProfileDAO tutorProfileDAO;
    private BrokeragePackageDAO packageDAO;

    private JTextArea txtBio;
    private JLabel lblSubjects;
    private JLabel lblRating;
    private JTable tblPackages;
    private DefaultTableModel packageModel;
    private JButton btnBook;
    private JButton btnClose;

    private List<BrokeragePackage> packageList;

    public TutorDetailFrm(TutorSearchFrm parent, User student, User tutor) {
        super(parent, "Hồ sơ gia sư: " + tutor.getDisplayName(), true);
        this.parent = parent;
        this.student = student;
        this.tutor = tutor;
        this.tutorProfileDAO = new TutorProfileDAO();
        this.packageDAO = new BrokeragePackageDAO();

        initComponents();
        loadData();
    }

    private void initComponents() {
        setSize(650, 500);
        setLocationRelativeTo(parent);

        JPanel mainPanel = new JPanel(new BorderLayout(12, 12));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // Info Panel
        JPanel infoPanel = new JPanel(new BorderLayout(8, 8));
        infoPanel.setBorder(BorderFactory.createTitledBorder("Thông tin gia sư"));

        JPanel headerInfo = new JPanel(new GridLayout(2, 1, 4, 4));
        JLabel lblName = new JLabel("Họ tên: " + tutor.getDisplayName());
        lblName.setFont(new Font("Segoe UI", Font.BOLD, 15));

        lblSubjects = new JLabel("Môn có thể dạy: Đang tải...");
        lblSubjects.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblRating = new JLabel("Đánh giá: 5.0 ★");
        lblRating.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblRating.setForeground(new Color(255, 153, 0));

        JPanel subPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
        subPanel.add(lblSubjects);
        subPanel.add(lblRating);

        headerInfo.add(lblName);
        headerInfo.add(subPanel);
        infoPanel.add(headerInfo, BorderLayout.NORTH);

        txtBio = new JTextArea(3, 40);
        txtBio.setLineWrap(true);
        txtBio.setWrapStyleWord(true);
        txtBio.setEditable(false);
        txtBio.setBackground(new Color(245, 245, 245));
        txtBio.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        infoPanel.add(new JScrollPane(txtBio), BorderLayout.CENTER);

        mainPanel.add(infoPanel, BorderLayout.NORTH);

        // Packages Panel
        JPanel packagesPanel = new JPanel(new BorderLayout(8, 8));
        packagesPanel.setBorder(BorderFactory.createTitledBorder("Các gói dịch vụ đang cung cấp"));

        String[] cols = {"Tên gói dịch vụ", "Chi tiết / Mô tả", "Giá dịch vụ"};
        packageModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int col) { return false; }
        };
        tblPackages = new JTable(packageModel);
        tblPackages.setRowHeight(26);
        tblPackages.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        packagesPanel.add(new JScrollPane(tblPackages), BorderLayout.CENTER);

        mainPanel.add(packagesPanel, BorderLayout.CENTER);

        // Bottom Buttons
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 5));
        btnBook = new JButton("Đặt lịch với gói này");
        btnBook.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnBook.setBackground(new Color(40, 167, 69));
        btnBook.setForeground(Color.WHITE);
        btnBook.setFocusPainted(false);
        btnBook.addActionListener(this);

        btnClose = new JButton("Đóng");
        btnClose.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        btnClose.addActionListener(this);

        bottomPanel.add(btnBook);
        bottomPanel.add(btnClose);
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);

        add(mainPanel);
    }

    private void loadData() {
        TutorProfile profile = tutorProfileDAO.findByTutorId(tutor.getId());
        if (profile != null) {
            lblSubjects.setText("Môn có thể dạy: " + (profile.getSubjectsCanTeach() != null ? profile.getSubjectsCanTeach() : "-"));
            lblRating.setText(String.format("Đánh giá: %.1f ★", profile.getRating()));
            txtBio.setText("Giới thiệu: " + (profile.getBio() != null ? profile.getBio() : "Chưa có lời tự giới thiệu."));
        } else {
            lblSubjects.setText("Môn có thể dạy: Chưa cập nhật");
            txtBio.setText("Chưa có thông tin hồ sơ chi tiết.");
        }

        packageList = packageDAO.findActiveByTutorId(tutor.getId());
        packageModel.setRowCount(0);

        for (BrokeragePackage pkg : packageList) {
            packageModel.addRow(new Object[]{
                pkg.getName(),
                pkg.getDescription() != null ? pkg.getDescription() : "-",
                String.format("%,d VNĐ", pkg.getPrice())
            });
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == btnBook) {
            int selectedRow = tblPackages.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn một gói học trong bảng để đặt lịch!", "Thông báo", JOptionPane.WARNING_MESSAGE);
                return;
            }
            BrokeragePackage selectedPackage = packageList.get(selectedRow);
            new BookingRequestFrm(this, student, tutor, selectedPackage).setVisible(true);
        } else if (e.getSource() == btnClose) {
            this.dispose();
        }
    }
}
