package me.vanjavk.model;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.List;

public class ActorTableModel extends AbstractTableModel {

    private static final String[] COLUMN_NAMES = {"ID", "Name"};

    private List<Actor> actors = new ArrayList<>();

    public ActorTableModel() {
    }


    public ActorTableModel(List<Actor> actors) {
        this.actors = actors;
    }

    public void setActors(List<Actor> actors) {
        this.actors = actors;
        fireTableDataChanged();
    }

    @Override
    public int getRowCount() {
        return actors.size();
    }

    @Override
    public int getColumnCount() {
        return COLUMN_NAMES.length;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        switch (columnIndex) {
            case 0 -> {return actors.get(rowIndex).getId();}
            case 1 -> {return actors.get(rowIndex).getName();}
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
