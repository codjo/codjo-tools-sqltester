package net.codjo.tools.sqltester.gui;
import static net.codjo.test.common.matcher.JUnitMatchers.*;
import net.codjo.tools.sqltester.batch.task.util.Constants;
import org.apache.tools.ant.BuildException;
import org.junit.Test;
/**
 *
 */
public class TaskExecutorTest {

    @Test
    public void test_executeTasks_NoFile() throws Exception {
        String file = "bidon.txt";
        try {
            TaskExecutor taskExecutor = new TaskExecutor(file);
            taskExecutor.executeTasks(Constants.BaseType.SYBASE, null);
            fail("Fichier introuvable !");
        }
        catch (BuildException e) {
            String expectedMessage = "Le fichier " + file + " est introuvable";
            assertThat(e.getMessage(), equalTo(expectedMessage));
        }
    }
}
