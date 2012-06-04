/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
package net.codjo.tools.sqltester.batch;
/**
 *
 */
public class SqlTester {

    private SqlTester() {
    }


    public static void main(String[] args) {
//        new CheckTasksExecutor("C:\\Dev\\projects\\capri\\capri-sql\\src\\main\\sql\\livraison-sql.txt");
        new CheckTasksExecutor(args[0]);
    }
}
