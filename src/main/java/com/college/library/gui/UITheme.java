package com.college.library.gui;

import javax.swing.*;
import javax.swing.border.AbstractBorder;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;

public class UITheme {

    // --- Color Palette ---
    public static final Color BG_APP = new Color(248, 250, 252);          // Slate 50 (#F8FAFC)
    public static final Color BG_CARD = new Color(255, 255, 255);         // White (#FFFFFF)
    public static final Color BG_HEADER = new Color(255, 255, 255);       // White (#FFFFFF)
    public static final Color BG_TABLE_HEADER = new Color(241, 245, 249); // Slate 100 (#F1F5F9)
    public static final Color BG_TABLE_ALT = new Color(250, 252, 255);    // Very faint blue-grey tint
    public static final Color BG_INPUT = new Color(255, 255, 255);

    // Brand / Primary Colors (Subtle Blue Accents)
    public static final Color PRIMARY = new Color(37, 99, 235);            // Blue 600 (#2563EB)
    public static final Color PRIMARY_HOVER = new Color(29, 78, 216);      // Blue 700 (#1D4ED8)
    public static final Color PRIMARY_PRESSED = new Color(30, 64, 175);    // Blue 800 (#1E40AF)
    public static final Color PRIMARY_LIGHT = new Color(239, 246, 255);    // Blue 50 (#EFF6FF)
    public static final Color PRIMARY_BORDER = new Color(191, 219, 254);   // Blue 200 (#BFDBFE)

    // Secondary / Neutral Colors
    public static final Color SECONDARY_BG = new Color(241, 245, 249);     // Slate 100 (#F1F5F9)
    public static final Color SECONDARY_HOVER = new Color(226, 232, 240);  // Slate 200 (#E2E8F0)
    public static final Color SECONDARY_TEXT = new Color(30, 41, 59);      // Slate 800 (#1E293B)
    public static final Color SECONDARY_BORDER = new Color(203, 213, 225); // Slate 300 (#CBD5E1)

    // Semantic / Status Colors
    public static final Color SUCCESS = new Color(16, 185, 129);           // Emerald 500 (#10B981)
    public static final Color SUCCESS_HOVER = new Color(5, 150, 105);      // Emerald 600 (#059669)
    public static final Color SUCCESS_BG = new Color(236, 253, 245);       // Emerald 50 (#ECFDF5)
    public static final Color SUCCESS_TEXT = new Color(6, 95, 70);         // Emerald 800 (#065F46)

    public static final Color DANGER = new Color(239, 68, 68);             // Red 500 (#EF4444)
    public static final Color DANGER_HOVER = new Color(220, 38, 38);       // Red 600 (#DC2626)
    public static final Color DANGER_BG = new Color(254, 242, 242);        // Red 50 (#FEF2F2)
    public static final Color DANGER_TEXT = new Color(153, 27, 27);        // Red 800 (#991B1B)

    public static final Color WARNING = new Color(245, 158, 11);           // Amber 500 (#F59E0B)
    public static final Color WARNING_HOVER = new Color(217, 119, 6);      // Amber 600 (#D97706)
    public static final Color WARNING_BG = new Color(254, 243, 199);       // Amber 50 (#FEF3C7)
    public static final Color WARNING_TEXT = new Color(146, 64, 14);       // Amber 800 (#92400E)

    public static final Color INFO = new Color(14, 165, 233);              // Sky 500 (#0EA5E9)
    public static final Color INFO_BG = new Color(240, 249, 255);          // Sky 50 (#F0F9FF)
    public static final Color INFO_TEXT = new Color(7, 89, 133);           // Sky 800 (#075985)

    // Dark Navy Typography Colors
    public static final Color TEXT_PRIMARY = new Color(15, 23, 42);        // Slate 900 (#0F172A)
    public static final Color TEXT_SECONDARY = new Color(71, 85, 105);     // Slate 600 (#475569)
    public static final Color TEXT_MUTED = new Color(148, 163, 184);       // Slate 400 (#94A3B8)
    public static final Color TEXT_WHITE = Color.WHITE;

