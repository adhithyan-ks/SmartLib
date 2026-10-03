package com.college.library.gui;

import javax.swing.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.Component;
import java.awt.Container;

public class TableSelectionHelper {
    
    /**
     * Sets up custom selection behavior for a JTable:
     * - Clicking an already selected row deselects it.
     */
    public static void setupTable(JTable table) {
        table.addMouseListener(new MouseAdapter() {
            private int lastSelectedRow = -1;

            @Override
            public void mousePressed(MouseEvent e) {
                int row = table.rowAtPoint(e.getPoint());
                if (row != -1 && row == lastSelectedRow) {
                    // Was already selected before this click, so deselect it
                    table.clearSelection();
                    lastSelectedRow = -1;
                } else {
                    lastSelectedRow = row;
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                lastSelectedRow = table.getSelectedRow();
            }
        });
    }

    /**
     * Groups multiple tables together so that selecting a row in one table 
     * clears the selection in all other tables in the group.
     */
    public static void setupMutuallyExclusiveTables(JTable... tables) {
        for (JTable table : tables) {
            setupTable(table);
            table.getSelectionModel().addListSelectionListener(e -> {
                if (!e.getValueIsAdjusting() && table.getSelectedRow() != -1) {
                    for (JTable otherTable : tables) {
                        if (otherTable != table) {
                            otherTable.clearSelection();
                        }
                    }
                }
            });
        }
    }

    /**
     * Adds a listener to a container (like a JPanel) so that clicking anywhere 
     * in the container (that isn't a table or explicitly ignoring it) clears 
     * the selection of the provided tables. Also triggers on focus of child components like TextFields.
     */
    public static void setupClickOutsideToClear(JComponent container, JTable[] tables, Component... ignoredComponents) {
        java.util.List<Component> ignoredList = java.util.Arrays.asList(ignoredComponents);
        
        container.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                clearAll(tables);
            }
        });
        
        // Add to all children recursively
        attachClearSelectionToChildren(container, tables, ignoredList);
    }
    
    private static void attachClearSelectionToChildren(Container container, JTable[] tables, java.util.List<Component> ignoredList) {
        for (Component c : container.getComponents()) {
            if (c instanceof JTable || ignoredList.contains(c) || c instanceof JScrollBar || c instanceof javax.swing.table.JTableHeader) {
                continue; // Don't attach to the tables, scrollbars, headers, or ignored components
            }
            
            c.addMouseListener(new MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent e) {
                    clearAll(tables);
                }
            });
            
            if (c instanceof Container) {
                attachClearSelectionToChildren((Container) c, tables, ignoredList);
            }
        }
    }
    
    private static void clearAll(JTable... tables) {
        for (JTable t : tables) {
            t.clearSelection();
        }
    }
}
