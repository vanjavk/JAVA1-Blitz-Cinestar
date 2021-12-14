package me.vanjavk.singleton;
public class Random extends java.util.Random {
    private Random() { }

    private static class SingletonHelper {
        private static final Random instance = new Random();
    }

    public static Random getInstance() {
        return Random.SingletonHelper.instance;
    }
}