    // Borders & Dividers
    public static final Color BORDER = new Color(226, 232, 240);           // Slate 200 (#E2E8F0)
    public static final Color BORDER_LIGHT = new Color(241, 245, 249);     // Slate 100 (#F1F5F9)
    public static final Color BORDER_FOCUS = new Color(59, 130, 246);      // Blue 500 (#3B82F6)
    public static final Color TABLE_GRID = new Color(241, 245, 249);
    public static final Color TABLE_SELECTION = new Color(224, 231, 255);  // Indigo 100 (#E0E7FF)

    // --- Typography ---
    private static final String FONT_NAME = getPreferredFontFamily();
    public static final Font FONT_HERO = new Font(FONT_NAME, Font.BOLD, 22);
    public static final Font FONT_TITLE = new Font(FONT_NAME, Font.BOLD, 17);
    public static final Font FONT_SUBTITLE = new Font(FONT_NAME, Font.PLAIN, 12);
    public static final Font FONT_SECTION = new Font(FONT_NAME, Font.BOLD, 14);
    public static final Font FONT_BODY = new Font(FONT_NAME, Font.PLAIN, 13);
    public static final Font FONT_BODY_BOLD = new Font(FONT_NAME, Font.BOLD, 13);
    public static final Font FONT_BUTTON = new Font(FONT_NAME, Font.BOLD, 12);
    public static final Font FONT_SMALL = new Font(FONT_NAME, Font.PLAIN, 12);
    public static final Font FONT_BADGE = new Font(FONT_NAME, Font.BOLD, 11);
    public static final Font FONT_STAT_NUM = new Font(FONT_NAME, Font.BOLD, 24);
    public static final Font FONT_STAT_LBL = new Font(FONT_NAME, Font.BOLD, 11);

    private static String getPreferredFontFamily() {
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        String[] fontNames = ge.getAvailableFontFamilyNames();
        for (String name : fontNames) {
            if ("Segoe UI".equalsIgnoreCase(name)) return "Segoe UI";
        }
        for (String name : fontNames) {
            if ("Inter".equalsIgnoreCase(name) || "Helvetica Neue".equalsIgnoreCase(name) || "Arial".equalsIgnoreCase(name)) {
                return name;
            }
        }
        return Font.SANS_SERIF;
    }

    /**
     * Initializes global Swing look and feel and uniform UI defaults.
     */
    public static void setupGlobalUI() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {
        }

