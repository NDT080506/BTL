package view.admin;

import dao.ActionLogDAO;
import dao.UserDAO;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import model.ActionLog;
import model.User;

/**
 * AdminActionLogInquiryFrm: Quản trị tra cứu nhật ký thao tác (CN06.07)
 */
public class AdminActionLogInquiryFrm extends JDialog implements ActionListener {
    private AdminHomeFrm parent;

    private ActionLogDAO actionLogDAO;
    private UserDAO userDAO;

    private JComboBox<String> cmbAction;
    private JTextField txtTargetType;
    private JButton btnSearch;
    private JButton btnRefresh;
    private JButton btnClose;

    private JTable tblLogs;
    private DefaultTableModel tableModel;
    private JTextArea txtDetail;

    private List<ActionLog> logList;

    public AdminActionLogInquiryFrm(AdminHomeFrm parent) {
        super(parent, "Tra cứu nhật ký thao tác hệ thống (Audit Logs)", true);
        this.parent = parent;
        this.actionLogDAO = new ActionLogDAO();
        this.userDAO = new UserDAO();

        initComponents();
        searchLogs();
    }

    private void initComponents() {
        setSize(880, 520);
        setLocationRelativeTo(parent);

        JPanel mainPanel = new JPanel(new BorderLayout(12, 12));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // Filter
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        filterPanel.setBorder(BorderFactory.createTitledBorder("Bộ lọc nhật ký thao tác"));

        filterPanel.add(new JLabel("Loại thao tác:"));
        cmbAction = new JComboBox<>(new String[]{
            "ALL", 
            "APPLY_TUTOR", 
            "UPDATE_USER_STATUS", 
            "REVIEW_TUTOR_APPLICATION", 
            "RESOLVE_DISPUTE"
        });
        filterPanel.add(cmbAction);

        filterPanel.add(new JLabel("Loại đối tượng (Target):"));
        txtTargetType = new JTextField(10);
        filterPanel.add(txtTargetType);

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

        String[] cols = {"STT", "Thời điểm", "Người thao tác", "Hành động (Action)", "Đối tượng", "Target ID", "Địa chỉ IP"};
        tableModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };
        tblLogs = new JTable(tableModel);
        tblLogs.setRowHeight(25);
        tblLogs.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tblLogs.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) showLogDetail();
        });

        splitPane.setTopComponent(new JScrollPane(tblLogs));

        txtDetail = new JTextArea(4, 40);
        txtDetail.setEditable(false);
        txtDetail.setBackground(new Color(245, 245, 245));
        txtDetail.setBorder(BorderFactory.createTitledBorder("Chi tiết bản ghi nhật ký kiểm toán"));
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

    private void searchLogs() {
        String action = (String) cmbAction.getSelectedItem();
        String targetType = txtTargetType.getText().trim();

        logList = actionLogDAO.search(action, targetType);
        tableModel.setRowCount(0);

        int stt = 1;
        for (ActionLog log : logList) {
            User actor = log.getUserId() != null ? userDAO.findById(log.getUserId()) : null;
            String actorName = actor != null ? actor.getDisplayName() : "Hệ thống";

            tableModel.addRow(new Object[]{
                stt++,
                log.getCreatedAt(),
                actorName,
                log.getAction(),
                log.getTargetType() != null ? log.getTargetType() : "-",
                log.getTargetId() != null ? log.getTargetId().toString().substring(0, 8) + "..." : "-",
                log.getIpAddress() != null ? log.getIpAddress() : "-"
            });
        }
        txtDetail.setText("");
    }

    private void showLogDetail() {
        int selected = tblLogs.getSelectedRow();
        if (selected >= 0 && selected < logList.size()) {
            ActionLog log = logList.get(selected);
            User actor = log.getUserId() != null ? userDAO.findById(log.getUserId()) : null;
            StringBuilder sb = new StringBuilder();
            sb.append("Mã nhật ký: ").append(log.getId()).append("\n");
            sb.append("Thời điểm ghi nhận: ").append(log.getCreatedAt()).append("\n");
            sb.append("Người thực hiện: ").append(actor != null ? actor.getDisplayName() : "Hệ thống")
              .append(" (User ID: ").append(log.getUserId()).append(")\n");
            sb.append("Hành động: ").append(log.getAction()).append("\n");
            sb.append("Đối tượng tác động: ").append(log.getTargetType()).append(" | Target ID: ").append(log.getTargetId()).append("\n");
            sb.append("IP Address: ").append(log.getIpAddress()).append(" | User Agent: ").append(log.getUserAgent());
            txtDetail.setText(sb.toString());
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == btnSearch || e.getSource() == btnRefresh) {
            searchLogs();
        } else if (e.getSource() == btnClose) {
            this.dispose();
        }
    }
}
