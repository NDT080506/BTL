package dao;

import static org.junit.Assert.*;

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
    public void test_CN02_CheckLogin_Tutor_Success() {
        User user = userDAO.checkLogin("tutor01", "123456");
        assertNotNull("Đăng nhập với tutor01/123456 phải thành công", user);
        assertEquals("tutor01", user.getUsername());
        assertEquals("TUTOR", user.getRole());
    }
}
