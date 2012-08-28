package net.codjo.tools.sqltester.batch;
import java.io.File;
import junit.framework.TestCase;
/**
 *
 */
public class ConnectionMetaDataTest extends TestCase {
    private ConnectionMetaData metadata;


    public void test_getCatalog() {
        assertEquals("LIB", metadata.getCatalog());
    }


    public void test_getBase() {
        assertEquals("LIB_INT15", metadata.getBase());
    }


    public void test_getUser() {
        assertEquals("LIB_dbo", metadata.getUser());
    }


    public void test_getPassword() {
        assertEquals("LIBPWD", metadata.getPassword());
    }


    public void test_getPort() {
        assertEquals("34100", metadata.getPort());
    }


    public void test_getServer() {
        assertEquals("ai-lib", metadata.getServer());
    }


    public void test_getJdbcUrl() {
        assertEquals("jdbc:sybase:Tds:ai-lib:34100", metadata.getJdbcUrl());
    }


    public void test_getDriver() {
        assertEquals("com.sybase.jdbc2.jdbc.SybDriver", metadata.getDriver());
    }


    public void test_getDatabaseType() {
        assertEquals("sybase", metadata.getDatabaseType());
    }


    @Override
    protected void setUp() throws Exception {
        String settingsPath = new File(getClass().getResource("../../../../../sybase-database.properties").getPath())
              .getParentFile().getPath();
        metadata = new ConnectionMetaData(settingsPath, "sybase-database.properties");
    }
}
