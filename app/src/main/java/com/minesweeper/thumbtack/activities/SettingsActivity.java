package com.minesweeper.thumbtack.activities;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.minesweeper.thumbtack.R;
import com.minesweeper.thumbtack.io.Settings;
import com.minesweeper.thumbtack.listeners.SpinnerListener;

import java.io.FileNotFoundException;

public class SettingsActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final Settings settings = new Settings();
        try {
            settings.fetch();
        } catch (FileNotFoundException e) {
            Log.d(SettingsActivity.class.getName(), "Failed to fetch settings.");
        }
        // Pull the mine count to set the spinner
        setContentView(R.layout.activity_settings);

        // Set up the mine count spinner
        final Spinner spinner = (Spinner) findViewById(R.id.mine_count_spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.mine_counts, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        int selection = settings.numMines - 1;
        spinner.setAdapter(adapter);
        spinner.post(new Runnable() {
            @Override
            public void run() {
                spinner.setOnItemSelectedListener(new SpinnerListener(settings));
            }
        });
        spinner.setSelection(selection, false);
        spinner.invalidate();
    }
}
