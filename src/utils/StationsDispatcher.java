package utils;

import lombok.AllArgsConstructor;
import lombok.Getter;
import models.PathDiagram;
import models.Train;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import static java.lang.System.exit;

public class StationsDispatcher {
    private PathDiagram pathDiagram;
    private Train[] trains;
    private AtomicInteger amountAccident;

    private Map<String, List<TrainInStation>> visitsStations = new HashMap<>();

    public StationsDispatcher(PathDiagram pathDiagram, Train[] trains, AtomicInteger amountAccident) {
        this.pathDiagram = pathDiagram;
        this.trains = trains;
        this.amountAccident = amountAccident;

        fillVisitsStation();
    }

    public void checkConflict() {
        PathDiagram.Station[] stations = pathDiagram.getStations();

        for (PathDiagram.Station curStation : stations) {
            List<TrainInStation> list = visitsStations.get(curStation.getName());

            if (list == null) { continue; }

            Map<Integer, Integer> countTrainsInSomeTime = new HashMap<>();
            for (TrainInStation curTrain : list) {
                int time = curTrain.timeVisitStation;
                countTrainsInSomeTime.put(time, countTrainsInSomeTime.getOrDefault(time, 0) + 1);
            }

            for (int time : countTrainsInSomeTime.keySet()) {
                int amountTrains = countTrainsInSomeTime.get(time);

                if (amountTrains > curStation.getSize()) {
                    System.out.println("Авария! На станции " + curStation.getName() + " в " +
                            String.valueOf(time) + " условных единиц времени одновременно " +
                            String.valueOf(amountTrains) + " поезда(ов)");
                    System.out.println("Вместимость станции : " + curStation.getSize());
                    System.out.println();
                    amountAccident.incrementAndGet();
                }
            }
        }
    }

    private void fillVisitsStation() {
        for (PathDiagram.Station station : pathDiagram.getStations()) {
            visitsStations.put(station.getName(), new ArrayList<>());
        }

        for (Train train : trains) {
            int currentTime = 0;

            putInMap(train.getStations()[0], train.getNumber(), currentTime);

            for (int i = 1; i < train.getStations().length; i++) {
                currentTime += pathDiagram.getLengthRoute(train.getStations()[i - 1], train.getStations()[i]);
                putInMap(train.getStations()[i], train.getNumber(), currentTime);
            }
        }
    }

    private void putInMap(String nameStation, int trainNumber, int currentTime) {
        ArrayList<TrainInStation> currentVisitedStation = (ArrayList<TrainInStation>) visitsStations.get(nameStation);
        currentVisitedStation.add(new TrainInStation(trainNumber, currentTime));
    }



    @Getter
    @AllArgsConstructor
    private static class TrainInStation {
        private int numberTrain;
        private int timeVisitStation;
    }
}
