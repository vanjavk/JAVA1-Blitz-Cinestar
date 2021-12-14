package me.vanjavk.singleton;

import java.util.ArrayList;
import java.util.List;

public class Threads {

    public List<Thread> movieThreads = new ArrayList<>();

    private static class SingletonHelper {
        private static final Threads instance = new Threads();
    }

    public static Threads getInstance() {
        return Threads.SingletonHelper.instance;
    }

}
