package sensors.example;

import com.google.gson.Gson;

import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class Configuration {

    final static String DEFAULT_FILE_NAME = "config.json";

    private Map<String, ArrayList<?>> config;

    public Configuration() {
        try (FileReader reader = new FileReader(DEFAULT_FILE_NAME)) {
            Gson gson = new Gson();
            config = gson.fromJson(reader, Map.class);
//            var xxx = getSources(); // @todo - usunąć
//            System.out.println(xxx);
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    public List<?> getSources() {

        return config.get("sources");
    }

    public List<?> getListeners() {
        return config.get("listeners");
    }
    public List<List<String>> getRoutes() {
        var cfg =  config.get("routes");
        return (List<List<String>>) cfg;
    }

}
