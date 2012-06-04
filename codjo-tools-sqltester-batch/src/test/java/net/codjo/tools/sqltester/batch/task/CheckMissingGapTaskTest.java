package net.codjo.tools.sqltester.batch.task;

import static net.codjo.tools.sqltester.batch.task.util.Constants.NEW_LINE;
import junit.framework.TestCase;
import org.apache.tools.ant.BuildException;

/**
 * Classe de test de {@link net.codjo.tools.sqltester.batch.task.CheckMissingGrantsTask}.
 */
public class CheckMissingGapTaskTest extends TestCase {
    private CheckMissingGapTask checkMissingGapTask;


    public void test_execute_OK() throws Exception {
        String file = getClass().getResource("MissingGaps_OK.txt").getFile();
        checkMissingGapTask = new CheckMissingGapTask(file);
        checkMissingGapTask.execute();
    }


    public void test_execute_KO() throws Exception {
        String file = getClass().getResource("MissingGaps_KO.txt").getFile();
        try {
            checkMissingGapTask = new CheckMissingGapTask(file);
            checkMissingGapTask.execute();
            fail("Gaps incorrects !");
        }
        catch (BuildException e) {
            String expectedMessage = "Aucun gap defini pour la table 'AP_LOG2'" + NEW_LINE;
            assertEquals(expectedMessage, e.getMessage());
        }
    }
}