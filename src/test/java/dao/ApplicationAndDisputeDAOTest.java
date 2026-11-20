package dao;

import static org.junit.Assert.*;

import java.sql.Timestamp;
import java.util.List;
import java.util.UUID;
import model.Booking;
import model.Dispute;
import model.TutorApplication;
import model.User;
import org.junit.Before;
import org.junit.Test;

/**
 * Unit Test cho nhóm Ứng tuyển gia sư và Xử lý khiếu nại
 * Bao phủ các luồng: CN06, CN10, CN12, CN17
 */
public class ApplicationAndDisputeDAOTest {
    private TutorApplicationDAO applicationDAO;
    private DisputeDAO disputeDAO;
    private BookingDAO bookingDAO;
    private UserDAO userDAO;

    @Before
    public void setUp() {
        applicationDAO = new TutorApplicationDAO();
        disputeDAO = new DisputeDAO();
        bookingDAO = new BookingDAO();
        userDAO = new UserDAO();
    }

    // CN06 - Nộp và theo dõi hồ sơ ứng tuyển gia sư: Học viên nộp hồ sơ ứng tuyển
    @Test
    public void test_CN06_SubmitTutorApplication() {
        User student = userDAO.checkLogin("student01", "123456");
        assertNotNull("student01 phải tồn tại", student);

        // Xóa hồ sơ pending cũ nếu có để tránh trùng
        TutorApplication existing = applicationDAO.findByUserId(student.getId());
        if (existing != null && "PENDING".equalsIgnoreCase(existing.getStatus())) {
            applicationDAO.deletePending(existing.getId());
        }

        TutorApplication app = new TutorApplication(student.getId(), "Giới thiệu gia sư Toán", "Toán 12", "http://cv.pdf", "http://test.pdf");
        boolean inserted = applicationDAO.insert(app);
        assertTrue("Nộp hồ sơ ứng tuyển gia sư phải thành công", inserted);
    }

    // CN10 - Quản trị xét duyệt hồ sơ ứng tuyển gia sư: Tra cứu danh sách hồ sơ chờ duyệt
    @Test
    public void test_CN10_ReviewTutorApplication() {
        List<TutorApplication> pendingList = applicationDAO.findPending();
        assertNotNull("Danh sách hồ sơ chờ duyệt không được null", pendingList);
    }

    // CN12 - Quản trị xử lý khiếu nại và hoàn tiền: Tra cứu khiếu nại chờ xử lý
    @Test
    public void test_CN12_DisputeRefund() {
        List<Dispute> pendingDisputes = disputeDAO.findPending();
        assertNotNull("Danh sách khiếu nại chờ xử lý không được null", pendingDisputes);
    }

    // CN17 - Học viên gửi khiếu nại buổi học đã thanh toán: Gửi khiếu nại
    @Test
    public void test_CN17_CreateDispute() {
        User student = userDAO.checkLogin("student01", "123456");
        User tutor = userDAO.checkLogin("tutor01", "123456");
        assertNotNull("student01 phải tồn tại", student);
        assertNotNull("tutor01 phải tồn tại", tutor);

        // Tạo một booking mẫu để khiếu nại thỏa mãn ràng buộc khóa ngoại
        Booking booking = new Booking(student.getId(), tutor.getId(), null,
                new Timestamp(System.currentTimeMillis() + 172800000L), 60, "Buổi học khiếu nại", 300000L);
        boolean bookSuccess = bookingDAO.insert(booking);
        assertTrue("Tạo booking mẫu cho khiếu nại thành công", bookSuccess);

        Dispute dispute = new Dispute(booking.getId(), student.getId(), "Gia sư vào trễ 30 phút", "http://proof.png", 150000L);
        boolean inserted = disputeDAO.insert(dispute);
        assertTrue("Học viên tạo khiếu nại thành công", inserted);
    }
}
