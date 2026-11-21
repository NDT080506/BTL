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

public class AdminUserManagementView extends JDialog implements ActionListener {
    private AdminHomeView parent;
    private User currentAdmin;

    private UserDAO userDAO;
    private ActionLogDAO actionLogDAO;

    private JTextField txtKeyword;
    private JComboBox<String> cmbRoleFilter;
    private JComboBox<String> cmbStatusFilter;
    private JButton btnSearch;
    private JButton btnLock;
    private JButton btnUnlock;
    private JButton btnRefresh;
    private JButton btnClose;

    private JTable tblUsers;
    private DefaultTableModel tableModel;
    private List<User> userList;

    public AdminUserManagementView(AdminHomeView parent, User admin) {
        super(parent, "Quản trị người dùng & Trạng thái tài khoản", true);
        this.parent = parent;
        this.currentAdmin = admin;
        this.userDAO = new UserDAO();
        this.actionLogDAO = new ActionLogDAO();

        initComponents();
        searchUsers();
    }

    private void initComponents() {
        setSize(850, 500);
        setLocationRelativeTo(parent);

        JPanel mainPanel = new JPanel(new BorderLayout(12, 12));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        filterPanel.setBorder(BorderFactory.createTitledBorder("Bộ lọc người dùng"));

        filterPanel.add(new JLabel("Từ khóa:"));
        txtKeyword = new JTextField(12);
        filterPanel.add(txtKeyword);

        filterPanel.add(new JLabel("Vai trò:"));
        cmbRoleFilter = new JComboBox<>(new String[]{"ALL", "STUDENT", "TUTOR", "ADMIN"});
        filterPanel.add(cmbRoleFilter);

        filterPanel.add(new JLabel("Trạng thái:"));
        cmbStatusFilter = new JComboBox<>(new String[]{"ALL", "ACTIVE", "LOCKED"});
        filterPanel.add(cmbStatusFilter);

        btnSearch = new JButton("Tìm kiếm");
        btnSearch.setBackground(new Color(24, 119, 242));
        btnSearch.setForeground(Color.WHITE);
        btnSearch.setFocusPainted(false);
        btnSearch.addActionListener(this);
        filterPanel.add(btnSearch);

        mainPanel.add(filterPanel, BorderLayout.NORTH);

        String[] cols = {"STT", "Username", "Tên hiển thị", "Email", "Vai trò", "Trạng thái", "Ngày tạo"};
        tableModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int col) { return false; }
        };
        tblUsers = new JTable(tableModel);
        tblUsers.setRowHeight(25);
        tblUsers.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        mainPanel.add(new JScrollPane(tblUsers), BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 5));

        btnRefresh = new JButton("Làm mới");
        btnRefresh.addActionListener(this);

        btnLock = new JButton("Khóa tài khoản");
        btnLock.setBackground(new Color(220, 53, 69));
        btnLock.setForeground(Color.WHITE);
        btnLock.setFocusPainted(false);
        btnLock.addActionListener(this);

        btnUnlock = new JButton("Mở khóa tài khoản");
        btnUnlock.setBackground(new Color(40, 167, 69));
        btnUnlock.setForeground(Color.WHITE);
        btnUnlock.setFocusPainted(false);
        btnUnlock.addActionListener(this);

        btnClose = new JButton("Đóng");
        btnClose.addActionListener(this);

        bottomPanel.add(btnRefresh);
        bottomPanel.add(btnLock);
        bottomPanel.add(btnUnlock);
        bottomPanel.add(btnClose);
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);

        add(mainPanel);
    }

    private void searchUsers() {
        String kw = txtKeyword.getText().trim();
        String role = (String) cmbRoleFilter.getSelectedItem();
        String status = (String) cmbStatusFilter.getSelectedItem();

        userList = userDAO.findAll(kw, role, status);
        tableModel.setRowCount(0);

        int stt = 1;
        for (User u : userList) {
            tableModel.addRow(new Object[]{
                stt++,
                u.getUsername(),
                u.getDisplayName(),
                u.getEmail() != null ? u.getEmail() : "-",
                u.getRole(),
                u.getStatus(),
                u.getCreatedAt()
            });
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == btnSearch || e.getSource() == btnRefresh) {
            searchUsers();
        } else if (e.getSource() == btnClose) {
            this.dispose();
        } else if (e.getSource() == btnLock || e.getSource() == btnUnlock) {
            int selectedRow = tblUsers.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn một tài khoản trong bảng!", "Thông báo", JOptionPane.WARNING_MESSAGE);
                return;
            }

            User targetUser = userList.get(selectedRow);

            if (e.getSource() == btnLock && targetUser.getId().equals(currentAdmin.getId())) {
                JOptionPane.showMessageDialog(this, "Quy tắc nghiệp vụ: Quản trị viên không được phép tự khóa tài khoản của chính mình!", "Cảnh báo", JOptionPane.ERROR_MESSAGE);
                return;
            }

            boolean isLock = (e.getSource() == btnLock);
            String newStatus = isLock ? "LOCKED" : "ACTIVE";
            String actionTitle = isLock ? "Khóa" : "Mở khóa";

            if (newStatus.equalsIgnoreCase(targetUser.getStatus())) {
                JOptionPane.showMessageDialog(this, "Tài khoản này đã ở trạng thái " + newStatus + " rồi!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                return;
            }

            int confirm = JOptionPane.showConfirmDialog(this,
                "Xác nhận " + actionTitle + " tài khoản [" + targetUser.getUsername() + "]?",
                "Xác nhận", JOptionPane.YES_NO_OPTION);

            if (confirm == JOptionPane.YES_OPTION) {
                boolean ok = userDAO.updateStatus(targetUser.getId(), newStatus);
                if (ok) {

                    actionLogDAO.insert(new ActionLog(currentAdmin.getId(), "UPDATE_USER_STATUS", "User", targetUser.getId()));

                    JOptionPane.showMessageDialog(this, "Đã " + actionTitle.toLowerCase() + " tài khoản thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
                    searchUsers();
                } else {
                    JOptionPane.showMessageDialog(this, "Có lỗi xảy ra khi cập nhật trạng thái!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }
}
