import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import java.awt.*;

/**
 * UITheme is a small helper class that keeps the look of the application in ONE place:
 * colours, fonts, and methods that style buttons, text fields, labels and tables.
 * Change a value here and the whole GUI changes with it.
 *
 * It only has static members, so we never create objects from it.
 */
public class UITheme {

    // ---------- Colour palette ----------
    public static final Color PRIMARY    = new Color(30, 58, 95);     // deep navy (banner, table header)
    public static final Color ACCENT     = new Color(37, 99, 235);    // blue (main buttons)
    public static final Color SECONDARY  = new Color(100, 116, 139);  // slate grey (secondary buttons)
    public static final Color BACKGROUND = new Color(243, 246, 250);  // soft grey-blue page background
    public static final Color CARD       = Color.WHITE;               // white panels
    public static final Color TEXT       = new Color(33, 37, 41);     // near-black text
    public static final Color BORDER     = new Color(218, 223, 230);  // light grey lines
    public static final Color STRIPE     = new Color(246, 248, 252);  // alternate table row
    public static final Color SUCCESS    = new Color(22, 128, 61);    // green ("Available")
    public static final Color DANGER     = new Color(185, 28, 28);    // red ("Borrowed")

    // ---------- Fonts (Segoe UI on Windows; Java picks a similar font elsewhere) ----------
    public static final Font BODY  = new Font("Segoe UI", Font.PLAIN, 14);
    public static final Font BOLD  = new Font("Segoe UI", Font.BOLD, 14);
    public static final Font SMALL = new Font("Segoe UI", Font.PLAIN, 13);
    public static final Font TITLE = new Font("Segoe UI", Font.BOLD, 24);
    public static final Font MONO  = new Font("Consolas", Font.PLAIN, 13);

    // Private constructor: this class is only a toolbox, nobody should create one
    private UITheme() {
    }

    // Makes pop-up dialogs (JOptionPane) use our fonts too. Call once at start-up.
    public static void setup() {
        UIManager.put("OptionPane.messageFont", BODY);
        UIManager.put("OptionPane.buttonFont", BOLD);
        UIManager.put("OptionPane.messageForeground", TEXT);
        UIManager.put("ComboBox.font", BODY);
    }

    // ---------- Buttons ----------
    public static JButton primaryButton(String text) {
        return button(text, ACCENT);
    }

    public static JButton secondaryButton(String text) {
        return button(text, SECONDARY);
    }

    // A flat, rounded button that gets darker on hover and when pressed
    private static JButton button(String text, Color base) {
        JButton b = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                Color fill = base;
                if (getModel().isPressed()) {
                    fill = base.darker().darker();
                } else if (getModel().isRollover()) {
                    fill = base.darker();
                }
                g2.setColor(fill);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2.dispose();

                super.paintComponent(g); // draws the text on top
            }
        };
        b.setFont(BOLD);
        b.setForeground(Color.WHITE);
        b.setContentAreaFilled(false);
        b.setBorderPainted(false);
        b.setFocusPainted(false);
        b.setOpaque(false);
        b.setRolloverEnabled(true);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.setBorder(BorderFactory.createEmptyBorder(9, 20, 9, 20));
        return b;
    }

    // ---------- Text fields and labels ----------
    public static void styleField(JTextField field) {
        field.setFont(BODY);
        field.setForeground(TEXT);
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER),
                BorderFactory.createEmptyBorder(6, 8, 6, 8)));
    }

    public static JLabel label(String text) {
        JLabel l = new JLabel(text);
        l.setFont(BOLD);
        l.setForeground(TEXT);
        return l;
    }

    // ---------- Card-style border with a coloured title ----------
    public static Border cardBorder(String title) {
        TitledBorder titled = BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(BORDER), title,
                TitledBorder.LEFT, TitledBorder.TOP, BOLD, PRIMARY);
        return BorderFactory.createCompoundBorder(titled, BorderFactory.createEmptyBorder(8, 12, 12, 12));
    }

    // ---------- Tables ----------
    public static void styleTable(JTable table) {
        table.setFont(BODY);
        table.setRowHeight(30);
        table.setShowVerticalLines(false);
        table.setGridColor(BORDER);
        table.setIntercellSpacing(new Dimension(0, 1));
        table.setSelectionBackground(new Color(219, 234, 254));
        table.setSelectionForeground(TEXT);
        table.setFillsViewportHeight(true);

        // Dark header with white bold text
        DefaultTableCellRenderer headerRenderer = new DefaultTableCellRenderer();
        headerRenderer.setOpaque(true);
        headerRenderer.setBackground(PRIMARY);
        headerRenderer.setForeground(Color.WHITE);
        headerRenderer.setFont(BOLD);
        headerRenderer.setHorizontalAlignment(SwingConstants.LEFT);
        headerRenderer.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));

        JTableHeader header = table.getTableHeader();
        header.setDefaultRenderer(headerRenderer);
        header.setReorderingAllowed(false);
        header.setPreferredSize(new Dimension(header.getPreferredSize().width, 34));

        // Body cells: striped rows, padding, and green/red colouring for the status words
        DefaultTableCellRenderer bodyRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                super.getTableCellRendererComponent(t, value, isSelected, false, row, column);
                setHorizontalAlignment(SwingConstants.LEFT);
                setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
                setFont(BODY);

                if (!isSelected) {
                    if (row % 2 == 0) {
                        setBackground(CARD);
                    } else {
                        setBackground(STRIPE);
                    }
                    setForeground(TEXT);

                    if ("Available".equals(value)) {
                        setForeground(SUCCESS);
                        setFont(BOLD);
                    } else if ("Borrowed".equals(value)) {
                        setForeground(DANGER);
                        setFont(BOLD);
                    }
                }
                return this;
            }
        };
        table.setDefaultRenderer(Object.class, bodyRenderer);
        table.setDefaultRenderer(Number.class, bodyRenderer);
    }
}