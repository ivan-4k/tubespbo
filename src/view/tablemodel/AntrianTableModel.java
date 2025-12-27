package view.tablemodel;

import javax.swing.table.AbstractTableModel;
import java.util.List;
import java.util.Map;

public class AntrianTableModel extends AbstractTableModel {
    private String[] columnNames = {"No", "Nama", "Umur", "Alamat", "Keluhan", "Dokter", "Spesialis", "Ruangan", "Status"};
    private List<Map<String, Object>> data;
    
    public AntrianTableModel(List<Map<String, Object>> data) {
        this.data = data;
    }
    
    @Override
    public int getRowCount() {
        return data.size();
    }
    
    @Override
    public int getColumnCount() {
        return columnNames.length;
    }
    
    @Override
    public String getColumnName(int column) {
        return columnNames[column];
    }
    
    @Override
    public Object getValueAt(int row, int column) {
        Map<String, Object> antrian = data.get(row);
        switch (column) {
            case 0: return antrian.get("nomor");
            case 1: return antrian.get("nama");
            case 2: return antrian.get("umur");
            case 3: return antrian.get("alamat");
            case 4: return antrian.get("keluhan");
            case 5: return antrian.get("dokter");
            case 6: return antrian.get("spesialis");
            case 7: return antrian.get("ruangan");
            case 8: return antrian.get("status");
            default: return null;
        }
    }
    
    public void updateData(List<Map<String, Object>> newData) {
        this.data = newData;
        fireTableDataChanged();
    }
}