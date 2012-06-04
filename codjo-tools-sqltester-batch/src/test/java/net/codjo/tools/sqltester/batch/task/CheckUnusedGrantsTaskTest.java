package net.codjo.tools.sqltester.batch.task;
import static net.codjo.tools.sqltester.batch.task.util.Constants.NEW_LINE;
import junit.framework.TestCase;
import org.apache.tools.ant.BuildException;

/**
 * Classe de test de {@link CheckUnusedGrantsTask}.
 */
public class CheckUnusedGrantsTaskTest extends TestCase {
    private CheckUnusedGrantsTask checkUnusedGrantsTask;


    public void test_execute_OK() throws Exception {
        String file = getClass().getResource("UnusedGrants_OK.txt").getFile();
        checkUnusedGrantsTask = new CheckUnusedGrantsTask(file);
        checkUnusedGrantsTask.execute();
    }


    public void test_execute_withDrop() throws Exception {
        String file = getClass().getResource("UnusedGrants_Drop.txt").getFile();
        checkUnusedGrantsTask = new CheckUnusedGrantsTask(file);
        checkUnusedGrantsTask.execute();
    }


    public void test_execute_KO() throws Exception {
        String file = getClass().getResource("UnusedGrants_KO.txt").getFile();
        try {
            checkUnusedGrantsTask = new CheckUnusedGrantsTask(file);
            checkUnusedGrantsTask.execute();
            fail("Grants incorrects !");
        }
        catch (BuildException e) {
            String expectedMessage = "Les grants pour l'objet 'AP_LOG' ne sont pas supprimes" + NEW_LINE;
            assertEquals(expectedMessage, e.getMessage());
        }
    }


    public void test_execute_noGrantFile() throws Exception {
        String file = getClass().getResource("UnusedGrants_NoGrants.txt").getFile();
        try {
            checkUnusedGrantsTask = new CheckUnusedGrantsTask(file);
            checkUnusedGrantsTask.execute();
            fail("Pas de grants !");
        }
        catch (BuildException e) {
            String expectedMessage = "Il existe aucun pointeur vers un script du repertoire PERMISSION";
            assertEquals(expectedMessage, e.getMessage());
        }
    }
}
