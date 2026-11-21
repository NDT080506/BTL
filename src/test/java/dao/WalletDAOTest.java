package dao;

import static org.junit.Assert.*;

import model.User;
import model.Wallet;
import model.Withdrawal;
import org.junit.Before;
import org.junit.Test;

public class WalletDAOTest {
    private WalletDAO walletDAO;
    private WithdrawalDAO withdrawalDAO;
    private UserDAO userDAO;

    @Before
    public void setUp() {
        walletDAO = new WalletDAO();
        withdrawalDAO = new WithdrawalDAO();
        userDAO = new UserDAO();
    }

    @Test
    public void test_CN16_CreateWithdrawal() {
        User tutor = userDAO.checkLogin("tutor01", "123456");
        assertNotNull("tutor01 phải tồn tại", tutor);
        Wallet wallet = walletDAO.findByUserId(tutor.getId());
        assertNotNull("Ví của tutor01 phải tồn tại", wallet);

        Withdrawal wd = new Withdrawal(tutor.getId(), wallet.getId(), 200000L, "Vietcombank", "0011004567890", "MINH");
        boolean inserted = withdrawalDAO.insert(wd);
        assertTrue("Tạo yêu cầu rút tiền thành công phải trả về true", inserted);
    }
}
