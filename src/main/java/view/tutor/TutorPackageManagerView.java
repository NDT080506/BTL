package view.tutor;

import dao.BrokeragePackageDAO;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import model.BrokeragePackage;
import model.User;

public class TutorPackageManagerView extends JDialog implements ActionListener {
    private TutorHomeView parent;
    private User tutor;

    private BrokeragePackageDAO packageDAO;

    private JTable tblPackages;
    private DefaultTableModel tableModel;
    private JTextField txtName;
    private JTextField txtPrice;
    private JTextArea txtDesc;
    private JButton btnAddPackage;
    private JButton btnRefresh;
    private JButton btnClose;

    private List<BrokeragePackage> packageList;

    public TutorPackageManagerView(TutorHomeView parent, User tutor) {
        super(parent, "Quản lý gói dịch vụ giảng dạy", true);
        this.parent = parent;
        this.tutor = tutor;
        this.packageDAO = new BrokeragePackageDAO();

        initComponents();
        loadPackages();
    }

    private void initComponents() {
        setSize(750, 480);
        setLocationRelativeTo(parent);

        JPanel mainPanel = new JPanel(new BorderLayout(12, 12));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JLabel lblTitle = new JLabel("CÁC GÓI DỊCH VỤ CỦA BẠN", JLabel.CENTER);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 15));
        lblTitle.setForeground(new Color(24, 119, 242));
        mainPanel.add(lblTitle, BorderLayout.NORTH);

        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        splitPane.setResizeWeight(0.6);

        String[] cols = {"STT", "Tên gói", "Mô tả", "Học phí", "Trạng thái"};
        tableModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };
        tblPackages = new JTable(tableModel);
        tblPackages.setRowHeight(25);
        splitPane.setTopComponent(new JScrollPane(tblPackages));

        JPanel addPanel = new JPanel(new GridBagLayout());
        addPanel.setBorder(BorderFactory.createTitledBorder("Thêm gói dịch vụ mới"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 6, 4, 6);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0.25;
        addPanel.add(new JLabel("Tên gói học (*):"), gbc);
        txtName = new JTextField(15);
        gbc.gridx = 1; gbc.gridy = 0; gbc.weightx = 0.75;
        addPanel.add(txtName, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        addPanel.add(new JLabel("Học phí (VNĐ) (*):"), gbc);
        txtPrice = new JTextField(15);
        gbc.gridx = 1; gbc.gridy = 1;
        addPanel.add(txtPrice, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        addPanel.add(new JLabel("Mô tả nội dung:"), gbc);
        txtDesc = new JTextArea(2, 15);
        txtDesc.setLineWrap(true);
        txtDesc.setWrapStyleWord(true);
        gbc.gridx = 1; gbc.gridy = 2;
        addPanel.add(new JScrollPane(txtDesc), gbc);

        btnAddPackage = new JButton("Thêm gói dịch vụ");
        btnAddPackage.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnAddPackage.setBackground(new Color(40, 167, 69));
        btnAddPackage.setForeground(Color.WHITE);
        btnAddPackage.setFocusPainted(false);
        btnAddPackage.addActionListener(this);
        gbc.gridx = 1; gbc.gridy = 3;
        addPanel.add(btnAddPackage, gbc);

        splitPane.setBottomComponent(addPanel);
        mainPanel.add(splitPane, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnRefresh = new JButton("Làm mới");
        btnRefresh.addActionListener(this);
        btnClose = new JButton("Đóng");
        btnClose.addActionListener(e -> this.dispose());
        bottomPanel.add(btnRefresh);
        bottomPanel.add(btnClose);
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);

        add(mainPanel);
    }

    private void loadPackages() {
        packageList = packageDAO.findActiveByTutorId(tutor.getId());
        tableModel.setRowCount(0);

        int stt = 1;
        for (BrokeragePackage pkg : packageList) {
            tableModel.addRow(new Object[]{
                stt++,
                pkg.getName(),
                pkg.getDescription() != null ? pkg.getDescription() : "-",
                String.format("%,d VNĐ", pkg.getPrice()),
                pkg.isActive() ? "Đang hoạt động" : "Tạm dừng"
            });
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == btnRefresh) {
            loadPackages();
        } else if (e.getSource() == btnAddPackage) {
            String name = txtName.getText().trim();
            String priceStr = txtPrice.getText().trim();
            String desc = txtDesc.getText().trim();

            if (name.isEmpty() || priceStr.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Vui lòng nhập tên gói và học phí!", "Thiếu thông tin", JOptionPane.WARNING_MESSAGE);
                return;
            }

            long price;
            try {
                price = Long.parseLong(priceStr);
                if (price <= 0) {
                    JOptionPane.showMessageDialog(this, "Học phí phải lớn hơn 0!", "Lỗi học phí", JOptionPane.WARNING_MESSAGE);
                    return;
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Học phí phải là số nguyên hợp lệ!", "Lỗi định dạng", JOptionPane.ERROR_MESSAGE);
                return;
            }

            BrokeragePackage pkg = new BrokeragePackage(tutor.getId(), name, desc, price, true);
            boolean ok = packageDAO.insert(pkg);
            if (ok) {
                JOptionPane.showMessageDialog(this, "Thêm gói dịch vụ thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
                txtName.setText("");
                txtPrice.setText("");
                txtDesc.setText("");
                loadPackages();
            } else {
                JOptionPane.showMessageDialog(this, "Lỗi khi lưu gói dịch vụ!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
