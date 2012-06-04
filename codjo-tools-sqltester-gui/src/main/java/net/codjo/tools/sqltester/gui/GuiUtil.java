package net.codjo.tools.sqltester.gui;
import com.jidesoft.swing.StyleRange;
import com.jidesoft.swing.StyledLabel;
import java.awt.Color;
import java.awt.Font;
import javax.swing.ImageIcon;
/**
 *
 */
public class GuiUtil {
    private GuiUtil() {
    }


    protected static ImageIcon getImageIcon(String name) {
        if (name != null) {
            return new ImageIcon(GuiUtil.class.getResource(name));
        }
        else {
            return null;
        }
    }


    protected static void customizeStyledLabel(StyledLabel label, String text) {
        if (text.startsWith(TaskExecutor.WARNING)) {
            label.addStyleRange(new StyleRange(0, TaskExecutor.WARNING.length(), Font.BOLD, Color.RED));
            label.addStyleRange(new StyleRange(TaskExecutor.WARNING.length(),
                                               -1,
                                               Font.ITALIC | Font.BOLD,
                                               Color.RED));
        }
        else if (text.startsWith(TaskExecutor.FAILURE)) {
            label.addStyleRange(new StyleRange(0, TaskExecutor.FAILURE.length(), Font.BOLD));
            label.addStyleRange(new StyleRange(TaskExecutor.FAILURE.length(),
                                               -1,
                                               Font.ITALIC | Font.BOLD,
                                               Color.BLACK));
        }
        else if (text.startsWith(TaskExecutor.INFO)) {
            label.addStyleRange(new StyleRange(0, TaskExecutor.INFO.length(), Font.PLAIN));
            label.addStyleRange(new StyleRange(TaskExecutor.INFO.length(), -1, Font.ITALIC, Color.BLACK));
        }
        else if (text.startsWith(TaskExecutor.TITLE)) {
            label.addStyleRange(new StyleRange(0, TaskExecutor.TITLE.length(), Font.BOLD, Color.BLUE));
            label.addStyleRange(new StyleRange(TaskExecutor.TITLE.length(),
                                               -1,
                                               Font.ITALIC | Font.BOLD,
                                               Color.BLUE, StyleRange.STYLE_UNDERLINED));
        }
        else if (text.startsWith(TaskExecutor.RESULT)) {
            label.addStyleRange(new StyleRange(0, TaskExecutor.FAILURE.length(), Font.BOLD));
            label.addStyleRange(new StyleRange(TaskExecutor.FAILURE.length(),
                                               -1,
                                               Font.ITALIC | Font.BOLD,
                                               Color.BLACK));
        }
    }


    protected static StyledLabel createStyledLabel(String text, StyleRange[] styleRanges) {
        StyledLabel label = new StyledLabel(text);
        label.addStyleRanges(styleRanges);
        return label;
    }


    protected static StyledLabel createStyledLabel(String text, StyleRange styleRange) {
        return createStyledLabel(text, new StyleRange[]{styleRange});
    }


    protected static StyleRange getBoldBlackStyle(int start, int length) {
        return new StyleRange(start, length, Font.BOLD, Color.BLACK);
    }


    protected static StyleRange getBoldRedStyle(int start, int length) {
        return new StyleRange(start, length, Font.BOLD, Color.RED);
    }


    protected static StyleRange getBoldBlueStyle(int start, int length) {
        return new StyleRange(start, length, Font.BOLD, Color.BLUE);
    }


    protected static StyleRange[] getBoldBlueRedStyle(int blueStart,
                                                      int blueLength,
                                                      int redStart,
                                                      int redLength) {
        return new StyleRange[]{GuiUtil.getBoldBlueStyle(blueStart, blueLength),
                                GuiUtil.getBoldRedStyle(redStart, redLength)};
    }


    protected static StyleRange getUnderlineBoldBlackStyle() {
        return new StyleRange(Font.BOLD, Color.BLACK, StyleRange.STYLE_UNDERLINED, Color.BLACK);
    }
}
