package net.codjo.tools.sqltester.batch.task;
import static net.codjo.tools.sqltester.batch.task.util.Constants.NEW_LINE;
import junit.framework.TestCase;
import org.apache.tools.ant.BuildException;
/**
 * Classe de test de {@link CheckMissingGrantsTask}.
 */
public class CheckMissingGrantsTaskTest extends TestCase {
    private CheckMissingGrantsTask checkMissingGrantsTask;


    public void test_execute_OK() throws Exception {
        String file = getClass().getResource("MissingGrants_OK.txt").getFile();
        checkMissingGrantsTask = new CheckMissingGrantsTask(file);
        checkMissingGrantsTask.execute();
    }


    public void test_execute_KO() throws Exception {
        String file = getClass().getResource("MissingGrants_KO.txt").getFile();
        try {
            checkMissingGrantsTask = new CheckMissingGrantsTask(file);
            checkMissingGrantsTask.execute();
            fail("Grants incorrects !");
        }
        catch (BuildException e) {
            String expectedMessage = "Aucun grant trouve pour l'objet 'AP_TOTO'" + NEW_LINE +
                                     "Aucun grant trouve pour l'objet 'VU_TEST'" + NEW_LINE +
                                     "Aucun grant trouve pour l'objet 'sp_test'" + NEW_LINE;
            assertEquals(expectedMessage, e.getMessage());
        }
    }


    public void test_execute_noGrantFile() throws Exception {
        String file = getClass().getResource("MissingGrants_NoGrants.txt").getFile();
        try {
            checkMissingGrantsTask = new CheckMissingGrantsTask(file);
            checkMissingGrantsTask.execute();
            fail("Pas de grants !");
        }
        catch (BuildException e) {
            String expectedMessage = "Il existe aucun pointeur vers un script du repertoire PERMISSION";
            assertEquals(expectedMessage, e.getMessage());
        }
    }


    public void test_execute_KO_withAlterDropAndGap() throws Exception {
        String file = getClass().getResource("MissingGrants_AlterDropGap.txt").getFile();
        try {
            checkMissingGrantsTask = new CheckMissingGrantsTask(file);
            checkMissingGrantsTask.execute();
            fail("Grants incorrects !");
        }
        catch (BuildException e) {
            String expectedMessage = "Aucun grant trouve pour l'objet 'AP_TOTO'" + NEW_LINE;
            assertEquals(expectedMessage, e.getMessage());
        }
    }
}
