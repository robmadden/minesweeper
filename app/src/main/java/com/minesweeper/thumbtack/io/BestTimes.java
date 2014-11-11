package com.minesweeper.thumbtack.io;

import android.content.Context;
import android.util.Log;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.minesweeper.thumbtack.activities.MainActivity;
import com.minesweeper.thumbtack.models.BestTime;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class BestTimes {
    private static final String TAG = BestTimes.class.getName();
    public static final int TOTAL_SCORES_TO_STORE = 5;
    private static final String FILENAME = "high_scores";
    private static List<BestTime> data;
    private ObjectMapper om;
    private Gson gson;

    public BestTimes() {
        om = new ObjectMapper();
        om.configure(SerializationFeature.WRITE_NULL_MAP_VALUES, false);
        om.configure(SerializationFeature.INDENT_OUTPUT,true);
        gson = new Gson();
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
        List<BestTime> times = fetch();
        if (times == null || times.size() == 0) {
            return 0;
        }

        int index = 0;
        int timeValue = time.intValue();
        for (BestTime t : times) {
            if (timeValue < t.time.intValue()) {
                break;
            }
            index++;
        }

        return index;
    }

    /*
     * Check if time is in the top five and if so then add it to the file
     */
    public void insert(String name, String displayedTime, int index) {
        FileOutputStream fos = null;
        try {
            List<BestTime> times = fetch();
            fos = MainActivity.appContext.openFileOutput(FILENAME, Context.MODE_PRIVATE);
            Integer time = toSeconds(displayedTime);
            BestTime s = new BestTime(time, name, displayedTime);
            times.add(index, s);
            String scoresAsJson = gson.toJson(times);
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

    /*
     * Return the high scores listed in the high scores file
     */
    public List<BestTime> fetch() {
        FileInputStream inputStream = null;
        BufferedReader reader = null;
        try {
            inputStream = MainActivity.appContext.openFileInput(FILENAME);
            reader = new BufferedReader(new InputStreamReader(inputStream));
            StringBuilder total = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                total.append(line);
            }

            data = gson.fromJson(total.toString(),  new TypeToken<List<BestTime>>(){}.getType());
        } catch(Exception e){
            Log.d(TAG, "filed to fetch high scores");
        } finally {
            try {
                if (inputStream != null) {
                    inputStream.close();
                }

                if (reader != null) {
                    reader.close();
                }
            } catch (Exception e) {
                Log.d(TAG, "faild to close filehandles");
            }
        }

        if (data == null) {
            data = new ArrayList<BestTime>(TOTAL_SCORES_TO_STORE);
        }
        return data;
    }
}
