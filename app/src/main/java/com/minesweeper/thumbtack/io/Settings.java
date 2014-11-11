package com.minesweeper.thumbtack.io;

import java.io.Serializable;

public class Settings extends GenericIO<Settings> implements Serializable {
    public static final int DEFAULT_MINE_COUNT = 10;
    public int numMines;

    public Settings() {
        super("settings");
    }

    @Override
    protected void deserialize(String json) {
        Settings data = gson.fromJson(json, Settings.class);
        this.numMines = data.numMines;
    }

    /*
     * Check if time is in the top five and if so then add it to the file
     */
    public void setMineCount(Integer numMines) {
        this.numMines = numMines;
        write(gson.toJson(this));
    }
}
