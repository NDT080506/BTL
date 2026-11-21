package dao;

import static org.junit.Assert.*;

import java.util.List;
import model.User;
import org.junit.Before;
import org.junit.Test;

public class UserDAOTest {
    private UserDAO userDAO;

    @Before
    public void setUp() {
        userDAO = new UserDAO();
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
}
