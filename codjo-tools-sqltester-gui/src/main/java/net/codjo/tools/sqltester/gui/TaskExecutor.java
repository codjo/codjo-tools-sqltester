package net.codjo.tools.sqltester.gui;
import net.codjo.tools.sqltester.batch.ConnectionMetaData;
import net.codjo.tools.sqltester.batch.task.CheckDependenciesTask;
import net.codjo.tools.sqltester.batch.task.CheckGrantsTask;
import net.codjo.tools.sqltester.batch.task.CheckMissingGapTask;
import net.codjo.tools.sqltester.batch.task.CheckMissingGrantsTask;
import net.codjo.tools.sqltester.batch.task.CheckMissingIndexTask;
import net.codjo.tools.sqltester.batch.task.CheckMissingProcsGrantsFromFSTask;
import net.codjo.tools.sqltester.batch.task.CheckMissingTablesGrantsFromFSTask;
import net.codjo.tools.sqltester.batch.task.CheckMissingViewsGrantsFromFSTask;
import net.codjo.tools.sqltester.batch.task.CheckScriptObjectNamesTask;
import net.codjo.tools.sqltester.batch.task.CheckScriptsExistenceTask;
import net.codjo.tools.sqltester.batch.task.CheckTask;
import net.codjo.tools.sqltester.batch.task.CheckUnusedGrantsTask;
import net.codjo.tools.sqltester.batch.task.ExecMysqlSqlFilesTask;
import net.codjo.tools.sqltester.batch.task.ExecSybaseSqlFilesTask;
import net.codjo.tools.sqltester.batch.task.util.Constants.BaseType;
import net.codjo.tools.sqltester.batch.task.util.Constants.CheckType;
import static net.codjo.tools.sqltester.batch.task.util.Constants.NEW_LINE;
import net.codjo.tools.sqltester.batch.task.util.TaskUtil;
import java.io.File;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import javax.swing.DefaultListModel;
import org.apache.tools.ant.BuildException;

/**
 *
 */
public class TaskExecutor {
    public static final String WARNING = "Warning : ";
    public static final String FAILURE = "Failure : ";
    public static final String INFO = "Info :       ";
    public static final String TITLE = "Title :      ";
    public static final String RESULT = "Result :  ";
    private ConnectionMetaData metadata;
    private int nbErrors = 0;
    private String deliverySqlFilePath;
    private DefaultListModel listModel = new DefaultListModel();


    public TaskExecutor(String deliverySqlFilePath) {
        this.deliverySqlFilePath = deliverySqlFilePath;
        metadata = new ConnectionMetaData();
    }


    public DefaultListModel getListModel() {
        return listModel;
    }


    void executeTasks(BaseType baseType, List<CheckType> checkList) {
        if (!new File(deliverySqlFilePath).exists()) {
            throw new BuildException("Le fichier " + deliverySqlFilePath + " est introuvable");
        }

        listModel.removeAllElements();
        String sqlFilePath = TaskUtil.assemblySql(deliverySqlFilePath);

        File applicationFile = new File(sqlFilePath);
        String contentOfFile = TaskUtil.getContentOfFile(applicationFile);
        if (contentOfFile == null || contentOfFile.trim().length() == 0) {
            listModel.addElement(FAILURE + "Aucun contrôle effectué.");
            listModel.addElement(FAILURE + "Le fichier " + sqlFilePath + " est vide !");
            return;
        }
        listModel.addElement(
              TITLE + "Début des vérifications sur la base '" + metadata.getCatalog() + "'");

        StringBuffer status = new StringBuffer();
        for (CheckType checkType : checkList) {
            status = lauchCheckTask(baseType, sqlFilePath, status, checkType);
        }
        showResult(status.toString());
    }


