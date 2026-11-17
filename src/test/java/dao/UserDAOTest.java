package dao;

import static org.junit.Assert.*;

import model.User;
import org.junit.Before;
import org.junit.Test;

/**
 * Unit Test cho lớp UserDAO theo giáo trình PTIT & Software Design Tutorial
 */
public class UserDAOTest {
    private UserDAO userDAO;

    @Before
    public void setUp() {
        userDAO = new UserDAO();
    }

    @Test
    public void testIsUsernameExists_ExistingUser() {
        // "admin" hoặc "tutor01" là tài khoản có trong seed data
        boolean exists = userDAO.isUsernameExists("admin");
        assertTrue("Username admin phải tồn tại trong CSDL", exists);
    }

    @Test
    public void testIsUsernameExists_NonExistingUser() {
        boolean exists = userDAO.isUsernameExists("non_existing_user_" + System.currentTimeMillis());
        assertFalse("Username ngẫu nhiên không được tồn tại", exists);
    }

    @Test
    public void testCheckLogin_Success() {
        User user = userDAO.checkLogin("student01", "123456");
        assertNotNull("Đăng nhập với student01/123456 phải thành công", user);
        assertEquals("student01", user.getUsername());
        assertEquals("STUDENT", user.getRole());
    }

    @Test
    public void testCheckLogin_WrongPassword() {
        User user = userDAO.checkLogin("student01", "wrong_password");
        assertNull("Đăng nhập sai mật khẩu phải trả về null", user);
    }

    @Test
    public void testCheckLogin_NonExistingUser() {
        User user = userDAO.checkLogin("no_user", "123456");
        assertNull("Đăng nhập tài khoản không tồn tại phải trả về null", user);
    }
}
