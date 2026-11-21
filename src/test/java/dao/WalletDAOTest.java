package dao;

import static org.junit.Assert.*;

import java.util.UUID;
import model.User;
import model.Wallet;
import org.junit.Before;
import org.junit.Test;

public class WalletDAOTest {
    private WalletDAO walletDAO;
    private UserDAO userDAO;

    @Before
    public void setUp() {
        walletDAO = new WalletDAO();
        userDAO = new UserDAO();
    }

    @Test
    public void test_CN05_PaymentCheckoutDebit() {
        Wallet w = new Wallet(UUID.randomUUID(), 500000);
        boolean success = w.debit(300000);
        assertTrue("Trừ số tiền hợp lệ khỏi ví phải thành công", success);
        assertEquals(200000, w.getAvailableBalance());

        boolean failInsufficient = w.debit(300000);
        assertFalse("Trừ số tiền vượt quá số dư khả dụng phải trả về false", failInsufficient);
        assertEquals(200000, w.getAvailableBalance());
    }

    @Test
    public void test_CN07_WalletCredit_TopUp() {
        Wallet w = new Wallet(UUID.randomUUID(), 100000);
        w.credit(500000);
        assertEquals(600000, w.getAvailableBalance());
    }
}
