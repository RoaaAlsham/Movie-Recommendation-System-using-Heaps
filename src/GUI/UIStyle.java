package GUI;

import java.awt.*;
import javax.swing.*;

public class UIStyle {

    public static final Color BACKGROUND =
            new Color(30, 30, 46);

    public static final Color PANEL =
            new Color(49, 50, 68);

    public static final Color BUTTON =
            new Color(203, 166, 247);

    public static final Color TEXT =
            Color.WHITE;

    public static final Font TITLE_FONT =
            new Font("Segoe UI", Font.BOLD, 22);

    public static final Font NORMAL_FONT =
            new Font("Segoe UI", Font.PLAIN, 18);

    public static final Font BUTTON_FONT =
            new Font("Segoe UI", Font.BOLD, 18);

    public static void styleButton(JButton button) {

        button.setBackground(BUTTON);
        button.setForeground(Color.BLACK);

        button.setFont(BUTTON_FONT);

        button.setFocusPainted(false);
    }

    public static void styleTextField(JTextField field) {

        field.setBackground(PANEL);
        field.setForeground(TEXT);

        field.setCaretColor(TEXT);

        field.setFont(NORMAL_FONT);
    }

    public static void styleTextArea(JTextArea area) {

        area.setBackground(PANEL);
        area.setForeground(TEXT);

        area.setFont(NORMAL_FONT);

        area.setLineWrap(false);
    }

    public static void styleComboBox(JComboBox<?> combo) {

        combo.setBackground(PANEL);
        combo.setForeground(TEXT);

        combo.setFont(NORMAL_FONT);
    }

    public static void stylePanel(JPanel panel) {

        panel.setBackground(BACKGROUND);
    }

    public static void styleLabel(JLabel label) {

        label.setForeground(TEXT);
        label.setFont(NORMAL_FONT);
    }
}