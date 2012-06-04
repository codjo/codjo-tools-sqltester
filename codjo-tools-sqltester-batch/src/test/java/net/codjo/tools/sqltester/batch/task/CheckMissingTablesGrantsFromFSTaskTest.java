package net.codjo.tools.sqltester.batch.task;
import static net.codjo.test.common.matcher.JUnitMatchers.*;
import static net.codjo.tools.sqltester.batch.task.util.Constants.NEW_LINE;
import org.apache.tools.ant.BuildException;
import org.junit.After;
import static org.junit.Assert.fail;
import org.junit.Before;
import org.junit.Test;

public class CheckMissingTablesGrantsFromFSTaskTest {
    private SqlDeliveryFixture sqlDeliveryFixture = new SqlDeliveryFixture();
    private CheckMissingTablesGrantsFromFSTask task;


    @Before
    public void setUp() throws Exception {
        sqlDeliveryFixture.doSetUp();
        task = new CheckMissingTablesGrantsFromFSTask(sqlDeliveryFixture.getDeliveryFilePath());
    }


    @After
    public void tearDown() throws Exception {
        sqlDeliveryFixture.doTearDown();
    }


    @Test
    public void test_execute_OK() throws Exception {
        sqlDeliveryFixture.mockGrants("AP_PUBLIC", "AP_PRIVATE", "AP_COMMON");
        sqlDeliveryFixture.mockRevokes("AP_PUBLIC", "AP_PRIVATE", "AP_COMMON");
        sqlDeliveryFixture.mockTables("AP_PUBLIC", "AP_PRIVATE");
        sqlDeliveryFixture.mockTables("Q_AP_SECURITY-sequence", "TR_AP_ISSUER_PARENT_SEQ_I");
        sqlDeliveryFixture.mockDirInTables(".svn");
        sqlDeliveryFixture.mockDirInTables("alter");

        task.execute();
    }


    @Test
    public void test_execute_grant_KO() throws Exception {
        sqlDeliveryFixture.mockGrants("AP_COMMON");
        sqlDeliveryFixture.mockRevokes("AP_PUBLIC", "AP_PRIVATE", "AP_COMMON");
        sqlDeliveryFixture.mockTables("AP_PUBLIC", "AP_PRIVATE");

        try {
            task.execute();
            fail();
        }
        catch (BuildException ex) {
            assertThat(ex.getMessage(), equalTo("Aucun grant défini pour la table : AP_PRIVATE" + NEW_LINE
                                                + "Aucun grant défini pour la table : AP_PUBLIC" + NEW_LINE));
        }
    }


    @Test
    public void test_execute_realTableName_KO() throws Exception {
        sqlDeliveryFixture.mockGrants("AP_COMMON", "AP_PRIVATE2");
        sqlDeliveryFixture.mockRevokes("AP_PUBLIC", "AP_PRIVATE", "AP_COMMON");
        sqlDeliveryFixture.mockTables("AP_PUBLIC", "AP_PRIVATE");

        try {
            task.execute();
            fail();
        }
        catch (BuildException ex) {
            assertThat(ex.getMessage(), equalTo("Aucun grant défini pour la table : AP_PRIVATE" + NEW_LINE
                                                + "Aucun grant défini pour la table : AP_PUBLIC" + NEW_LINE));
        }
    }


    @Test
    public void test_execute_revoke_KO() throws Exception {
        sqlDeliveryFixture.mockGrants("AP_PUBLIC", "AP_PRIVATE", "AP_COMMON");
        sqlDeliveryFixture.mockRevokes("AP_COMMON");
        sqlDeliveryFixture.mockTables("AP_PUBLIC", "AP_PRIVATE");

        try {
            task.execute();
            fail();
        }
        catch (BuildException ex) {
            assertThat(ex.getMessage(),
                       equalTo("Aucun revoke défini pour la table : AP_PRIVATE" + NEW_LINE
                               + "Aucun revoke défini pour la table : AP_PUBLIC" + NEW_LINE));
        }
    }


    @Test
    public void test_execute_ignoreGap() throws Exception {
        sqlDeliveryFixture.mockGrants("AP_COMMON");
        sqlDeliveryFixture.mockRevokes("AP_COMMON");
        sqlDeliveryFixture.mockTables("AP_PUBLIC-gap");

        task.execute();
    }
}
