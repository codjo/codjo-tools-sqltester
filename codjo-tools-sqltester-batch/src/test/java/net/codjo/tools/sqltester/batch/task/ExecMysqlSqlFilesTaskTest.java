/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
package net.codjo.tools.sqltester.batch.task;

import net.codjo.tools.sqltester.batch.ConnectionMetaData;
import static net.codjo.tools.sqltester.batch.task.util.Constants.NEW_LINE;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;
import junit.framework.TestCase;
import org.apache.tools.ant.BuildException;

/**
 * Classe de test de {@link net.codjo.tools.sqltester.batch.task.ExecSybaseSqlFilesTask}.
 */
public class ExecMysqlSqlFilesTaskTest extends TestCase {
    private ExecMysqlSqlFilesTask execSqlFilesTask;
    private ConnectionMetaData metadata;


    public void test_execute_scriptWithError() throws Exception {
        String file = getClass().getResource("ExecSqlFiles_KO_mysql.txt").getFile();
        try {
            execSqlFilesTask = new ExecMysqlSqlFilesTask(file, metadata);
            execSqlFilesTask.execute();
            fail("Erreur dans le script AP_TEST_KO.txt !");
        }
        catch (BuildException e) {
            String expectedMessage =
                  "Erreur lors de l'execution de la commande." + NEW_LINE
                  + "Error message :" + NEW_LINE
                  + "ERROR 1064 (42000) at line 3: Erreur de syntaxe près de 'toto   not null" + NEW_LINE
                  + ") ENGINE=InnoDB' à la ligne 3" + NEW_LINE
                  + "Output message :";
            assertEquals(expectedMessage, e.getMessage().trim());
            assertTrue(doesTableExist("AP_TEST"));
            assertFalse(doesTableExist("AP_TEST2"));
        }
    }


    public void test_execute_connectionWithError() throws Exception {
        String file = getClass().getResource("ExecSqlFiles_KO_mysql.txt").getFile();
        metadata.setCatalog("BIDON");
        try {
            execSqlFilesTask = new ExecMysqlSqlFilesTask(file, metadata);
            execSqlFilesTask.execute();
            fail("Erreur dans la connection !");
        }
        catch (BuildException e) {
            String expectedMessage = "Erreur lors de l'execution de la commande." + NEW_LINE
                                     + "Error message :" + NEW_LINE
                                     + "ERROR 1044 (42000): Accès refusé pour l'utilisateur: '"
                                     + metadata.getUser() + "'@'@%'. Base 'BIDON'"
                                     + NEW_LINE
                                     + "Output message :";
            assertEquals(expectedMessage, e.getMessage().trim());
            assertFalse(doesTableExist("AP_TEST"));
            assertFalse(doesTableExist("AP_TEST2"));
        }
    }


    public void test_execute_ok() throws Exception {
        String file = getClass().getResource("ExecSqlFiles_OK_mysql.txt").getFile();
        execSqlFilesTask = new ExecMysqlSqlFilesTask(file, metadata);
        execSqlFilesTask.execute();
        assertTrue(doesTableExist("AP_TEST"));
    }


    @Override
    protected void setUp() throws Exception {
        metadata = new ConnectionMetaData(getClass().getResource("../settings-mysql.xml").getPath());
        dropTables();
    }


    @Override
    protected void tearDown() throws Exception {
        dropTables();
    }


    private void dropTables() throws ClassNotFoundException, SQLException {
        dropTable("AP_TEST");
        dropTable("AP_TEST2");
    }


    private boolean doesTableExist(String tableName) throws ClassNotFoundException, SQLException {
        Connection connection = builConnection();
        try {
            ResultSet resultSet = connection.createStatement()
                  .executeQuery("select 1 from information_schema.TABLES"
                                + " where TABLE_SCHEMA = '" + metadata.getCatalog() + "'"
                                + " and TABLE_NAME = '" + tableName + "'");
            return resultSet.next();
        }
        catch (SQLException e) {
            return false;
        }
        finally {
            closeConnection(connection);
        }
    }


    private void dropTable(String tableName) throws ClassNotFoundException, SQLException {
        Connection connection = builConnection();
        try {
            connection.createStatement().execute("drop table if exists " + tableName);
        }
        catch (SQLException e) {
            ;
        }
        finally {
            closeConnection(connection);
        }
    }


    private Connection builConnection() {
        Properties props = new Properties();
        props.put("user", metadata.getUser());
        props.put("password", metadata.getPassword());

        Connection connection = null;
        try {
            Class.forName(metadata.getDriver());
            connection = DriverManager.getConnection(metadata.getJdbcUrl(), props);
            connection.setCatalog(metadata.getCatalog());
        }
        catch (Exception e) {
            ;
        }

        return connection;
    }


    private void closeConnection(Connection connection) throws SQLException {
        connection.close();
    }
}