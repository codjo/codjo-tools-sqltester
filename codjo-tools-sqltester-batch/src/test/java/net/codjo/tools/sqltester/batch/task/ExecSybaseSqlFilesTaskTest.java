/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
package net.codjo.tools.sqltester.batch.task;

import net.codjo.database.common.api.JdbcFixture;
import static net.codjo.database.common.api.structure.SqlTable.table;
import net.codjo.tools.sqltester.batch.ConnectionMetaData;
import static net.codjo.tools.sqltester.batch.task.util.Constants.NEW_LINE;
import junit.framework.TestCase;
import org.apache.tools.ant.BuildException;
import org.junit.Ignore;

/**
 * Classe de test de {@link ExecSybaseSqlFilesTask}.
 */
public class ExecSybaseSqlFilesTaskTest extends TestCase {
    private JdbcFixture jdbcFixture = JdbcFixture.newFixture();
    private ExecSybaseSqlFilesTask execSqlFilesTask;
    private ConnectionMetaData metadata;


    public void ttest_execute_scriptWithError() throws Exception {
        String file = getClass().getResource("ExecSqlFiles_KO.txt").getFile();
        try {
            execSqlFilesTask = new ExecSybaseSqlFilesTask(file, metadata);
            execSqlFilesTask.execute();
            fail("Erreur dans le script AP_TEST_KO.txt !");
        }
        catch (BuildException e) {
            String expectedMessage =
                  "Erreur lors de l'execution du script : 'table/AP_TEST_KO.txt'" + NEW_LINE
                  + "Msg 2715, Level 16, State 1:" + NEW_LINE
                  + "Server 'DAF_DEV1_SQL', Line 3:" + NEW_LINE
                  + "Can't find type 'toto'.";
            assertEquals(expectedMessage, e.getMessage().trim());
            jdbcFixture.advanced().assertExists("AP_TEST");
            jdbcFixture.advanced().assertDoesntExist("AP_TEST2");
        }
    }


    public void test_execute_connectionWithError() throws Exception {
        String file = getClass().getResource("ExecSqlFiles_KO.txt").getFile();
        metadata.setBase("BIDON");
        try {
            execSqlFilesTask = new ExecSybaseSqlFilesTask(file, metadata);
            execSqlFilesTask.execute();
            fail("Erreur dans la connection !");
        }
        catch (BuildException e) {
            String expectedMessage = "Erreur lors de l'execution de la commande." + NEW_LINE
                                     + "Output message :" + NEW_LINE + "CT-LIBRARY error:" + NEW_LINE
                                     + "	ct_connect(): directory service layer: internal directory control layer error: Requested server name not found.";
            assertEquals(expectedMessage, e.getMessage().trim());
            jdbcFixture.advanced().assertDoesntExist("AP_TEST");
            jdbcFixture.advanced().assertDoesntExist("AP_TEST2");
        }
    }


    @Ignore
    public void ttest_execute_ok() throws Exception {
        String file = getClass().getResource("ExecSqlFiles_OK.txt").getFile();
        execSqlFilesTask = new ExecSybaseSqlFilesTask(file, metadata);
        execSqlFilesTask.execute();
        jdbcFixture.advanced().assertExists("AP_TEST");
    }


    @Override
    protected void setUp() throws Exception {
        jdbcFixture.doSetUp();
        metadata = new ConnectionMetaData(getClass().getResource("../settings-sybase.xml").getPath());
        dropTables();
    }


    @Override
    protected void tearDown() throws Exception {
        dropTables();
        jdbcFixture.doTearDown();
    }


    private void dropTables() {
        jdbcFixture.drop(table("AP_TEST"));
        jdbcFixture.drop(table("AP_TEST2"));
    }
}