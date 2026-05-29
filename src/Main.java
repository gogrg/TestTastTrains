import config.ConfigLoader;
import config.ConfigLoader.ConfigData;
import models.PathDiagram;
import models.Train;
import utils.PathDispatcher;
import utils.StationsDispatcher;

import java.util.concurrent.atomic.AtomicInteger;

public class Main {
    public static void main(String[] args) {
        AtomicInteger amountAccident = new AtomicInteger(0);
        try {
            ConfigData config = ConfigLoader.loadConfig("config/TestInputData1.json");

            PathDiagram pathDiagram = ConfigLoader.createPathDiagram(config);
            Train[] trains = ConfigLoader.createTrains(config);


            StationsDispatcher stationsDispatcher = new StationsDispatcher(pathDiagram, trains, amountAccident);
            stationsDispatcher.checkConflict();

            PathDispatcher pathDispatcher = new PathDispatcher(pathDiagram, trains, amountAccident);
            pathDispatcher.checkConflict();

        } catch (Exception e) {
            System.err.println("Ошибка: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
        System.out.println("Проверка завершена. Аварий: " + String.valueOf(amountAccident));
    }
}