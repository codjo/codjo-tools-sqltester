package net.codjo.tools.sqltester.batch.task;
import net.codjo.tools.sqltester.batch.ConnectionMetaData;
import org.apache.tools.ant.BuildException;
/**
 *
 */
public class ExecMysqlSqlFilesTask extends AbstractExecSqlFiles implements CheckTask {
    private static final String TASK_NAME = "Execution des scripts SQL";
    private String applicationFileName;


    public ExecMysqlSqlFilesTask(String applicationFileName, ConnectionMetaData metadata) {
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
    public String getProcessInputMessage() {
        return null;
    }


    @Override
    public String removeConnectionMessage(String processMessage) {
        return processMessage;
    }


    @Override
    public String getQueryDelimiter() {
        return ";";
    }


    @Override
    public String createSqlScriptCommand(String scriptFileName) {
        return "mysql " + getConnectionMetaData().getCatalog()
               + " -h " + getConnectionMetaData().getServer()
               + " -P " + getConnectionMetaData().getPort()
               + " -u " + getConnectionMetaData().getUser()
               + " -p" + getConnectionMetaData().getPassword()
               + " <\"" + scriptFileName + "\">  log.txt";
    }


    @Override
    public String getSqlErrorKeyWord() {
        return "error ";
    }


    @Override
    protected String getScriptNotFoundErrorKeyWord() {
        return null;
    }
}