        // Apply clean modern defaults to standard Swing components
        UIManager.put("Panel.background", BG_APP);
        UIManager.put("Label.font", FONT_BODY);
        UIManager.put("Label.foreground", TEXT_PRIMARY);
        UIManager.put("TextField.font", FONT_BODY);
        UIManager.put("TextField.foreground", TEXT_PRIMARY);
        UIManager.put("PasswordField.font", FONT_BODY);
        UIManager.put("PasswordField.foreground", TEXT_PRIMARY);
        UIManager.put("ComboBox.font", FONT_BODY);
        UIManager.put("ComboBox.foreground", TEXT_PRIMARY);
        UIManager.put("Button.font", FONT_BUTTON);
        UIManager.put("Table.font", FONT_BODY);
        UIManager.put("TableHeader.font", FONT_BODY_BOLD);
        UIManager.put("TableHeader.background", BG_TABLE_HEADER);
        UIManager.put("TableHeader.foreground", TEXT_SECONDARY);
        UIManager.put("Table.selectionBackground", TABLE_SELECTION);
        UIManager.put("Table.selectionForeground", TEXT_PRIMARY);
        UIManager.put("Table.gridColor", TABLE_GRID);
        UIManager.put("ScrollPane.background", BG_APP);
        UIManager.put("OptionPane.background", BG_CARD);
        UIManager.put("OptionPane.messageForeground", TEXT_PRIMARY);
    }

    // --- Custom Buttons ---

    public enum ButtonType {
        PRIMARY, SECONDARY, SUCCESS, DANGER, WARNING, OUTLINE_PRIMARY
    }

    public static class ModernButton extends JButton {
        private final ButtonType type;
        private boolean isHovered = false;
        private boolean isPressed = false;
        private int cornerRadius = 6;

        public ModernButton(String text, ButtonType type) {
            super(text);
            this.type = type;
            setFont(FONT_BUTTON);
            setFocusPainted(false);
            setContentAreaFilled(false);
            setBorderPainted(false);
            setOpaque(false);
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            setMargin(new Insets(4, 12, 4, 12));

            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    if (isEnabled()) {
                        isHovered = true;
                        repaint();
                    }
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    isHovered = false;
                    isPressed = false;
                    repaint();
                }

                @Override
                public void mousePressed(MouseEvent e) {
                    if (isEnabled()) {
                        isPressed = true;
                        repaint();
                    }
                }

                @Override
                public void mouseReleased(MouseEvent e) {
                    isPressed = false;
                    repaint();
                }
            });
        }

        public void setCornerRadius(int radius) {
            this.cornerRadius = radius;
            repaint();
        }

        @Override
        public Dimension getPreferredSize() {
            Dimension d = super.getPreferredSize();
            return new Dimension(Math.max(d.width + 12, 68), Math.max(d.height + 4, 30));
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB);

            int width = getWidth();
            int height = getHeight();

            Color bg;
            Color fg;
            Color borderColor = null;

            if (!isEnabled()) {
                bg = new Color(241, 245, 249);
                fg = new Color(148, 163, 184);
                borderColor = new Color(226, 232, 240);
            } else {
                switch (type) {
                    case PRIMARY:
                        bg = isPressed ? PRIMARY_PRESSED : (isHovered ? PRIMARY_HOVER : PRIMARY);
                        fg = TEXT_WHITE;
                        break;
                    case SUCCESS:
                        bg = isPressed ? SUCCESS_HOVER.darker() : (isHovered ? SUCCESS_HOVER : SUCCESS);
                        fg = TEXT_WHITE;
                        break;
                    case DANGER:
                        bg = isPressed ? DANGER_HOVER.darker() : (isHovered ? DANGER_HOVER : DANGER);
                        fg = TEXT_WHITE;
                        break;
                    case WARNING:
                        bg = isPressed ? WARNING_HOVER.darker() : (isHovered ? WARNING_HOVER : WARNING);
                        fg = TEXT_WHITE;
                        break;
                    case OUTLINE_PRIMARY:
                        bg = isHovered ? PRIMARY_LIGHT : BG_CARD;
                        fg = PRIMARY;
                        borderColor = PRIMARY;
                        break;
                    case SECONDARY:
                    default:
                        bg = isPressed ? SECONDARY_HOVER.darker() : (isHovered ? SECONDARY_HOVER : BG_CARD);
                        fg = SECONDARY_TEXT;
                        borderColor = SECONDARY_BORDER;
                        break;
                }
            }

            // Fill background
            g2.setColor(bg);
            g2.fill(new RoundRectangle2D.Float(0, 0, width, height, cornerRadius, cornerRadius));

            // Paint border if applicable
            if (borderColor != null) {
                g2.setColor(borderColor);
                g2.setStroke(new BasicStroke(1.2f));
                g2.draw(new RoundRectangle2D.Float(0.5f, 0.5f, width - 1, height - 1, cornerRadius, cornerRadius));
            }

            // Draw text
            g2.setFont(getFont());
            g2.setColor(fg);
            FontMetrics fm = g2.getFontMetrics();
            int textX = (width - fm.stringWidth(getText())) / 2;
            int textY = (height + fm.getAscent() - fm.getDescent()) / 2;
            g2.drawString(getText(), textX, textY);

            g2.dispose();
        }
    }

    public static JButton createPrimaryButton(String text) {
        return new ModernButton(text, ButtonType.PRIMARY);
    }

    public static JButton createSecondaryButton(String text) {
        return new ModernButton(text, ButtonType.SECONDARY);
    }

    public static JButton createSuccessButton(String text) {
        return new ModernButton(text, ButtonType.SUCCESS);
    }

    public static JButton createDangerButton(String text) {
        return new ModernButton(text, ButtonType.DANGER);
    }

    public static JButton createWarningButton(String text) {
        return new ModernButton(text, ButtonType.WARNING);
    }

    public static JButton createBackButton(String text) {
        String label = text.startsWith("<") || text.startsWith("←") ? text : "←  " + text;
        ModernButton btn = new ModernButton(label, ButtonType.SECONDARY);
        btn.setFont(FONT_BUTTON);
        return btn;
    }

    // --- Inputs & Controls ---

    public static JTextField createTextField(int columns) {
        JTextField field = new JTextField(columns);
        styleInputField(field);
        return field;
    }

    public static JPasswordField createPasswordField(int columns) {
        JPasswordField field = new JPasswordField(columns);
        styleInputField(field);
        return field;
    }

    public static void styleInputField(JTextField field) {
        field.setFont(FONT_BODY);
        field.setForeground(TEXT_PRIMARY);
        field.setBackground(BG_INPUT);
        field.setCaretColor(PRIMARY);
        field.setPreferredSize(new Dimension(field.getPreferredSize().width, 32));

        Border normalBorder = new CompoundBorder(
                new RoundedBorder(BORDER, 6, 1),
                new EmptyBorder(5, 10, 5, 10)
        );
        Border focusBorder = new CompoundBorder(
                new RoundedBorder(BORDER_FOCUS, 6, 1.5f),
                new EmptyBorder(5, 10, 5, 10)
        );

        field.setBorder(normalBorder);
        field.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                field.setBorder(focusBorder);
                field.repaint();
            }

            @Override
            public void focusLost(FocusEvent e) {
                field.setBorder(normalBorder);
                field.repaint();
            }
        });
    }

    public static <E> void styleComboBox(JComboBox<E> box) {
        box.setFont(FONT_BODY);
        box.setForeground(TEXT_PRIMARY);
        box.setBackground(BG_CARD);
        box.setPreferredSize(new Dimension(box.getPreferredSize().width, 32));
        box.setBorder(new CompoundBorder(
                new RoundedBorder(BORDER, 6, 1),
                new EmptyBorder(4, 8, 4, 8)
        ));
    }

    // --- Card & Panel Factory ---

    public static class CardPanel extends JPanel {
        private int cornerRadius = 10;
        private Color backgroundColor = BG_CARD;
        private Color borderColor = BORDER;

        public CardPanel() {
            super();
            setOpaque(false);
            setBorder(new EmptyBorder(16, 20, 16, 20));
        }

        public CardPanel(LayoutManager layout) {
            super(layout);
            setOpaque(false);
            setBorder(new EmptyBorder(16, 20, 16, 20));
        }

        public void setCornerRadius(int radius) {
            this.cornerRadius = radius;
            repaint();
        }

        public void setBorderColor(Color color) {
            this.borderColor = color;
            repaint();
        }

        public void setBackgroundColor(Color color) {
            this.backgroundColor = color;
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int width = getWidth();
            int height = getHeight();

            // Background
            g2.setColor(backgroundColor);
            g2.fill(new RoundRectangle2D.Float(0, 0, width, height, cornerRadius, cornerRadius));

            // Border
            if (borderColor != null) {
                g2.setColor(borderColor);
                g2.setStroke(new BasicStroke(1.0f));
                g2.draw(new RoundRectangle2D.Float(0.5f, 0.5f, width - 1, height - 1, cornerRadius, cornerRadius));
            }

            g2.dispose();
            super.paintComponent(g);
        }
    }

    public static CardPanel createCardPanel() {
        return new CardPanel();
    }

    public static CardPanel createCardPanel(LayoutManager layout) {
        return new CardPanel(layout);
    }

    /**
     * Creates a standardized, professional header panel with back navigation and title.
     * Prevents title/subtitle text from overlapping action buttons at any window size.
     */
    public static JPanel createHeaderPanel(String title, String subtitle, JButton backBtn, JComponent... rightComponents) {
        JPanel header = new JPanel(new BorderLayout(14, 0));
        header.setBackground(BG_HEADER);
        header.setBorder(new CompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER),
                new EmptyBorder(12, 20, 12, 20)
        ));

        if (backBtn != null) {
            JPanel westWrapper = new JPanel(new GridBagLayout());
            westWrapper.setOpaque(false);
            westWrapper.add(backBtn);
            header.add(westWrapper, BorderLayout.WEST);
        }

        JPanel titleBlock = new JPanel(new GridBagLayout());
        titleBlock.setOpaque(false);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel titleLbl = new JLabel(title);
        titleLbl.setFont(FONT_TITLE);
        titleLbl.setForeground(TEXT_PRIMARY);
        titleBlock.add(titleLbl, gbc);

        if (subtitle != null && !subtitle.trim().isEmpty()) {
            gbc.gridy = 1;
            gbc.insets = new Insets(2, 0, 0, 0);
            JLabel subLbl = new JLabel(subtitle);
            subLbl.setFont(FONT_SUBTITLE);
            subLbl.setForeground(TEXT_SECONDARY);
            titleBlock.add(subLbl, gbc);
        }

        header.add(titleBlock, BorderLayout.CENTER);

        if (rightComponents != null && rightComponents.length > 0) {
            JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
            rightPanel.setOpaque(false);
            for (JComponent c : rightComponents) {
                if (c != null) rightPanel.add(c);
            }
            JPanel eastWrapper = new JPanel(new GridBagLayout());
            eastWrapper.setOpaque(false);
            eastWrapper.add(rightPanel);
            header.add(eastWrapper, BorderLayout.EAST);
        }

        return header;
    }

    /**
     * Creates a styled section header label.
     */
    public static JLabel createSectionHeader(String title) {
        JLabel lbl = new JLabel(title);
        lbl.setFont(FONT_SECTION);
        lbl.setForeground(TEXT_PRIMARY);
        lbl.setBorder(new EmptyBorder(0, 0, 8, 0));
        return lbl;
    }

    // --- Table Styling ---

    public static void styleTable(JTable table) {
        table.setFont(FONT_BODY);
        table.setForeground(TEXT_PRIMARY);
        table.setBackground(BG_CARD);
        table.setRowHeight(34);
        table.setShowGrid(true);
        table.setGridColor(TABLE_GRID);
        table.setShowVerticalLines(false);
        table.setIntercellSpacing(new Dimension(0, 1));
        table.setSelectionBackground(TABLE_SELECTION);
        table.setSelectionForeground(TEXT_PRIMARY);
        table.setFillsViewportHeight(true);

        // Header Styling
        JTableHeader header = table.getTableHeader();
        header.setFont(FONT_BODY_BOLD);
        header.setForeground(TEXT_SECONDARY);
        header.setBackground(BG_TABLE_HEADER);
        header.setPreferredSize(new Dimension(header.getPreferredSize().width, 36));
        header.setReorderingAllowed(false);
        header.setDefaultRenderer(new TableHeaderRenderer());

        // Cell Renderer with Alternating Rows and Badge Support
        TableCellRenderer modernRenderer = new ModernTableCellRenderer();
        table.setDefaultRenderer(Object.class, modernRenderer);
        table.setDefaultRenderer(String.class, modernRenderer);
        table.setDefaultRenderer(Integer.class, modernRenderer);
        table.setDefaultRenderer(Double.class, modernRenderer);
    }

    private static class TableHeaderRenderer extends DefaultTableCellRenderer {
        public TableHeaderRenderer() {
            setOpaque(true);
            setHorizontalAlignment(SwingConstants.LEFT);
            setBorder(new CompoundBorder(
                    BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER),
                    new EmptyBorder(0, 12, 0, 12)
            ));
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            setFont(FONT_BODY_BOLD);
            setForeground(TEXT_SECONDARY);
            setBackground(BG_TABLE_HEADER);
            return this;
        }
    }

    private static class ModernTableCellRenderer extends DefaultTableCellRenderer {
        private final StatusBadgePanel badgePanel = new StatusBadgePanel();

        public ModernTableCellRenderer() {
            setBorder(new EmptyBorder(0, 12, 0, 12));
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            String colName = table.getColumnName(column).toLowerCase();
            String strVal = value != null ? value.toString() : "";

            // Check if this column is a status column or contains status-like values
            if (isStatusValue(colName, strVal)) {
                badgePanel.setStatus(strVal, isSelected);
                return badgePanel;
            }

            Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            setFont(FONT_BODY);

            if (isSelected) {
                c.setBackground(TABLE_SELECTION);
                c.setForeground(TEXT_PRIMARY);
            } else {
                c.setBackground(row % 2 == 0 ? BG_CARD : BG_TABLE_ALT);
                c.setForeground(TEXT_PRIMARY);
            }

            return c;
        }

        private boolean isStatusValue(String colName, String val) {
            if (colName.contains("status")) return true;
            String upper = val.toUpperCase().trim();
            return upper.equals("ACTIVE") || upper.equals("INACTIVE") ||
                    upper.equals("AVAILABLE") || upper.equals("ISSUED") ||
                    upper.equals("PENDING") || upper.equals("APPROVED") ||
                    upper.equals("REJECTED") || upper.equals("FULFILLED") ||
                    upper.equals("RETURNED");
        }
    }

    /**
     * Renders clean, modern pill-shaped status badges for table cells.
     */
    private static class StatusBadgePanel extends JPanel {
        private final JLabel label = new JLabel();
        private Color badgeBg = BG_APP;
        private Color badgeFg = TEXT_PRIMARY;

        public StatusBadgePanel() {
            setLayout(new FlowLayout(FlowLayout.LEFT, 10, 6));
            setOpaque(true);
            label.setFont(FONT_BADGE);
            label.setBorder(new EmptyBorder(2, 8, 2, 8));
            add(label);
        }

        public void setStatus(String status, boolean isSelected) {
            String upper = status != null ? status.toUpperCase().trim() : "";
            label.setText(upper.isEmpty() ? "-" : upper);

            if (isSelected) {
                setBackground(TABLE_SELECTION);
            } else {
                setBackground(BG_CARD);
            }

            if ("ACTIVE".equals(upper) || "AVAILABLE".equals(upper) || "APPROVED".equals(upper) || "RETURNED".equals(upper)) {
                badgeBg = SUCCESS_BG;
                badgeFg = SUCCESS_TEXT;
            } else if ("INACTIVE".equals(upper) || "REJECTED".equals(upper)) {
                badgeBg = DANGER_BG;
                badgeFg = DANGER_TEXT;
            } else if ("PENDING".equals(upper) || "ISSUED".equals(upper)) {
                badgeBg = WARNING_BG;
                badgeFg = WARNING_TEXT;
            } else if ("FULFILLED".equals(upper)) {
                badgeBg = INFO_BG;
                badgeFg = INFO_TEXT;
            } else {
                badgeBg = SECONDARY_BG;
                badgeFg = SECONDARY_TEXT;
            }

            label.setForeground(badgeFg);
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (label.getText().isEmpty() || label.getText().equals("-")) return;

            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            Point pt = label.getLocation();
            int w = label.getWidth();
            int h = label.getHeight();

            g2.setColor(badgeBg);
            g2.fill(new RoundRectangle2D.Float(pt.x, pt.y, w, h, h, h));
            g2.dispose();
        }
    }

    // --- Rounded Border Implementation ---

    public static class RoundedBorder extends AbstractBorder {
        private final Color color;
        private final int radius;
        private final float thickness;

        public RoundedBorder(Color color, int radius, float thickness) {
            this.color = color;
            this.radius = radius;
            this.thickness = thickness;
        }

        @Override
        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(color);
            g2.setStroke(new BasicStroke(thickness));
            g2.draw(new RoundRectangle2D.Float(x + thickness / 2, y + thickness / 2, width - thickness, height - thickness, radius, radius));
            g2.dispose();
        }

        @Override
        public Insets getBorderInsets(Component c) {
            int p = (int) Math.ceil(thickness);
            return new Insets(p, p, p, p);
        }
    }
}
