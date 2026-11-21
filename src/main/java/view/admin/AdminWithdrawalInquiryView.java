package view.admin;

import dao.UserDAO;
import dao.WithdrawalDAO;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import model.User;
import model.Withdrawal;

public class AdminWithdrawalInquiryView extends JDialog implements ActionListener {
    private AdminHomeView parent;

    private WithdrawalDAO withdrawalDAO;
    private UserDAO userDAO;

    private JTextField txtKeyword;
    private JComboBox<String> cmbStatusFilter;
    private JButton btnSearch;
    private JButton btnRefresh;
    private JButton btnClose;

    private JTable tblWithdrawals;
    private DefaultTableModel tableModel;
    private JTextArea txtDetail;

    private List<Withdrawal> withdrawalList;

    public AdminWithdrawalInquiryView(AdminHomeView parent) {
        super(parent, "Tra cứu danh sách yêu cầu rút tiền", true);
        this.parent = parent;
        this.withdrawalDAO = new WithdrawalDAO();
        this.userDAO = new UserDAO();

        initComponents();
        searchWithdrawals();
    }

    private void initComponents() {
        setSize(880, 520);
        setLocationRelativeTo(parent);

        JPanel mainPanel = new JPanel(new BorderLayout(12, 12));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // Filter
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        filterPanel.setBorder(BorderFactory.createTitledBorder("Bộ lọc tra cứu rút tiền"));

        filterPanel.add(new JLabel("Ngân hàng / Tên chủ thẻ:"));
        txtKeyword = new JTextField(15);
        filterPanel.add(txtKeyword);

        filterPanel.add(new JLabel("Trạng thái:"));
        cmbStatusFilter = new JComboBox<>(new String[]{"ALL", "PENDING", "PROCESSING", "COMPLETED", "REJECTED"});
        filterPanel.add(cmbStatusFilter);

        btnSearch = new JButton("Tìm kiếm");
        btnSearch.setBackground(new Color(24, 119, 242));
        btnSearch.setForeground(Color.WHITE);
        btnSearch.setFocusPainted(false);
        btnSearch.addActionListener(this);
        filterPanel.add(btnSearch);

        mainPanel.add(filterPanel, BorderLayout.NORTH);

        // Center Split
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        splitPane.setResizeWeight(0.65);

        String[] cols = {"STT", "Người đề nghị", "Số tiền rút", "Ngân hàng", "Số tài khoản (đã che)", "Chủ tài khoản", "Trạng thái", "Ngày tạo"};
        tableModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };
        tblWithdrawals = new JTable(tableModel);
        tblWithdrawals.setRowHeight(25);
        tblWithdrawals.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tblWithdrawals.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) showWithdrawalDetail();
        });

        splitPane.setTopComponent(new JScrollPane(tblWithdrawals));

        txtDetail = new JTextArea(4, 40);
        txtDetail.setEditable(false);
        txtDetail.setBackground(new Color(245, 245, 245));
        txtDetail.setBorder(BorderFactory.createTitledBorder("Thông tin chi tiết yêu cầu rút tiền"));
        splitPane.setBottomComponent(new JScrollPane(txtDetail));

        mainPanel.add(splitPane, BorderLayout.CENTER);

        // Bottom
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

    private void searchWithdrawals() {
        String kw = txtKeyword.getText().trim();
        String status = (String) cmbStatusFilter.getSelectedItem();

        withdrawalList = withdrawalDAO.findAll(kw, status);
        tableModel.setRowCount(0);

        int stt = 1;
        for (Withdrawal w : withdrawalList) {
            User user = userDAO.findById(w.getUserId());
            String userName = user != null ? user.getDisplayName() : "N/A";

            tableModel.addRow(new Object[]{
                stt++,
                userName,
                String.format("%,d VNĐ", w.getAmount()),
                w.getBankName(),
                w.getMaskedAccountNumber(),
                w.getAccountHolder(),
                w.getStatus(),
                w.getCreatedAt()
            });
        }
        txtDetail.setText("");
    }

    private void showWithdrawalDetail() {
        int selected = tblWithdrawals.getSelectedRow();
        if (selected >= 0 && selected < withdrawalList.size()) {
            Withdrawal w = withdrawalList.get(selected);
            User user = userDAO.findById(w.getUserId());
            StringBuilder sb = new StringBuilder();
            sb.append("Mã yêu cầu: ").append(w.getId()).append("\n");
            sb.append("Người đề nghị rút: ").append(user != null ? user.getDisplayName() : "N/A")
              .append(" (Username: ").append(user != null ? user.getUsername() : "").append(")\n");
            sb.append("Số tiền: ").append(String.format("%,d VNĐ", w.getAmount())).append("\n");
            sb.append("Ngân hàng nhận: ").append(w.getBankName()).append(" | Số tài khoản: ").append(w.getMaskedAccountNumber())
              .append(" | Tên chủ tài khoản: ").append(w.getAccountHolder()).append("\n");
            sb.append("Trạng thái hiện tại: ").append(w.getStatus()).append("\n");
            sb.append("Lưu ý: Màn hình này phục vụ tra cứu thông tin; chưa thực hiện thao tác duyệt/chi trả trực tiếp tại đây.");
            txtDetail.setText(sb.toString());
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == btnSearch || e.getSource() == btnRefresh) {
            searchWithdrawals();
        } else if (e.getSource() == btnClose) {
            this.dispose();
        }
    }
}
