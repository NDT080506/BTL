package dao;

import static org.junit.Assert.*;

import java.sql.Timestamp;
import java.util.UUID;
import org.junit.Before;
import org.junit.Test;

public class BookingDAOTest {
    private BookingDAO bookingDAO;

    @Before
    public void setUp() {
        bookingDAO = new BookingDAO();
    }

    @Test
    public void test_CN04_BookingScheduleAvailable() {
        Timestamp futureTime = Timestamp.valueOf("2030-01-01 08:00:00");
        UUID randomTutorId = UUID.randomUUID();
        boolean available = bookingDAO.isScheduleAvailable(randomTutorId, futureTime, 60);
        assertTrue("Khung giờ trống trong tương lai phải khả dụng (true)", available);
    }
}
