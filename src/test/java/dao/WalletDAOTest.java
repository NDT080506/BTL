package dao;

import static org.junit.Assert.*;

import java.util.UUID;
import model.Wallet;
import org.junit.Before;
import org.junit.Test;

/**
 * Unit Test cho WalletDAO
 */
public class WalletDAOTest {
    private WalletDAO walletDAO;

    @Before
    public void setUp() {
        walletDAO = new WalletDAO();
    }

    @Test
    public void testFindById_NonExisting() {
        Wallet w = walletDAO.findById(UUID.randomUUID());
        assertNull("Ví không tồn tại phải trả về null", w);
    }

    @Test
    public void testWalletDebit_InsufficientBalance() {
        Wallet w = new Wallet(UUID.randomUUID(), 100000);
        boolean success = w.debit(200000);
        assertFalse("Trừ số tiền lớn hơn số dư khả dụng phải trả về false", success);
        assertEquals(100000, w.getAvailableBalance());
    }

    @Test
    public void testWalletDebit_SufficientBalance() {
        Wallet w = new Wallet(UUID.randomUUID(), 500000);
        boolean success = w.debit(200000);
        assertTrue("Trừ số tiền hợp lệ phải trả về true", success);
        assertEquals(300000, w.getAvailableBalance());
    }

    @Test
    public void testWalletCredit() {
        Wallet w = new Wallet(UUID.randomUUID(), 100000);
        w.credit(50000);
        assertEquals(150000, w.getAvailableBalance());
    }
}
