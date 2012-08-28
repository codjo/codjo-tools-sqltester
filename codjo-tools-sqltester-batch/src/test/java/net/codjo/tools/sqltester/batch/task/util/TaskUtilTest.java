/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
package net.codjo.tools.sqltester.batch.task.util;

import static net.codjo.test.common.matcher.JUnitMatchers.*;
import net.codjo.tools.sqltester.batch.task.util.Constants.CREATED_OBJECTS;
import static net.codjo.tools.sqltester.batch.task.util.Constants.NEW_LINE;
import java.io.File;
import org.apache.tools.ant.BuildException;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

/**
 * Classe de test de {@link TaskUtil}.
 */
public class TaskUtilTest {

    @Test
    public void test_getContentOfFile_OK() throws Exception {
        String applicationFileName = getClass().getResource("../TaskUtil_OK.txt").getFile();

        String expected = "table/AP_LOG.txt" + NEW_LINE + "table\\AP_LOG2.txt" + NEW_LINE
                          + "permission/grant.sql";
        String actual = TaskUtil.getContentOfFile(new File(applicationFileName));
        assertThat(actual.trim(), equalTo(expected));
    }


    @Test
    public void test_getContentOfFile_noFile() throws Exception {
        String applicationFileName = "applicationBidon.txt";
        try {
            TaskUtil.getContentOfFile(new File(applicationFileName + "BIDON"));
            fail("Impossible de lire le fichier");
        }
        catch (BuildException e) {
            assertThat(e.getMessage(),
                       equalTo("Impossible de lire le fichier "
                               + new File(applicationFileName).getAbsolutePath()
                               + "BIDON"));
        }
    }


    @Test
    public void test_findUnexistingFilesIn_KO() throws Exception {
        String applicationFileName = getClass().getResource("../TaskUtil_KO.txt").getFile();
        String expected =
              "Les fichiers suivants sont introuvables :" + NEW_LINE
              + "table\\AP_TOTO.txt" + NEW_LINE
              + "view/VU_TEST.sql" + NEW_LINE
              + "indexe/AP_TITI.txt" + NEW_LINE
              + "procedure/sp_test.sql" + NEW_LINE;
        String actual = TaskUtil.findUnexistingFilesIn(applicationFileName);
        assertThat(actual, equalTo(expected));
    }


    @Test
    public void test_findUnexistingFilesIn_emptyFile() throws Exception {
        String applicationFileName = getClass().getResource("../taskUtil_Empty.txt").getFile();
        String actual = TaskUtil.findUnexistingFilesIn(applicationFileName);
        assertThat(actual, equalTo(""));
    }


    @Test
    public void test_removeCommentBlocks_oneLine() throws Exception {
        String toEvaluate = "aaa" + NEW_LINE + "bbb--ccc --bidon" + NEW_LINE + "ddd";
        String expected = "aaa" + NEW_LINE + "bbb" + NEW_LINE + "ddd";
        assertThat(TaskUtil.removeCommentBlocks(toEvaluate), equalTo(expected));
    }


    @Test
    public void test_removeCommentBlocks_oneBlock() throws Exception {
        String toEvaluate =
              "start" + NEW_LINE + "aaa /* bbb" + NEW_LINE + "bidon */ ccc" + NEW_LINE
              + "end";
        String expected = "start" + NEW_LINE + "aaa  ccc" + NEW_LINE + "end";
        assertEquals(expected, TaskUtil.removeCommentBlocks(toEvaluate));
    }


    @Test
    public void test_removeCommentBlocks_twoBlocks() throws Exception {
        String toEvaluate =
              "start" + NEW_LINE + "aaa /* bbb" + NEW_LINE + "*/middle/* bidon */ ccc"
              + NEW_LINE + "end";
        String expected = "start" + NEW_LINE + "aaa middle ccc" + NEW_LINE + "end";
        assertEquals(expected, TaskUtil.removeCommentBlocks(toEvaluate));
    }


    @Test
    public void test_removeCommentBlocks_twoTypes() throws Exception {
        String toEvaluate =
              "--start" + NEW_LINE + "aaa /* bbb" + NEW_LINE + "*/middle/* bidon */ ccc"
              + NEW_LINE + "end";
        String expected = NEW_LINE + "aaa middle ccc" + NEW_LINE + "end";
        assertEquals(expected, TaskUtil.removeCommentBlocks(toEvaluate));
    }


    @Test
    public void test_findUnexistingFilesIn_OK() throws Exception {
        String applicationFileName = getClass().getResource("../TaskUtil_OK.txt").getFile();
        String actual = TaskUtil.findUnexistingFilesIn(applicationFileName);
        assertEquals("", actual);
    }


    @Test
    public void test_doesPathContainDirectory() throws Exception {
        assertTrue(TaskUtil.doesPathContainDirectory("table/alter/ma_table.tab", "ALTER"));
        assertTrue(TaskUtil.doesPathContainDirectory("table/ALTER/ma_table.tab", "ALTER"));

        assertFalse(TaskUtil.doesPathContainDirectory("table/ALTERma_table.tab", "ALTER"));
        assertFalse(TaskUtil.doesPathContainDirectory("tableALTER/ma_table.tab", "ALTER"));
        assertFalse(TaskUtil.doesPathContainDirectory("tableALTERma_table.tab", "ALTER"));
    }


