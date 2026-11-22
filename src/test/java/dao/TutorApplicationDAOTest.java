package dao;

import static org.junit.Assert.*;

import java.util.List;
import java.util.UUID;
import model.TutorApplication;
import model.User;
import org.junit.Before;
import org.junit.Test;

public class TutorApplicationDAOTest {
    private TutorApplicationDAO applicationDAO;
    private UserDAO userDAO;

    @Before
    public void setUp() {
        applicationDAO = new TutorApplicationDAO();
        userDAO = new UserDAO();
    }

    @Test
    public void testFindPending_Standard() {
        List<TutorApplication> pendingList = applicationDAO.findPending();
        assertNotNull("Danh sách hồ sơ chờ duyệt không được null", pendingList);
        assertTrue("Trong CSDL mẫu có hồ sơ ứng tuyển đang PENDING", pendingList.size() > 0);
    }

    @Test
    public void testFindByUserId_Standard() {
        User student = userDAO.checkLogin("student02", "123456");
        assertNotNull(student);

        TutorApplication app = applicationDAO.findByUserId(student.getId());
        assertNotNull("student02 có hồ sơ ứng tuyển trong CSDL", app);
        assertEquals(student.getId(), app.getUserId());
    }

    @Test
    public void testFindByUserId_Exception_NotFound() {
        TutorApplication app = applicationDAO.findByUserId(UUID.randomUUID());
        assertNull("User ngẫu nhiên chưa nộp đơn phải trả về null", app);
    }

    @Test
    public void testInsert_Standard() {
        try {
            DAO.con.setAutoCommit(false);

            User student = userDAO.checkLogin("student01", "123456");
            assertNotNull(student);

            TutorApplication app = new TutorApplication(student.getId(), "Kinh nghiệm dạy kèm", "Tiếng Anh", "http://cv.pdf", "90/100");
            boolean inserted = applicationDAO.insert(app);
            assertTrue("Nộp hồ sơ ứng tuyển thành công", inserted);

            TutorApplication inDb = applicationDAO.findById(app.getId());
            assertNotNull(inDb);
            assertEquals("PENDING", inDb.getStatus());
        } catch (Exception e) {
            e.printStackTrace();
            fail("Lỗi khi test insert application: " + e.getMessage());
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
    public void testUpdateStatus_Standard() {
        try {
            DAO.con.setAutoCommit(false);

            List<TutorApplication> list = applicationDAO.findPending();
            assertNotNull(list);
            assertTrue(list.size() > 0);

            TutorApplication first = list.get(0);
            boolean updated = applicationDAO.updateStatus(first.getId(), "APPROVED", "Hồ sơ xuất sắc");
            assertTrue("Quản trị viên phê duyệt hồ sơ phải thành công", updated);

            TutorApplication inDb = applicationDAO.findById(first.getId());
            assertNotNull(inDb);
            assertEquals("APPROVED", inDb.getStatus());
            assertEquals("Hồ sơ xuất sắc", inDb.getAdminNote());
        } catch (Exception e) {
            e.printStackTrace();
            fail("Lỗi khi test updateStatus application: " + e.getMessage());
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
