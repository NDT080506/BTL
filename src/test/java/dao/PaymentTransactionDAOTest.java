package dao;

import static org.junit.Assert.*;

import java.util.List;
import java.util.UUID;
import model.Booking;
import model.PaymentTransaction;
import model.User;
import model.Wallet;
import org.junit.Before;
import org.junit.Test;

public class PaymentTransactionDAOTest {
    private PaymentTransactionDAO txDAO;
    private WalletDAO walletDAO;
    private UserDAO userDAO;
    private BookingDAO bookingDAO;

    @Before
    public void setUp() {
        txDAO = new PaymentTransactionDAO();
        walletDAO = new WalletDAO();
        userDAO = new UserDAO();
        bookingDAO = new BookingDAO();
    }

    @Test
    public void testHasSuccessfulPayment_Exception_NotPaid() {
        boolean paid = txDAO.hasSuccessfulPayment(UUID.randomUUID());
        assertFalse("Booking ngẫu nhiên không được có thanh toán thành công", paid);
    }

    @Test
    public void testCreatePendingDeposit_Standard() {
        try {
            DAO.con.setAutoCommit(false);

            User student = userDAO.checkLogin("student01", "123456");
            assertNotNull(student);
            Wallet wallet = walletDAO.findByUserId(student.getId());
            assertNotNull(wallet);

            String refCode = "DEP_TEST_" + System.currentTimeMillis();
            PaymentTransaction tx = txDAO.createPendingDeposit(wallet, 500000L, refCode);
            assertNotNull("Tạo giao dịch nạp tiền PENDING phải thành công", tx);
            assertEquals("DEPOSIT", tx.getType());
            assertEquals(500000L, tx.getAmount());
        } catch (Exception e) {
            e.printStackTrace();
            fail("Lỗi khi test create pending deposit: " + e.getMessage());
        } finally {
            try {
                if (!DAO.con.getAutoCommit()) {
                    DAO.con.rollback();
                }
                DAO.con.setAutoCommit(true);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    @Test
    public void testTransferBetweenWallets_Standard() {
        try {
            DAO.con.setAutoCommit(false);

            User student = userDAO.checkLogin("student01", "123456");
            User tutor = userDAO.checkLogin("tutor01", "123456");
            assertNotNull(student);
            assertNotNull(tutor);

            Wallet studentWallet = walletDAO.findByUserId(student.getId());
            Wallet tutorWallet = walletDAO.findByUserId(tutor.getId());
            assertNotNull(studentWallet);
            assertNotNull(tutorWallet);

            List<Booking> list = bookingDAO.findByStudent(student.getId());
            assertNotNull(list);
            assertTrue(list.size() > 0);
            Booking booking = list.get(0);

            PaymentTransaction tx = txDAO.transferBetweenWallets(booking, studentWallet, tutorWallet, 100000L);
            assertNotNull("Thực hiện chuyển tiền giữa 2 ví thành công", tx);
            assertEquals("TRANSFER", tx.getType());
            assertEquals("SUCCESS", tx.getStatus());
        } catch (Exception e) {
            e.printStackTrace();
            fail("Lỗi khi test transfer between wallets: " + e.getMessage());
        } finally {
            try {
                if (!DAO.con.getAutoCommit()) {
                    DAO.con.rollback();
                }
                DAO.con.setAutoCommit(true);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }
}
