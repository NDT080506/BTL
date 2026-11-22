package dao;

import static org.junit.Assert.*;

import java.util.List;
import java.util.UUID;
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
    public void testIsUsernameExists_Standard() {
        boolean exists = userDAO.isUsernameExists("admin");
        assertTrue("Username admin phải tồn tại trong CSDL", exists);
    }

    @Test
    public void testIsUsernameExists_Exception_NotFound() {
        boolean notExists = userDAO.isUsernameExists("non_existing_user_" + System.currentTimeMillis());
        assertFalse("Username ngẫu nhiên không được tồn tại", notExists);
    }

    @Test
    public void testIsUsernameExists_Boundary_Empty() {
        boolean empty = userDAO.isUsernameExists("");
        assertFalse("Username rỗng không được tồn tại", empty);
    }

    @Test
    public void testCheckLogin_Standard() {
        User user = userDAO.checkLogin("student01", "123456");
        assertNotNull("Đăng nhập với student01/123456 phải thành công", user);
        assertEquals("student01", user.getUsername());
        assertEquals("STUDENT", user.getRole());
    }

    @Test
    public void testCheckLogin_Exception_WrongPassword() {
        User user = userDAO.checkLogin("student01", "wrong_pass_999");
        assertNull("Đăng nhập với mật khẩu sai phải trả về null", user);
    }

    @Test
    public void testCheckLogin_Exception_UserNotFound() {
        User user = userDAO.checkLogin("user_not_found_" + System.currentTimeMillis(), "123456");
        assertNull("Đăng nhập với tài khoản không tồn tại phải trả về null", user);
    }

    @Test
    public void testCheckLogin_Exception_LockedUser() {
        try {
            DAO.con.setAutoCommit(false);

            User temp = new User("locked_user_test", "123456", "Locked User", "locked@test.vn", "STUDENT");
            temp.setStatus("LOCKED");
            userDAO.insert(temp);

            User user = userDAO.checkLogin("locked_user_test", "123456");
            assertNull("Tài khoản bị khóa (LOCKED) không được phép đăng nhập", user);
        } catch (Exception e) {
            e.printStackTrace();
            fail("Ngoại lệ khi test đăng nhập tài khoản bị khóa: " + e.getMessage());
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
    public void testSearchTutors_Standard() {
        List<User> tutors = userDAO.searchTutors("", "Toán");
        assertNotNull("Danh sách gia sư tìm kiếm không được null", tutors);
        assertTrue("Phải tìm thấy ít nhất 1 gia sư dạy Toán", tutors.size() > 0);
    }

    @Test
    public void testSearchTutors_Exception_NotFound() {
        List<User> tutors = userDAO.searchTutors("TuKhoaKhongTonTai9999", "MonHocKhongCo");
        assertNotNull("Danh sách gia sư không được null", tutors);
        assertEquals("Không tìm thấy kết quả thì danh sách phải rỗng", 0, tutors.size());
    }

    @Test
    public void testSearchTutors_Boundary_EmptyKeywords() {
        List<User> tutors = userDAO.searchTutors("", "");
        assertNotNull("Danh sách gia sư không được null", tutors);
        assertTrue("Tìm kiếm với từ khóa rỗng phải trả về tất cả gia sư", tutors.size() >= 8);
    }

    @Test
    public void testInsertUser_Standard() {
        try {
            DAO.con.setAutoCommit(false);

            String uniqueName = "test_user_" + System.currentTimeMillis();
            User user = new User(uniqueName, "123456", "Học viên Test", "test@tutortech.vn", "STUDENT");
            boolean inserted = userDAO.insert(user);
            assertTrue("Thêm người dùng mới phải thành công", inserted);

            User inDb = userDAO.checkLogin(uniqueName, "123456");
            assertNotNull("Người dùng vừa thêm phải tìm thấy trong CSDL", inDb);
            assertEquals("Học viên Test", inDb.getDisplayName());
        } catch (Exception e) {
            e.printStackTrace();
            fail("Lỗi khi test insert user: " + e.getMessage());
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

            User user = userDAO.checkLogin("student01", "123456");
            assertNotNull("student01 phải tồn tại", user);

            boolean updated = userDAO.updateStatus(user.getId(), "LOCKED");
            assertTrue("Cập nhật trạng thái LOCKED phải thành công", updated);

            User lockedUser = userDAO.findById(user.getId());
            assertNotNull(lockedUser);
            assertEquals("LOCKED", lockedUser.getStatus());
        } catch (Exception e) {
            e.printStackTrace();
            fail("Lỗi khi test update status: " + e.getMessage());
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
    public void testFindById_Standard() {
        User user = userDAO.checkLogin("admin", "123456");
        assertNotNull("admin phải tồn tại", user);

        User found = userDAO.findById(user.getId());
        assertNotNull("Tìm người dùng theo ID hợp lệ phải thành công", found);
        assertEquals("admin", found.getUsername());
    }

    @Test
    public void testFindById_Exception_NotFound() {
        User found = userDAO.findById(UUID.randomUUID());
        assertNull("Tìm người dùng với ID không tồn tại phải trả về null", found);
    }
}
