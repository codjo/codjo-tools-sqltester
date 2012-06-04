/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
package net.codjo.tools.sqltester.batch.task;
import net.codjo.tools.sqltester.batch.task.util.TaskUtil;
import java.io.File;
import org.apache.tools.ant.BuildException;
/**
 *
 */
public abstract class AbstractCheckGrantsExistence implements CheckTask {
    private static final String PERMISSION_STRING = "PERMISSION";
    protected String applicationFilePath;
    protected String contentOfFile;


    protected AbstractCheckGrantsExistence(String applicationFilePath) {
        this.applicationFilePath = applicationFilePath;
    }


    public void execute() throws BuildException {
        contentOfFile = TaskUtil.getContentOfFile(applicationFilePath);
        contentOfFile = contentOfFile.replace("\\", "/");

        StringBuffer grantErrors = checkGrants();
        if (grantErrors.length() != 0) {
            throw new BuildException(grantErrors.toString());
        }
    }


    protected abstract void evaluateDeliveryFile(String permissionFileContent, StringBuffer errors);


    protected abstract void checkGrant(String permissionFileContent, String objectName, StringBuffer errors);


    private StringBuffer checkGrants() throws BuildException {
        if (!TaskUtil.doesPathStartWithDirectory(contentOfFile, PERMISSION_STRING)) {
            return new StringBuffer("Il existe aucun pointeur vers un script du repertoire "
                                    + PERMISSION_STRING);
        }

        String permissionFileContent = getPermissionFileContent();

        StringBuffer errors = new StringBuffer();
        evaluateDeliveryFile(permissionFileContent, errors);
        return errors;
    }


    private String getPermissionFileContent() {
        File permissionFile =
              new File(new File(applicationFilePath).getParentFile(), "/permission/grant.sql");
        if (!permissionFile.exists()) {
            throw new BuildException(
                  "Impossible de trouver le fichier de permissions : '/permission/grant.sql'");
        }
        return TaskUtil.getContentOfFile(permissionFile);
    }
}
