package me.vanjavk.factory;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

public class UrlConnectionFactory {
    public static HttpURLConnection getHttpUrlConnection(String path, int timeout, String requestMethod) throws IOException {
        URL url = new URL(path);
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setConnectTimeout(timeout);
        con.setReadTimeout(timeout);
        con.setRequestMethod(requestMethod);
        con.setRequestProperty("User-Agent", "Mozilla/5.0");
        con.connect();
        return con;
    }
}
