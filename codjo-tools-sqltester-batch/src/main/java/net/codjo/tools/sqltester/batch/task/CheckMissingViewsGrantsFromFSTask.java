package net.codjo.tools.sqltester.batch.task;
import java.io.File;
import java.io.FileFilter;
import org.apache.tools.ant.BuildException;

public class CheckMissingViewsGrantsFromFSTask extends AbstractCheckMissingAllGrants {
    private static final String TASK_NAME = "Verification des grants et revokes manquants pour les vues";
    private static final String DEFAULT_ERROR_MESSAGE = "Aucun %s défini pour la vue : %s";
    private static final String OBJECT_TYPE = "view";


    public CheckMissingViewsGrantsFromFSTask(String deliveryFilePath) {
        super(deliveryFilePath);
    }


    @Override
    protected String getErrorMessage() {
        return DEFAULT_ERROR_MESSAGE;
    }


    @Override
    protected FileFilter getFileFilter() {
        return new MyFileFilter();
    }


    public String getTaskName() {
        return TASK_NAME;
    }


    public void execute() throws BuildException {
        executeImplFor(OBJECT_TYPE);
    }


    private static class MyFileFilter implements FileFilter {
        public boolean accept(File pathname) {
            String name = pathname.getName();
            return !".svn".endsWith(name) && !"drop".equals(name);
        }
    }
}