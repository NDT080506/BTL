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
    public void testFindByUserId_Standard() {
        User student = userDAO.checkLogin("student01", "123456");
        assertNotNull(student);

        Wallet wallet = walletDAO.findByUserId(student.getId());
        assertNotNull("Ví của student01 phải tồn tại trong CSDL", wallet);
        assertEquals(student.getId(), wallet.getUserId());
        assertTrue("Số dư ví ban đầu phải >= 0", wallet.getAvailableBalance() >= 0);
    }

    @Test
    public void testFindByUserId_Exception_NotFound() {
        Wallet wallet = walletDAO.findByUserId(UUID.randomUUID());
        assertNull("User ngẫu nhiên không có ví phải trả về null", wallet);
    }

    @Test
    public void testCredit_Standard() {
        try {
            DAO.con.setAutoCommit(false);

            User student = userDAO.checkLogin("student01", "123456");
            assertNotNull(student);
            Wallet wallet = walletDAO.findByUserId(student.getId());
            assertNotNull(wallet);

            long oldBalance = wallet.getAvailableBalance();
            boolean credited = walletDAO.credit(wallet.getId(), 200000L);
            assertTrue("Nạp tiền vào ví thành công", credited);

            Wallet updated = walletDAO.findById(wallet.getId());
            assertNotNull(updated);
            assertEquals(oldBalance + 200000L, updated.getAvailableBalance());
        } catch (Exception e) {
            e.printStackTrace();
            fail("Lỗi khi test credit wallet: " + e.getMessage());
        } finally {
            try {
                DAO.con.rollback();
                DAO.con.setAutoCommit(true);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    @Test
    public void testDebit_Standard() {
        try {
            DAO.con.setAutoCommit(false);

            User student = userDAO.checkLogin("student01", "123456");
            assertNotNull(student);
            Wallet wallet = walletDAO.findByUserId(student.getId());
            assertNotNull(wallet);

            if (wallet.getAvailableBalance() < 100000L) {
                walletDAO.credit(wallet.getId(), 500000L);
                wallet = walletDAO.findById(wallet.getId());
            }

            long oldBalance = wallet.getAvailableBalance();
            boolean debited = walletDAO.debit(wallet.getId(), 50000L);
            assertTrue("Trừ tiền khỏi ví khi đủ số dư phải thành công", debited);

            Wallet updated = walletDAO.findById(wallet.getId());
            assertNotNull(updated);
            assertEquals(oldBalance - 50000L, updated.getAvailableBalance());
        } catch (Exception e) {
            e.printStackTrace();
            fail("Lỗi khi test debit wallet: " + e.getMessage());
        } finally {
            try {
                DAO.con.rollback();
                DAO.con.setAutoCommit(true);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    @Test
    public void testDebit_Exception_InsufficientBalance() {
        try {
            DAO.con.setAutoCommit(false);

            User student = userDAO.checkLogin("student01", "123456");
            assertNotNull(student);
            Wallet wallet = walletDAO.findByUserId(student.getId());
            assertNotNull(wallet);

            boolean debited = walletDAO.debit(wallet.getId(), 9999999999L);
            assertFalse("Trừ số tiền vượt quá số dư khả dụng phải trả về false", debited);
        } catch (Exception e) {
            e.printStackTrace();
            fail("Lỗi khi test debit vượt quá số dư: " + e.getMessage());
        } finally {
            try {
                DAO.con.rollback();
                DAO.con.setAutoCommit(true);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    @Test
    public void testDomainModel_DebitCreditLogic() {
        Wallet w = new Wallet(UUID.randomUUID(), 500000);
        assertTrue(w.debit(300000));
        assertEquals(200000, w.getAvailableBalance());

        assertFalse(w.debit(300000));
        assertEquals(200000, w.getAvailableBalance());

        w.credit(100000);
        assertEquals(300000, w.getAvailableBalance());
    }
}
