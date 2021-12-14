package me.vanjavk;

import me.vanjavk.dal.Repository;
import me.vanjavk.dal.RepositoryFactory;
import me.vanjavk.model.Actor;
import me.vanjavk.model.Director;
import me.vanjavk.model.Movie;
import me.vanjavk.parsers.rss.MovieParser;
import me.vanjavk.singleton.Configuration;
import me.vanjavk.singleton.Threads;
import me.vanjavk.utils.MessageUtils;
import org.apache.commons.io.FileUtils;

import javax.swing.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class ImportPanel {
    private JPanel pnlImport;
    private JButton btnDelete;
    private JButton btnFetch;
    private JList lsMovie;
    private JList lsDirector;
    private JList lsActor;

    private Repository repository;

    private final DefaultListModel<Director> directorModel;
    private final DefaultListModel<Actor> actorModel;
    private final DefaultListModel<Movie> movieModel;

    public ImportPanel() {
        directorModel = new DefaultListModel<>();
        actorModel = new DefaultListModel<>();
        movieModel = new DefaultListModel<>();
        btnFetch.addActionListener(e -> deleteAndFetch());
        btnDelete.addActionListener(e -> delete());
        try {
            repository = RepositoryFactory.getRepository();
            refresh();
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        pnlImport.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentShown(ComponentEvent e) {
                refresh();
            }
        });
    }


    private void delete() {
        try {
            repository.clearDatabase();
            if (Files.exists(Paths.get(Configuration.DOWNLOAD_DIR))) {
            FileUtils.cleanDirectory(new File(Configuration.DOWNLOAD_DIR));}
        } catch (IOException exception) {
            MessageUtils.showErrorMessage("Filesystem error", "Cannot delete files.");
            exception.printStackTrace();
        } catch (Exception exception) {
            MessageUtils.showErrorMessage("Database error", "Cannot clear database.");
            exception.printStackTrace();
        }
        refresh();
    }

    private void deleteAndFetch() {
        try {
            delete();
            Threads threads = Threads.getInstance();
            threads.movieThreads.clear();

            long startTime = System.nanoTime();
            MovieParser.parse(repository);
            threads.movieThreads.forEach(x -> {
                try {
                    x.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            });
            long endTime = System.nanoTime();
            long timeElapsed = endTime - startTime;
            System.out.println("Execution time in seconds : " +
                    timeElapsed / 1000000000);
            refresh();
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    private void refreshLists() throws Exception {
        loadDirectors();
        loadActors();
        loadMovies();
    }

    private void loadDirectors() throws Exception {
        repository = RepositoryFactory.getRepository();
        List<Director> directors = repository.selectDirectors();
        directorModel.clear();
        directors.forEach(director -> directorModel.addElement(director));
        lsDirector.setModel(directorModel);
    }

    private void loadActors() throws Exception {
        repository = RepositoryFactory.getRepository();
        List<Actor> actors = repository.selectActors();
        actorModel.clear();
        actors.forEach(actor -> actorModel.addElement(actor));
        lsActor.setModel(actorModel);
    }

    private void loadMovies() throws Exception {
        repository = RepositoryFactory.getRepository();
        List<Movie> movies = repository.selectMovies();
        movieModel.clear();
        movies.forEach(movie -> movieModel.addElement(movie));
        lsMovie.setModel(movieModel);
    }

    public void refresh() {
        try {
            refreshLists();
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    public JPanel getPanel() {
        return pnlImport;
    }
}
