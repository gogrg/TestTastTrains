package models;

import lombok.Getter;

@Getter
public class Train {
    private int number;
    private final String[] stations;

    public Train(int numberTrain, String listStations) {
        this.number = numberTrain;
        stations = listStations.split(",");
    }
}
