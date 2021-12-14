package me.vanjavk.singleton;

import java.time.format.DateTimeFormatter;

public class Configuration {
    public static final String DOWNLOAD_DIR = "movies";
    public static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.RFC_1123_DATE_TIME;
    public static final DateTimeFormatter DATE_FORMATTER_SQL = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    public static final String SERVER_NAME = "192.168.100.174";
    public static final String DATABASE_NAME = "JAVAPROJ";
    public static final String USER = "sa";
    public static final String PASSWORD = "IlIrVHKadE6I16oMlRuU";

    private Configuration() { }

}

