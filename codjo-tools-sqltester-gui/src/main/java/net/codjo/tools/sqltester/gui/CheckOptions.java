package net.codjo.tools.sqltester.gui;
import com.jidesoft.dialog.AbstractDialogPage;
import com.jidesoft.dialog.BannerPanel;
import com.jidesoft.plaf.UIDefaultsLookup;
import com.jidesoft.swing.CheckBoxListWithSelectable;
import com.jidesoft.swing.JideButton;
import com.jidesoft.swing.JideTabbedPane;
import com.jidesoft.swing.PartialGradientLineBorder;
import com.jidesoft.swing.PartialSide;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.BevelBorder;
import javax.swing.border.CompoundBorder;
import javax.swing.border.TitledBorder;
/**
 *
 */
public class CheckOptions extends AbstractDialogPage {
    private String[] mandatoryControls;
    private String[] controls;
    private CheckBoxListWithSelectable optionalControlsSelectable;
    private CheckBoxListWithSelectable mandatoryControlsSelectable;


    public CheckOptions(String name, Icon icon, String[] mandatoryControls, String[] controls) {
        super(name, icon);
        this.mandatoryControls = mandatoryControls;
        this.controls = controls;
    }


    public CheckBoxListWithSelectable getSelectedOptionalControls() {
        return optionalControlsSelectable;
    }


    public CheckBoxListWithSelectable getSelectedMandatoryControls() {
        return mandatoryControlsSelectable;
    }


    public void lazyInitialize() {
        initComponents();
    }


    public void initComponents() {
        setLayout(new BorderLayout());
        JideTabbedPane tabbedPane = createTabbedPane();
        add(tabbedPane, BorderLayout.CENTER);
    }


    private JideTabbedPane createTabbedPane() {
        final JideTabbedPane tabbedPane = new JideTabbedPane(JideTabbedPane.TOP);
        tabbedPane.setTabShape(JideTabbedPane.SHAPE_DEFAULT);
        tabbedPane.setOpaque(true);

        tabbedPane.addTab("Contôles obligatoires",
                          GuiUtil.getImageIcon("config.png"),
                          createMandatoryControlsPanel());
        tabbedPane.setMnemonicAt(0, KeyEvent.VK_O);
        tabbedPane.addTab("Contôles pré-définis",
                          GuiUtil.getImageIcon("config.png"),
                          createOptionalControlsPanel());
        tabbedPane.setMnemonicAt(1, KeyEvent.VK_P);
        // TODO : contrôles utilisateurs
        /*       tabbedPane.addTab("Contôles utilisateurs",
                         GuiUtil.getImageIcon("config.png"),
                         createUserControlsPanel());
       tabbedPane.setMnemonicAt(2, KeyEvent.VK_U);*/
        tabbedPane.setBoldActiveTab(true);
        tabbedPane.setSelectedIndex(1);
        return tabbedPane;
    }


    private JPanel createMandatoryControlsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(createBanner(
              "La liste des contôles ci-dessous sont obligatoires.\nIls seront donc exécutés."),
                  BorderLayout.NORTH);
        mandatoryControlsSelectable = new CheckBoxListWithSelectable(mandatoryControls);
        mandatoryControlsSelectable.selectAll();
        mandatoryControlsSelectable.setEnabled(false);
        mandatoryControlsSelectable.setBorder(createBorder("Liste des contrôles"));
        panel.add(mandatoryControlsSelectable);

        return panel;
    }


    private JPanel createOptionalControlsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(createBanner("La liste des contôles ci-dessous sont optionels.\n"
                               + "Vous pouvez donc choisir ceux que vous souhaitez exécuter."),
                  BorderLayout.NORTH);

        optionalControlsSelectable = new CheckBoxListWithSelectable(controls);
        optionalControlsSelectable.setBorder(createBorder("Liste des contrôles"));
        panel.add(optionalControlsSelectable, BorderLayout.CENTER);
        panel.add(getOptionsPanel(optionalControlsSelectable), BorderLayout.SOUTH);
        return panel;
    }

/*  TODO : contrôles utilisateurs
    private JPanel createUserControlsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(createBanner("La liste des contôles ci-dessous sont définis par les utilisateurs.\n"
                               + "Vous pouvez donc choisir ceux que vous souhaitez exécuter."),
                  BorderLayout.NORTH);
        String[] userControls = {"Control 1", "Control 2"};
        CheckBoxListWithSelectable userControlsSelectable = new CheckBoxListWithSelectable(userControls);
        userControlsSelectable.setBorder(createBorder("Liste des contrôles"));
        panel.add(userControlsSelectable);
        panel.add(getOptionsPanel(userControlsSelectable), BorderLayout.SOUTH);

        return panel;
    }
*/


    public Component getOptionsPanel(final CheckBoxListWithSelectable controlsSelectable) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));

        JideButton selectAllButton = new JideButton(new AbstractAction("Select All") {
            public void actionPerformed(ActionEvent e) {
                controlsSelectable.selectAll();
            }
        });
        selectAllButton.setButtonStyle(JideButton.TOOLBAR_STYLE);

        JideButton selectNoneButton = new JideButton(new AbstractAction("Select None") {
            public void actionPerformed(ActionEvent e) {
                controlsSelectable.selectNone();
            }
        });
        selectNoneButton.setButtonStyle(JideButton.TOOLBAR_STYLE);

        final JCheckBox checkBoxEnabled = new JCheckBox("Enabled Select");
        checkBoxEnabled.addActionListener(new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                controlsSelectable.setCheckBoxEnabled(checkBoxEnabled.isSelected());
            }
        });
        checkBoxEnabled.setSelected(controlsSelectable.isCheckBoxEnabled());
        checkBoxEnabled.setBackground(Color.WHITE);

        panel.add(selectAllButton);
        panel.add(selectNoneButton);
        panel.add(checkBoxEnabled);
        panel.setBackground(Color.WHITE);
        panel.setBorder(createBorder(""));
        return panel;
    }


    private CompoundBorder createBorder(String title) {
        TitledBorder border = BorderFactory.createTitledBorder(new PartialGradientLineBorder(new Color[]{
              new Color(0, 0, 128), UIDefaultsLookup.getColor("control")}, 2, PartialSide.NORTH),
                                                               title,
                                                               TitledBorder.CENTER,
                                                               TitledBorder.ABOVE_TOP);
        return BorderFactory.createCompoundBorder(border, BorderFactory.createEmptyBorder(6, 4, 4, 4));
    }


    private BannerPanel createBanner(String bannerText) {
        BannerPanel bannerPanel =
              new BannerPanel("Configuration", bannerText, GuiUtil.getImageIcon("settings.png"));
        bannerPanel.setFont(new Font("Tahoma", Font.PLAIN, 11));
        bannerPanel.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
        bannerPanel.setTitleIconLocation(SwingConstants.LEADING);
        bannerPanel.setGradientPaint(Color.WHITE, Color.LIGHT_GRAY, false);
        bannerPanel.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED,
                                                              Color.white,
                                                              Color.lightGray,
                                                              Color.lightGray,
                                                              Color.gray));
        return bannerPanel;
    }
}
