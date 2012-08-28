/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
package net.codjo.tools.sqltester.gui;
import com.jidesoft.plaf.LookAndFeelFactory;
/**
 *
 */
public class SqlTesterGui {

    private SqlTesterGui() {
    }


    public static void main(String[] args) {
        LookAndFeelFactory.installDefaultLookAndFeelAndExtension();
        new TesterLogic(args[0]);
    }
}