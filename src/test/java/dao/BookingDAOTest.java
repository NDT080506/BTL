package dao;

import static org.junit.Assert.*;

import java.sql.Timestamp;
import java.util.UUID;
import org.junit.Before;
import org.junit.Test;

/**
 * Unit Test cho BookingDAO
 */
public class BookingDAOTest {
    private BookingDAO bookingDAO;

    @Before
    public void setUp() {
        bookingDAO = new BookingDAO();
    }

    @Test
    public void testFindById_NonExisting() {
        assertNull("Booking không tồn tại phải trả về null", bookingDAO.findById(UUID.randomUUID()));
    }

    @Test
    public void testIsScheduleAvailable_NewTime() {
        // Khung giờ xa trong tương lai năm 2030 chắc chắn không bị trùng
        Timestamp futureTime = Timestamp.valueOf("2030-01-01 08:00:00");
        UUID randomTutorId = UUID.randomUUID();
        boolean available = bookingDAO.isScheduleAvailable(randomTutorId, futureTime, 60);
        assertTrue("Khung giờ trống phải trả về true", available);
    }

    @Test
    public void testIsScheduleAvailable_ConflictPending() {
        UserDAO userDAO = new UserDAO();
        model.User tutor = userDAO.checkLogin("tutor02", "123456");
        if (tutor != null) {
            Timestamp conflictTime = Timestamp.valueOf("2030-01-01 08:30:00");
            boolean available = bookingDAO.isScheduleAvailable(tutor.getId(), conflictTime, 60);
            assertFalse("Khung giờ trùng với lịch PENDING của gia sư phải trả về false", available);
        }
    }
}
