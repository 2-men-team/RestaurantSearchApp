package project;

public final class Config {
    private Config() { }

    public static final String STOP_WORDS_PATH = "data/stop_words_ru.txt";
    public static final String DATA_SET_PATH   = "data/restaurants.csv";
    public static final String DB_PATH         = "data/db_ru.ser";

    public static final int PORT = 9991;
    public static final String HOST = "localhost";
}
