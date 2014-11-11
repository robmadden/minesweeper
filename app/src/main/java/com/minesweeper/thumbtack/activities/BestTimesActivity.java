package com.minesweeper.thumbtack.activities;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.widget.TextView;

import com.minesweeper.thumbtack.R;
import com.minesweeper.thumbtack.io.BestTimes;
import com.minesweeper.thumbtack.models.BestTime;

public class BestTimesActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        setContentView(R.layout.activity_high_scores);
        TextView nameViewOne = (TextView) findViewById(R.id.high_score_name_1);
        TextView nameViewTwo = (TextView) findViewById(R.id.high_score_name_2);
        TextView nameViewThree = (TextView) findViewById(R.id.high_score_name_3);
        TextView nameViewFour = (TextView) findViewById(R.id.high_score_name_4);
        TextView nameViewFive = (TextView) findViewById(R.id.high_score_name_5);
        TextView valueViewOne = (TextView) findViewById(R.id.high_score_value_1);
        TextView valueViewTwo = (TextView) findViewById(R.id.high_score_value_2);
        TextView valueViewThree = (TextView) findViewById(R.id.high_score_value_3);
        TextView valueViewFour = (TextView) findViewById(R.id.high_score_value_4);
        TextView valueViewFive = (TextView) findViewById(R.id.high_score_value_5);

        BestTimes times = new BestTimes();
        try { times.fetch(); } catch (Exception e) {}

        if (times.data != null) {
            int size = times.data.size();
            if (size > 0) {
                BestTime t1 = times.data.get(0);
                if (t1 != null) {
                    nameViewOne.setText(t1.name);
                    valueViewOne.setText(t1.readableTime);
                }
            }

            if (size > 1) {
                BestTime t2 = times.data.get(1);
                if (t2 != null) {
                    nameViewTwo.setText(t2.name);
                    valueViewTwo.setText(t2.readableTime);
                }
            }

            if (size > 2) {
                BestTime t3 = times.data.get(2);
                if (t3 != null) {
                    nameViewThree.setText(t3.name);
                    valueViewThree.setText(t3.readableTime);
                }
            }

            if (size > 3) {
                BestTime t4 = times.data.get(3);
                if (t4 != null) {
                    nameViewFour.setText(t4.name);
                    valueViewFour.setText(t4.readableTime);
                }
            }

            if (size > 4) {
                BestTime t5 = times.data.get(4);
                if (t5 != null) {
                    nameViewFive.setText(t5.name);
                    valueViewFive.setText(t5.readableTime);
                }
            }
        }
    }
}
