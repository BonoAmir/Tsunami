package com.example.android.tusso;

import android.util.Log;

public class Event {


    private String event;

    private Long date ;

    private int alert;


    public  Event(String eve , Long da , int ale)
    {
        event=eve;
        date=da;
        alert=ale;

    }

    public String getEvent() {
        return event;
    }

    public Long getDate() {
        return date;
    }

    public int getAlert() {
        return alert;
    }
}
