package net.codjo.tools.sqltester.gui;
/*
 * @(#)IntelliJOptionsDialog.java
 *
 * Copyright 2002 - 2003 JIDE Software Inc. All rights reserved.
 */

import net.codjo.gui.toolkit.waiting.WaitingPanel;
import net.codjo.tools.sqltester.batch.task.util.Constants.BaseType;
import net.codjo.tools.sqltester.batch.task.util.Constants.CheckType;
import com.jidesoft.dialog.AbstractDialogPage;
import com.jidesoft.dialog.ButtonPanel;
import com.jidesoft.dialog.MultiplePageDialog;
import com.jidesoft.dialog.PageList;
import com.jidesoft.swing.JideSwingUtilities;
import com.jidesoft.swing.PartialLineBorder;
import com.jidesoft.swing.Searchable;
import com.jidesoft.swing.SearchableBar;
import com.jidesoft.swing.SearchableUtils;
import com.jidesoft.swing.StyleRange;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.KeyStroke;
import javax.swing.border.TitledBorder;

/**
 * Demoed Component: {@link MultiplePageDialog} <br> Required jar files: jide-common.jar, jide-dialogs.jar
 * <br> Required L&F: Jide L&F extension required
 */
public class TesterGui extends MultiplePageDialog {
    private PageList pageList;
    private String deliverySqlFilePath;
    private BaseType baseType = BaseType.SYBASE;
    private List<CheckType> checkList = new ArrayList<CheckType>();
    private Map<String, CheckType> sybaseControls;
    private Map<String, CheckType> mysqlControls;
    private TaskExecutor taskExecutor;
    private JPanel resultPanel;
    private JPanel requirementPanel;
    private WaitingPanel waitingPanel = new WaitingPanel();
    private JList resultList;


    public TesterGui(Frame owner, String title, String deliverySqlFilePath) throws HeadlessException {
        super(owner, title);
        sybaseControls = buildSybaseControls();
        mysqlControls = buildMysqlControls();
        this.deliverySqlFilePath = deliverySqlFilePath;
        taskExecutor = new TaskExecutor(deliverySqlFilePath);
        initGui();
    }


    public TesterGui(String title, String deliverySqlFilePath) throws HeadlessException {
        this(null, title, deliverySqlFilePath);
    }


    @Override
    public void initComponents() {
        super.initComponents();
        setGlassPane(waitingPanel);
        getContentPanel().setBorder(
              BorderFactory.createCompoundBorder(BorderFactory.createEmptyBorder(10, 10, 0, 10),
                                                 BorderFactory.createRaisedBevelBorder()));

        getButtonPanel().setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
    }


    @Override
    public ButtonPanel createButtonPanel() {
        JButton helpButton = new JButton(new HelpAction());
        helpButton.setName("Requirement");

        JButton resultButton = new JButton(new ResultAction());
        resultButton.setName("Result");
        resultButton.setEnabled(false);

        JButton launchButton = new JButton(new LaunchTasksAction(resultButton));
        launchButton.setName("Launch");

        JButton cancelButton = new JButton(new AbstractAction("Fermer", GuiUtil.getImageIcon("exit.png")) {
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });

        ButtonPanel buttonPanel = new ButtonPanel();
        buttonPanel.add(helpButton);
        buttonPanel.add(resultButton);
        buttonPanel.add(launchButton);
        buttonPanel.add(cancelButton);
        return buttonPanel;
    }


    @Override
    public Dimension getPreferredSize() {
        return new Dimension(800, 600);
    }


