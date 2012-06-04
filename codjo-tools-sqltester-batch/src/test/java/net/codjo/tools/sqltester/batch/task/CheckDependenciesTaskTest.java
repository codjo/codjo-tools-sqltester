/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
package net.codjo.tools.sqltester.batch.task;
import net.codjo.database.common.api.JdbcFixture;
import static net.codjo.database.common.api.structure.SqlTable.table;
import static net.codjo.test.common.matcher.JUnitMatchers.*;
import static net.codjo.tools.sqltester.batch.task.util.Constants.NEW_LINE;
import java.sql.Connection;
import java.sql.SQLException;
import org.apache.tools.ant.BuildException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
/**
 * Classe de test de {@link CheckDependenciesTask}.
 */
public class CheckDependenciesTaskTest {
    private JdbcFixture jdbcFixture = JdbcFixture.newFixture();
    private CheckDependenciesTask checkDependenciesTask;
    private Connection connection;


    @Before
    public void setUp() throws Exception {
        jdbcFixture.doSetUp();
        connection = jdbcFixture.getConnection();
    }


    @After
    public void tearDown() throws Exception {
        dropObjectsForTest();
        jdbcFixture.doTearDown();
    }


    @Test
    public void test_checkDependencies_NoAlter() throws Exception {
        String file = getClass().getResource("Dependencies_NoAlter.txt").getFile();
        checkDependenciesTask = new CheckDependenciesTask(file, connection);
        checkDependenciesTask.execute();
    }


    @Test
    public void test_checkDependencies_DepKO() throws Exception {
        String file = getClass().getResource("Dependencies_KO.txt").getFile();
        createObjectsForTest();
        try {
            checkDependenciesTask = new CheckDependenciesTask(file, connection);
            checkDependenciesTask.execute();
            fail("Dépendances incorrectes !");
        }
        catch (BuildException e) {
            String expectedMessage =
                  "Les objets dependants de la table 'TEST_DEP' ne sont pas relivres :" + NEW_LINE + "sp_test"
                  + NEW_LINE;
            assertThat(e.getMessage(), equalTo(expectedMessage));
        }
    }


    @Test
    public void test_checkDependencies_TableNotFoundAndDepKO() throws Exception {
        String file = getClass().getResource("TableNotFoundAndDependencies_KO.txt").getFile();
        createObjectsForTest();
        try {
            checkDependenciesTask = new CheckDependenciesTask(file, connection);
            checkDependenciesTask.execute();
            fail("Dépendances incorrectes !");
        }
        catch (BuildException e) {
            String expectedMessage =
                  "La table 'AP_TOTO' est introuvable dans la base specifiee" + NEW_LINE +
                  "Les objets dependants de la table 'TEST_DEP' ne sont pas relivres :" + NEW_LINE + "sp_test"
                  + NEW_LINE;
            assertThat(e.getMessage(), equalTo(expectedMessage));
        }
    }


    @Test
    public void test_checkDependencies_DepDropKO() throws Exception {
        String file = getClass().getResource("Dependencies_Drop_KO.txt").getFile();
        createObjectsForTest();

        checkDependenciesTask = new CheckDependenciesTask(file, connection);
        checkDependenciesTask.execute();
    }


    @Test
    public void test_checkDependencies_DepOK() throws Exception {
        String file = getClass().getResource("Dependencies_OK.txt").getFile();
        createObjectsForTest();
        checkDependenciesTask = new CheckDependenciesTask(file, connection);
        checkDependenciesTask.execute();
    }


    private void createObjectsForTest() throws SQLException {
        jdbcFixture.create(table("TEST_DEP"), "a varchar(2) null");
        jdbcFixture.executeUpdate("create proc sp_test as begin select a from TEST_DEP end");
    }


    private void dropObjectsForTest() {
        jdbcFixture.drop(table("TEST_DEP"));
        try {
            jdbcFixture.executeUpdate("drop proc sp_test");
        }
        catch (SQLException e) {
            ;
        }
    }
}
