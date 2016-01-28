/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.lidinsky.tools.text;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 *
 * @author jilm
 */
public class TableFormatter {
    
    private String[] head;
    private final List<String[]> rows;
    private final int lineWidth = 80;
    private int columns = 0;
    private int[] columnLengths;
    
    public TableFormatter() {
        rows = new ArrayList<>();
    }
    
    public TableFormatter appendHead(String ... head) {
        this.head = head;
        analyze(head);
        if (this.head != null) {
            columns = Math.max(columns, head.length);
            
        }
        return this;
    }
    
    public TableFormatter appendRow(String ... elements) {
        rows.add(elements);
        if (elements != null) {
            columns = Math.max(columns, elements.length);
        }
        return this;
    } 
    
    private void analyze(String[] row) {
        if (row == null) {
            row = new String[] { "<null>" };
        }
        columns = Math.max(columns, row.length);
        if (columnLengths == null) {
            columnLengths = new int[columns];
            for (int i = 0; i < columns; i++) {
                columnLengths[i] = row[i] == null ? 0 : row[i].length();
            }
        } else if (columnLengths.length < columns) {
            int[] temp = new int[columns];
            for (int i = 0; i < columnLengths.length; i++) {
                temp[i] = Math.max(columnLengths[i], row[i] == null ? 0 : row[i].length());
            }
            for (int i = columnLengths.length; i < columns; i++) {
                temp[i] = row[i] == null ? 0 : row[i].length();
            }
            columnLengths = temp;
        } else {
            for (int i = 0; i < row.length; i++) {
                columnLengths[i] = Math.max(columnLengths[i], row[i] == null ? 0 : row[i].length());
            }
        }
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        if (rows.isEmpty()) {
            sb.append("<An empty table>\n");
        } else {
            if (head != null) {
                for (int i = 0; i < head.length; i++) {
                    sb.append(allignCenter(head[i], columnLengths[i]))
                            .append(" ");
                }
                sb.append("\n===========================================\n");
            }
            for (String[] row : rows) {
                for (int i = 0; i < row.length; i++) {
                    sb.append(allignRight(row[i], columnLengths[i]))
                            .append(" ");
                }
                sb.append("\n");
            }
        }
        return sb.toString();
    }

    private String allignRight(String text, int length) {
        if (text == null) {
            return repeatChar(' ', length);
        } else if (text.length() >= length) {
            return text;
        } else {
            return repeatChar(' ', length - text.length()) + text;
        }
    }
    
    private String repeatChar(char ch, int count) {
        char[] buffer = new char[count];
        Arrays.fill(buffer, ch);
        return new String(buffer);
    }

    private String allignCenter(String text, int length) {
        if (text == null) {
            return repeatChar(' ', length);
        } else if (text.length() >= length) {
            return text;
        } else {
            int left = (length - text.length()) / 2;
            int right = length - text.length() - left;
            return repeatChar(' ', left) + text + repeatChar(' ', right);
        }
    }
    
}