    void showOptionsDialog() {
        setStyle(MultiplePageDialog.ICON_STYLE);
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                super.windowClosing(e);
                dispose();
            }
        });
        createPageList();
        pack();
        JideSwingUtilities.globalCenterWindow(this);
        setVisible(true);
    }


    JList getResultList() {
        return resultList;
    }


    void updateResultList(DefaultListModel listModel) {
        resultList.setModel(listModel);
        resultPanel.removeAll();
        resultPanel.add(new JScrollPane(resultList));
        resultPanel.revalidate();
    }


    private void initGui() {
        requirementPanel = createRequirementPanel();
        resultList = createResultList();
        resultPanel = createResultPanel();
        createSearchableBar();
    }


    private void createSearchableBar() {
        Searchable searchable = SearchableUtils.installSearchable(resultList);
        searchable.setRepeats(true);
        SearchableBar.install(
              searchable,
              KeyStroke.getKeyStroke(KeyEvent.VK_F, KeyEvent.CTRL_DOWN_MASK),
              new SearchableBar.Installer() {
                  public void openSearchBar(SearchableBar searchableBar) {
                      resultPanel.add(searchableBar, BorderLayout.AFTER_LAST_LINE);
                      resultPanel.invalidate();
                      resultPanel.revalidate();
                  }


                  public void closeSearchBar(SearchableBar searchableBar) {
                      resultPanel.remove(searchableBar);
                      resultPanel.invalidate();
                      resultPanel.revalidate();
                  }
              });
    }


    private JPanel createResultPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(new JScrollPane(resultList));
        panel.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createTitledBorder(
              new PartialLineBorder(Color.gray, 1, true), "",
              TitledBorder.CENTER, TitledBorder.CENTER), BorderFactory.createEmptyBorder(6, 4, 4, 4)));
        return panel;
    }


    private JList createResultList() {
        return new JList(new DefaultListModel()) {
            @Override
            public Dimension getPreferredScrollableViewportSize() {
                return new Dimension(300, 100);
            }
        };
    }


    private void createPageList() {
        pageList = new PageList();

        CheckOptions sybaseCheckOptions = new CheckOptions("Sybase", GuiUtil.getImageIcon("logo_sybase.gif"),
                                                           getMandatoryControls(),
                                                           getSybaseControls());
        AbstractDialogPage mysqlCheckOptions = new CheckOptions("MySQL",
                                                                GuiUtil.getImageIcon("logo_mysql.gif"),
                                                                getMandatoryControls(),
                                                                getMysqlControls());

        pageList.append(sybaseCheckOptions);
        pageList.append(mysqlCheckOptions);

        setPageList(pageList);
    }


    private JPanel createRequirementPanel() {
        JPanel stylePanel = new JPanel();
        stylePanel.setLayout(new BoxLayout(stylePanel, BoxLayout.Y_AXIS));

        stylePanel.add(GuiUtil.createStyledLabel(
              "-> Pour les applications ayant un module \"datagen\", une génération doit être lancée préalablement.",
              GuiUtil.getUnderlineBoldBlackStyle()));
        stylePanel.add(new JLabel(" "));
        stylePanel.add(GuiUtil.createStyledLabel(
              "-> Le fichier 'livraison-sql.txt' doit être de la forme suivante :",
              GuiUtil.getUnderlineBoldBlackStyle()));
        stylePanel.add(GuiUtil.createStyledLabel(
              "constraint\\MY_CONSTRAINT.sql", GuiUtil.getBoldBlueStyle(0, 10)));
        stylePanel.add(GuiUtil.createStyledLabel(
              "constraint\\drop\\MY_OLD_CONSTRAINT.sql", GuiUtil.getBoldBlueRedStyle(0, 10, 11, 4)));
        stylePanel.add(GuiUtil.createStyledLabel("table\\MY_TABLE.tab", GuiUtil.getBoldBlueStyle(0, 5)));
        stylePanel.add(GuiUtil.createStyledLabel(
              "table\\alter\\MY_OTHER_TABLE.tab", GuiUtil.getBoldBlueRedStyle(0, 5, 6, 5)));
        stylePanel.add(GuiUtil.createStyledLabel(
              "table\\drop\\MY_OLD_TABLE.tab", GuiUtil.getBoldBlueRedStyle(0, 5, 6, 4)));
        stylePanel.add(GuiUtil.createStyledLabel("index\\MY_INDEX.sql", GuiUtil.getBoldBlueStyle(0, 5)));
        stylePanel.add(GuiUtil.createStyledLabel(
              "index\\drop\\MY_OLD_INDEX.sql", GuiUtil.getBoldBlueRedStyle(0, 5, 6, 5)));
        stylePanel.add(GuiUtil.createStyledLabel(
              "procedure\\myDir\\MY_PROC.sql", new StyleRange[]{GuiUtil.getBoldBlueStyle(0, 9),
                                                                GuiUtil.getBoldBlackStyle(10, 6)}));
        stylePanel.add(GuiUtil.createStyledLabel(
              "procedure\\drop\\MY_OLD_PROC.sql", GuiUtil.getBoldBlueRedStyle(0, 9, 10, 5)));
        stylePanel.add(GuiUtil.createStyledLabel("trigger\\MY_TRIGGER.txt", GuiUtil.getBoldBlueStyle(0, 7)));
        stylePanel.add(GuiUtil.createStyledLabel(
              "trigger\\drop\\MY_OLD_TRIGGER.txt", GuiUtil.getBoldBlueRedStyle(0, 7, 8, 5)));
        stylePanel.add(GuiUtil.createStyledLabel("Scripts\\MY_SCRIPT.sql", GuiUtil.getBoldBlueStyle(0, 7)));
        stylePanel.add(GuiUtil.createStyledLabel("permission\\revoke.sql", GuiUtil.getBoldBlueStyle(0, 10)));
        stylePanel.add(GuiUtil.createStyledLabel("permission\\grant.sql", GuiUtil.getBoldBlueStyle(0, 10)));

        JPanel panel = new JPanel(new BorderLayout());
        panel.add(new JScrollPane(stylePanel));
        panel.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createTitledBorder(
              new PartialLineBorder(Color.gray, 1, true), "",
              TitledBorder.CENTER, TitledBorder.CENTER), BorderFactory.createEmptyBorder(6, 4, 4, 4)));
        panel.setPreferredSize(new Dimension(700, 500));
        return panel;
    }


    private String[] getMandatoryControls() {
        return new String[]{
              "Existence des fichiers",
              "Inexistence des \"grants all\" et des \"grants to public\"",
              "Execution des scripts (2 fois)"
        };
    }


    private String[] getSybaseControls() {
        return sybaseControls.keySet().toArray(new String[sybaseControls.size()]);
    }


    private String[] getMysqlControls() {
        return mysqlControls.keySet().toArray(new String[mysqlControls.size()]);
    }


    private Map<String, CheckType> buildSybaseControls() {
        Map<String, CheckType> controls = new LinkedHashMap<String, CheckType>();
        addCommonControls(controls);
        controls.put("Gaps maquants", CheckType.MISSING_GAP);
        controls.put("Dependances pour les alters de tables", CheckType.DEPENDENCIES);
        return controls;
    }


    private Map<String, CheckType> buildMysqlControls() {
        Map<String, CheckType> controls = new LinkedHashMap<String, CheckType>();
        addCommonControls(controls);
        return controls;
    }


    private void addCommonControls(Map<String, CheckType> controls) {
        controls.put("Index manquants", CheckType.MISSING_INDEX);
        controls.put("Grants manquants", CheckType.MISSING_GRANT);
        controls.put("Grants en trop", CheckType.UNUSED_GRANT);
        controls.put("Grants et revokes manquants pour les tables", CheckType.MISSING_TABLES_GRANTS);
        controls.put("Grants et revokes manquants pour les vues", CheckType.MISSING_VIEWS_GRANTS);
        controls.put("Grants et revokes manquants pour les procédures stockées",
                     CheckType.MISSING_PROCS_GRANTS);
        controls.put("Cohérence entre les noms des scripts et des objets", CheckType.SCRIPT_OBJECT_NAME);
    }


    private List<CheckType> getSelectedChecks() {
        checkList.clear();
        checkList.add(CheckType.SCRIPTS_EXISTENCE);
        checkList.add(CheckType.GRANTS);
        checkList.add(CheckType.EXEC_SQL_FILES);
        checkList.add(CheckType.EXEC_SQL_FILES);

        CheckOptions dialogPage = (CheckOptions)pageList.getCurrentPage();
        Object[] selectedControls = dialogPage.getSelectedOptionalControls().getSelectedObjects();
        for (Object selectedControl : selectedControls) {
            String control = (String)selectedControl;
            checkList.add(sybaseControls.get(control));
        }
        return checkList;
    }


    private class LaunchTasksAction extends AbstractAction {
        private final JButton resultButton;


        private LaunchTasksAction(JButton resultButton) {
            super("Lancer", GuiUtil.getImageIcon("run.png"));
            this.resultButton = resultButton;
        }


        public void actionPerformed(ActionEvent e) {
            waitingPanel.exec(new Runnable() {
                public void run() {
                    taskExecutor = new TaskExecutor(deliverySqlFilePath);
                    if ("MySQL".equals(pageList.getCurrentPage().getName())) {
                        baseType = BaseType.MYSQL;
                    }
                    else {
                        baseType = BaseType.SYBASE;
                    }
                    taskExecutor.executeTasks(baseType, getSelectedChecks());
                    updateResultList(taskExecutor.getListModel());
                    resultButton.setEnabled(true);
                }
            });
        }
    }
    private class HelpAction extends AbstractAction {
        private HelpAction() {
            super("Pré-requis", GuiUtil.getImageIcon("help.png"));
        }


        public void actionPerformed(ActionEvent e) {
            JDialog result = new JDialog(TesterGui.this, "Pré-requis pour les contrôles");
            result.getContentPane().setLayout(new BorderLayout());
            result.getContentPane().add(requirementPanel, BorderLayout.CENTER);
            result.pack();
            JideSwingUtilities.globalCenterWindow(result);
            result.setVisible(true);
        }
    }
    private class ResultAction extends AbstractAction {
        private ResultAction() {
            super("Résultat", GuiUtil.getImageIcon("info.png"));
        }


        public void actionPerformed(ActionEvent e) {
            JDialog result = new JDialog(TesterGui.this, "Résultat des contrôles");
            result.getContentPane().setLayout(new BorderLayout());
            resultPanel.setPreferredSize(new Dimension(800, 600));
            result.getContentPane().add(resultPanel, BorderLayout.CENTER);
            result.pack();
            JideSwingUtilities.globalCenterWindow(result);
            result.setVisible(true);
        }
    }
}
