package net.codjo.tools.sqltester.batch.task;
import static net.codjo.tools.sqltester.batch.task.util.Constants.NEW_LINE;
import net.codjo.tools.sqltester.batch.task.util.TaskUtil;

/**
 * Task permettant de vérifier que si.
 */
public class CheckUnusedGrantsTask extends AbstractCheckGrantsExistence {
    private static final String TASK_NAME = "Verification des grants en trop";


    public CheckUnusedGrantsTask(String applicationFileName) {
        super(applicationFileName);
    }


    public String getTaskName() {
        return TASK_NAME;
    }


    @Override
    protected void evaluateDeliveryFile(String permissionFileContent, StringBuffer errors) {
        String[] scripts = contentOfFile.split(NEW_LINE);
        for (String script : scripts) {
            if (TaskUtil.doesPathContainDirectory(script, "drop")
                && TaskUtil.doesPathStartWithDirectory(script, "table")) {
                checkGrant(permissionFileContent, TaskUtil.getObjectName(script), errors);
            }
        }
    }


    @Override
    protected void checkGrant(String permissionFileContent, String objectName, StringBuffer errors) {
        if (permissionFileContent.contains(objectName + " ")) {
            errors.append("Les grants pour l'objet '").append(objectName).append("' ne sont pas supprimes")
                  .append(NEW_LINE);
        }
    }
}
