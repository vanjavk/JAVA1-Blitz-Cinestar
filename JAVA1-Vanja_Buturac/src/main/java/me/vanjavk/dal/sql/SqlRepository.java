package me.vanjavk.dal.sql;

import me.vanjavk.dal.Repository;
import me.vanjavk.model.Actor;
import me.vanjavk.model.Director;
import me.vanjavk.model.Movie;
import me.vanjavk.model.User;
import me.vanjavk.singleton.Configuration;

import javax.sql.DataSource;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Types;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class SqlRepository implements Repository {

    private static final String ID_USER = "IDUser";
    private static final String USERNAME = "Username";
    private static final String PASSWORD = "Password";
    private static final String ADMIN = "Admin";

    private static final String ID_DIRECTOR = "IDDirector";
    private static final String ID_ACTOR = "IDActor";
    private static final String NAME = "Name";


    private static final String ID_MOVIE = "IDMovie";
    private static final String TITLE = "Title";
    private static final String DESCRIPTION = "Description";
    private static final String GENRE = "Genre";
    private static final String DURATION = "Duration";
    private static final String PUBLISHED_DATE = "PublishedDate";
    private static final String PICTURE_PATH = "PicturePath";

    private static final String CREATE_USER = "{ CALL createUser (?,?,?) }";
    private static final String SELECT_USER = "{ CALL selectUser (?) }";

    private static final String CREATE_MOVIE = "{ CALL createMovie (?,?,?,?,?,?,?)  }";
    private static final String CREATE_DIRECTOR = "{ CALL createDirector (?,?) }";
    private static final String CREATE_ACTOR = "{ CALL createActor (?,?) }";

    private static final String ADD_DIRECTOR = "{ CALL addDirector (?,?,?)  }";
    private static final String ADD_ACTOR = "{ CALL addActor (?,?,?)  }";

    private static final String REMOVE_DIRECTOR = "{ CALL removeDirector (?,?)  }";
    private static final String REMOVE_ACTOR = "{ CALL removeActor (?,?)  }";

    private static final String SELECT_ALL_MOVIES = "{ CALL selectMovies }";
    private static final String SELECT_ALL_DIRECTORS = "{ CALL selectDirectors }";
    private static final String SELECT_DIRECTORS = "{ CALL selectDirectors (?) }";
    private static final String SELECT_ALL_ACTORS = "{ CALL selectActors }";
    private static final String SELECT_ACTORS = "{ CALL selectActors (?) }";

    private static final String SELECT_MOVIE = "{ CALL selectMovie (?) }";
    private static final String SELECT_DIRECTOR = "{ CALL selectDirector (?) }";
    private static final String SELECT_ACTOR = "{ CALL selectActor (?) }";

    private static final String UPDATE_MOVIE = "{ CALL updateMovie (?,?,?,?,?,?,?)  }";
    private static final String UPDATE_DIRECTOR = "{ CALL updateDirector (?,?) }";
    private static final String UPDATE_ACTOR = "{ CALL updateActor (?,?) }";

    private static final String DELETE_MOVIE = "{ CALL deleteMovie (?)  }";
    private static final String DELETE_DIRECTOR = "{ CALL deleteDirector (?) }";
    private static final String DELETE_ACTOR = "{ CALL deleteActor (?) }";

    private static final String CLEAR_DATABASE = "{ CALL clearDatabase }";

    @Override
    public int createUser(User user) throws Exception {
        DataSource dataSource = DataSourceSingleton.getInstance();
        try (Connection con = dataSource.getConnection();
             CallableStatement stmt = con.prepareCall(CREATE_USER)) {

            stmt.setString(1, user.getUsername());
            stmt.setString(2, user.getPassword());
            stmt.registerOutParameter(3, Types.INTEGER);

            stmt.executeUpdate();
            return stmt.getInt(3);
        }
    }

    @Override
    public Optional<User> selectUser(String username) throws Exception {
        DataSource dataSource = DataSourceSingleton.getInstance();
        try (Connection con = dataSource.getConnection();
             CallableStatement stmt = con.prepareCall(SELECT_USER)) {

            stmt.setString(1, username);
            try (ResultSet resultSet = stmt.executeQuery()) {
                if (resultSet.next()) {
                    return Optional.of(new User(
                            resultSet.getInt(ID_USER),
                            resultSet.getString(USERNAME),
                            resultSet.getString(PASSWORD),
                            resultSet.getInt(ADMIN)));
                }
            }
        }
        return Optional.empty();
    }


    @Override
    public Movie createMovie(Movie movie) throws Exception {
        DataSource dataSource = DataSourceSingleton.getInstance();
        try (Connection con = dataSource.getConnection();
             CallableStatement stmt = con.prepareCall(CREATE_MOVIE)) {

            stmt.setString(1, movie.getTitle());
            stmt.setString(2, movie.getDescription());
            stmt.setString(3, movie.getGenre());
            stmt.setInt(4, movie.getDuration());
            stmt.setString(5, movie.getPublishedDate().format(Configuration.DATE_FORMATTER_SQL));
            stmt.setString(6, movie.getPicturePath());
            stmt.registerOutParameter(7, Types.INTEGER);
            stmt.executeUpdate();
            movie.setId(stmt.getInt(7));
            return movie;
        }
    }

    @Override
    public Director createDirector(String name) throws Exception {
        DataSource dataSource = DataSourceSingleton.getInstance();
        try (Connection con = dataSource.getConnection();
             CallableStatement stmt = con.prepareCall(CREATE_DIRECTOR)) {

            stmt.setString(1, name);
            stmt.registerOutParameter(2, Types.INTEGER);

            stmt.executeUpdate();
            return new Director(stmt.getInt(2), name);
        }
    }

    @Override
    public Actor createActor(String name) throws Exception {
        DataSource dataSource = DataSourceSingleton.getInstance();
        try (Connection con = dataSource.getConnection();
             CallableStatement stmt = con.prepareCall(CREATE_ACTOR)) {

            stmt.setString(1, name);
            stmt.registerOutParameter(2, Types.INTEGER);

            stmt.executeUpdate();
            return new Actor(stmt.getInt(2), name);
        }
    }

    @Override
    public int addDirector(int IdDirector, int IdMovie) throws Exception {
        DataSource dataSource = DataSourceSingleton.getInstance();
        try (Connection con = dataSource.getConnection();
             CallableStatement stmt = con.prepareCall(ADD_DIRECTOR)) {

            stmt.setInt(1, IdDirector);
            stmt.setInt(2, IdMovie);
            stmt.registerOutParameter(3, Types.INTEGER);

            stmt.executeUpdate();
            return stmt.getInt(3);
        }
    }

    @Override
    public int addActor(int IdActor, int IdMovie) throws Exception {
        DataSource dataSource = DataSourceSingleton.getInstance();
        try (Connection con = dataSource.getConnection();
             CallableStatement stmt = con.prepareCall(ADD_ACTOR)) {

            stmt.setInt(1, IdActor);
            stmt.setInt(2, IdMovie);
            stmt.registerOutParameter(3, Types.INTEGER);

            stmt.executeUpdate();
            return stmt.getInt(3);
        }
    }

    @Override
    public void removeDirector(int IdDirector, int IdMovie) throws Exception {
        DataSource dataSource = DataSourceSingleton.getInstance();
        try (Connection con = dataSource.getConnection();
             CallableStatement stmt = con.prepareCall(REMOVE_DIRECTOR)) {

            stmt.setInt(1, IdDirector);
            stmt.setInt(2, IdMovie);

            stmt.executeUpdate();
        }
    }

    @Override
    public void removeActor(int IdActor, int IdMovie) throws Exception {
        DataSource dataSource = DataSourceSingleton.getInstance();
        try (Connection con = dataSource.getConnection();
             CallableStatement stmt = con.prepareCall(REMOVE_ACTOR)) {

            stmt.setInt(1, IdActor);
            stmt.setInt(2, IdMovie);

            stmt.executeUpdate();
        }
    }

    @Override
    public List<Movie> selectMovies() throws Exception {
        List<Movie> movies = new ArrayList<>();
        DataSource dataSource = DataSourceSingleton.getInstance();
        try (Connection con = dataSource.getConnection();
             CallableStatement stmt = con.prepareCall(SELECT_ALL_MOVIES);
             ResultSet resultSet = stmt.executeQuery()) {

            while (resultSet.next()) {
                movies.add(selectMovie(resultSet.getInt(ID_MOVIE)));
            }
        }
        return movies;
    }

    @Override
    public List<Director> selectDirectors() throws Exception {
        List<Director> directors = new ArrayList<>();
        DataSource dataSource = DataSourceSingleton.getInstance();
        try (Connection con = dataSource.getConnection();
             CallableStatement stmt = con.prepareCall(SELECT_ALL_DIRECTORS);
             ResultSet resultSet = stmt.executeQuery()) {

            while (resultSet.next()) {
                directors.add(new Director(
                        resultSet.getInt(ID_DIRECTOR),
                        resultSet.getString(NAME)));
            }
        }
        return directors;
    }

    @Override
    public List<Director> selectDirectors(int IDMovie) throws Exception {
        List<Director> directors = new ArrayList<>();
        DataSource dataSource = DataSourceSingleton.getInstance();
        try (Connection con = dataSource.getConnection();
             CallableStatement stmt = con.prepareCall(SELECT_DIRECTORS)) {

            stmt.setInt(1, IDMovie);
            try (ResultSet resultSet = stmt.executeQuery()) {

                while (resultSet.next()) {
                    directors.add(new Director(
                            resultSet.getInt(ID_DIRECTOR),
                            resultSet.getString(NAME)));
                }
            }
        }
        return directors;
    }

    @Override
    public List<Actor> selectActors() throws Exception {
        List<Actor> actors = new ArrayList<>();
        DataSource dataSource = DataSourceSingleton.getInstance();
        try (Connection con = dataSource.getConnection();
             CallableStatement stmt = con.prepareCall(SELECT_ALL_ACTORS);
             ResultSet resultSet = stmt.executeQuery()) {

            while (resultSet.next()) {
                actors.add(new Actor(
                        resultSet.getInt(ID_ACTOR),
                        resultSet.getString(NAME)));
            }
        }
        return actors;
    }

    @Override
    public List<Actor> selectActors(int IDMovie) throws Exception {
        List<Actor> actors = new ArrayList<>();
        DataSource dataSource = DataSourceSingleton.getInstance();
        try (Connection con = dataSource.getConnection();
             CallableStatement stmt = con.prepareCall(SELECT_ACTORS)) {

            stmt.setInt(1, IDMovie);
            try (ResultSet resultSet = stmt.executeQuery()) {

                while (resultSet.next()) {
                    actors.add(new Actor(
                            resultSet.getInt(ID_ACTOR),
                            resultSet.getString(NAME)));
                }
            }
        }
        return actors;
    }

    @Override
    public Movie selectMovie(int IDMovie) throws Exception {
        DataSource dataSource = DataSourceSingleton.getInstance();
        try (Connection con = dataSource.getConnection();
             CallableStatement stmt = con.prepareCall(SELECT_MOVIE)) {

            stmt.setInt(1, IDMovie);
            try (ResultSet resultSet = stmt.executeQuery()) {
                resultSet.next();
                return new Movie(
                        resultSet.getInt(ID_MOVIE),
                        resultSet.getString(TITLE),
                        resultSet.getString(DESCRIPTION),
                        selectDirectors(IDMovie),
                        selectActors(IDMovie),
                        resultSet.getString(GENRE),
                        resultSet.getInt(DURATION),
                        resultSet.getString(PICTURE_PATH),
                        LocalDateTime.parse(resultSet.getString(PUBLISHED_DATE), Configuration.DATE_FORMATTER_SQL)
                );
            }
        }
    }

    @Override
    public Director selectDirector(int IDDirector) throws Exception {
        DataSource dataSource = DataSourceSingleton.getInstance();
        try (Connection con = dataSource.getConnection();
             CallableStatement stmt = con.prepareCall(SELECT_DIRECTOR)) {

            stmt.setInt(1, IDDirector);
            try (ResultSet resultSet = stmt.executeQuery()) {
                resultSet.next();
                return new Director(
                        resultSet.getInt(ID_DIRECTOR),
                        resultSet.getString(NAME));

            }
        }
    }

    @Override
    public Actor selectActor(int IDActor) throws Exception {
        DataSource dataSource = DataSourceSingleton.getInstance();
        try (Connection con = dataSource.getConnection();
             CallableStatement stmt = con.prepareCall(SELECT_ACTOR)) {

            stmt.setInt(1, IDActor);
            try (ResultSet resultSet = stmt.executeQuery()) {
                resultSet.next();
                return new Actor(
                        resultSet.getInt(ID_ACTOR),
                        resultSet.getString(NAME));
            }
        }
    }

    @Override
    public void updateMovie(Movie movie) throws Exception {
        DataSource dataSource = DataSourceSingleton.getInstance();
        try (Connection con = dataSource.getConnection();
             CallableStatement stmt = con.prepareCall(UPDATE_MOVIE)) {

            stmt.setInt(1, movie.getId());
            stmt.setString(2, movie.getTitle());
            stmt.setString(3, movie.getDescription());
            stmt.setString(4, movie.getGenre());
            stmt.setInt(5, movie.getDuration());
            stmt.setString(6, movie.getPublishedDate().format(Configuration.DATE_FORMATTER_SQL));
            stmt.setString(7, movie.getPicturePath());
            stmt.executeUpdate();
        }
    }

    @Override
    public void updateDirector(Director director) throws Exception {
        DataSource dataSource = DataSourceSingleton.getInstance();
        try (Connection con = dataSource.getConnection();
             CallableStatement stmt = con.prepareCall(UPDATE_DIRECTOR)) {

            stmt.setInt(1, director.getId());
            stmt.setString(2, director.getName());
            stmt.executeUpdate();
        }
    }

    @Override
    public void updateActor(Actor actor) throws Exception {
        DataSource dataSource = DataSourceSingleton.getInstance();
        try (Connection con = dataSource.getConnection();
             CallableStatement stmt = con.prepareCall(UPDATE_ACTOR)) {

            stmt.setInt(1, actor.getId());
            stmt.setString(2, actor.getName());
            stmt.executeUpdate();
        }
    }

    @Override
    public void deleteMovie(int IDMovie) throws Exception {
        DataSource dataSource = DataSourceSingleton.getInstance();
        try (Connection con = dataSource.getConnection();
             CallableStatement stmt = con.prepareCall(DELETE_MOVIE)) {

            stmt.setInt(1, IDMovie);
            stmt.executeUpdate();

        }
    }

    @Override
    public void deleteDirector(int IDDirector) throws Exception {
        DataSource dataSource = DataSourceSingleton.getInstance();
        try (Connection con = dataSource.getConnection();
             CallableStatement stmt = con.prepareCall(DELETE_DIRECTOR)) {

            stmt.setInt(1, IDDirector);
            stmt.executeUpdate();

        }
    }

    @Override
    public void deleteActor(int IDActor) throws Exception {
        DataSource dataSource = DataSourceSingleton.getInstance();
        try (Connection con = dataSource.getConnection();
             CallableStatement stmt = con.prepareCall(DELETE_ACTOR)) {

            stmt.setInt(1, IDActor);
            stmt.executeUpdate();

        }
    }

    @Override
    public void clearDatabase() throws Exception {
        DataSource dataSource = DataSourceSingleton.getInstance();
        try (Connection con = dataSource.getConnection();
             CallableStatement stmt = con.prepareCall(CLEAR_DATABASE)) {

            stmt.executeUpdate();
        }
    }
}
