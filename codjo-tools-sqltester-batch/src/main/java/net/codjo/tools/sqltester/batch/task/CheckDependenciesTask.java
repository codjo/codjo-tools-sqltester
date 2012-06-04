/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
package net.codjo.tools.sqltester.batch.task;
import static net.codjo.tools.sqltester.batch.task.util.Constants.NEW_LINE;
import net.codjo.tools.sqltester.batch.task.util.TaskUtil;
import java.io.File;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import org.apache.tools.ant.BuildException;
/**
 * Task permettant de vérifier que si.
 */
public class CheckDependenciesTask implements CheckTask {
    private static final String TASK_NAME = "Verification des dependances pour les alters de tables";
    private static final String ALTER_STRING = "ALTER";
    private String applicationFileName;
    private Connection connection;
    private String contentOfFile;


    public CheckDependenciesTask(String applicationFileName, Connection connection) {
        this.connection = connection;
        this.applicationFileName = applicationFileName;
    }


    public String getTaskName() {
        return TASK_NAME;
    }


    public void execute() throws BuildException {
        contentOfFile = TaskUtil.getContentOfFile(new File(applicationFileName));
        contentOfFile = contentOfFile.replace("\\", "/");

        try {
            StringBuffer dependencyErrors = checkDependencies();
            if (dependencyErrors.length() != 0) {
                throw new BuildException(dependencyErrors.toString());
            }
        }
        catch (SQLException e) {
            throw new BuildException("Impossible de determiner les dependances", e);
        }
    }


    private StringBuffer checkDependencies() throws BuildException, SQLException {
        if (!TaskUtil.doesPathContainDirectory(contentOfFile, ALTER_STRING)) {
            return new StringBuffer();
        }

        StringBuffer errors = new StringBuffer();
        String[] scripts = contentOfFile.split(NEW_LINE);
        for (String script : scripts) {
            if (TaskUtil.doesPathContainDirectory(script, ALTER_STRING)) {
                errors.append(checkDependency(TaskUtil.getObjectName(script)));
            }
        }
        return errors;
    }


    private StringBuffer checkDependency(String tableName) throws BuildException, SQLException {
        StringBuffer depErrors = new StringBuffer();
        if (!isTableExists(tableName)) {
            depErrors.append("La table '").append(tableName)
                  .append("' est introuvable dans la base specifiee").append(NEW_LINE);
            return depErrors;
        }

        Statement statement = connection.createStatement();
        try {
            ResultSet resultSet = statement.executeQuery("select distinct object = object_name(d.id)"
                                                         + " from sysdepends d, sysobjects o"
                                                         + " where depid = object_id('" + tableName + "')"
                                                         + " order by object");
            if (!resultSet.next()) {
                return new StringBuffer();
            }
            doCheck(resultSet.getString(1), tableName, depErrors);
            while (resultSet.next()) {
                doCheck(resultSet.getString(1), tableName, depErrors);
            }
            return depErrors;
        }
        finally {
            statement.close();
        }
    }


    private boolean isTableExists(String tableName) throws BuildException, SQLException {
        Statement statement = connection.createStatement();
        try {
            ResultSet resultSet =
                  statement.executeQuery("select 1 from sysobjects where id= object_id('" + tableName + "')");
            return resultSet.next();
        }
        finally {
            statement.close();
        }
    }


    private void doCheck(String dependedObject, String tableName, StringBuffer errors) {
        if (!contentOfFile.contains(dependedObject)
            || TaskUtil.doesContainDropObject(contentOfFile, dependedObject)) {
            if (errors.length() == 0) {
                errors.append("Les objets dependants de la table '").append(tableName)
                      .append("' ne sont pas relivres :");
                errors.append(NEW_LINE);
            }
            errors.append(dependedObject).append(NEW_LINE);
        }
    }
}
