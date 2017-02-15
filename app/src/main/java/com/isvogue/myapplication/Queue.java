package com.isvogue.myapplication;

import java.util.LinkedList;

/**
 *  Data: Queue for Point(Float joy, Float TimeStamp)
 */
public class Queue {
    private LinkedList<Point> list;

    // Queue constructor
    public Queue()
    {
        list = new LinkedList();
    }

    public boolean isEmpty()
    {
        return (list.size() == 0);
    }

    public void enqueue(Point item)
    {
        list.add(item);
    }

    public Point dequeue()
    {
        Point item = list.get(1);
        list.remove(1);
        return item;
    }

    public int size()
    {
        return list.size();
    }

    //Override
    //grab first timeStamp on Queue
    public float peek()
    {
        return list.get(1).timeStamp;
    }

    public float getMean(){
        float sum = 0;
        for(int i = 1; i < list.size(); i++){
            sum += list.get(i).joy;
        }
        return sum/list.size();
    }
}
