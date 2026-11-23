package blackboxtesting;

import dao.*;

import static org.junit.Assert.*;

import java.sql.Timestamp;
import java.util.List;
import java.util.UUID;
import model.Booking;
import model.Dispute;
import model.User;
import org.junit.Before;
import org.junit.Test;

public class DisputeDAOTest {
    private DisputeDAO disputeDAO;
    private BookingDAO bookingDAO;
    private UserDAO userDAO;

    @Before
    public void setUp() {
        disputeDAO = new DisputeDAO();
        bookingDAO = new BookingDAO();
        userDAO = new UserDAO();
    }

    @Test
    public void testFindPending_Standard() {
        List<Dispute> list = disputeDAO.findPending();
        assertNotNull("Danh sách khiếu nại chờ xử lý không được null", list);
        assertTrue("Trong CSDL mẫu có khiếu nại PENDING", list.size() > 0);
    }

    @Test
    public void testFindById_Standard() {
        List<Dispute> list = disputeDAO.findPending();
        assertNotNull(list);
        assertTrue(list.size() > 0);

        Dispute first = list.get(0);
        Dispute found = disputeDAO.findById(first.getId());
        assertNotNull("Tìm khiếu nại theo ID hợp lệ phải thành công", found);
        assertEquals(first.getReason(), found.getReason());
    }

    @Test
    public void testFindById_Exception_NotFound() {
        Dispute found = disputeDAO.findById(UUID.randomUUID());
        assertNull("Tìm khiếu nại với ID không tồn tại phải trả về null", found);
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
                    new Timestamp(System.currentTimeMillis() + 86400000L), 60, "Booking để test khiếu nại", 300000L);
            bookingDAO.insert(booking);

            Dispute dispute = new Dispute(booking.getId(), student.getId(), "Gia sư không vào lớp", "http://evidence.png", 150000L);
            boolean inserted = disputeDAO.insert(dispute);
            assertTrue("Tạo khiếu nại mới phải thành công", inserted);

            Dispute inDb = disputeDAO.findById(dispute.getId());
            assertNotNull(inDb);
            assertEquals("PENDING", inDb.getStatus());
        } catch (Exception e) {
            e.printStackTrace();
            fail("Lỗi khi test insert dispute: " + e.getMessage());
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

            List<Dispute> list = disputeDAO.findPending();
            assertNotNull(list);
            assertTrue(list.size() > 0);

            Dispute first = list.get(0);
            boolean updated = disputeDAO.updateStatus(first.getId(), "APPROVED", "Chấp nhận hoàn tiền cho học viên");
            assertTrue("Xử lý khiếu nại thành công", updated);

            Dispute inDb = disputeDAO.findById(first.getId());
            assertNotNull(inDb);
            assertEquals("APPROVED", inDb.getStatus());
        } catch (Exception e) {
            e.printStackTrace();
            fail("Lỗi khi test updateStatus dispute: " + e.getMessage());
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

