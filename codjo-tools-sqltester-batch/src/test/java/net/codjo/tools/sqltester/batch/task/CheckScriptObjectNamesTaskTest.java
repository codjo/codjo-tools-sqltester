package net.codjo.tools.sqltester.batch.task;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.fail;
import org.apache.tools.ant.BuildException;
import org.junit.Test;
/**
 *
 */
public class CheckScriptObjectNamesTaskTest {
    private CheckScriptObjectNamesTask checkScriptObjectNamesTask;


    @Test
    public void test_execute_OK() throws Exception {
        String file = getClass().getResource("ScriptObjectNames_OK.txt").getFile();
        checkScriptObjectNamesTask = new CheckScriptObjectNamesTask(file);
        checkScriptObjectNamesTask.execute();
    }


    @Test
    public void test_execute_KO() throws Exception {
        String file = getClass().getResource("ScriptObjectNames_KO.txt").getFile();
        checkScriptObjectNamesTask = new CheckScriptObjectNamesTask(file);
        try {
            checkScriptObjectNamesTask.execute();
            fail("Noms incorrects !");
        }
        catch (BuildException ex) {
            String expectedMessage
                  = "L'objet créé dans le script 'table/AP_TEST wrongScriptName.txt' ne corespond pas au nom de ce script";
            assertEquals(expectedMessage, ex.getMessage());
        }
    }
}
