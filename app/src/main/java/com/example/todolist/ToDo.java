package com.example.todolist;

import androidx.annotation.NonNull;

import com.google.android.gms.maps.model.LatLng;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.UUID;

/*
This is a serializable class to store information about to-dos.
*/

public class ToDo implements Serializable {
    private final UUID uuid = UUID.randomUUID(); // Implementation recommended by https://www.baeldung.com/java-uuid
    private String text;
    private boolean done;
    private Date date;
    private float maxGrade;
    private float gradeReceived;
    private String locationName;
    /*
    The latitude and longitude are Strings even though they should be double. This is because while saving location latitude and longitude values,
    the computer performs approximations with the double values which results in changing the latitude and longitude position. As a result,
    the correct position is not return back. So, these have been created as String and their values are converted from String to Double and vice-versa.
     */
    private String latitude;
    private String longitude;
    private final ArrayList<String> tags = new ArrayList<>();


    public ToDo(String text) {
        this.text = text;
    }

    // all arg constructor
    public ToDo(String text, boolean done, Date date) {
        this.text = text;
        this.done = done;
        this.date = date;
    }

    // getters and setters
    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public boolean isDone() {
        return done;
    }

    public void setDone(boolean done) {
        this.done = done;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public float getMaxGrade() { return maxGrade; }

    public void setMaxGrade(float maxGrade) { this.maxGrade = maxGrade; }

    public float getGradeReceived() { return gradeReceived; }

    public void setGradeReceived(float gradeReceived) { this.gradeReceived = gradeReceived; }

    public String getLocationName() {
        return locationName;
    }

    public void setLocationName(String locationName) {
        this.locationName = locationName;
    }

    public String  getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public boolean addTag(String tag) {
        if (!tag.equals("")) {
            tags.add(tag);
        }
        else
            return false;
        return true;
    }

    public boolean removeTag(String tag) {
        return tags.remove(tag);
    }

    public ArrayList<String> getTags() {
        return tags;
    }

    @Override
    public String toString() {
        return "ToDo{" +
                "uuid=" + uuid +
                ", text='" + text + '\'' +
                ", done=" + done +
                ", date=" + date +
                ", maxGrade=" + maxGrade +
                ", gradeReceived=" + gradeReceived +
                ", locationName='" + locationName + '\'' +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                ", tags=" + tags +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        return getClass() == o.getClass() && uuid.equals(((ToDo) o).uuid);
    }
    public static Comparator<ToDo> DueDateAscComparator= (t1, t2) -> {
        if(t1.getDate()==null)
            return 1;
        else if(t2.getDate()==null)
            return -1;
        else return t1.getDate().compareTo(t2.getDate());
    };
    public static Comparator<ToDo> DueDateDescComparator= (t1, t2) -> {
        if(t1.getDate()==null)
            return 1;
        else if(t2.getDate()==null)
            return -1;
        else return t2.getDate().compareTo(t1.getDate());
    };
    public static Comparator<ToDo> TotalMarksComparator= (t1, t2) -> {
        if (t2.getMaxGrade() - t1.getMaxGrade() > 0)
            return 1;
        else if ((t2.getMaxGrade() - t1.getMaxGrade() < 0))
            return -1;
        else { // if the total Grades are the same, then sort Ascendingly
            if(t1.getDate()==null)
                return 1;
            else if(t2.getDate()==null)
                return -1;
            else return t1.getDate().compareTo(t2.getDate());
        }
    };
}

