package dao;

import static org.junit.Assert.*;

import java.util.UUID;
import model.TutorProfile;
import model.User;
import org.junit.Before;
import org.junit.Test;

public class TutorProfileDAOTest {
    private TutorProfileDAO profileDAO;
    private UserDAO userDAO;

    @Before
    public void setUp() {
        profileDAO = new TutorProfileDAO();
        userDAO = new UserDAO();
    }

    @Test
    public void testFindByTutorId_Standard() {
        User tutor = userDAO.checkLogin("tutor01", "123456");
        assertNotNull("tutor01 phải tồn tại", tutor);

        TutorProfile profile = profileDAO.findByTutorId(tutor.getId());
        assertNotNull("Hồ sơ gia sư của tutor01 phải tồn tại", profile);
        assertEquals(tutor.getId(), profile.getUserId());
        assertNotNull("Môn dạy không được null", profile.getSubjectsCanTeach());
        assertTrue("Điểm đánh giá sao phải > 0", profile.getRating() > 0);
    }

    @Test
    public void testFindByTutorId_Exception_NotFound() {
        TutorProfile profile = profileDAO.findByTutorId(UUID.randomUUID());
        assertNull("Tìm hồ sơ với ID ngẫu nhiên phải trả về null", profile);
    }

    @Test
    public void testInsertProfile_Standard() {
        try {
            DAO.con.setAutoCommit(false);

            String uniqueName = "new_tutor_" + System.currentTimeMillis();
            User newTutor = new User(uniqueName, "123456", "Gia sư mới", "newtutor@tutortech.vn", "TUTOR");
            userDAO.insert(newTutor);

            TutorProfile profile = new TutorProfile(newTutor.getId(), "Kinh nghiệm dạy 3 năm", "Vật lý", 4.9);
            boolean inserted = profileDAO.insert(profile);
            assertTrue("Thêm hồ sơ gia sư thành công", inserted);

            TutorProfile inDb = profileDAO.findByTutorId(newTutor.getId());
            assertNotNull(inDb);
            assertEquals("Vật lý", inDb.getSubjectsCanTeach());
        } catch (Exception e) {
            e.printStackTrace();
            fail("Lỗi khi test insert tutor profile: " + e.getMessage());
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
