package me.vanjavk;

import me.vanjavk.dal.Repository;
import me.vanjavk.dal.RepositoryFactory;
import me.vanjavk.model.*;
import me.vanjavk.singleton.Configuration;
import me.vanjavk.singleton.Random;
import me.vanjavk.utils.FileUtilities;
import me.vanjavk.utils.MessageUtils;

import javax.swing.*;
import javax.swing.text.JTextComponent;
import java.awt.*;
import java.awt.datatransfer.Transferable;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class EditMoviePanel {
    private List<JTextComponent> validationFields;
    private List<JLabel> errorLabels;

    private JPanel pnlEditMovie;
    private JTable tbMovie;
    private JButton btnAdd;
    private JButton btnUpdate;
    private JButton btnDelete;
    private JTextField tfTitle;
    private JTextArea taDescription;
    private JTextField tfGenre;
    private JTextField tfDuration;
    private JTextField tfPublishedDate;
    private JLabel lbIcon;
    private JLabel lbTitleError;
    private JLabel lbDescriptionError;
    private JLabel lbGenreError;
    private JLabel lbDurationError;
    private JLabel lbPublishedDateError;
    private JTable tbDirectors;
    private JTable tbActors;
    private JTable tbAllDirectors;
    private JTable tbAllActors;
    private JTextField tfPicturePath;
    private JLabel lbPicturePathError;
    private JButton btnChoosePicture;
    private JButton btnRemoveActor;
    private JButton btnRemoveDirector;
    private JScrollPane jspAllDirectors;
    private JScrollPane jspAllActors;

    private MovieTableModel movieTableModel;
    private DirectorTableModel directorsTableModel;
    private DirectorTableModel allDirectorsTableModel;
    private ActorTableModel actorsTableModel;
    private ActorTableModel allActorsTableModel;

    private Movie selected;


    private Repository repository;

    public JPanel getPanel() {
        return pnlEditMovie;
    }

    public EditMoviePanel() {
        lbIcon.setSize(new Dimension(83, 124));
        setDefaultIcon();

        tfTitle.addCaretListener(e -> formValid());
        taDescription.addCaretListener(e -> formValid());
        tfGenre.addCaretListener(e -> formValid());
        tfDuration.addCaretListener(e -> formValid());
        tfPublishedDate.addCaretListener(e -> formValid());
        tfPicturePath.addCaretListener(e -> formValid());
        pnlEditMovie.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentShown(ComponentEvent e) {
                refresh();
            }
        });

        try {
            repository = RepositoryFactory.getRepository();
            initValidation();
            initTable();
        } catch (Exception exception) {
            exception.printStackTrace();
        }

        btnAdd.addActionListener(e -> {
            try {
                if (!formValid()) {
                    MessageUtils.showWarningMessage("Input error", "Invalid input!");
                    return;
                }
                repository.createMovie(new Movie(tfTitle.getText().trim(), taDescription.getText(), tfGenre.getText().trim(),
                        Integer.valueOf(tfDuration.getText().trim()),
                        LocalDateTime.parse(tfPublishedDate.getText().trim(), Configuration.DATE_FORMATTER_SQL),
                        uploadPicture(tfPicturePath.getText().trim())));
                refresh();
            } catch (SQLException exception) {
                switch (exception.getErrorCode()) {
                    case 2627 -> MessageUtils.showWarningMessage("Action failed", "Movie with same name already exists");
                    default -> MessageUtils.showErrorMessage("Database error", "Unknown error!");
                }
            } catch (Exception exception) {
            }
        });
        btnUpdate.addActionListener(e -> {
            try {
                if (selected == null) {
                    MessageUtils.showWarningMessage("Action error", "No movie selected!");
                    return;
                }
                if (!formValid()) {
                    MessageUtils.showWarningMessage("Input error", "Invalid input!");
                    return;
                }
                selected.setTitle(tfTitle.getText().trim());
                selected.setDescription(taDescription.getText());
                selected.setGenre(tfGenre.getText().trim());
                selected.setDuration(Integer.valueOf(tfDuration.getText().trim()));
                selected.setPublishedDate(LocalDateTime.parse(tfPublishedDate.getText().trim(), Configuration.DATE_FORMATTER_SQL));
                if (!tfPicturePath.getText().trim().equals(selected.getPicturePath())) {
                    try {
                        Files.delete(Paths.get(selected.getPicturePath()));
                    } catch (Exception exception) {
                        exception.printStackTrace();
                    }
                    selected.setPicturePath(uploadPicture(tfPicturePath.getText().trim()));
                }
                repository.updateMovie(selected);
                refresh();
            } catch (SQLException exception) {

                switch (exception.getErrorCode()) {
                    case 2627 -> MessageUtils.showWarningMessage("Action failed", "Movie with same name already exists");
                    default -> MessageUtils.showErrorMessage("Database error", "Unknown error!");
                }
            } catch (Exception exception) {
            }
        });
        btnDelete.addActionListener(e -> {
            try {
                if (selected == null) {
                    MessageUtils.showWarningMessage("Action error", "No Movie selected!");
                    return;
                }
                repository.deleteMovie(selected.getId());
                try {
                    Files.delete(Paths.get(selected.getPicturePath()));
                } catch (Exception exception) {
                    exception.printStackTrace();
                }
                refresh();
            } catch (Exception exception) {
                if (exception instanceof SQLException) {
                    switch (((SQLException) exception).getErrorCode()) {
                        default -> MessageUtils.showErrorMessage("Database error", "Unknown error!");
                    }
                }
                exception.printStackTrace();
            }
        });

        btnChoosePicture.addActionListener(e -> {
            File file = FileUtilities.uploadFile("Images", "jpg", "jpeg", "png");
            if (file == null) {
                return;
            }
            tfPicturePath.setText(file.getAbsolutePath());
            setIcon(lbIcon, file);
        });
        btnRemoveDirector.addActionListener(e -> {
            try {
                if (selected == null) {
                    MessageUtils.showErrorMessage("Action error", "No Movie selected!");
                    return;
                }
                int selectedDirector;
                try {
                    selectedDirector = (int) directorsTableModel.getValueAt(tbDirectors.getRowSorter().convertRowIndexToModel(tbDirectors.getSelectedRow()), 0);
                } catch (Exception exception) {
                    MessageUtils.showWarningMessage("Action error", "No Director selected!");
                    return;
                }
                repository.removeDirector(selectedDirector, selected.getId());
                refreshSelection();
            } catch (Exception exception) {
                if (exception instanceof SQLException) {
                    MessageUtils.showErrorMessage("Database error", "Unknown error!");
                }
                exception.printStackTrace();
            }
        });
        btnRemoveActor.addActionListener(e -> {
            try {
                if (selected == null) {
                    MessageUtils.showWarningMessage("Action error", "No Movie selected!");
                    return;
                }
                int selectedActor;
                try {
                    selectedActor = (int) actorsTableModel.getValueAt(tbActors.getRowSorter().convertRowIndexToModel(tbActors.getSelectedRow()), 0);
                } catch (Exception exception) {
                    MessageUtils.showWarningMessage("Action error", "No Actor selected!");
                    return;
                }
                repository.removeActor(selectedActor, selected.getId());
                refreshSelection();
            } catch (Exception exception) {
                if (exception instanceof SQLException) {

                    MessageUtils.showErrorMessage("Database error", "Unknown error!");
                }
                exception.printStackTrace();
            }
        });

        tbMovie.getSelectionModel().addListSelectionListener(lse -> {
            if (!lse.getValueIsAdjusting()) {
                showMovie();
                refreshSelection();
            }
        });
    }

    private void initDragAndDrop() {
        tbAllActors.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tbAllActors.setDragEnabled(true);
        tbAllActors.setTransferHandler(new ActorExportTransferHandler());
        tbActors.setDropMode(DropMode.ON);
        tbActors.setTransferHandler(new ActorImportTransferHandler());


        tbAllDirectors.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tbAllDirectors.setDragEnabled(true);
        tbAllDirectors.setTransferHandler(new DirectorExportTransferHandler());
        tbDirectors.setDropMode(DropMode.ON);
        tbDirectors.setTransferHandler(new DirectorImportTransferHandler());
    }

    private String uploadPicture(String picturePath) throws IOException {
        String internetName = picturePath.substring(picturePath.lastIndexOf(File.separator) + 1);
        String pictureName = internetName.substring(0, internetName.lastIndexOf(".")) + Math.abs(Random.getInstance().nextInt()) + internetName.substring(internetName.lastIndexOf("."));
        String localPicturePath = Configuration.DOWNLOAD_DIR + File.separator + pictureName;
        try {
            FileUtilities.copy(picturePath, localPicturePath);
        } catch (Exception exception) {
            System.out.println(localPicturePath);
        }
        return localPicturePath;
    }

    private void initTable() throws Exception {
        tbDirectors.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tbDirectors.setAutoCreateRowSorter(true);
        tbDirectors.setRowHeight(25);
        directorsTableModel = new DirectorTableModel();
        tbDirectors.setModel(directorsTableModel);

        tbAllDirectors.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tbAllDirectors.setAutoCreateRowSorter(true);
        tbAllDirectors.setRowHeight(25);
        allDirectorsTableModel = new DirectorTableModel(repository.selectDirectors());
        tbAllDirectors.setModel(allDirectorsTableModel);

        tbActors.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tbActors.setAutoCreateRowSorter(true);
        tbActors.setRowHeight(25);
        actorsTableModel = new ActorTableModel();
        tbActors.setModel(actorsTableModel);

        tbAllActors.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tbAllActors.setAutoCreateRowSorter(true);
        tbAllActors.setRowHeight(25);
        allActorsTableModel = new ActorTableModel(repository.selectActors());
        tbAllActors.setModel(allActorsTableModel);

        tbMovie.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tbMovie.setAutoCreateRowSorter(true);
        tbMovie.setRowHeight(25);
        movieTableModel = new MovieTableModel(repository.selectMovies());
        tbMovie.setModel(movieTableModel);

        tbActors.setDragEnabled(true);
        tbAllActors.setDragEnabled(true);

        tbActors.setDropMode(DropMode.INSERT);
        tbAllActors.setDropMode(DropMode.INSERT);

        initDragAndDrop();
    }

    private void showMovie() {

        try {
            selected = repository.selectMovie((int) movieTableModel.getValueAt(tbMovie.getRowSorter().convertRowIndexToModel(tbMovie.getSelectedRow()), 0));
            fillForm(selected);
        } catch (SQLException exception) {
            exception.printStackTrace();
            MessageUtils.showErrorMessage("Database error", "Unable to fetch movies");
            return;
        } catch (Exception exception) {
            return;
        }


    }

    private void clearForm() {
        validationFields.forEach(e -> e.setText(""));
        errorLabels.forEach(e -> e.setText(""));
        selected = null;
    }

    private void initValidation() {
        validationFields = Arrays.asList(tfTitle, taDescription, tfGenre, tfDuration, tfPublishedDate, tfPicturePath);
        errorLabels = Arrays.asList(lbTitleError, lbDescriptionError, lbGenreError, lbDurationError, lbPublishedDateError, lbPicturePathError);
    }

    private void setDefaultIcon() {
        setIcon(lbIcon, new File(EditMoviePanel.class.getClassLoader().getResource("no_pic.png").getPath()));
    }

    private void setIcon(JLabel label, File file) {
        try {
            label.setIcon(FileUtilities.createIcon(file.getAbsolutePath(), label.getWidth(), label.getHeight()));
        }catch (Exception exception) {
            MessageUtils.showErrorMessage("Error", "Unable to set icon!");
        }
    }

    private boolean formValid() {
        boolean ok = true;

        for (int i = 0; i < validationFields.size(); i++) {
            ok &= !validationFields.get(i).getText().trim().isEmpty();
            errorLabels.get(i).setText(validationFields.get(i).getText().trim().isEmpty() ? "X" : "");

            if (validationFields.get(i) == tfPublishedDate) {
                try {
                    LocalDateTime.parse(validationFields.get(i).getText().trim(), Configuration.DATE_FORMATTER_SQL);
                    errorLabels.get(i).setText("");
                } catch (Exception e) {
                    ok = false;
                    errorLabels.get(i).setText("X");
                }
            } else if (validationFields.get(i) == tfDuration) {
                try {
                    Integer.parseInt(validationFields.get(i).getText().trim());
                    errorLabels.get(i).setText("");
                } catch (Exception e) {
                    ok = false;
                    errorLabels.get(i).setText("X");
                    errorLabels.get(i).setText("X");
                }
            }

        }
        return ok;
    }

    private void refreshSelection() {
        if (selected == null) {
            directorsTableModel.setDirectors(new ArrayList<>());
            actorsTableModel.setActors(new ArrayList<>());
            return;
        }
        try {
            directorsTableModel.setDirectors(repository.selectDirectors(selected.getId()));
            actorsTableModel.setActors(repository.selectActors(selected.getId()));
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    private void fillForm(Movie movie) {
        tfTitle.setText(movie.getTitle());
        taDescription.setText(movie.getDescription());
        tfGenre.setText(movie.getGenre());
        tfDuration.setText(String.valueOf((movie.getDuration())));
        tfPublishedDate.setText(movie.getPublishedDate().format(Configuration.DATE_FORMATTER_SQL));
        tfPicturePath.setText(movie.getPicturePath());
        if (movie.getPicturePath() != null) {
            setIcon(lbIcon, new File(movie.getPicturePath()));
        } else {
            setDefaultIcon();
        }
    }

    public void refresh() {
        try {
            allActorsTableModel.setActors(repository.selectActors());
            allDirectorsTableModel.setDirectors(repository.selectDirectors());
            movieTableModel.setMovies(repository.selectMovies());
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        clearForm();
        setDefaultIcon();
        formValid();
        refreshSelection();
    }

    private class ActorExportTransferHandler extends TransferHandler {

        @Override
        public int getSourceActions(JComponent c) {
            return COPY;
        }

        @Override
        public Transferable createTransferable(JComponent c) {
            try {
                return repository.selectActor((int) allActorsTableModel.getValueAt(tbAllActors.getRowSorter().convertRowIndexToModel(tbAllActors.getSelectedRow()), 0));
            } catch (Exception exception) {
                exception.printStackTrace();
            }
            return null;
        }
    }

    private class ActorImportTransferHandler extends TransferHandler {

        @Override
        public boolean canImport(TransferHandler.TransferSupport support) {
            return support.isDataFlavorSupported(Actor.ACTOR_FLAVOR);
        }

        @Override
        public boolean importData(TransferHandler.TransferSupport support) {
            if (selected == null) {
                MessageUtils.showWarningMessage("Action error", "No Movie selected!");
                return false;
            }
            try {
                Actor actor = (Actor) support.getTransferable().getTransferData(Actor.ACTOR_FLAVOR);
                repository.addActor(actor.getId(), selected.getId());
                refreshSelection();
                return true;
            } catch (Exception exception) {
                exception.printStackTrace();
            }
            return false;
        }
    }

    private class DirectorExportTransferHandler extends TransferHandler {
        @Override
        public int getSourceActions(JComponent c) {
            return COPY;
        }

        @Override
        public Transferable createTransferable(JComponent c) {
            try {
                return repository.selectDirector((int) allDirectorsTableModel.getValueAt(tbAllDirectors.getRowSorter().convertRowIndexToModel(tbAllDirectors.getSelectedRow()), 0));
            } catch (Exception exception) {
                exception.printStackTrace();
            }
            return null;
        }
    }

    private class DirectorImportTransferHandler extends TransferHandler {
        @Override
        public boolean canImport(TransferHandler.TransferSupport support) {
            return support.isDataFlavorSupported(Director.DIRECTOR_FLAVOR);
        }

        @Override
        public boolean importData(TransferHandler.TransferSupport support) {
            if (selected == null) {
                MessageUtils.showWarningMessage("Action error", "No Movie selected!");
                return false;
            }
            try {
                Director director = (Director) support.getTransferable().getTransferData(Director.DIRECTOR_FLAVOR);
                repository.addDirector(director.getId(), selected.getId());
                refreshSelection();
                return true;
            } catch (Exception exception) {
                exception.printStackTrace();
            }
            return false;
        }
    }

}



