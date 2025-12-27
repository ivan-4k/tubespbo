package view.tablemodel;

import javax.swing.table.AbstractTableModel;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;

public class AntrianSelesaiTableModel extends AbstractTableModel {
    private String[] columnNames = {"No", "Nama", "Umur", "Alamat", "Keluhan", "Dokter", "Waktu Selesai"};
    private List<Map<String, Object>> data;
    
    public AntrianSelesaiTableModel(List<Map<String, Object>> data) {
        this.data = data;
    }
    
    @Override
    public int getRowCount() {
        return data != null ? data.size() : 0;
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
        if (data == null || data.isEmpty()) return null;
        
        Map<String, Object> antrian = data.get(row);
        switch (column) {
            case 0: return antrian.get("nomor");
            case 1: return antrian.get("nama");
            case 2: return antrian.get("umur");
            case 3: return antrian.get("alamat");
            case 4: return antrian.get("keluhan");
            case 5: return antrian.get("dokter");
            case 6: {
                Object waktu = antrian.get("waktu_selesai");
                if (waktu != null && !waktu.toString().isEmpty() && !waktu.toString().equals("null")) {
                    return formatWaktu(waktu.toString());
                }
                return "Belum ada waktu";
            }
            default: return null;
        }
    }
    
    private String formatWaktu(String waktuServer) {
        try {
            SimpleDateFormat sdfInput = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            SimpleDateFormat sdfOutput = new SimpleDateFormat("HH:mm:ss");
            return sdfOutput.format(sdfInput.parse(waktuServer));
        } catch (Exception e) {
            return waktuServer;
        }
    }
    
    public void updateData(List<Map<String, Object>> newData) {
        this.data = newData;
        fireTableDataChanged();
    }
}