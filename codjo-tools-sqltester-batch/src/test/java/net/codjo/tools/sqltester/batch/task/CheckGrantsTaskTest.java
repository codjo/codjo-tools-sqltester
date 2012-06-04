/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
package net.codjo.tools.sqltester.batch.task;
import static net.codjo.tools.sqltester.batch.task.util.Constants.NEW_LINE;
import org.apache.tools.ant.BuildException;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import org.junit.Test;
/**
 * Classe de test de {@link CheckGrantsTask}.
 */
public class CheckGrantsTaskTest {
    private CheckGrantsTask task;


    @Test
    public void test_checkSqlFilesGrant() throws Exception {
        String file = getClass().getResource("Grants_KO.txt").getFile();
        try {
            task = new CheckGrantsTask(file);
            task.execute();
            fail("Grants incorrects !");
        }
        catch (BuildException e) {
            String expectedMessage =
                  "Les fichiers suivants contiennent des 'grant to public' :" + NEW_LINE
                  + "AP_LOG.txt" + NEW_LINE
                  + "Les fichiers suivants contiennent des 'grant all' :" + NEW_LINE
                  + "AP_LOG2.txt" + NEW_LINE;
            assertEquals(expectedMessage, e.getMessage());
        }
    }


    @Test
    public void test_checkSqlFilesGrant_ignoreEmptyLine() throws Exception {
        SqlDeliveryFixture fixture = new SqlDeliveryFixture();
        fixture.doSetUp();

        fixture.mockDeliveryFile("table/AP_Common.sql", "", "permission/grant.sql");
        fixture.mockGrants("AP_Common");
        try {
            task = new CheckGrantsTask(fixture.getDeliveryFilePath());
            task.execute();
        }
        finally {
            fixture.doTearDown();
        }
    }


    @Test
    public void test_checkGrantToPublic_ko() throws Exception {
        String toEvaluate =
              "start" + NEW_LINE
              + "grant select, insert, delete, update, references on AP_LOG to Maintenance,Public,Batch"
              + NEW_LINE + "end";
        assertFalse(CheckGrantsTask.checkGrantToPublic(toEvaluate.toUpperCase()));
    }


    @Test
    public void test_checkGrantToPublic_ok() throws Exception {
        String toEvaluate =
              "start" + NEW_LINE
              + "grant select, insert, delete, update, references on AP_LOG to Maintenance,Batch"
              + NEW_LINE + "end";
        assertTrue(CheckGrantsTask.checkGrantToPublic(toEvaluate.toUpperCase()));
    }


    @Test
    public void test_checkGrantToPublic_twoGrants() throws Exception {
        String toEvaluate =
              "start" + NEW_LINE
              + "grant select, insert, delete, update, references on AP_LOG to Maintenance,Batch"
              + NEW_LINE
              + "grant select, insert, delete, update, references on AP_LOG to Maintenance,Public,Batch"
              + NEW_LINE + "end";
        assertFalse(CheckGrantsTask.checkGrantToPublic(toEvaluate.toUpperCase()));
    }


    @Test
    public void test_checkGrantToPublic_lineEndsWithPublic() throws Exception {
        String toEvaluate =
              "start" + NEW_LINE
              + "grant select, insert, delete, update, references on AP_LOG to Maintenance,Batch,Public"
              + NEW_LINE
              + "grant select, insert, delete, update, references on AP_LOG to Maintenance,Batch"
              + NEW_LINE + "end";
        assertFalse(CheckGrantsTask.checkGrantToPublic(toEvaluate.toUpperCase()));
    }


    @Test
    public void test_checkGrantToPublic_fileEndsWithPublic() throws Exception {
        String toEvaluate =
              "start" + NEW_LINE
              + "grant select, insert, delete, update, references on AP_LOG to Maintenance,Batch"
              + NEW_LINE
              + "grant select, insert, delete, update, references on AP_LOG to Maintenance,Batch,"
              + NEW_LINE + "PuBlic go";
        assertFalse(CheckGrantsTask.checkGrantToPublic(toEvaluate.toUpperCase()));
    }


