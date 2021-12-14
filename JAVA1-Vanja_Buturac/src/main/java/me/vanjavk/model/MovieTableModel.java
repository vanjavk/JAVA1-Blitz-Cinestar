package me.vanjavk.model;

import javax.swing.table.AbstractTableModel;
import java.util.List;

public class MovieTableModel extends AbstractTableModel {


    private static final String[] COLUMN_NAMES = {"ID", "Title", "Description", "Genre", "Duration", "Published date", "Picture path"};

    private List<Movie> movies;

    public MovieTableModel(List<Movie> directors) {
        this.movies = directors;
    }

    public void setMovies(List<Movie> movies) {
        this.movies = movies;
        fireTableDataChanged();
    }

    @Override
    public int getRowCount() {
        return movies.size();
    }

    @Override
    public int getColumnCount() {
        return COLUMN_NAMES.length;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        switch (columnIndex) {
            case 0 -> {return movies.get(rowIndex).getId();}
            case 1 -> {return movies.get(rowIndex).getTitle();}
            case 2 -> {return movies.get(rowIndex).getDescription();}
            case 3 -> {return movies.get(rowIndex).getGenre();}
            case 4 -> {return movies.get(rowIndex).getDuration();}
            case 5 -> {return movies.get(rowIndex).getPublishedDate();}
            case 6 -> {return movies.get(rowIndex).getPicturePath();}
            default -> throw new RuntimeException("No such column!");
        }
    }

    @Override
    public Class getColumnClass(int column) {
        switch (column) {
            case 0 -> {return Integer.class;}
            case 4 -> {return Integer.class;}
            default -> {return String.class;}
        }
    }

    @Override
    public String getColumnName(int column) {
        return COLUMN_NAMES[column];
    }
}
