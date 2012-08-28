package net.codjo.tools.sqltester.batch;
import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import net.codjo.tools.sqltester.batch.task.CheckDependenciesTask;
import net.codjo.tools.sqltester.batch.task.CheckGrantsTask;
import net.codjo.tools.sqltester.batch.task.CheckMissingGapTask;
import net.codjo.tools.sqltester.batch.task.CheckMissingGrantsTask;
import net.codjo.tools.sqltester.batch.task.CheckMissingIndexTask;
import net.codjo.tools.sqltester.batch.task.CheckMissingProcsGrantsFromFSTask;
import net.codjo.tools.sqltester.batch.task.CheckMissingTablesGrantsFromFSTask;
import net.codjo.tools.sqltester.batch.task.CheckScriptsExistenceTask;
import net.codjo.tools.sqltester.batch.task.CheckTask;
import net.codjo.tools.sqltester.batch.task.CheckUnusedGrantsTask;
import net.codjo.tools.sqltester.batch.task.ExecMysqlSqlFilesTask;
import net.codjo.tools.sqltester.batch.task.ExecOracleSqlFilesTask;
import net.codjo.tools.sqltester.batch.task.ExecSybaseSqlFilesTask;
import net.codjo.tools.sqltester.batch.task.util.TaskUtil;
import org.apache.log4j.Logger;
import org.apache.tools.ant.BuildException;

import static net.codjo.tools.sqltester.batch.task.util.Constants.NEW_LINE;

public class CheckTasksExecutor {
    private static final Logger LOG = Logger.getLogger(CheckTasksExecutor.class);
    private ConnectionMetaData metadata;
    private String deliverySqlFilePath;
    private int nbErrors = 0;


    public CheckTasksExecutor(String deliverySqlFilePath) {
        this.deliverySqlFilePath = deliverySqlFilePath;
        try {
            metadata = new ConnectionMetaData(deliverySqlFilePath);
        }
        catch (IOException e) {
            throw new BuildException(
                  "Le fichier database.properties est introuvable dans le répertoire target/test-classes du module sql");
        }
        executeTasks();
    }


    private void executeTasks() {
        if (!new File(deliverySqlFilePath).exists()) {
            throw new BuildException("Le fichier " + deliverySqlFilePath + " est introuvable");
        }

        String sqlFilePath = TaskUtil.assemblySql(deliverySqlFilePath);

        File applicationFile = new File(sqlFilePath);
        String contentOfFile = TaskUtil.getContentOfFile(applicationFile);
        if (contentOfFile == null) {
            LOG.info("Le fichier " + sqlFilePath + " est vide !");
            return;
        }

        LOG.info("Debut des verifications sur la base '" + metadata.getCatalog() + "'");
        StringBuffer status = null;
        if ("sybase".equalsIgnoreCase(metadata.getDatabaseType())) {
            status = executeCheckTasksForSybase(sqlFilePath);
        }
        else if ("mysql".equalsIgnoreCase(metadata.getDatabaseType())) {
            status = executeCheckTasksForMysql(sqlFilePath);
        }
        else if ("oracle".equalsIgnoreCase(metadata.getDatabaseType())) {
            status = executeCheckTasksForOracle(sqlFilePath);
        }
        showResult(status);
    }


    private StringBuffer executeCheckTasksForSybase(String sqlFilePath) {
        StringBuffer status = new StringBuffer();
        executeTask(new CheckScriptsExistenceTask(sqlFilePath), status);
        executeTask(new CheckGrantsTask(sqlFilePath), status);
        executeTask(new CheckMissingGrantsTask(sqlFilePath), status);
        executeTask(new CheckUnusedGrantsTask(sqlFilePath), status);
        executeTask(new CheckMissingIndexTask(sqlFilePath), status);
        executeTask(new CheckMissingGapTask(sqlFilePath), status);
        executeTask(new CheckMissingTablesGrantsFromFSTask(sqlFilePath), status);
        executeTask(new CheckMissingProcsGrantsFromFSTask(sqlFilePath), status);

        Connection connection = null;
        try {
            connection = TaskUtil.builConnection(metadata);
            executeTask(new CheckDependenciesTask(sqlFilePath, connection), status);
            executeTask(new ExecSybaseSqlFilesTask(sqlFilePath, metadata), status);
        }
        catch (SQLException e) {
            LOG.error("Erreur d'ouverture de connection : " + e.getLocalizedMessage());
        }
        catch (ClassNotFoundException e) {
            LOG.error("Erreur de chargement du driver JDBC : " + e.getLocalizedMessage());
        }
        finally {
            try {
                if (connection != null) {
                    TaskUtil.closeConnection(connection);
                }
            }
            catch (SQLException e) {
                LOG.error("Erreur de fermeture de connection : " + e.getLocalizedMessage());
            }
        }
        return status;
    }


    private StringBuffer executeCheckTasksForMysql(String sqlFilePath) {
        StringBuffer status = new StringBuffer();
        executeTask(new CheckScriptsExistenceTask(sqlFilePath), status);
        executeTask(new CheckGrantsTask(sqlFilePath), status);
        executeTask(new CheckMissingGrantsTask(sqlFilePath), status);
        executeTask(new CheckUnusedGrantsTask(sqlFilePath), status);
        executeTask(new CheckMissingIndexTask(sqlFilePath), status);
        executeTask(new CheckMissingTablesGrantsFromFSTask(sqlFilePath), status);
        executeTask(new CheckMissingProcsGrantsFromFSTask(sqlFilePath), status);
        executeTask(new ExecMysqlSqlFilesTask(sqlFilePath, metadata), status);

        return status;
    }


    private StringBuffer executeCheckTasksForOracle(String sqlFilePath) {
        StringBuffer status = new StringBuffer();
        executeTask(new CheckScriptsExistenceTask(sqlFilePath), status);
        executeTask(new CheckGrantsTask(sqlFilePath), status);
        executeTask(new CheckMissingGrantsTask(sqlFilePath), status);
        executeTask(new CheckUnusedGrantsTask(sqlFilePath), status);
        executeTask(new CheckMissingIndexTask(sqlFilePath), status);
        executeTask(new CheckMissingTablesGrantsFromFSTask(sqlFilePath), status);
        executeTask(new CheckMissingProcsGrantsFromFSTask(sqlFilePath), status);
        executeTask(new ExecOracleSqlFilesTask(sqlFilePath, metadata), status);

        return status;
    }


    private void showResult(StringBuffer status) {
        if (nbErrors != 0) {
            LOG.info(NEW_LINE);
            LOG.info("########################################################################");
            LOG.info(" " + nbErrors + " erreur(s) potentielle(s) detectee(s)");
            LOG.info("########################################################################");
            LOG.info(NEW_LINE);
            LOG.info(status);
            LOG.info("------------------------------------------------------------------------");
            LOG.info(" CHECK FAILURE");
            LOG.info("------------------------------------------------------------------------");
        }
        else {
            LOG.info("##########################");
            LOG.info("# Aucune erreur detectee #");
            LOG.info("##########################");
        }
    }


    private StringBuffer executeTask(CheckTask checkTask, StringBuffer status) {
        try {
            checkTask.execute();
            LOG.info(checkTask.getTaskName());
        }
        catch (BuildException e) {
            nbErrors++;
            LOG.info(checkTask.getTaskName() + " <<< FAILURE!");
            if (status.length() != 0) {
                status.append(NEW_LINE).append(NEW_LINE);
            }
            status.append("ERREUR ").append(nbErrors).append(" ===> ").append(e.getMessage());
        }
        return status;
    }
}
