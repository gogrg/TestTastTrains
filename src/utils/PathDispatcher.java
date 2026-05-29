package utils;

import lombok.AllArgsConstructor;
import lombok.Getter;
import models.PathDiagram;
import models.Train;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

import static java.lang.System.exit;

public class PathDispatcher {
    private PathDiagram pathDiagram;
    private Train[] trains;
    private AtomicInteger amountAccident;

    private Map<String, List<TrainInPath>> pathsMap = new HashMap<>();

    public PathDispatcher(PathDiagram pathDiagram, Train[] trains, AtomicInteger amountAccident) {
        this.pathDiagram = pathDiagram;
        this.trains = trains;
        this.amountAccident = amountAccident;

        checkCorrectness();
        fillPath();
        sort();

    }

    public void checkConflict() {
        //проход по всем путям
        for (List<TrainInPath> dataPath :  pathsMap.values()) {
            //проход по каждому проезду поезда на пути
            for (int i = 0; i < dataPath.size() - 1; i++) {
                //сверка со следующими поездами
                for (int j = i + 1; j < dataPath.size(); j++) {
                    //Если поезда движутся по одному пути в разных направлениях - происходит авария
                    if (dataPath.get(i).getEndTime() > dataPath.get(j).getStartTime() &&
                        !dataPath.get(i).getDirection().equals(dataPath.get(j).getDirection())) {

                        System.out.println("Авария! На пути " + dataPath.get(i).getDirection() + " столкнулись поезда " + dataPath.get(i).getTrainNumber() + " и " + dataPath.get(j).getTrainNumber());
                        System.out.println("Временной промежуток: " + String.valueOf(dataPath.get(j).getStartTime()) + "-" + String.valueOf(dataPath.get(i).getEndTime()));
                        System.out.println();
                        amountAccident.incrementAndGet();
                    }
                    if (dataPath.get(i).getEndTime() < dataPath.get(j).getStartTime()) {
                        continue;
                    }
                }
            }
        }
    }

    private void fillPath() {
        int routes[][] = pathDiagram.getRoutes();

        //составляет список всех путей, которыми идут поезда
        for (int i = 0; i < routes.length - 1; i++) {
            for (int j = i + 1; j < routes[i].length; j++) {
                if (routes[i][j] == 0) continue;

                pathsMap.put(pathDiagram.getStations()[i].getName() + "-" + pathDiagram.getStations()[j].getName(), new ArrayList<>());
            }
        }

        //надо закинуть данные о поездах, времени и направлении, когда они находятся на пути
        for (Train train : trains) {
            int curTime = 0;

            String[] curTrainStationNames = train.getStations();

            for (int i = 0; i < curTrainStationNames.length - 1; i++) {
                String key = pathsMap.containsKey(curTrainStationNames[i] + "-" + curTrainStationNames[i+1]) ?
                        curTrainStationNames[i] + "-" + curTrainStationNames[i+1] : curTrainStationNames[i + 1] + "-" + curTrainStationNames[i];

                int timeInPath = pathDiagram.getLengthRoute(curTrainStationNames[i], curTrainStationNames[i+1]);
                String direction = curTrainStationNames[i] + "-" + curTrainStationNames[i + 1];

                putInMap(key, train.getNumber(), curTime, curTime + timeInPath, direction);

                curTime += timeInPath;
            }
        }
    }

    private void putInMap(String pathName, int trainNumber, int startTime, int endTime, String direction) {
        try {
            ArrayList<TrainInPath> currentVisitedPathData = (ArrayList<TrainInPath>) pathsMap.get(pathName);
            currentVisitedPathData.add(new TrainInPath(trainNumber, startTime, endTime, direction));
        } catch (NullPointerException e) {
            System.out.println("При попытке добавить данные о поездах на пути не удалось получить путь с заданным именем");
            e.printStackTrace();
            exit(4);
        }
    }

    private void checkCorrectness() {
        for (Train train : trains) {
            String[] routeTrain = train.getStations();

            for (int i = 0; i < routeTrain.length - 1; i++) {
                if (pathDiagram.getLengthRoute(routeTrain[i], routeTrain[i + 1]) == 0) {
                    System.out.println("Ошибка! Для поезда задан невозможный путь(нет прямого соединения между станциями)");
                    System.out.println("Поезд: " + train.getNumber() + "; Перегон: " + routeTrain[i] + "->" + routeTrain[i + 1]);
                    exit(2);
                }
            }
        }
    }

    private void sort() {
        for (List<TrainInPath> list : pathsMap.values()) {
            list.sort(Comparator.comparingInt(TrainInPath::getStartTime));
        }
    }

    @AllArgsConstructor
    @Getter
    private static class TrainInPath {
        private int trainNumber;
        private int startTime;
        private int endTime;
        private String direction;
    }
}
