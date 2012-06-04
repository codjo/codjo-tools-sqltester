package net.codjo.tools.sqltester.batch.task;
import static net.codjo.tools.sqltester.batch.task.util.Constants.NEW_LINE;
import net.codjo.tools.sqltester.batch.task.util.TaskUtil;
/**
 * Task permettant de vérifier que si.
 */
public class CheckMissingGrantsTask extends AbstractCheckGrantsExistence {
    private static final String TASK_NAME = "Verification des grants manquants";


    public CheckMissingGrantsTask(String applicationFileName) {
        super(applicationFileName);
    }


    public String getTaskName() {
        return TASK_NAME;
    }


    @Override
    protected void evaluateDeliveryFile(String permissionFileContent, StringBuffer errors) {
        String[] scripts = contentOfFile.split(NEW_LINE);
        for (String script : scripts) {
            if (TaskUtil.doesPathContainTable(script)
                || TaskUtil.doesPathContainView(script)
                || TaskUtil.doesPathContainProc(script)) {
                checkGrant(permissionFileContent, TaskUtil.getObjectName(script), errors);
            }
        }
    }


    @Override
    protected void checkGrant(String permissionFileContent, String objectName, StringBuffer errors) {
        if (!permissionFileContent.contains(objectName)) {
            errors.append("Aucun grant trouve pour l'objet '").append(objectName).append("'")
                  .append(NEW_LINE);
        }
    }
}
