package view.admin;

import dao.BrokeragePackageDAO;
import dao.UserDAO;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import model.BrokeragePackage;
import model.User;

public class AdminPackageInquiryView extends JDialog implements ActionListener {
    private AdminHomeView parent;

    private BrokeragePackageDAO packageDAO;
    private UserDAO userDAO;

    private JTextField txtKeyword;
    private JComboBox<String> cmbActiveFilter;
    private JButton btnSearch;
    private JButton btnRefresh;
    private JButton btnClose;

    private JTable tblPackages;
    private DefaultTableModel tableModel;
    private JTextArea txtDetail;

    private List<BrokeragePackage> packageList;

    public AdminPackageInquiryView(AdminHomeView parent) {
        super(parent, "Tra cứu danh sách gói dịch vụ gia sư", true);
        this.parent = parent;
        this.packageDAO = new BrokeragePackageDAO();
        this.userDAO = new UserDAO();

        initComponents();
        searchPackages();
    }

    private void initComponents() {
        setSize(850, 500);
        setLocationRelativeTo(parent);

        JPanel mainPanel = new JPanel(new BorderLayout(12, 12));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        filterPanel.setBorder(BorderFactory.createTitledBorder("Bộ lọc gói dịch vụ"));

        filterPanel.add(new JLabel("Tên gói dịch vụ:"));
        txtKeyword = new JTextField(15);
        filterPanel.add(txtKeyword);

        filterPanel.add(new JLabel("Tình trạng:"));
        cmbActiveFilter = new JComboBox<>(new String[]{"Tất cả", "Đang hoạt động", "Đã ngừng"});
        filterPanel.add(cmbActiveFilter);

        btnSearch = new JButton("Tìm kiếm");
        btnSearch.setBackground(new Color(24, 119, 242));
        btnSearch.setForeground(Color.WHITE);
        btnSearch.setFocusPainted(false);
        btnSearch.addActionListener(this);
        filterPanel.add(btnSearch);

        mainPanel.add(filterPanel, BorderLayout.NORTH);

        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        splitPane.setResizeWeight(0.65);

        String[] cols = {"STT", "Tên gói dịch vụ", "Gia sư cung cấp", "Mức giá", "Tình trạng", "Ngày tạo"};
        tableModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };
        tblPackages = new JTable(tableModel);
        tblPackages.setRowHeight(25);
        tblPackages.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tblPackages.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) showPackageDetail();
        });

        splitPane.setTopComponent(new JScrollPane(tblPackages));

        txtDetail = new JTextArea(4, 40);
        txtDetail.setEditable(false);
        txtDetail.setBackground(new Color(245, 245, 245));
        txtDetail.setBorder(BorderFactory.createTitledBorder("Mô tả chi tiết gói học"));
        splitPane.setBottomComponent(new JScrollPane(txtDetail));

        mainPanel.add(splitPane, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 5));
        btnRefresh = new JButton("Làm mới");
        btnRefresh.addActionListener(this);

        btnClose = new JButton("Đóng");
        btnClose.addActionListener(this);

        bottomPanel.add(btnRefresh);
        bottomPanel.add(btnClose);
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);

        add(mainPanel);
    }

    private void searchPackages() {
        String kw = txtKeyword.getText().trim();
        int activeIdx = cmbActiveFilter.getSelectedIndex();
        Boolean activeOnly = activeIdx == 1 ? Boolean.TRUE : (activeIdx == 2 ? Boolean.FALSE : null);

        packageList = packageDAO.findAll(kw, activeOnly);
        tableModel.setRowCount(0);

        int stt = 1;
        for (BrokeragePackage pkg : packageList) {
            User tutor = userDAO.findById(pkg.getTutorId());
            String tutorName = tutor != null ? tutor.getDisplayName() : "N/A";

            tableModel.addRow(new Object[]{
                stt++,
                pkg.getName(),
                tutorName,
                String.format("%,d VNĐ", pkg.getPrice()),
                pkg.isActive() ? "Đang hoạt động" : "Đã tạm dừng",
                pkg.getCreatedAt()
            });
        }
        txtDetail.setText("");
    }

    private void showPackageDetail() {
        int selected = tblPackages.getSelectedRow();
        if (selected >= 0 && selected < packageList.size()) {
            BrokeragePackage pkg = packageList.get(selected);
            User tutor = userDAO.findById(pkg.getTutorId());
            StringBuilder sb = new StringBuilder();
            sb.append("Tên gói: ").append(pkg.getName()).append("\n");
            sb.append("Gia sư sở hữu: ").append(tutor != null ? tutor.getDisplayName() : "N/A")
              .append(" (Email: ").append(tutor != null ? tutor.getEmail() : "").append(")\n");
            sb.append("Mức giá: ").append(String.format("%,d VNĐ", pkg.getPrice())).append("\n");
            sb.append("Mô tả nội dung: ").append(pkg.getDescription() != null ? pkg.getDescription() : "Không có mô tả thêm.");
            txtDetail.setText(sb.toString());
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == btnSearch || e.getSource() == btnRefresh) {
            searchPackages();
        } else if (e.getSource() == btnClose) {
            this.dispose();
        }
    }
}
