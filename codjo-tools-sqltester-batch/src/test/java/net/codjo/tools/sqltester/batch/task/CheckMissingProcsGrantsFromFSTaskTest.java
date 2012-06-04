package net.codjo.tools.sqltester.batch.task;
import static net.codjo.test.common.matcher.JUnitMatchers.*;
import static net.codjo.tools.sqltester.batch.task.util.Constants.NEW_LINE;
import org.apache.tools.ant.BuildException;
import org.junit.After;
import static org.junit.Assert.fail;
import org.junit.Before;
import org.junit.Test;

public class CheckMissingProcsGrantsFromFSTaskTest {
    private SqlDeliveryFixture sqlDeliveryFixture = new SqlDeliveryFixture();
    private CheckMissingProcsGrantsFromFSTask task;


    @Before
    public void setUp() throws Exception {
        sqlDeliveryFixture.doSetUp();

        task = new CheckMissingProcsGrantsFromFSTask(sqlDeliveryFixture.getDeliveryFilePath());
    }


    @After
    public void tearDown() throws Exception {
        sqlDeliveryFixture.doTearDown();
    }


    @Test
    public void test_execute_OK() throws Exception {
        sqlDeliveryFixture.mockGrants("sp_Public", "sp_Private", "sp_Common");
        sqlDeliveryFixture.mockRevokes("sp_Public", "sp_Private", "sp_Common");
        sqlDeliveryFixture.mockProcedures("sp_Public", "sp_Private");

        task.execute();
    }


    @Test
    public void test_execute_grant_KO() throws Exception {
        sqlDeliveryFixture.mockGrants("sp_Common");
        sqlDeliveryFixture.mockRevokes("sp_Public", "sp_Private", "sp_Common");
        sqlDeliveryFixture.mockProcedures("sp_Public", "sp_Private");

        try {
            task.execute();
            fail();
        }
        catch (BuildException ex) {
            assertThat(ex.getMessage(),
                       equalTo("Aucun grant défini pour la procedure : sp_Private" + NEW_LINE
                               + "Aucun grant défini pour la procedure : sp_Public" + NEW_LINE));
        }
    }


    @Test
    public void test_execute_revoke_KO() throws Exception {
        sqlDeliveryFixture.mockGrants("sp_Public", "sp_Private", "sp_Common");
        sqlDeliveryFixture.mockRevokes("sp_Common");
        sqlDeliveryFixture.mockProcedures("sp_Public", "sp_Private");

        try {
            task.execute();
            fail();
        }
        catch (BuildException ex) {
            assertThat(ex.getMessage(),
                       equalTo("Aucun revoke défini pour la procedure : sp_Private" + NEW_LINE
                               + "Aucun revoke défini pour la procedure : sp_Public" + NEW_LINE));
        }
    }


    @Test
    public void test_execute_subDir_KO() throws Exception {
        sqlDeliveryFixture.mockGrants("sp_Public", "sp_Private", "sp_Common");
        sqlDeliveryFixture.mockRevokes("sp_Common");
        sqlDeliveryFixture.mockProcedures("sp_Private");
        sqlDeliveryFixture.mockProceduresInDir("tata", "sp_Public");

        try {
            task.execute();
            fail();
        }
        catch (BuildException ex) {
            assertThat(ex.getMessage(),
                       equalTo("Aucun revoke défini pour la procedure : sp_Private" + NEW_LINE
                               + "Aucun revoke défini pour la procedure : sp_Public" + NEW_LINE));
        }
    }


    @Test
    public void test_execute_subDir_OK() throws Exception {
        sqlDeliveryFixture.mockGrants("sp_Public", "sp_Private", "sp_Common");
        sqlDeliveryFixture.mockRevokes("sp_Public", "sp_Private", "sp_Common");
        sqlDeliveryFixture.mockProcedures("sp_Private");
        sqlDeliveryFixture.mockProceduresInDir("tata", "sp_Public");

        task.execute();
    }


    @Test
    public void test_execute_subDirSvn_OK() throws Exception {
        sqlDeliveryFixture.mockGrants("sp_Public", "sp_Private", "sp_Common");
        sqlDeliveryFixture.mockRevokes("sp_Public", "sp_Private", "sp_Common");
        sqlDeliveryFixture.mockProcedures("sp_Private");
        sqlDeliveryFixture.mockProceduresInDir("tata", "sp_Public");
        sqlDeliveryFixture.mockProceduresInDir(".svn", "props.base");

        task.execute();
    }


    @Test
    public void test_execute_ignoreDrop() throws Exception {
        sqlDeliveryFixture.mockGrants("sp_Private", "sp_Common");
        sqlDeliveryFixture.mockRevokes("sp_Private", "sp_Common");
        sqlDeliveryFixture.mockProcedures("sp_Private");
        sqlDeliveryFixture.mockProceduresInDir("drop", "sp_Public");
        sqlDeliveryFixture.mockProceduresInDir(".svn", "props.base");

        task.execute();
    }
}
