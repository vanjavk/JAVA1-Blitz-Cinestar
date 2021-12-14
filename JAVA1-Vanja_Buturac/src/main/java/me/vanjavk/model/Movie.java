package me.vanjavk.model;

import javax.xml.bind.annotation.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@XmlAccessorType(XmlAccessType.FIELD)
public class Movie {
    private int id;
    private String title;
    private String description;
    @XmlElementWrapper
    @XmlElement(name = "director")
    private List<Director> directors = new ArrayList<>();
    @XmlElementWrapper
    @XmlElement(name = "actor")
    private List<Actor> actors = new ArrayList<>();
    private String genre;
    private int duration;
    private String picturePath;
    private LocalDateTime publishedDate;

    public Movie() {
    }

    public Movie(String title) {
        this.title = title;
    }

    public Movie(int id, String title, String description, List<Director> directors, List<Actor> actors, String genre, int duration, String picturePath, LocalDateTime publishedDate) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.directors = directors;
        this.actors = actors;
        this.genre = genre;
        this.duration = duration;
        this.picturePath = picturePath;
        this.publishedDate = publishedDate;
    }

    public Movie(String title, String description, String genre, int duration, LocalDateTime publishedDate, String picturePath) {
        this.title = title;
        this.description = description;
        this.genre = genre;
        this.duration = duration;
        this.publishedDate = publishedDate;
        this.picturePath = picturePath;
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<Director> getDirectors() {
        return directors;
    }

    public void setDirectors(List<Director> directors) {
        this.directors = directors;
    }

    public List<Actor> getActors() {
        return actors;
    }

    public void setActors(List<Actor> actors) {
        this.actors = actors;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public String getPicturePath() {
        return picturePath;
    }

    public void setPicturePath(String picturePath) {
        this.picturePath = picturePath;
    }

    public LocalDateTime getPublishedDate() {
        return publishedDate;
    }

    public void setPublishedDate(LocalDateTime publishedDate) {
        this.publishedDate = publishedDate;
    }

    @Override
    public String toString() {
        return title;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Movie) {
            return Objects.equals(title.toLowerCase(), (((Movie) obj).title.toLowerCase()));
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(title.toLowerCase());
    }

}
