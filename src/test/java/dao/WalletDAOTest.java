package dao;

import static org.junit.Assert.*;

import java.util.List;
import java.util.UUID;
import model.User;
import model.Wallet;
import model.Withdrawal;
import org.junit.Before;
import org.junit.Test;

/**
 * Unit Test cho nhóm Ví tiền, Thanh toán và Rút tiền
 * Bao phủ các luồng: CN05, CN07, CN13, CN16
 */
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

    // CN05 - Thanh toán tiền thuê gia sư trực tiếp vào ví: Logic trừ tiền (debit)
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

    // CN07 - Nạp tiền vào ví bằng cổng mô phỏng: Logic cộng tiền (credit)
    @Test
    public void test_CN07_WalletCredit_TopUp() {
        Wallet w = new Wallet(UUID.randomUUID(), 100000);
        w.credit(500000);
        assertEquals(600000, w.getAvailableBalance());
    }

    // CN13 - Quản trị tra cứu yêu cầu rút tiền: Tra cứu danh sách yêu cầu rút tiền
    @Test
    public void test_CN13_WithdrawalInquiry() {
        List<Withdrawal> list = withdrawalDAO.findAll("", "");
        assertNotNull("Danh sách yêu cầu rút tiền tra cứu không được null", list);
    }

    // CN16 - Gia sư tạo yêu cầu rút tiền về ngân hàng: Tạo yêu cầu rút
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
