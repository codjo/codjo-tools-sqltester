package net.codjo.tools.sqltester.batch.task;
import static net.codjo.test.common.matcher.JUnitMatchers.*;
import static net.codjo.tools.sqltester.batch.task.util.Constants.NEW_LINE;
import org.apache.tools.ant.BuildException;
import org.junit.After;
import static org.junit.Assert.fail;
import org.junit.Before;
import org.junit.Test;

public class CheckMissingViewsGrantsFromFSTaskTest {
    private SqlDeliveryFixture sqlDeliveryFixture = new SqlDeliveryFixture();
    private CheckMissingViewsGrantsFromFSTask task;


    @Before
    public void setUp() throws Exception {
        sqlDeliveryFixture.doSetUp();

        task = new CheckMissingViewsGrantsFromFSTask(sqlDeliveryFixture.getDeliveryFilePath());
    }


    @After
    public void tearDown() throws Exception {
        sqlDeliveryFixture.doTearDown();
    }


    @Test
    public void test_execute_OK() throws Exception {
        sqlDeliveryFixture.mockGrants("VU_PUBLIC", "VU_PRIVATE", "VU_COMMON");
        sqlDeliveryFixture.mockRevokes("VU_PUBLIC", "VU_PRIVATE", "VU_COMMON");
        sqlDeliveryFixture.mockViews("VU_PUBLIC", "VU_PRIVATE");
        sqlDeliveryFixture.mockDirInViews(".svn");
        sqlDeliveryFixture.mockDirInViews("drop");

        task.execute();
    }


    @Test
    public void test_execute_grant_KO() throws Exception {
        sqlDeliveryFixture.mockGrants("VU_COMMON");
        sqlDeliveryFixture.mockRevokes("VU_PUBLIC", "VU_PRIVATE", "VU_COMMON");
        sqlDeliveryFixture.mockViews("VU_PUBLIC", "VU_PRIVATE");

        try {
            task.execute();
            fail();
        }
        catch (BuildException ex) {
            assertThat(ex.getMessage(), equalTo("Aucun grant défini pour la vue : VU_PRIVATE" + NEW_LINE
                                                + "Aucun grant défini pour la vue : VU_PUBLIC" + NEW_LINE));
        }
    }


    @Test
    public void test_execute_realViewName_KO() throws Exception {
        sqlDeliveryFixture.mockGrants("VU_COMMON", "VU_PRIVATE2");
        sqlDeliveryFixture.mockRevokes("VU_PUBLIC", "VU_PRIVATE", "VU_COMMON");
        sqlDeliveryFixture.mockViews("VU_PUBLIC", "VU_PRIVATE");

        try {
            task.execute();
            fail();
        }
        catch (BuildException ex) {
            assertThat(ex.getMessage(), equalTo("Aucun grant défini pour la vue : VU_PRIVATE" + NEW_LINE
                                                + "Aucun grant défini pour la vue : VU_PUBLIC" + NEW_LINE));
        }
    }


    @Test
    public void test_execute_revoke_KO() throws Exception {
        sqlDeliveryFixture.mockGrants("VU_PUBLIC", "VU_PRIVATE", "VU_COMMON");
        sqlDeliveryFixture.mockRevokes("VU_COMMON");
        sqlDeliveryFixture.mockViews("VU_PUBLIC", "VU_PRIVATE");

        try {
            task.execute();
            fail();
        }
        catch (BuildException ex) {
            assertThat(ex.getMessage(), equalTo("Aucun revoke défini pour la vue : VU_PRIVATE" + NEW_LINE
                                                + "Aucun revoke défini pour la vue : VU_PUBLIC" + NEW_LINE));
        }
    }
}