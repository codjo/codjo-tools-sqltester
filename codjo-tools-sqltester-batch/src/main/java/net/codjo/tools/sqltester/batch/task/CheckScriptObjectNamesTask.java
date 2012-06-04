package net.codjo.tools.sqltester.batch.task;
import static net.codjo.tools.sqltester.batch.task.util.Constants.NEW_LINE;
import net.codjo.tools.sqltester.batch.task.util.TaskUtil;
import java.io.File;
import org.apache.tools.ant.BuildException;

/**
 * Task permettant de vérifier que si.
 */
public class CheckScriptObjectNamesTask implements CheckTask {
    private static final String TASK_NAME
          = "Verification de la correspondance des noms des scripts et des objets";
    private String applicationFileName;


    public CheckScriptObjectNamesTask(String applicationFileName) {
        this.applicationFileName = applicationFileName;
    }


    public String getTaskName() {
        return TASK_NAME;
    }


    public void execute() throws BuildException {
        StringBuilder errors = new StringBuilder();
        File applicationFile = new File(applicationFileName);
        String contentOfFile = TaskUtil.getContentOfFile(applicationFileName).replace("\\", "/");
        String[] scripts = contentOfFile.split(NEW_LINE);
        for (String script1 : scripts) {
            String script = script1.trim();
            File scriptFile = new File(applicationFile.getParentFile(), script);
            String scriptContent = TaskUtil.getContentOfFile(scriptFile);
            if (!TaskUtil.doesScriptContainsCreateObjectName(scriptContent,
                                                             TaskUtil.getObjectType(script),
                                                             TaskUtil.getObjectName(script))) {
                if (errors.length() != 0) {
                    errors.append(NEW_LINE);
                }
                errors.append("L'objet créé dans le script '")
                      .append(script)
                      .append("' ne corespond pas au nom de ce script");
            }
        }
        if (errors.length() != 0) {
            throw new BuildException(errors.toString());
        }
    }
}