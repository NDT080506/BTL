package junittesting;

import static org.junit.Assert.*;

import dao.BookingDAO;
import dao.BrokeragePackageDAO;
import dao.UserDAO;
import java.sql.Timestamp;
import java.util.List;
import java.util.UUID;
import model.Booking;
import model.BrokeragePackage;
import model.User;
import org.junit.Before;
import org.junit.Test;

public class BookingDAOTest {
    private BookingDAO bookingDAO;
    private BrokeragePackageDAO packageDAO;
    private UserDAO userDAO;

    @Before
    public void setUp() {
        bookingDAO = new BookingDAO();
        packageDAO = new BrokeragePackageDAO();
        userDAO = new UserDAO();
    }

    @Test
    public void test_CN04_BookingScheduleAvailable() {
        Timestamp futureTime = Timestamp.valueOf("2030-01-01 08:00:00");
        UUID randomTutorId = UUID.randomUUID();
        boolean available = bookingDAO.isScheduleAvailable(randomTutorId, futureTime, 60);
        assertTrue("Khung giờ trống trong tương lai phải khả dụng (true)", available);
    }

    @Test
    public void test_CN08_TutorResponseBookingUseCase() {
        User student = userDAO.checkLogin("student01", "123456");
        User tutor = userDAO.checkLogin("tutor01", "123456");
        assertNotNull("student01 phải tồn tại", student);
        assertNotNull("tutor01 phải tồn tại", tutor);

        Booking booking = new Booking(student.getId(), tutor.getId(), null,
                new Timestamp(System.currentTimeMillis() + 86400000L), 60, "Test response", 250000L);
        boolean inserted = bookingDAO.insert(booking);
        assertTrue("Tạo booking thử nghiệm phải thành công", inserted);

        boolean updated = bookingDAO.updateResponse(booking.getId(), "ACCEPTED");
        assertTrue("Gia sư chấp nhận lịch đặt (ACCEPTED) phải thành công", updated);
    }

    @Test
    public void test_CN11_FindActivePackages() {
        List<BrokeragePackage> packages = packageDAO.findAll("", true);
        assertNotNull("Danh sách gói học đang hoạt động không được null", packages);
    }

    @Test
    public void test_CN15_BrokeragePackageInsert() {
        User tutor = userDAO.checkLogin("tutor01", "123456");
        assertNotNull("tutor01 phải tồn tại", tutor);
        BrokeragePackage pkg = new BrokeragePackage(tutor.getId(), "Gói Test JUnit CN15", "Khóa học thử nghiệm", 300000L, true);
        boolean inserted = packageDAO.insert(pkg);
        assertTrue("Gia sư thêm mới gói dịch vụ phải thành công", inserted);
    }
}
