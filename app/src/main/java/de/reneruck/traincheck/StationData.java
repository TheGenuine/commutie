package de.reneruck.traincheck;

/**
 * Created by reneruck on 13/11/2015.
 *
 * @author reneruck
 * @since 13/11/2015
 */
public class StationData {

    private String trainCode;
    private String destination;
    private int dueIn;
    private int late;
    private String expDepart;
    private String schDepart;

    public StationData() {
    }

    public StationData(String trainCode, String destination, int dueIn, int late, String expDepart, String schDepart) {
        this.trainCode = trainCode;
        this.destination = destination;
        this.dueIn = dueIn;
        this.late = late;
        this.expDepart = expDepart;
        this.schDepart = schDepart;
    }

    public String getTrainCode() {
        return trainCode;
    }

    public String getDestination() {
        return destination;
    }

    public int getDueIn() {
        return dueIn;
    }

    public int getLate() {
        return late;
    }

    public String getExpDepart() {
        return expDepart;
    }

    public String getSchDepart() {
        return schDepart;
    }

    public void setTrainCode(String trainCode) {
        this.trainCode = trainCode;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public void setDueIn(int dueIn) {
        this.dueIn = dueIn;
    }

    public void setLate(int late) {
        this.late = late;
    }

    public void setExpDepart(String expDepart) {
        this.expDepart = expDepart;
    }

    public void setSchDepart(String schArrival) {
        this.schDepart = schArrival;
    }

    @Override
    public String toString() {
        return "StationData{" +
                "trainCode='" + trainCode + '\'' +
                ", destination='" + destination + '\'' +
                ", dueIn=" + dueIn +
                ", late=" + late +
                ", expDepart='" + expDepart + '\'' +
                ", schArrival='" + schDepart + '\'' +
                '}';
    }
}
