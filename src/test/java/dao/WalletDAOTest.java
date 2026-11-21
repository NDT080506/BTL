package dao;

import static org.junit.Assert.*;

import java.util.List;
import model.Withdrawal;
import org.junit.Before;
import org.junit.Test;

public class WalletDAOTest {
    private WithdrawalDAO withdrawalDAO;

    @Before
    public void setUp() {
        withdrawalDAO = new WithdrawalDAO();
    }

    @Test
    public void test_CN13_WithdrawalInquiry() {
        List<Withdrawal> list = withdrawalDAO.findAll("", "");
        assertNotNull("Danh sách yêu cầu rút tiền tra cứu không được null", list);
    }
}
