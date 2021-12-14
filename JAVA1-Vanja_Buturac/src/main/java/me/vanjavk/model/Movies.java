package me.vanjavk.model;

import javax.xml.bind.annotation.*;
import java.util.List;

@XmlRootElement(name = "movies")
@XmlAccessorType(XmlAccessType.FIELD)
public class Movies {

    @XmlElement(name = "movie")
    private List<Movie> movies;

    public List<Movie> getMovies() {
        return movies;
    }

    public Movies() {
    }
    public Movies(List<Movie> movies) {
        this.movies = movies;
    }

    @Override
    public String toString() {
        return "Movies{" +
                "Movies=" + movies +
                '}';
    }
}
