package net.codjo.tools.sqltester.gui;
import com.jidesoft.list.StyledListCellRenderer;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.JPopupMenu;
/**
 *
 */
public class TesterLogic {
    private JPopupMenu popupMenu = new JPopupMenu();
    private TesterGui gui;


    public TesterLogic(String deliverySqlFilePath) {
        gui = new TesterGui("SQL Tester", deliverySqlFilePath);
        gui.getResultList().addMouseListener(new ClearLogListener());
        gui.getResultList().setCellRenderer(new ResultStyledListCellRenderer());
        popupMenu.add(new ClearLogAction());
        gui.showOptionsDialog();
    }


    private class ClearLogAction extends AbstractAction {
        ClearLogAction() {
            super("Effacer les résultats");
        }


        public void actionPerformed(ActionEvent arg0) {
            gui.updateResultList(new DefaultListModel());
        }
    }

    private class ClearLogListener extends MouseAdapter {

        @Override
        public void mousePressed(MouseEvent event) {
            showClearLogPopup(event);
        }


        @Override
        public void mouseReleased(MouseEvent event) {
            showClearLogPopup(event);
        }


        private void showClearLogPopup(MouseEvent event) {
            if (event.isPopupTrigger()) {
                popupMenu.show(event.getComponent(), event.getX(), event.getY());
            }
        }
    }

    private class ResultStyledListCellRenderer extends StyledListCellRenderer {
        @Override
        protected void customizeStyledLabel(JList list,
                                            Object value,
                                            int index,
                                            boolean isSelected,
                                            boolean cellHasFocus) {
            super.customizeStyledLabel(list, value, index, isSelected, cellHasFocus);
            String text = getText();
            setBorder(BorderFactory.createEmptyBorder(0, 2, 0, 2));
            setIcon(null);
            GuiUtil.customizeStyledLabel(this, text);
        }
    }
}
