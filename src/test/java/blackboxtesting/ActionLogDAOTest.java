package blackboxtesting;

import dao.*;

import static org.junit.Assert.*;

import java.util.List;
import java.util.UUID;
import model.ActionLog;
import model.User;
import org.junit.Before;
import org.junit.Test;

public class ActionLogDAOTest {
    private ActionLogDAO actionLogDAO;
    private UserDAO userDAO;

    @Before
    public void setUp() {
        actionLogDAO = new ActionLogDAO();
        userDAO = new UserDAO();
    }

    @Test
    public void testSearch_Standard() {
        List<ActionLog> list = actionLogDAO.search("REVIEW_TUTOR_APPLICATION", "");
        assertNotNull("Danh sách nhật ký không được null", list);
        assertTrue("Trong CSDL mẫu có log REVIEW_TUTOR_APPLICATION", list.size() > 0);
    }

    @Test
    public void testSearch_Exception_NotFound() {
        List<ActionLog> list = actionLogDAO.search("ACTION_KHONG_TON_TAI_9999", "");
        assertNotNull("Danh sách không được null", list);
        assertEquals("Hành động không tồn tại phải trả về danh sách rỗng", 0, list.size());
    }

    @Test
    public void testFindById_Standard() {
        List<ActionLog> list = actionLogDAO.search("", "");
        assertNotNull(list);
        assertTrue(list.size() > 0);

        ActionLog first = list.get(0);
        ActionLog found = actionLogDAO.findById(first.getId());
        assertNotNull("Tìm log theo ID hợp lệ phải thành công", found);
        assertEquals(first.getAction(), found.getAction());
    }

    @Test
    public void testFindById_Exception_NotFound() {
        ActionLog found = actionLogDAO.findById(UUID.randomUUID());
        assertNull("Tìm log với ID ngẫu nhiên phải trả về null", found);
    }

    @Test
    public void testInsert_Standard() {
        try {
            DAO.con.setAutoCommit(false);

            User user = userDAO.checkLogin("admin", "123456");
            UUID adminId = (user != null) ? user.getId() : UUID.randomUUID();

            ActionLog log = new ActionLog(adminId, "TEST_LOG_ROLLBACK", "System", adminId);
            boolean inserted = actionLogDAO.insert(log);
            assertTrue("Ghi nhật ký thao tác thành công", inserted);

            ActionLog inDb = actionLogDAO.findById(log.getId());
            assertNotNull(inDb);
            assertEquals("TEST_LOG_ROLLBACK", inDb.getAction());
        } catch (Exception e) {
            e.printStackTrace();
            fail("Lỗi khi test insert action log: " + e.getMessage());
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

