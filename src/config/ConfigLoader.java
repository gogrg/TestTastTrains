package config;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import models.Train;
import models.PathDiagram;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class ConfigLoader {

    public static class ConfigData {
        public String[] stations;
        public String[] routes;
        public TrainData[] trains;
    }

    public static class TrainData {
        public int number;
        public String route;
    }

    public static ConfigData loadConfig(String filePath) throws IOException {
        String json = new String(Files.readAllBytes(Paths.get(filePath)));
        return new Gson().fromJson(json, ConfigData.class);
    }

    public static PathDiagram createPathDiagram(ConfigData config) {
        // Преобразуем массивы строк в формат, который ожидает PathDiagram
        String stationsStr = String.join(";", config.stations);
        String routesStr = String.join(";\n", config.routes);

        return new PathDiagram(routesStr, stationsStr);
    }

    public static Train[] createTrains(ConfigData config) {
        Train[] trains = new Train[config.trains.length];
        for (int i = 0; i < config.trains.length; i++) {
            trains[i] = new Train(config.trains[i].number, config.trains[i].route);
        }
        return trains;
    }
}