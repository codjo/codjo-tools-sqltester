/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
package net.codjo.tools.sqltester.batch.task;

import static net.codjo.tools.sqltester.batch.task.util.Constants.NEW_LINE;
import net.codjo.tools.sqltester.batch.task.util.TaskUtil;
import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.tools.ant.BuildException;

/**
 * Task permettant de vérifier le contenu des scripts SQL.
 */
public class CheckGrantsTask implements CheckTask {
    private static final String TASK_NAME = "Verification du fichier des grants";
    private static final int FLAGS = Pattern.DOTALL | Pattern.MULTILINE | Pattern.CASE_INSENSITIVE;
    private String applicationFileName;


    public CheckGrantsTask(String applicationFileName) {
        this.applicationFileName = applicationFileName;
    }


    public String getTaskName() {
        return TASK_NAME;
    }


    public void execute() throws BuildException {
        StringBuffer grantErrors;
        grantErrors = doCheckGrants();
        if (grantErrors.length() != 0) {
            throw new BuildException(grantErrors.toString());
        }
    }


    protected static boolean checkGrantToPublic(String toEvaluate) {
        Pattern pattern = Pattern.compile(".*[,\\s^]to.*[,\\s^]public[,\\s$].*", FLAGS);
        return isSafeSQL(toEvaluate, pattern, "PUBLIC");
    }


    protected static boolean checkGrantAll(String toEvaluate) {
        Pattern pattern = Pattern.compile(".*[,\\s^]all.*[\\s^]on[\\s$].*", FLAGS);
        return isSafeSQL(toEvaluate, pattern, "ALL");
    }


    private static boolean isSafeSQL(String toEvaluate, Pattern pattern, String toFind) {
        if (!toEvaluate.contains(toFind)) {
            return true;
        }

        Matcher matcher;
        String[] theString = toEvaluate.split("GRANT");
        for (int loop = 1; loop < theString.length; loop++) {
            String loopString = theString[loop];
            matcher = pattern.matcher(loopString);
            if (matcher.matches()) {
                return false;
            }
        }
        return true;
    }


    private StringBuffer doCheckGrants() throws BuildException {
        StringBuffer grantPublicErrors = new StringBuffer();
        StringBuffer grantAllErrors = new StringBuffer();

        File applicationFile = new File(applicationFileName);
        String contentOfFile = TaskUtil.getContentOfFile(applicationFile);

        String[] scripts = contentOfFile.replace("\\", "/").split(NEW_LINE);
        for (String script : scripts) {
            if (!"".equals(script)) {
                File parentFile = applicationFile.getParentFile();
                File sqlFile = new File(parentFile, script.trim());
                if (sqlFile.exists()) {
                    checkSqlFile(sqlFile, grantPublicErrors, grantAllErrors);
                }
            }
        }
        return getGrantErrorFiles(grantPublicErrors, grantAllErrors);
    }


    private StringBuffer getGrantErrorFiles(StringBuffer grantPublicErrors, StringBuffer grantAllErrors) {
        StringBuffer grantErrors = new StringBuffer();
        if (grantPublicErrors.length() != 0) {
            grantErrors.append("Les fichiers suivants contiennent des 'grant to public' :").append(NEW_LINE)
                  .append(grantPublicErrors.toString());
        }
        if (grantAllErrors.length() != 0) {
            grantErrors.append("Les fichiers suivants contiennent des 'grant all' :").append(NEW_LINE)
                  .append(grantAllErrors.toString());
        }
        return grantErrors;
    }


    private void checkSqlFile(File sqlFile, StringBuffer grantPublicErrors, StringBuffer grantAllErrors)
          throws BuildException {
        String contentOfScript = TaskUtil.getContentOfFile(sqlFile);
        String noCommentContent = TaskUtil.removeCommentBlocks(contentOfScript).toUpperCase();
        if (!noCommentContent.contains("GRANT")) {
            return;
        }

        if (!checkGrantToPublic(noCommentContent)) {
            grantPublicErrors.append(sqlFile.getName().trim()).append(NEW_LINE);
        }
        if (!checkGrantAll(noCommentContent)) {
            grantAllErrors.append(sqlFile.getName().trim()).append(NEW_LINE);
        }
    }
}
