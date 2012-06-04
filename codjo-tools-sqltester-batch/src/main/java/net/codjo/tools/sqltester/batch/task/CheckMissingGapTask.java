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
public class CheckMissingGapTask implements CheckTask {
    private static final String TASK_NAME = "Verification des gaps manquants";
    private String applicationFileName;
    private String contentOfFile;


    public CheckMissingGapTask(String applicationFileName) {
        this.applicationFileName = applicationFileName;
    }


    public String getTaskName() {
        return TASK_NAME;
    }


    public void execute() throws BuildException {
        File applicationFile = new File(applicationFileName);
        contentOfFile = TaskUtil.getContentOfFile(applicationFile);
        contentOfFile = contentOfFile.replace("\\", "/");

        StringBuffer gapErrors = doCheckGaps(applicationFile);
        if (gapErrors.length() != 0) {
            throw new BuildException(gapErrors.toString());
        }
    }


    private StringBuffer doCheckGaps(File applicationFile) throws BuildException {
        StringBuffer gapErrors = new StringBuffer();

        String[] scripts = contentOfFile.split(NEW_LINE);
        for (String script : scripts) {
            if (TaskUtil.doesPathContainTable(script)) {
                File parentFile = applicationFile.getParentFile();
                File sqlFile = new File(parentFile, script.trim());
                checkSqlFile(sqlFile, TaskUtil.getObjectName(script), gapErrors);
            }
        }
        return gapErrors;
    }


    private void checkSqlFile(File sqlFile, String tableName, StringBuffer gapErrors)
          throws BuildException {
        String contentOfScript = TaskUtil.getContentOfFile(sqlFile);
        String noCommentContent = TaskUtil.removeCommentBlocks(contentOfScript).toUpperCase();

        if (noCommentContent.contains(" IDENTITY ")
            && !contentOfFile.contains(tableName + "-gap")) {
            gapErrors.append("Aucun gap defini pour la table '")
                  .append(tableName).append("'").append(NEW_LINE);
        }
    }
}