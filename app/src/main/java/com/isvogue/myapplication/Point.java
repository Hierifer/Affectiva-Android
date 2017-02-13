package com.isvogue.myapplication;

/**
 * Created by Hu on 2/12/2017.
 */

public class Point{
    float joy;
    float timeStamp;
    public Point(float joy, float timeStamp){
        this.joy = joy;
        this.timeStamp = timeStamp;
    }
    public float getJoy() {
        return joy;
    }
    public void setJoy(float joy) {
        this.joy = joy;
    }
    public float getTimeStamp() {
        return timeStamp;
    }
    public void setTimeStamp(float timeStamp) {
        this.timeStamp = timeStamp;
    }

}
