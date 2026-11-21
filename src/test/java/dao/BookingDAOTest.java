package dao;

import static org.junit.Assert.*;

import java.util.List;
import model.BrokeragePackage;
import org.junit.Before;
import org.junit.Test;

public class BookingDAOTest {
    private BrokeragePackageDAO packageDAO;

    @Before
    public void setUp() {
        packageDAO = new BrokeragePackageDAO();
    }

    @Test
    public void test_CN11_FindActivePackages() {
        List<BrokeragePackage> packages = packageDAO.findAll("", true);
        assertNotNull("Danh sách gói học đang hoạt động không được null", packages);
    }
}
