package com.konstdev.exitconfs;

import java.io.Serializable;

public class Trip implements Serializable {
    public boolean confirmed;

    public boolean isConfirmed() {
        return confirmed;
    }

    public String getId() {
        return id;
    }

    public String getExitDate() {
        return exitDate;
    }

    public String getExitTime() {
        return exitTime;
    }

    public String getGoingTo() {
        return goingTo;
    }

    public String getGroup() {
        return group;
    }

    public String getMadrich_name() {
        return madrich_name;
    }

    public String getReturnDate() {
        return returnDate;
    }

    public String getReturnTime() {
        return returnTime;
    }

    public String getStudents_ids() {
        return students_ids;
    }

    public String getStudents_names() {
        return students_names;
    }

    public String id,exitDate, exitTime, goingTo, group, madrich_name, returnDate, returnTime,
            students_ids, students_names, confirmations;


    public Trip(String id,boolean confirmed, String exitDate, String exitTime, String goingTo,
                          String group, String madrich_name, String returnDate,
                          String returnTime, String students_ids, String students_names, String confirmations) {
        this.id = id;
        this.confirmed = confirmed;
        this.exitDate = exitDate;
        this.exitTime = exitTime;
        this.goingTo = goingTo;
        this.group = group;
        this.madrich_name = madrich_name;
        this.returnDate = returnDate;
        this.returnTime = returnTime;
        this.students_ids = students_ids;
        this.students_names = students_names;
        this.confirmations = confirmations;
    }

    public Trip(){

    }

}
