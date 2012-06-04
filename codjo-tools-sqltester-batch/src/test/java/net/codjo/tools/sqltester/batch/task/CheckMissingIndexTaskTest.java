package net.codjo.tools.sqltester.batch.task;

import junit.framework.TestCase;
import org.apache.tools.ant.BuildException;

/**
 * Classe de test de {@link CheckMissingIndexTask}.
 */
public class CheckMissingIndexTaskTest extends TestCase {
    private CheckMissingIndexTask checkMissingIndexTask;


    public void test_execute_OK() throws Exception {
        String file = getClass().getResource("MissingIndexes_OK.txt").getFile();
        checkMissingIndexTask = new CheckMissingIndexTask(file);
        checkMissingIndexTask.execute();
    }


    public void test_execute_KO() throws Exception {
        String file = getClass().getResource("MissingIndexes_KO.txt").getFile();
        try {
            checkMissingIndexTask = new CheckMissingIndexTask(file);
            checkMissingIndexTask.execute();
            fail("Gaps incorrects !");
        }
        catch (BuildException e) {
            String expectedMessage = "Aucun index défini pour la table 'AP_LOG2'";
            assertEquals(expectedMessage, e.getMessage());
        }
    }
}