package dao;

import static org.junit.Assert.*;

import java.sql.Timestamp;
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
    public void test_CN08_TutorResponseBookingUseCase() {
        User student = userDAO.checkLogin("student01", "123456");
        User tutor = userDAO.checkLogin("tutor01", "123456");
        assertNotNull("student01 phải tồn tại", student);
        assertNotNull("tutor01 phải tồn tại", tutor);

        // Tạo một booking PENDING để gia sư phản hồi
        Booking booking = new Booking(student.getId(), tutor.getId(), null,
                new Timestamp(System.currentTimeMillis() + 86400000L), 60, "Test response", 250000L);
        boolean inserted = bookingDAO.insert(booking);
        assertTrue("Tạo booking thử nghiệm phải thành công", inserted);

        boolean updated = bookingDAO.updateResponse(booking.getId(), "ACCEPTED");
        assertTrue("Gia sư chấp nhận lịch đặt (ACCEPTED) phải thành công", updated);
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
