package me.vanjavk.parsers.rss;

import com.microsoft.sqlserver.jdbc.SQLServerException;
import me.vanjavk.dal.Repository;
import me.vanjavk.factory.ParserFactory;
import me.vanjavk.factory.UrlConnectionFactory;
import me.vanjavk.model.Actor;
import me.vanjavk.model.Director;
import me.vanjavk.model.Movie;
import me.vanjavk.singleton.Configuration;
import me.vanjavk.singleton.Random;
import me.vanjavk.singleton.Threads;
import me.vanjavk.utils.FileUtilities;
import org.jsoup.Jsoup;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.events.Characters;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.HttpURLConnection;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.*;

public class MovieParser {

    private static final String RSS_URL = "https://www.blitz-cinestar.hr/rss.aspx?najava=1";
    private static final int TIMEOUT = 10000;
    private static final String REQUEST_METHOD = "GET";

    public static HashSet<Movie> parse(Repository repository) throws Exception {
        HashSet<Movie> movies = new HashSet<>();
        HashSet<Actor> actorsAll = new HashSet<>();
        HashSet<Director> directorsAll = new HashSet<>();
        HttpURLConnection con = UrlConnectionFactory.getHttpUrlConnection(RSS_URL, TIMEOUT, REQUEST_METHOD);
        XMLEventReader reader = ParserFactory.createXMLEventReader(con.getInputStream());
        Optional<TagType> tagType = Optional.empty();
        Movie movie = null;
        StartElement startElement;
        EndElement endElement;
        while (reader.hasNext()) {
            XMLEvent event = reader.nextEvent();
            switch (event.getEventType()) {
                case XMLStreamConstants.START_ELEMENT -> {
                    startElement = event.asStartElement();
                    String qName = startElement.getName().getLocalPart();
                    tagType = TagType.from(qName);
                }
                case XMLStreamConstants.END_ELEMENT -> {
                    endElement = event.asEndElement();
                    String qName = endElement.getName().getLocalPart();
                    tagType = TagType.from(qName);
                    if (tagType.isPresent()) {
                        switch (tagType.get()) {
                            case ITEM -> {
                                Threads threads = Threads.getInstance();
                                Movie movieForThread = movie;
                                threads.movieThreads.add(new Thread(() -> {
                                            try {
                                                Movie finalMovie;
                                                if (movies.contains(movieForThread)) {
                                                    new File(movieForThread.getPicturePath()).delete();
                                                    return;
                                                } else {
                                                    movies.add(movieForThread);
                                                }
                                                finalMovie = repository.createMovie(movieForThread);
                                                for (Actor a : movieForThread.getActors()) {
                                                    if (actorsAll.contains(a)) {
                                                        repository.addActor(actorsAll.stream().filter(s -> s.getName().equals(a.getName())).findFirst().get().getId(), finalMovie.getId());
                                                    }else{
                                                    Actor actor = repository.createActor(a.getName());
                                                    repository.addActor(actor.getId(), finalMovie.getId());
                                                    actorsAll.add(actor);
                                                    }
                                                }
                                                for (Director d : movieForThread.getDirectors()) {
                                                    if (directorsAll.contains(d)) {
                                                        repository.addDirector(directorsAll.stream().filter(s -> s.getName().equals(d.getName())).findFirst().get().getId(), finalMovie.getId());
                                                    }else{
                                                    Director director = repository.createDirector(d.getName());
                                                    repository.addDirector(director.getId(), finalMovie.getId());
                                                    directorsAll.add(director);
                                                    }
                                                }
                                            } catch (SQLServerException exception) {
                                                new File(movieForThread.getPicturePath()).delete();
                                            } catch (Exception exception) {
                                                System.out.println("Error while importing movies to database: " + movieForThread.getTitle());
                                            }
                                        })
                                );
                                threads.movieThreads.get(threads.movieThreads.size() - 1).start();
                            }
                        }
                    }
                }
                case XMLStreamConstants.CHARACTERS -> {
                    if (tagType.isPresent()) {
                        Characters characters = event.asCharacters();
                        String data = characters.getData().trim();

                        switch (tagType.get()) {
                            case ITEM -> {
                                movie = new Movie();
                            }
                            case PUB_DATE -> {
                                movie.setPublishedDate(LocalDateTime.parse(data, Configuration.DATE_FORMATTER));
                            }
                            case DESCRIPTION -> {
                                if (movie != null && !data.isEmpty()) {
                                    movie.setDescription(Jsoup.parse(data).text());
                                }
                            }
                            case ORIGANAZIV -> {
                                if (movie != null && !data.isEmpty()) {
                                    movie.setTitle(data.replace(" IMAX", "")
                                            .replace(" 3D", "")
                                            .replace(" 4DX", "")
                                            .trim());
                                }
                            }
                            case TRAJANJE -> {
                                if (movie != null && !data.isEmpty()) {
                                    movie.setDuration(Integer.valueOf(data));
                                }
                            }
                            case ZANR -> {
                                if (movie != null && !data.isEmpty()) {
                                    movie.setGenre(data);
                                }
                            }
                            case PLAKAT -> {
                                if (movie != null && !data.isEmpty()) {
                                    String internetName = data.substring(data.lastIndexOf("/") + 1);
                                    String pictureName = internetName.substring(0, internetName.lastIndexOf(".")) + Math.abs(Random.getInstance().nextInt()) + internetName.substring(internetName.lastIndexOf("."));
                                    String localPicturePath = Configuration.DOWNLOAD_DIR + File.separator + pictureName;
                                    if (Files.exists(Paths.get(localPicturePath))) {
                                        movie.setPicturePath(localPicturePath);
                                    }
                                    try {
                                        FileUtilities.copyFromUrl("https" + data.substring(4), localPicturePath);
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                    movie.setPicturePath(localPicturePath);
                                }
                            }
                            case GLUMCI -> {
                                if (movie != null && !data.isEmpty()) {
                                    List<Actor> actors = makeListWith(Actor.class, data);
                                    movie.setActors(actors);
                                }
                            }
                            case REDATELJ -> {
                                if (movie != null && !data.isEmpty()) {
                                    List<Director> directors = makeListWith(Director.class, data);
                                    movie.setDirectors(directors);
                                }
                            }
                        }
                    }
                }
            }
        }
        return movies;
    }

    private static <T> List<T> makeListWith(Class<T> className, String data) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        List<T> entities = new ArrayList<>() {
            @Override
            public String toString() {
                StringJoiner sj = new StringJoiner(", ");
                this.forEach(director -> sj.add(director.toString()));
                return sj.toString();
            }
        };
        String[] str = data.split(", ");
        for (String name : str) {
            if (name.contains(",")) {
                String[] str1 = name.split(",");
                for (String name1 : str1) {
                    entities.add(className.getConstructor(String.class).newInstance(name1.trim()));
                }
            } else {
                entities.add(className.getConstructor(String.class).newInstance(name.trim()));
            }
        }

        return entities;
    }

    private enum TagType {

        ITEM("item"),
        TITLE("title"),
        PUB_DATE("pubDate"),
        DESCRIPTION("description"),
        ORIGANAZIV("orignaziv"),
        REDATELJ("redatelj"),
        GLUMCI("glumci"),
        TRAJANJE("trajanje"),
        ZANR("zanr"),
        PLAKAT("plakat"),
        LINK("link"),
        SLIKE("slike"),
        TRAILER("trailer"),
        POCETAK("pocetak");

        private final String name;

        TagType(String name) {
            this.name = name;
        }

        private static Optional<TagType> from(String name) {
            for (TagType value : values()) {
                if (value.name.equals(name)) {
                    return Optional.of(value);
                }
            }
            return Optional.empty();
        }
    }
}