    @Test
    public void test_doesContainDropObject() throws Exception {
        assertTrue(TaskUtil.doesContainDropObject("procedure/drop/sp_test.sql", "sp_test"));
        assertFalse(TaskUtil.doesContainDropObject("procedure/drop/sp_test_toto.sql", "sp_test"));
        assertTrue(TaskUtil.doesContainDropObject("table/drop/AP_TEST.tab", "AP_TEST"));
        assertFalse(TaskUtil.doesContainDropObject("table/drop/AP_TEST_titi.tab", "AP_TEST"));

        assertFalse(TaskUtil.doesContainDropObject("procedure/sp_test.sql", "sp_test"));
        assertFalse(TaskUtil.doesContainDropObject("table/alter/AP_TEST.tab", "AP_TEST"));
    }


    @Test
    public void test_doesPathStartWithDirectory() throws Exception {
        String toEvaluate = "procedure/drop/sp_test.sql" + NEW_LINE + "table/alter/AP_TEST.tab";

        assertTrue(TaskUtil.doesPathStartWithDirectory(toEvaluate, "procedure"));
        assertTrue(TaskUtil.doesPathStartWithDirectory(toEvaluate, "table"));

        assertFalse(TaskUtil.doesPathStartWithDirectory(toEvaluate, "drop"));
        assertFalse(TaskUtil.doesPathStartWithDirectory(toEvaluate, "alter"));
    }


    @Test
    public void test_doesPathContainTable() throws Exception {
        assertTrue(TaskUtil.doesPathContainTable("table/AP_TEST.tab"));

        assertFalse(TaskUtil.doesPathContainTable("table/alter/AP_TEST.tab"));
        assertFalse(TaskUtil.doesPathContainTable("table/drop/AP_TEST.tab"));
        assertFalse(TaskUtil.doesPathContainTable("table/AP_TEST-gap.tab"));
        assertFalse(TaskUtil.doesPathContainTable("view/VU_TEST.sql"));
        assertFalse(TaskUtil.doesPathContainTable("table/Q_AP_SECURITY-sequence.sql"));
        assertFalse(TaskUtil.doesPathContainTable("table/TR_AP_ISSUER_PARENT_SEQ_I.sql"));
    }


    @Test
    public void test_doesPathContainView() throws Exception {
        assertTrue(TaskUtil.doesPathContainView("view/VU_TEST.sql"));

        assertFalse(TaskUtil.doesPathContainView("view/drop/AP_TEST.tab"));
        assertFalse(TaskUtil.doesPathContainView("table/AP_TEST.tab"));
    }


    @Test
    public void test_doesPathContainProc() throws Exception {
        assertTrue(TaskUtil.doesPathContainProc("procedure/sp_test.sql"));

        assertFalse(TaskUtil.doesPathContainProc("procedure/drop/sp_test.sql"));
        assertFalse(TaskUtil.doesPathContainProc("table/AP_TEST.tab"));
    }


    @Test
    public void test_getObjectName() throws Exception {
        assertEquals("sp_test", TaskUtil.getObjectName("procedure/sp_test.sql"));
        assertEquals("sp_test", TaskUtil.getObjectName("procedure/drop/sp_test.sql"));
    }


    @Test
    public void test_getObjectType() throws Exception {
        assertEquals(CREATED_OBJECTS.PROCEDURE, TaskUtil.getObjectType("procedure/sp_test.sql"));
        assertEquals(CREATED_OBJECTS.TABLE, TaskUtil.getObjectType("table/AP_test.sql"));
        assertEquals(CREATED_OBJECTS.VIEW, TaskUtil.getObjectType("view/VU_test.sql"));
        assertEquals(CREATED_OBJECTS.TRIGGER, TaskUtil.getObjectType("trigger/TR_test.sql"));
        assertEquals(CREATED_OBJECTS.OTHER, TaskUtil.getObjectType("permission/grant.sql"));
        assertEquals(CREATED_OBJECTS.OTHER, TaskUtil.getObjectType("table/drop/AP_test.sql"));
        assertEquals(CREATED_OBJECTS.OTHER, TaskUtil.getObjectType("view/drop/vu_test.sql"));
        assertEquals(CREATED_OBJECTS.OTHER, TaskUtil.getObjectType("trigger/drop/TR_test.sql"));
    }


    @Test
    public void test_doesScriptContainsCreateObjectName() throws Exception {
        assertTrue(TaskUtil.doesScriptContainsCreateObjectName(
              "start\n create table #test (a int null)\n create proc sp_test\nend",
              CREATED_OBJECTS.PROC,
              "sp_test"));
        assertFalse(TaskUtil.doesScriptContainsCreateObjectName("start\n create procedure sp_test\nend",
                                                                CREATED_OBJECTS.PROCEDURE,
                                                                "sp_test2"));
    }
}
