package blackboxtesting;

import dao.*;

import static org.junit.Assert.*;

import java.util.List;
import java.util.UUID;
import model.BrokeragePackage;
import model.User;
import org.junit.Before;
import org.junit.Test;

public class BrokeragePackageDAOTest {
    private BrokeragePackageDAO packageDAO;
    private UserDAO userDAO;

    @Before
    public void setUp() {
        packageDAO = new BrokeragePackageDAO();
        userDAO = new UserDAO();
    }

    @Test
    public void testFindActiveByTutorId_Standard() {
        User tutor = userDAO.checkLogin("tutor01", "123456");
        assertNotNull("tutor01 phải tồn tại", tutor);

        List<BrokeragePackage> list = packageDAO.findActiveByTutorId(tutor.getId());
        assertNotNull("Danh sách gói học đang mở không được null", list);
        assertTrue("tutor01 phải có ít nhất 1 gói học đang mở", list.size() > 0);
    }

    @Test
    public void testFindActiveByTutorId_Exception_NotFound() {
        List<BrokeragePackage> list = packageDAO.findActiveByTutorId(UUID.randomUUID());
        assertNotNull("Danh sách không được null", list);
        assertEquals("Gia sư ngẫu nhiên không có gói học phải trả về list rỗng", 0, list.size());
    }

    @Test
    public void testFindById_Standard() {
        List<BrokeragePackage> list = packageDAO.findAll("", true);
        assertNotNull(list);
        assertTrue(list.size() > 0);

        BrokeragePackage first = list.get(0);
        BrokeragePackage found = packageDAO.findById(first.getId());
        assertNotNull("Tìm gói học theo ID hợp lệ phải thành công", found);
        assertEquals(first.getName(), found.getName());
    }

    @Test
    public void testFindById_Exception_NotFound() {
        BrokeragePackage found = packageDAO.findById(UUID.randomUUID());
        assertNull("Tìm gói học với ID không tồn tại phải trả về null", found);
    }

    @Test
    public void testFindAll_Standard() {
        List<BrokeragePackage> list = packageDAO.findAll("Toán", true);
        assertNotNull("Tra cứu gói học theo từ khóa không được null", list);
        assertTrue(list.size() > 0);
    }

    @Test
    public void testFindAll_Exception_NotFound() {
        List<BrokeragePackage> list = packageDAO.findAll("GoiKhongTonTai9999", true);
        assertNotNull("Danh sách không được null", list);
        assertEquals("Từ khóa không khớp phải trả về danh sách rỗng", 0, list.size());
    }

    @Test
    public void testInsert_Standard() {
        try {
            DAO.con.setAutoCommit(false);

            User tutor = userDAO.checkLogin("tutor01", "123456");
            assertNotNull(tutor);

            BrokeragePackage pkg = new BrokeragePackage(tutor.getId(), "Gói Test JUnit", "Mô tả khóa học test", 350000L, true);
            boolean inserted = packageDAO.insert(pkg);
            assertTrue("Thêm mới gói dịch vụ phải thành công", inserted);

            BrokeragePackage inDb = packageDAO.findById(pkg.getId());
            assertNotNull(inDb);
            assertEquals("Gói Test JUnit", inDb.getName());
        } catch (Exception e) {
            e.printStackTrace();
            fail("Lỗi khi test insert package: " + e.getMessage());
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

