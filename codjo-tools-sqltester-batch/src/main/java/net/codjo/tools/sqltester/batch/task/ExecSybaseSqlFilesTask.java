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
public class ExecSybaseSqlFilesTask extends AbstractExecSqlFiles implements CheckTask {
    private static final String TASK_NAME = "Execution des scripts SQL";
    private String applicationFileName;


    public ExecSybaseSqlFilesTask(String applicationFileName, ConnectionMetaData metadata) {
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
        return "\ngo";
    }


    @Override
    public String getProcessInputMessage() {
        return null;
    }


    @Override
    public String removeConnectionMessage(String processMessage) {
        return processMessage;
    }


    @Override
    public String createSqlScriptCommand(String scriptFileName) {
        return "isql -U" + getConnectionMetaData().getUser()
               + " -P" + getConnectionMetaData().getPassword()
               + " -S" + getConnectionMetaData().getBase()
               + " -D" + getConnectionMetaData().getCatalog()
               + " -i \"" + scriptFileName + "\"";
    }


    @Override
    public String getSqlErrorKeyWord() {
        return "msg ";
    }


    @Override
    protected String getScriptNotFoundErrorKeyWord() {
        return null;
    }
}