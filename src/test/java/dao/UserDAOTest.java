package dao;

import static org.junit.Assert.*;

import java.util.List;
import java.util.UUID;
import model.ActionLog;
import model.User;
import org.junit.Before;
import org.junit.Test;

public class UserDAOTest {
    private UserDAO userDAO;
    private ActionLogDAO actionLogDAO;

    @Before
    public void setUp() {
        userDAO = new UserDAO();
        actionLogDAO = new ActionLogDAO();
    }

    @Test
    public void test_CN01_Register_UsernameCheck() {
        boolean exists = userDAO.isUsernameExists("admin");
        assertTrue("Username admin phải tồn tại trong CSDL", exists);

        boolean notExists = userDAO.isUsernameExists("non_existing_" + System.currentTimeMillis());
        assertFalse("Username ngẫu nhiên không được tồn tại", notExists);
    }

    @Test
    public void test_CN02_CheckLogin_Success() {
        User user = userDAO.checkLogin("student01", "123456");
        assertNotNull("Đăng nhập với student01/123456 phải thành công", user);
        assertEquals("student01", user.getUsername());
        assertEquals("STUDENT", user.getRole());
    }

    @Test
    public void test_CN03_SearchTutors() {
        List<User> tutors = userDAO.searchTutors("", "Toán");
        assertNotNull("Danh sách gia sư tìm kiếm không được null", tutors);
    }

    @Test
    public void test_CN09_UpdateUserStatus() {
        User user = userDAO.checkLogin("student01", "123456");
        assertNotNull("student01 phải tồn tại", user);
        boolean updated = userDAO.updateStatus(user.getId(), "ACTIVE");
        assertTrue("Cập nhật trạng thái tài khoản ACTIVE phải thành công", updated);
    }

    @Test
    public void test_CN14_ActionLogInquiry() {
        User user = userDAO.checkLogin("admin", "123456");
        UUID adminId = (user != null) ? user.getId() : UUID.randomUUID();
        ActionLog log = new ActionLog(adminId, "TEST_ACTION_LOG", "User", adminId);
        boolean inserted = actionLogDAO.insert(log);
        assertTrue("Ghi nhật ký thao tác thành công", inserted);

        List<ActionLog> logs = actionLogDAO.search("TEST_ACTION_LOG", "");
        assertNotNull("Danh sách nhật ký không được null", logs);
        assertFalse("Danh sách nhật ký phải chứa bản ghi vừa tạo", logs.isEmpty());
    }
}
