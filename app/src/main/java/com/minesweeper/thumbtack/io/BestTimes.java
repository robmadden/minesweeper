package com.minesweeper.thumbtack.io;

import android.content.Context;
import android.util.Log;

import com.google.gson.reflect.TypeToken;
import com.minesweeper.thumbtack.activities.MainActivity;
import com.minesweeper.thumbtack.models.BestTime;

import java.io.FileOutputStream;
import java.lang.reflect.Type;
import java.util.List;

public class BestTimes extends GenericIO<BestTime> {
    private transient static final String TAG = BestTimes.class.getName();
    public transient static final int TOTAL_SCORES_TO_STORE = 5;
    private static final String fileName = "high_scores";

    public static List<BestTime> data;
    public BestTimes() {
        super("high_scores");
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
    public int getBestTimeIndex(Integer time) {
        int index = 0;
        try {
            fetch();
            if (this.data == null || this.data.size() == 0) {
                return 0;
            }

            int timeValue = time.intValue();
            for (BestTime t : this.data) {
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
    public void insert(String name, String displayedTime, int index) {
        FileOutputStream fos = null;
        try {
            fetch();
            fos = MainActivity.appContext.openFileOutput(fileName, Context.MODE_PRIVATE);
            Integer time = toSeconds(displayedTime);
            BestTime s = new BestTime(time, name, displayedTime);
            data.add(index, s);
            String scoresAsJson = gson.toJson(data);
            fos.write(scoresAsJson.getBytes());
        } catch (Exception e) {
            Log.d(TAG, "failed to add new high score");
        } finally {
            if (fos != null) {
                try {
                    fos.flush();
                    fos.close();
                } catch (Exception e) {
                    Log.d(TAG, "failed to add new high score");
                }
            }
        }
    }

    @Override
    protected void deserialize(String json) {
        Type t = new TypeToken<List<BestTime>>(){}.getType();
        data = gson.fromJson(json, t);
    }
}
