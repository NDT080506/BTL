package dao;

import static org.junit.Assert.*;

import java.util.List;
import model.Dispute;
import model.TutorApplication;
import org.junit.Before;
import org.junit.Test;

public class ApplicationAndDisputeDAOTest {
    private TutorApplicationDAO applicationDAO;
    private DisputeDAO disputeDAO;

    @Before
    public void setUp() {
        applicationDAO = new TutorApplicationDAO();
        disputeDAO = new DisputeDAO();
    }

    @Test
    public void test_CN10_ReviewTutorApplication() {
        List<TutorApplication> pendingList = applicationDAO.findPending();
        assertNotNull("Danh sách hồ sơ chờ duyệt không được null", pendingList);
    }

    @Test
    public void test_CN12_DisputeRefund() {
        List<Dispute> pendingDisputes = disputeDAO.findPending();
        assertNotNull("Danh sách khiếu nại chờ xử lý không được null", pendingDisputes);
    }
}
