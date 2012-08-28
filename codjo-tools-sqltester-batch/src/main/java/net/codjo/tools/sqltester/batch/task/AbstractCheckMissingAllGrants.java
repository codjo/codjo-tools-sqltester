package net.codjo.tools.sqltester.batch.task;
import static net.codjo.tools.sqltester.batch.task.util.Constants.NEW_LINE;
import net.codjo.tools.sqltester.batch.task.util.TaskUtil;
import java.io.File;
import java.io.FileFilter;
import java.util.Formatter;
import org.apache.tools.ant.BuildException;
/**
 *
 */
public abstract class AbstractCheckMissingAllGrants implements CheckTask {
    private String deliveryFilePath;


    protected AbstractCheckMissingAllGrants(String deliveryFilePath) {
        this.deliveryFilePath = deliveryFilePath;
    }


    private void executeImpl(File deliveryDir,
                             String scriptType,
                             String fileContent,
                             StringBuilder errors) {
        for (File file : deliveryDir.listFiles(getFileFilter())) {
            if (file.isDirectory()) {
                executeImpl(file, scriptType, fileContent, errors);
            }
            else {
                String objectName = file.getName().substring(0, file.getName().length() - 4);
                if (!fileContent.replaceAll("\t", " ").contains(" " + objectName + " ")) {
                    errors.append(new Formatter().format(getErrorMessage(), scriptType, objectName));
                    if (errors.length() > 0) {
                        errors.append(NEW_LINE);
                    }
                }
            }
        }
    }


    protected void executeImplFor(String objectType) {
        File deliveryDir = new File(deliveryFilePath).getParentFile();
        StringBuilder errors = new StringBuilder();

        String grantContent = TaskUtil.getContentOfFile(new File(deliveryDir, "permission/grant.sql"));
        executeImpl(new File(deliveryDir, objectType), "grant", grantContent, errors);

        String revokeContent = TaskUtil.getContentOfFile(new File(deliveryDir, "permission/revoke.sql"));
        executeImpl(new File(deliveryDir, objectType), "revoke", revokeContent, errors);

        if (errors.length() > 0) {
            throw new BuildException(errors.toString());
        }
    }


    protected abstract String getErrorMessage();


    protected abstract FileFilter getFileFilter();
}
