package blackboxtesting;

import dao.*;

import static org.junit.Assert.*;

import java.util.List;
import java.util.UUID;
import model.User;
import model.Wallet;
import model.Withdrawal;
import org.junit.Before;
import org.junit.Test;

public class WithdrawalDAOTest {
    private WithdrawalDAO withdrawalDAO;
    private WalletDAO walletDAO;
    private UserDAO userDAO;

    @Before
    public void setUp() {
        withdrawalDAO = new WithdrawalDAO();
        walletDAO = new WalletDAO();
        userDAO = new UserDAO();
    }

    @Test
    public void testFindAll_Standard() {
        List<Withdrawal> list = withdrawalDAO.findAll("", "");
        assertNotNull("Danh sách yêu cầu rút tiền không được null", list);
        assertTrue("Trong CSDL mẫu có yêu cầu rút tiền", list.size() > 0);
    }

    @Test
    public void testFindAll_Exception_NotFound() {
        List<Withdrawal> list = withdrawalDAO.findAll("NganHangKhongTonTai9999", "REJECTED");
        assertNotNull("Danh sách không được null", list);
        assertEquals("Không tìm thấy kết quả thì danh sách phải rỗng", 0, list.size());
    }

    @Test
    public void testFindById_Standard() {
        List<Withdrawal> list = withdrawalDAO.findAll("", "");
        assertNotNull(list);
        assertTrue(list.size() > 0);

        Withdrawal first = list.get(0);
        Withdrawal found = withdrawalDAO.findById(first.getId());
        assertNotNull("Tìm yêu cầu rút tiền theo ID hợp lệ phải thành công", found);
        assertEquals(first.getAccountNumber(), found.getAccountNumber());
    }

    @Test
    public void testFindById_Exception_NotFound() {
        Withdrawal found = withdrawalDAO.findById(UUID.randomUUID());
        assertNull("Tìm yêu cầu rút tiền với ID ngẫu nhiên phải trả về null", found);
    }

    @Test
    public void testInsert_Standard() {
        try {
            DAO.con.setAutoCommit(false);

            User tutor = userDAO.checkLogin("tutor01", "123456");
            assertNotNull(tutor);
            Wallet wallet = walletDAO.findByUserId(tutor.getId());
            assertNotNull(wallet);

            Withdrawal wd = new Withdrawal(tutor.getId(), wallet.getId(), 200000L, "Vietcombank", "0123456789", "NGUYEN VAN MINH");
            boolean inserted = withdrawalDAO.insert(wd);
            assertTrue("Tạo yêu cầu rút tiền thành công phải trả về true", inserted);

            Withdrawal inDb = withdrawalDAO.findById(wd.getId());
            assertNotNull(inDb);
            assertEquals("PENDING", inDb.getStatus());
        } catch (Exception e) {
            e.printStackTrace();
            fail("Lỗi khi test insert withdrawal: " + e.getMessage());
        } finally {
            try {
                DAO.con.rollback();
                DAO.con.setAutoCommit(true);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }
}

