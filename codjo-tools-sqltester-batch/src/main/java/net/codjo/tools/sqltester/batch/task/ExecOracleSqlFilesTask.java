/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
package net.codjo.tools.sqltester.batch.task;

import net.codjo.tools.sqltester.batch.ConnectionMetaData;
import org.apache.tools.ant.BuildException;

/**
 * Task permettant d'exécuter les scripts SQL référencés dans le fichier "application.txt".
 */
public class ExecOracleSqlFilesTask extends AbstractExecSqlFiles implements CheckTask {
    private static final String TASK_NAME = "Execution des scripts SQL";
    private String applicationFileName;


    public ExecOracleSqlFilesTask(String applicationFileName, ConnectionMetaData metadata) {
        this.applicationFileName = applicationFileName;
        setConnectionMetaData(metadata);
    }


    public String getTaskName() {
        return TASK_NAME;
    }


    public void execute() throws BuildException {
        executeContentOfFile(applicationFileName);
    }


    @Override
    public String getQueryDelimiter() {
        return ";";
    }


    @Override
    public String getProcessInputMessage() {
        return "\n\nexit";
    }


    @Override
    public String removeConnectionMessage(String processMessage) {
        int promptIndex = processMessage.indexOf("SQL>");
        if (promptIndex != -1) {
            processMessage = processMessage.substring(0, promptIndex);
        }
        int productionIndex = processMessage.lastIndexOf("Production");
        if (productionIndex != -1) {
            processMessage = processMessage.substring(productionIndex + 10);
        }
        return processMessage;
    }


    @Override
    public String createSqlScriptCommand(String scriptFileName) {
        return "sqlplus " + getConnectionMetaData().getUser() + "/"
               + getConnectionMetaData().getPassword() + "@"
               + getConnectionMetaData().getBase() + " @./"
               + "\"" + scriptFileName + "\"";
    }


    @Override
    public String getSqlErrorKeyWord() {
        return "ora-";
    }


    @Override
    protected String getScriptNotFoundErrorKeyWord() {
        return null;
    }
}