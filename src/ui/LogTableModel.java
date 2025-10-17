package ui;

import model.LogEntry;

import javax.swing.table.AbstractTableModel;
import java.text.SimpleDateFormat;
import java.util.List;

public class LogTableModel extends AbstractTableModel {
    private final List<LogEntry> logs;
    private final String[] cols = {"ID","Subprograma","Gravedad","Mensaje","Fecha"};
    private final SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public LogTableModel(List<LogEntry> logs){ this.logs = logs; }

    @Override public int getRowCount(){ return logs.size(); }
    @Override public int getColumnCount(){ return cols.length; }
    @Override public String getColumnName(int col){ return cols[col]; }
    @Override
    public Object getValueAt(int row, int col){
        LogEntry e = logs.get(row);
        return switch(col){
            case 0 -> e.getId();
            case 1 -> e.getSubprograma();
            case 2 -> e.getGravedad();
            case 3 -> e.getMensaje();
            case 4 -> df.format(e.getFecha());
            default -> null;
        };
    }
}