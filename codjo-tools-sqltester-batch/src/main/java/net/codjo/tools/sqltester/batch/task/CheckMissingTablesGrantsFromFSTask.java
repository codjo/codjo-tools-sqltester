package net.codjo.tools.sqltester.batch.task;
import java.io.File;
import java.io.FileFilter;
import org.apache.tools.ant.BuildException;

public class CheckMissingTablesGrantsFromFSTask extends AbstractCheckMissingAllGrants {
    private static final String TASK_NAME = "Verification des grants et revokes manquants pour les tables";
    private static final String DEFAULT_ERROR_MESSAGE = "Aucun %s défini pour la table : %s";
    private static final String OBJECT_TYPE = "table";


    public CheckMissingTablesGrantsFromFSTask(String deliveryFilePath) {
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
        return new MyFileFilter();
    }


    private static class MyFileFilter implements FileFilter {
        public boolean accept(File pathname) {
            return pathname.isFile() && !pathname.getName().contains("-gap.")
                   && !pathname.getName().contains("-sequence.") && !pathname.getName().contains("_SEQ_I.");
        }
    }
}
