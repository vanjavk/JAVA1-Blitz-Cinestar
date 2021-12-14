package me.vanjavk.model;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.List;

public class DirectorTableModel extends AbstractTableModel {

    private static final String[] COLUMN_NAMES = {"ID", "Name"};

    private List<Director> directors = new ArrayList<>();

    public DirectorTableModel() {
    }

    public DirectorTableModel(List<Director> directors) {
        this.directors = directors;
    }

    public void setDirectors(List<Director> directors) {
        this.directors = directors;
        fireTableDataChanged();
    }

    @Override
    public int getRowCount() {
        return directors.size();
    }

    @Override
    public int getColumnCount() {
        return COLUMN_NAMES.length;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        switch (columnIndex) {
            case 0 -> {return directors.get(rowIndex).getId();}
            case 1 -> {return directors.get(rowIndex).getName();}
            default -> throw new RuntimeException("No such column!");
        }
    }

    @Override
    public Class getColumnClass(int column) {
        switch (column) {
            case 0:
                return Integer.class;
            default:
                return String.class;
        }
    }

    @Override
    public String getColumnName(int column) {
        return COLUMN_NAMES[column];
    }
}
