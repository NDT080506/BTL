package view.tutor;

import dao.ActionLogDAO;
import dao.WalletDAO;
import dao.WithdrawalDAO;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;
import model.ActionLog;
import model.User;
import model.Wallet;
import model.Withdrawal;

/**
 * CreateWithdrawalFrm: Màn hình Yêu cầu rút tiền về tài khoản ngân hàng
 * Cung cấp dữ liệu đầu vào cho CN06.06 (Quản trị tra cứu yêu cầu rút tiền)
 */
public class CreateWithdrawalFrm extends JDialog implements ActionListener {
    private TutorHomeFrm parent;
    private User tutor;
    private Wallet wallet;

    private JTextField txtAmount;
    private JComboBox<String> cmbBank;
    private JTextField txtAccountNumber;
    private JTextField txtAccountHolder;
    private JButton btnSubmit;
    private JButton btnCancel;

    private WithdrawalDAO withdrawalDAO;
    private WalletDAO walletDAO;
    private ActionLogDAO actionLogDAO;

    public CreateWithdrawalFrm(TutorHomeFrm parent, User tutor, Wallet wallet) {
        super(parent, "Tạo yêu cầu rút tiền về ngân hàng", true);
        this.parent = parent;
        this.tutor = tutor;
        this.wallet = wallet;

        this.withdrawalDAO = new WithdrawalDAO();
        this.walletDAO = new WalletDAO();
        this.actionLogDAO = new ActionLogDAO();

        initComponents();
    }

    private void initComponents() {
        setSize(480, 380);
        setLocationRelativeTo(parent);
        setResizable(false);

        JPanel mainPanel = new JPanel(new BorderLayout(12, 12));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        JLabel lblTitle = new JLabel("YÊU CẦU RÚT TIỀN VỀ NGÂN HÀNG", JLabel.CENTER);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 15));
        lblTitle.setForeground(new Color(24, 119, 242));
        mainPanel.add(lblTitle, BorderLayout.NORTH);

        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Số dư hiện tại
        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0.35;
        formPanel.add(new JLabel("Số dư ví hiện tại:"), gbc);
        JLabel lblBal = new JLabel(String.format("%,d VNĐ", wallet.getAvailableBalance()));
        lblBal.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblBal.setForeground(new Color(40, 167, 69));
        gbc.gridx = 1; gbc.gridy = 0; gbc.weightx = 0.65;
        formPanel.add(lblBal, gbc);

        // Số tiền muốn rút
        gbc.gridx = 0; gbc.gridy = 1;
        formPanel.add(new JLabel("Số tiền muốn rút (*):"), gbc);
        txtAmount = new JTextField("500000");
        gbc.gridx = 1; gbc.gridy = 1;
        formPanel.add(txtAmount, gbc);

        // Ngân hàng
        gbc.gridx = 0; gbc.gridy = 2;
        formPanel.add(new JLabel("Ngân hàng nhận (*):"), gbc);
        cmbBank = new JComboBox<>(new String[]{"Vietcombank (VCB)", "Techcombank (TCB)", "MB Bank", "BIDV", "VietinBank", "ACB"});
        gbc.gridx = 1; gbc.gridy = 2;
        formPanel.add(cmbBank, gbc);

        // Số tài khoản
        gbc.gridx = 0; gbc.gridy = 3;
        formPanel.add(new JLabel("Số tài khoản (*):"), gbc);
        txtAccountNumber = new JTextField("0011004567890");
        gbc.gridx = 1; gbc.gridy = 3;
        formPanel.add(txtAccountNumber, gbc);

        // Chủ tài khoản
        gbc.gridx = 0; gbc.gridy = 4;
        formPanel.add(new JLabel("Tên chủ tài khoản (*):"), gbc);
        txtAccountHolder = new JTextField(tutor.getDisplayName().toUpperCase());
        gbc.gridx = 1; gbc.gridy = 4;
        formPanel.add(txtAccountHolder, gbc);

        mainPanel.add(formPanel, BorderLayout.CENTER);

        // Buttons
        JPanel btnPanel = new JPanel(new GridLayout(1, 2, 15, 0));
        btnSubmit = new JButton("Gửi yêu cầu rút");
        btnSubmit.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnSubmit.setBackground(new Color(24, 119, 242));
        btnSubmit.setForeground(Color.WHITE);
        btnSubmit.setFocusPainted(false);
        btnSubmit.addActionListener(this);

        btnCancel = new JButton("Hủy bỏ");
        btnCancel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        btnCancel.addActionListener(e -> this.dispose());

        btnPanel.add(btnSubmit);
        btnPanel.add(btnCancel);
        mainPanel.add(btnPanel, BorderLayout.SOUTH);

        add(mainPanel);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == btnSubmit) {
            String amtStr = txtAmount.getText().trim();
            String bankName = (String) cmbBank.getSelectedItem();
            String accNum = txtAccountNumber.getText().trim();
            String accHolder = txtAccountHolder.getText().trim();

            if (accNum.isEmpty() || accHolder.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Vui lòng nhập đầy đủ số tài khoản và chủ tài khoản!", "Thiếu thông tin", JOptionPane.WARNING_MESSAGE);
                return;
            }

            long amount;
            try {
                amount = Long.parseLong(amtStr);
                if (amount <= 0 || amount > wallet.getAvailableBalance()) {
                    JOptionPane.showMessageDialog(this, "Số tiền rút không hợp lệ hoặc vượt quá số dư khả dụng!", "Lỗi", JOptionPane.WARNING_MESSAGE);
                    return;
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Số tiền không đúng định dạng!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }

            Withdrawal wd = new Withdrawal(tutor.getId(), wallet.getId(), amount, bankName, accNum, accHolder);
            boolean ok = withdrawalDAO.insert(wd);
            if (ok) {
                actionLogDAO.insert(new ActionLog(tutor.getId(), "CREATE_WITHDRAWAL", "Withdrawal", wd.getId()));
                JOptionPane.showMessageDialog(this, 
                    "Tạo yêu cầu rút tiền thành công!\nSố tiền: " + String.format("%,d VNĐ", amount) + 
                    "\nTrạng thái: Đang chờ xử lý.\nQuản trị viên có thể tra cứu tại mục Yêu cầu rút tiền.", 
                    "Thành công", JOptionPane.INFORMATION_MESSAGE);
                this.dispose();
                parent.loadWallet();
            } else {
                JOptionPane.showMessageDialog(this, "Lỗi khi lưu yêu cầu rút tiền!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
