package com.minesweeper.thumbtack.io;

import android.util.Log;

import com.google.gson.reflect.TypeToken;
import com.minesweeper.thumbtack.models.BestTime;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class BestTimes extends GenericIO<BestTime> {
    private transient static final String TAG = BestTimes.class.getName();
    public transient static final int TOTAL_SCORES_TO_STORE = 5;
    private static final String fileName = "best_times";
    private Settings settings;

    public static HashMap<Integer, List<BestTime>> data;
    public BestTimes() {
        super(fileName);
        this.settings = new Settings();
    }

    /*
     * Simple helper function to map readable time to seconds
     */
    public Integer toSeconds(String displayedTime) {
        String[] parts = displayedTime.split(":");
        int minutes = Integer.valueOf(parts[0]);
        int seconds = Integer.valueOf(parts[1]);
        return (minutes * 60) + seconds;
    }

    /*
     * Iterate through list and get index of best time
     */
    public int getBestTimeIndex(Integer numMines, Integer time) {
        if (data == null) {
            return 0;
        }

        List<BestTime> times = data.get(numMines);

        int index = 0;
        try {
            fetch();
            if (times == null || times.size() == 0) {
                return 0;
            }

            int timeValue = time.intValue();
            for (BestTime t : times) {
                if (timeValue < t.time.intValue()) {
                    break;
                }
                index++;
            }
        } catch (Exception e) {
            Log.d(TAG, "failed to get best time index");
        }

        return index;
    }

    /*
     * Check if time is in the top five and if so then add it to the file
     */
    public void insert(Integer numMines, String name, String displayedTime, int index) {
        try {
            fetch();
        } catch (Exception e) {
            Log.d(TAG, "failed to fetch data");
        }

        if (data == null) {
            data = new HashMap<Integer, List<BestTime>>();
        }

        if (data.get(numMines) == null) {
            data.put(numMines, new ArrayList<BestTime>());
        }

        List<BestTime> times = data.get(numMines);
        Integer time = toSeconds(displayedTime);
        BestTime s = new BestTime(time, name, displayedTime);
        times.add(index, s);
        data.put(numMines, times);
        String scoresAsJson = gson.toJson(data);
        write(scoresAsJson);
    }

    @Override
    protected void deserialize(String json) {
        Type t = new TypeToken<HashMap<Integer, List<BestTime>>>(){}.getType();
        data = gson.fromJson(json, t);
    }
}
