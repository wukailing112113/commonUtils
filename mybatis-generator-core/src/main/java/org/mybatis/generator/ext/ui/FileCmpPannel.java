package org.mybatis.generator.ext.ui;

/**
 */

import com.wins.shop.util.StringUtil;
import org.apache.commons.io.FileUtils;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableModel;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

class JTableModel extends AbstractTableModel {

    String[][] data;
    public JTableModel(String[][] data) {
        this.data = data;
    }
    private static final long serialVersionUID = 1L;
    private static final String[] COLUMN_NAMES = new String[] {"old", "new", "compare" };
    private static final Class<?>[] COLUMN_TYPES = new Class<?>[] {Integer.class, String.class, JButton.class,  JButton.class};

    @Override public int getColumnCount() {
        return COLUMN_NAMES.length;
    }

    public int getRowCount() { return data.length;}

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return false;
    }

    @Override public String getColumnName(int columnIndex) {
        return COLUMN_NAMES[columnIndex];
    }

    @Override public Class<?> getColumnClass(int columnIndex) {
        return COLUMN_TYPES[columnIndex];
    }

    @Override public Object getValueAt(final int rowIndex, final int columnIndex) {
                /*Adding components*/
        switch (columnIndex) {
            case 0: return data[rowIndex][columnIndex];
            case 1: return data[rowIndex][columnIndex];
            case 2:
                final JButton button = new JButton(COLUMN_NAMES[columnIndex]);
                button.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent arg0) {
                        JOptionPane.showMessageDialog(JOptionPane.getFrameForComponent(button),
                                "Button clicked for row "+rowIndex);
                    }
                });
                return button;
            default: return "Error";
        }
    }
}

class JTableButtonRenderer implements TableCellRenderer {
    @Override public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        JButton button = (JButton)value;
        return button;
    }
}

public class FileCmpPannel implements ListSelectionListener {
    private JTable fileTable;
    private String meldLocation;
    protected Set<String> fileCmpStateSet = new HashSet<String>();
    protected String[][] dataItems;

    public FileCmpPannel(String[][] dataValues, String meldLocation) {
        this.meldLocation = StringUtil.isEmptyString(meldLocation)?"":meldLocation;
        this.dataItems = dataValues;
        init(dataValues);
    }

    public void compare2File(String oldFilePath, String newFilePath) {
        Runtime rt = Runtime.getRuntime();
        int exitVal = 0;
        try {
            Process proc = rt.exec(meldLocation + "meld " + oldFilePath + " " + newFilePath);
            exitVal = proc.waitFor();
        } catch (InterruptedException e1) {
            e1.printStackTrace();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        System.out.println("Process exitValue: " + exitVal);
        fileCmpStateSet.add(oldFilePath);
    }

    public void compare2FileSel() {
        int[] selRows = fileTable.getSelectedRows();
        if (selRows.length == 0)
            return;

        TableModel tm = fileTable.getModel();
        String oldFilePath = tm.getValueAt(selRows[0], 0).toString();
        String newFilePath = tm.getValueAt(selRows[0], 1).toString();
        compare2File(oldFilePath, newFilePath);
    }

    public void finishFileCmp() {
        for (String[] itemRow: dataItems) {
            String oldFilePath = itemRow[0];
            if (!fileCmpStateSet.contains(oldFilePath)) {
                try {
                    String chgName = oldFilePath + "." + (new Date()).getTime();
                    FileUtils.moveFile(new File(oldFilePath), new File(chgName));
                    FileUtils.moveFile(new File(itemRow[1]), new File(oldFilePath));
                    FileUtils.deleteQuietly( new File(chgName));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    // Constructor of main frame
    public void init(final String[][] data)
    {
        JFrame frame = new JFrame("Table");
        frame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {System.exit(0);}});
        JPanel panel = new JPanel(new BorderLayout());//(new GridLayout(2,1));
        frame.setContentPane(panel);

        TableModel dataModel = new JTableModel(data);
        fileTable = new JTable(dataModel);
        TableCellRenderer buttonRenderer = new JTableButtonRenderer();
        fileTable.getColumn("compare").setCellRenderer(buttonRenderer);

        ListSelectionModel listMod =  fileTable.getSelectionModel();
        listMod.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        listMod.addListSelectionListener(this);

        fileTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    compare2FileSel();
                }
            }
        });

        JScrollPane jScrollPane = new JScrollPane(fileTable);
        frame.add(jScrollPane, BorderLayout.NORTH);

        JButton okButton = new JButton("确定");
        okButton.setPreferredSize(new Dimension(40, 40));
        okButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                finishFileCmp();
            }
        });

        jScrollPane.setPreferredSize(new Dimension(550, 300));

        panel.add(jScrollPane, BorderLayout.CENTER);
        panel.add(okButton, BorderLayout.SOUTH);

        frame.pack();
        frame.setVisible(true);
    }

    // Main entry point for this example
    public static void main( String args[] )
    {
        // Create some data
        String dataValues[][] =
                {
                        { "F:\\workspace\\iba-core\\src\\main\\java\\com\\iba\\life\\entity\\user\\Ushop.java",
                                "F:\\workspace\\iba-core\\src\\main\\java\\com\\iba\\life\\entity\\user\\Ushop.java.1", "67" },
                };
        FileCmpPannel mainFrame	= new FileCmpPannel(dataValues, null);
    }

    @Override
    public void valueChanged(ListSelectionEvent e) {
    }
}

