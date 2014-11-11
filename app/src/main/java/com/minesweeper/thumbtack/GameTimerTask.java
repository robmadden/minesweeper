package com.minesweeper.thumbtack;

import android.widget.TextView;

import com.minesweeper.thumbtack.activities.MainActivity;

import java.util.TimerTask;

public class GameTimerTask extends TimerTask {
    private MainActivity activity;
    private int minutes = 0;
    private int seconds = 0;
    private TextView timerView;

    public GameTimerTask(MainActivity activity) {
        this.activity = activity;
        timerView = (TextView) activity.findViewById(R.id.timer);
    }

    @Override
    public void run() {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
            if (seconds < 10) {
                timerView.setText(String.valueOf(minutes) + ":" + "0" + String.valueOf(seconds));
            } else if (seconds == 60) {
                minutes++;
                seconds = 0;
                timerView.setText(String.valueOf(minutes) + ":" + "0" + String.valueOf(seconds));
            } else {
                timerView.setText(String.valueOf(minutes) + ":" + String.valueOf(seconds));
            }

            seconds += 1;
            }
        });
    }
}
