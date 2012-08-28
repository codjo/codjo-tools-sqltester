/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
package net.codjo.tools.sqltester.batch.task;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;
import junit.framework.TestCase;
import net.codjo.tools.sqltester.batch.ConnectionMetaData;
import org.apache.tools.ant.BuildException;
import org.junit.Ignore;

import static net.codjo.tools.sqltester.batch.task.util.Constants.NEW_LINE;

/**
 * Classe de test de {@link net.codjo.tools.sqltester.batch.task.ExecOracleSqlFilesTask}.
 */
public class ExecOracleSqlFilesTaskTest extends TestCase {
    private ExecOracleSqlFilesTask execSqlFilesTask;
    private ConnectionMetaData metadata;


    public void test_execute_scriptWithError() throws Exception {
        String file = getClass().getResource("ExecSqlFiles_KO_oracle.txt").getFile();
        try {
            execSqlFilesTask = new ExecOracleSqlFilesTask(file, metadata);
            execSqlFilesTask.execute();
            fail("Erreur dans le script AP_TEST_KO.txt !");
        }
        catch (BuildException e) {
            assertTrue(e.getMessage().trim().contains("ERROR at line 1:" + NEW_LINE
                                                      + "ORA-00907: missing right parenthesis"));
            assertTrue(doesTableExist("AP_TEST"));
            assertFalse(doesTableExist("AP_TEST2"));
        }
    }


    public void test_execute_connectionWithError() throws Exception {
        String file = getClass().getResource("ExecSqlFiles_KO_oracle.txt").getFile();
        metadata.setBase("BIDON");
        try {
            execSqlFilesTask = new ExecOracleSqlFilesTask(file, metadata);
            execSqlFilesTask.execute();
            fail("Erreur dans la connection !");
        }
        catch (BuildException e) {
            assertTrue(e.getMessage().trim().contains(
                  "ERROR:" + NEW_LINE
                  + "ORA-12154: TNS:could not resolve the connect identifier specified"));
            metadata.setBase("IDWDEV2");
            assertFalse(doesTableExist("AP_TEST"));
            assertFalse(doesTableExist("AP_TEST2"));
        }
    }


    @Ignore
    public void test_execute_ok() throws Exception {
        String file = getClass().getResource("ExecSqlFiles_OK_oracle.txt").getFile();
        execSqlFilesTask = new ExecOracleSqlFilesTask(file, metadata);
        execSqlFilesTask.execute();
        assertTrue(doesTableExist("AP_TEST"));
    }


    @Override
    protected void setUp() throws Exception {
        String settingsPath = new File(getClass().getResource("../../../../../../oracle-database.properties").getPath())
              .getParentFile().getPath();
        metadata = new ConnectionMetaData(settingsPath, "oracle-database.properties");
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
            connection.createStatement().executeQuery("select 1 from " + tableName);
            return true;
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
            connection.createStatement().execute("drop table " + tableName);
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