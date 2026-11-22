package dao;

import static org.junit.Assert.*;

import java.sql.Timestamp;
import java.util.List;
import java.util.UUID;
import model.Booking;
import model.User;
import org.junit.Before;
import org.junit.Test;

public class BookingDAOTest {
    private BookingDAO bookingDAO;
    private UserDAO userDAO;

    @Before
    public void setUp() {
        bookingDAO = new BookingDAO();
        userDAO = new UserDAO();
    }

    @Test
    public void testIsScheduleAvailable_Standard() {
        Timestamp futureTime = Timestamp.valueOf("2035-01-01 08:00:00");
        UUID randomTutorId = UUID.randomUUID();
        boolean available = bookingDAO.isScheduleAvailable(randomTutorId, futureTime, 60);
        assertTrue("Khung giờ trống trong tương lai phải khả dụng (true)", available);
    }

    @Test
    public void testIsScheduleAvailable_Exception_Busy() {
        User tutor = userDAO.checkLogin("tutor01", "123456");
        assertNotNull(tutor);

        Timestamp busyTime = Timestamp.valueOf("2030-01-01 08:30:00");
        boolean available = bookingDAO.isScheduleAvailable(tutor.getId(), busyTime, 60);
        assertFalse("Khung giờ trùng với lịch đã ACCEPTED phải trả về false", available);
    }

    @Test
    public void testInsert_Standard() {
        try {
            DAO.con.setAutoCommit(false);

            User student = userDAO.checkLogin("student01", "123456");
            User tutor = userDAO.checkLogin("tutor01", "123456");
            assertNotNull(student);
            assertNotNull(tutor);

            Booking booking = new Booking(student.getId(), tutor.getId(), null,
                    new Timestamp(System.currentTimeMillis() + 86400000L), 60, "Đặt lịch test JUnit", 300000L);
            boolean inserted = bookingDAO.insert(booking);
            assertTrue("Tạo lịch đặt học viên phải thành công", inserted);
        } catch (Exception e) {
            e.printStackTrace();
            fail("Lỗi khi test insert booking: " + e.getMessage());
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
    public void testFindPendingByTutor_Standard() {
        User tutor = userDAO.checkLogin("tutor01", "123456");
        assertNotNull(tutor);

        List<Booking> list = bookingDAO.findPendingByTutor(tutor.getId());
        assertNotNull("Danh sách lịch hẹn chờ duyệt không được null", list);
        assertTrue("tutor01 có lịch DG02 đang PENDING trong CSDL", list.size() > 0);
    }

    @Test
    public void testFindPendingByTutor_Exception_NotFound() {
        List<Booking> list = bookingDAO.findPendingByTutor(UUID.randomUUID());
        assertNotNull(list);
        assertEquals("Gia sư không có lịch hẹn phải trả về danh sách rỗng", 0, list.size());
    }

    @Test
    public void testFindByStudent_Standard() {
        User student = userDAO.checkLogin("student01", "123456");
        assertNotNull(student);

        List<Booking> list = bookingDAO.findByStudent(student.getId());
        assertNotNull("Danh sách lịch học của học viên không được null", list);
        assertTrue("student01 có lịch học trong CSDL", list.size() > 0);
    }

    @Test
    public void testUpdateResponse_Standard() {
        try {
            DAO.con.setAutoCommit(false);

            User student = userDAO.checkLogin("student01", "123456");
            User tutor = userDAO.checkLogin("tutor01", "123456");
            assertNotNull(student);
            assertNotNull(tutor);

            Booking booking = new Booking(student.getId(), tutor.getId(), null,
                    new Timestamp(System.currentTimeMillis() + 172800000L), 60, "Test phản hồi", 250000L);
            bookingDAO.insert(booking);

            boolean updated = bookingDAO.updateResponse(booking.getId(), "ACCEPTED");
            assertTrue("Gia sư chấp nhận lịch hẹn phải trả về true", updated);
        } catch (Exception e) {
            e.printStackTrace();
            fail("Lỗi khi test updateResponse booking: " + e.getMessage());
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