    @SuppressWarnings({"OverlyCoupledMethod"})
    private StringBuffer lauchCheckTask(BaseType baseType,
                                        String sqlFilePath,
                                        StringBuffer status,
                                        CheckType checkType) {
        switch (checkType) {
            case GRANTS:
                status = executeTask(new CheckGrantsTask(sqlFilePath), status);
                break;
            case DEPENDENCIES:
                executeCheckDependenciesTask(sqlFilePath, status);
                break;
            case EXEC_SQL_FILES:
                if (baseType == BaseType.SYBASE) {
                    executeTask(new ExecSybaseSqlFilesTask(sqlFilePath, metadata), status);
                }
                else if (baseType == BaseType.MYSQL) {
                    executeTask(new ExecMysqlSqlFilesTask(sqlFilePath, metadata), status);
                }
                break;
            case MISSING_GAP:
                executeTask(new CheckMissingGapTask(sqlFilePath), status);
                break;
            case MISSING_GRANT:
                executeTask(new CheckMissingGrantsTask(sqlFilePath), status);
                break;
            case MISSING_INDEX:
                executeTask(new CheckMissingIndexTask(sqlFilePath), status);
                break;
            case MISSING_PROCS_GRANTS:
                executeTask(new CheckMissingProcsGrantsFromFSTask(sqlFilePath), status);
                break;
            case MISSING_TABLES_GRANTS:
                executeTask(new CheckMissingTablesGrantsFromFSTask(sqlFilePath), status);
                break;
            case MISSING_VIEWS_GRANTS:
                executeTask(new CheckMissingViewsGrantsFromFSTask(sqlFilePath), status);
                break;
            case SCRIPTS_EXISTENCE:
                executeTask(new CheckScriptsExistenceTask(sqlFilePath), status);
                break;
            case UNUSED_GRANT:
                executeTask(new CheckUnusedGrantsTask(sqlFilePath), status);
                break;
            case SCRIPT_OBJECT_NAME:
                executeTask(new CheckScriptObjectNamesTask(sqlFilePath), status);
                break;
            default:
                throw new IllegalStateException("Contrôle inconnu : " + checkType.toString());
        }
        return status;
    }


    private void executeCheckDependenciesTask(String sqlFilePath, StringBuffer status) {
        Connection connection = null;
        try {
            connection = TaskUtil.builConnection(metadata);
            executeTask(new CheckDependenciesTask(sqlFilePath, connection), status);
        }
        catch (SQLException e) {
            listModel.addElement(
                  WARNING + "Erreur d'ouverture de connection : " + e.getLocalizedMessage());
        }
        catch (ClassNotFoundException e) {
            listModel.addElement(
                  WARNING + "Erreur de chargement du driver JDBC : " + e.getLocalizedMessage());
        }
        finally {
            try {
                if (connection != null) {
                    TaskUtil.closeConnection(connection);
                }
            }
            catch (SQLException e) {
                listModel.addElement(
                      WARNING + "Erreur de fermeture de connection : " + e.getLocalizedMessage());
            }
        }
    }


    public void showResult(String status) {
        if (nbErrors != 0) {
            listModel.addElement(WARNING + "###############################################################");
            listModel.addElement(
                  WARNING + " CHECK FAILURE : " + nbErrors + " erreur(s) potentielle(s) détectée(s)");
            listModel.addElement(WARNING + "###############################################################");
            String[] strings = status.split(NEW_LINE);
            for (String string : strings) {
                listModel.addElement(WARNING + string);
            }
        }
        else {
            listModel.addElement(RESULT + "##################");
            listModel.addElement(RESULT + "# Aucune erreur détectée #");
            listModel.addElement(RESULT + "##################");
        }
    }


    private StringBuffer executeTask(CheckTask checkTask, StringBuffer status) {
        try {
            checkTask.execute();
            listModel.addElement(INFO + checkTask.getTaskName());
        }
        catch (BuildException e) {
            nbErrors++;
            listModel.addElement(FAILURE + checkTask.getTaskName() + " <<< FAILURE!");
            if (status.length() != 0) {
                status.append(NEW_LINE).append("").append(NEW_LINE);
            }
            status.append("ERREUR ")
                  .append(nbErrors)
                  .append(" :")
                  .append(NEW_LINE)
                  .append(e.getMessage());
        }
        return status;
    }
}
