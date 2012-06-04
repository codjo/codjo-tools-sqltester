package net.codjo.tools.sqltester.batch.task;
import net.codjo.tools.sqltester.batch.task.util.TaskUtil;
import org.apache.tools.ant.BuildException;
/**
 * Task permettant de vérifier que si.
 */
public class CheckScriptsExistenceTask implements CheckTask {
    private static final String TASK_NAME = "Verification de l'existence des scripts";
    private String applicationFileName;


    public CheckScriptsExistenceTask(String applicationFileName) {
        this.applicationFileName = applicationFileName;
    }


    public String getTaskName() {
        return TASK_NAME;
    }


    public void execute() throws BuildException {
        String filesNotFound;
        filesNotFound = TaskUtil.findUnexistingFilesIn(applicationFileName);
        if (filesNotFound.length() != 0) {
            throw new BuildException(filesNotFound);
        }
    }
}
