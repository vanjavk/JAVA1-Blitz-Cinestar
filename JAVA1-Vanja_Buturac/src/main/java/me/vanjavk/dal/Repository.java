package me.vanjavk.dal;


import me.vanjavk.model.Actor;
import me.vanjavk.model.Director;
import me.vanjavk.model.Movie;
import me.vanjavk.model.User;

import java.util.List;
import java.util.Optional;

public interface Repository {
    int createUser(User user) throws Exception;
    Optional<User> selectUser(String username) throws Exception;

    Movie createMovie(Movie movie) throws Exception;
    Director createDirector(String name) throws Exception;
    Actor createActor(String name) throws Exception;

    int addDirector(int IDDirector, int IDMovie) throws Exception;
    int addActor(int IDActor, int IDMovie) throws Exception;

    void removeDirector(int IDDirector, int IDMovie) throws Exception;
    void removeActor(int IDActor, int IDMovie) throws Exception;

    List<Movie> selectMovies() throws Exception;
    List<Director> selectDirectors() throws Exception;
    List<Director> selectDirectors(int IDMovie) throws Exception;
    List<Actor> selectActors() throws Exception;
    List<Actor> selectActors(int IDMovie) throws Exception;

    Movie selectMovie(int IDMovie) throws Exception;
    Director selectDirector(int IDDirector) throws Exception;
    Actor selectActor(int IDActor) throws Exception;

    void updateMovie(Movie movie) throws Exception;
    void updateDirector(Director director) throws Exception;
    void updateActor(Actor actor) throws Exception;

    void deleteMovie(int IDMovie) throws Exception;
    void deleteDirector(int IDDirector) throws Exception;
    void deleteActor(int IDActor) throws Exception;

    void clearDatabase() throws Exception;

}
