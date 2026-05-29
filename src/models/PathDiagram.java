package models;

import lombok.AllArgsConstructor;
import lombok.Getter;

import static java.lang.System.exit;

@Getter
public class PathDiagram {
    private int[][] routes;
    private Station[] stations;

    public PathDiagram(String routesStr, String stationsStr) {
        fillStations(stationsStr);
        fillRoutes(routesStr);

        if (routes.length != stations.length) {
            System.out.println("Ошибка! Количество станций не соответствует сетке путей");
            exit(1);
        }
    }

    private void fillStations(String stationsStr) {
        String[] extractStringStations = stationsStr.split(";");
        stations = new Station[extractStringStations.length];

        for (int i = 0; i < stations.length; i++) {
            stations[i] = new Station(extractStringStations[i].trim());
        }
    }

    private void fillRoutes(String routesStr) {
        String[] jsonRows = routesStr.split(";\\n?");

        routes = new int[jsonRows.length][];

        for (int numberRow = 0; numberRow < jsonRows.length; numberRow++) {
            String row = jsonRows[numberRow].trim();
            if (row.isEmpty()) continue;

            String[] elementsRow = row.split("\\s+"); // разбиваем по пробелам

            int[] lengthRoutes = new int[elementsRow.length];
            for (int i = 0; i < lengthRoutes.length; i++) {
                int value = Integer.parseInt(elementsRow[i]);
                if (value < 0) {
                    System.out.println("Ошибка! Некорректная длина пути: " + value);
                    exit(3);
                }
                lengthRoutes[i] = value;
            }

            routes[numberRow] = lengthRoutes;
        }
    }

    public void printDiagram() {
        for (int i = 0; i < routes.length; i++) {
            for (int j = 0; j < routes[i].length; j++) {
                System.out.print(routes[i][j] + " ");
            }
            System.out.println();
        }
    }

    public int getLengthRoute(String stationA, String stationB) {
        return routes[getNumberStation(stationA)][getNumberStation(stationB)];
    }

    public int getNumberStation(String nameStation) {
        for (int i = 0; i < stations.length; i++) {
            if (stations[i].getName().equals(nameStation)) {
                return i;
            }
        }
        return -1;
    }

    @Getter
    @AllArgsConstructor
    public static class Station {
        private String name;
        private int size;

        public Station(String inputData) {
            String[] inputSplit = inputData.split(" :");
            name = inputSplit[0].trim();
            size = Integer.valueOf(inputSplit[1].trim());
        }
    }
}
