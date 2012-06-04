/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
package net.codjo.tools.sqltester.batch.task;
import static net.codjo.tools.sqltester.batch.task.util.Constants.NEW_LINE;
import junit.framework.TestCase;
import org.apache.tools.ant.BuildException;
/**
 * Classe de test de {@link CheckScriptsExistenceTask}.
 */
public class CheckScriptsExistenceTaskTest extends TestCase {
    private CheckScriptsExistenceTask checkScriptsExistenceTask;


    public void test_checkSqlFilesExistence_KO() throws Exception {
        String file = getClass().getResource("ScriptsExistence_KO.txt").getFile();
        try {
            checkScriptsExistenceTask = new CheckScriptsExistenceTask(file);
            checkScriptsExistenceTask.execute();
            fail("Des fichiers sont introuvables !");
        }
        catch (BuildException e) {
            String expectedMessage =
                  "Les fichiers suivants sont introuvables :" + NEW_LINE
                  + "table\\AP_TOTO.txt" + NEW_LINE
                  + "view/VU_TEST.sql" + NEW_LINE
                  + "indexe/AP_TITI.txt" + NEW_LINE
                  + "procedure/sp_test.sql" + NEW_LINE;
            assertEquals(expectedMessage, e.getMessage());
        }
    }


    public void test_checkSqlFilesExistence_OK() throws Exception {
        String file = getClass().getResource("ScriptsExistence_OK.txt").getFile();
        checkScriptsExistenceTask = new CheckScriptsExistenceTask(file);
        checkScriptsExistenceTask.execute();
    }
}
