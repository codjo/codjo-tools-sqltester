/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
package net.codjo.tools.sqltester.batch.task;

import static net.codjo.tools.sqltester.batch.task.util.Constants.NEW_LINE;
import net.codjo.tools.sqltester.batch.task.util.TaskUtil;
import java.io.File;
import org.apache.tools.ant.BuildException;

/**
 * Task permettant de vérifier l'existence des gaps.
 */
public class CheckMissingIndexTask implements CheckTask {
    private static final String TASK_NAME = "Verification des indexes manquants";
    private String applicationFileName;
    private String contentOfFile;


    public CheckMissingIndexTask(String applicationFileName) {
        this.applicationFileName = applicationFileName;
    }


    public String getTaskName() {
        return TASK_NAME;
    }


    public void execute() throws BuildException {
        contentOfFile = TaskUtil.getContentOfFile(new File(applicationFileName));
        contentOfFile = contentOfFile.replace("\\", "/");

        StringBuffer indexErrors = doCheckIndexes();
        if (indexErrors.length() != 0) {
            throw new BuildException(indexErrors.toString());
        }
    }


    private StringBuffer doCheckIndexes() throws BuildException {
        StringBuffer indexErrors = new StringBuffer();

        String[] scripts = contentOfFile.split(NEW_LINE);
        for (String script : scripts) {
            if (TaskUtil.doesPathContainTable(script)) {
                checkSqlFile(TaskUtil.getObjectName(script), indexErrors);
            }
        }
        return indexErrors;
    }


    private void checkSqlFile(String tableName, StringBuffer indexErrors) throws BuildException {
        if (!contentOfFile.contains("index/" + tableName)) {
            if (indexErrors.length() != 0) {
                indexErrors.append(NEW_LINE);
            }
            indexErrors.append("Aucun index défini pour la table '").append(tableName).append("'");
        }
    }
}