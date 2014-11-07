package com.minesweeper.thumbtack;

import java.io.Serializable;

public class BestTime implements Serializable {

    public BestTime(Integer time, String name, String readableTime) {
        this.time = time;
        this.name = name;
        this.readableTime = readableTime;
    }

    public Integer time;
    public String name;
    public String readableTime;
}