    @Test
    public void test_checkGrantToPublic_lineStartsWithPublic() throws Exception {
        String toEvaluate =
              "start" + NEW_LINE
              + "grant select, insert, delete, update, references on AP_LOG to Maintenance,Batch,"
              + NEW_LINE
              + "public grant select, insert, delete, update, references on AP_LOG to Maintenance,Batch"
              + NEW_LINE + "END";
        assertFalse(CheckGrantsTask.checkGrantToPublic(toEvaluate.toUpperCase()));
    }


    @Test
    public void test_checkGrantToPublic_lineSpacedEndsWithPublic() throws Exception {
        String toEvaluate =
              "start" + NEW_LINE
              + "grant select, insert, delete, update, references on AP_LOG to Maintenance, Batch, "
              + NEW_LINE
              + " grant select, insert, delete, update, references on AP_LOG to Maintenance, Batch, public "
              + NEW_LINE + " END ";
        assertFalse(CheckGrantsTask.checkGrantToPublic(toEvaluate.toUpperCase()));
    }


    @Test
    public void test_checkGrantAll_KO() throws Exception {
        String toEvaluate =
              "start" + NEW_LINE + "grant all on AP_LOG to Maintenance,Batch" + NEW_LINE
              + "end";
        assertFalse(CheckGrantsTask.checkGrantAll(toEvaluate.toUpperCase()));
    }


    @Test
    public void test_checkGrantAll_OK() throws Exception {
        String toEvaluate =
              "start" + NEW_LINE
              + "grant select, insert, delete, update, references on AP_LOG to Maintenance,Batch"
              + NEW_LINE + "all at end";
        assertTrue(CheckGrantsTask.checkGrantAll(toEvaluate.toUpperCase()));
    }


    @Test
    public void test_checkGrantAll_twoGrants() throws Exception {
        String toEvaluate =
              "start" + NEW_LINE + "grant all on AP_LOG to Maintenance,Batch" + NEW_LINE
              + "grant select, insert, delete, update, references on AP_LOG to Maintenance,Batch"
              + NEW_LINE + "end";
        assertFalse(CheckGrantsTask.checkGrantAll(toEvaluate.toUpperCase()));
    }


    @Test
    public void test_checkGrantAll_lineSpaced() throws Exception {
        String toEvaluate =
              "start" + NEW_LINE + "grant" + NEW_LINE + "all" + NEW_LINE
              + "on AP_LOG to Maintenance,Batch" + NEW_LINE
              + "grant select, insert, delete, update, references on AP_LOG_ALL to Maintenance,Batch"
              + NEW_LINE + "end";
        assertFalse(CheckGrantsTask.checkGrantAll(toEvaluate.toUpperCase()));
    }


    @Test
    public void test_checkGrantAll_lineSpacedOut() throws Exception {
        String toEvaluate =
              "start" + NEW_LINE + "grant insert," + NEW_LINE + "all" + NEW_LINE
              + ",update on AP_LOG to Maintenance,Batch" + NEW_LINE
              + "grant select, insert, delete, update, references on AP_LOG_ALL to Maintenance,Batch"
              + NEW_LINE + "end";
        assertFalse(CheckGrantsTask.checkGrantAll(toEvaluate.toUpperCase()));
    }


    @Test
    public void test_checkGrantAll_InsertUpdateAll() throws Exception {
        String toEvaluate =
              NEW_LINE + "grant insert,all,update on AP_LOG to Maintenance,Batch"
              + NEW_LINE
              + "grant select, insert, delete, update, references on AP_LOG to Maintenance,Batch"
              + NEW_LINE + "end";
        assertFalse(CheckGrantsTask.checkGrantAll(toEvaluate.toUpperCase()));
    }
}
