package net.codjo.tools.sqltester.batch.task;
import java.io.File;
import java.io.FileFilter;
import org.apache.tools.ant.BuildException;

public class CheckMissingProcsGrantsFromFSTask extends AbstractCheckMissingAllGrants {
    private static final String TASK_NAME
          = "Verification des grants et revokes manquants pour les procédures";
    private static final String DEFAULT_ERROR_MESSAGE = "Aucun %s défini pour la procedure : %s";
    private static final String OBJECT_TYPE = "procedure";


    public CheckMissingProcsGrantsFromFSTask(String deliveryFilePath) {
        super(deliveryFilePath);
    }


    public String getTaskName() {
        return TASK_NAME;
    }


    public void execute() throws BuildException {
        executeImplFor(OBJECT_TYPE);
    }


    @Override
    protected String getErrorMessage() {
        return DEFAULT_ERROR_MESSAGE;
    }


    @Override
    protected FileFilter getFileFilter() {
        return new MyFilenameFilter();
    }


    private static class MyFilenameFilter implements FileFilter {
        public boolean accept(File pathname) {
            String name = pathname.getName();
            return !".svn".endsWith(name) && !(pathname.isDirectory() && "drop".equals(name));
        }
    }
}
