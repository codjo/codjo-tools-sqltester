package net.codjo.tools.sqltester.batch.task;
import net.codjo.tools.sqltester.batch.ConnectionMetaData;
import static net.codjo.tools.sqltester.batch.task.util.Constants.NEW_LINE;
import net.codjo.tools.sqltester.batch.task.util.TaskUtil;
import net.codjo.util.system.WindowsExec;
import java.io.File;
import org.apache.tools.ant.BuildException;

/**
 *
 */
public abstract class AbstractExecSqlFiles {
    private final WindowsExec windowsExec = new WindowsExec();
    private ConnectionMetaData connectionMetaData;


    public void setConnectionMetaData(ConnectionMetaData connectionMetaData) {
        this.connectionMetaData = connectionMetaData;
    }


    public void executeContentOfFile(String deliveryFileName) {
        File workingDirectory = new File(deliveryFileName).getParentFile();
        String[] scripts = getScripts(deliveryFileName);
        for (String scriptName : scripts) {
            File scriptFile = new File(workingDirectory, scriptName);
            String scriptContent = TaskUtil.getContentOfFile(scriptFile);
            if (!"".equals(scriptName.trim()) && !"".equals(scriptContent)) {
                executeScript(scriptName, createSqlScriptCommand(scriptName), workingDirectory);
            }
        }
    }


    public abstract String getQueryDelimiter();


    public abstract String getProcessInputMessage();


    public abstract String removeConnectionMessage(String processMessage);


    public abstract String createSqlScriptCommand(String scriptFileName);


    public abstract String getSqlErrorKeyWord();


    protected abstract String getScriptNotFoundErrorKeyWord();


    protected ConnectionMetaData getConnectionMetaData() {
        return connectionMetaData;
    }


    private String[] getScripts(String deliveryFileName) {
        String contentOfFile = TaskUtil.getContentOfFile(new File(deliveryFileName)).replace("\\", "/");
        return contentOfFile.split(NEW_LINE);
    }


    private void executeScript(String scriptName, String cmd, File workingDirectory) {
        StringBuilder sqlLog = new StringBuilder();

        String cmds = "cmd.exe /c " + cmd;

        windowsExec.setProcessInput(getProcessInputMessage());
        int returnCode = windowsExec.exec(cmds, workingDirectory);

        if (returnCode != 0) {
            sqlLog.append("Erreur lors de l'execution de la commande.")
                  .append(NEW_LINE)
                  .append(windowsExec.getErrorMessage());
            throw new BuildException(sqlLog.toString());
        }

        String processMessage = windowsExec.getProcessMessage();
        processMessage = removeConnectionMessage(processMessage);

        String lowerCaseProcessMessage = processMessage.toLowerCase();
        String scriptNotFoundKeyWord = getScriptNotFoundErrorKeyWord();
        if (scriptNotFoundKeyWord != null && lowerCaseProcessMessage.contains(scriptNotFoundKeyWord)) {
            sqlLog.append("Erreur :").append(NEW_LINE).append(processMessage);
            throw new BuildException(sqlLog.toString());
        }
        if (lowerCaseProcessMessage.contains(getSqlErrorKeyWord())) {
            sqlLog.append("Erreur lors de l'execution du script : '").append(scriptName.trim()).append("'")
                  .append(NEW_LINE)
                  .append(processMessage);
            throw new BuildException(sqlLog.toString());
        }
    }
}
