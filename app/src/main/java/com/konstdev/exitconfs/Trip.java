package com.konstdev.exitconfs;

public class Trip {
    private String departureTime;
    private String returnTime;
    private String destination;
    private String status;

    public Trip(String departureTime, String returnTime, String destination, String status) {
        this.departureTime = departureTime;
        this.returnTime = returnTime;
        this.destination = destination;
        this.status = status;
    }

    public String getDepartureTime() {
        return departureTime;
    }

    public String getReturnTime() {
        return returnTime;
    }

    public String getDestination() {
        return destination;
    }

    public String getStatus() {
        return status;
    }
}
