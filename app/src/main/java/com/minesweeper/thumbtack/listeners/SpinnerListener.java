package com.minesweeper.thumbtack.listeners;

import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;

import com.minesweeper.thumbtack.io.Settings;

public class SpinnerListener implements OnItemSelectedListener {
    private Settings settings;

    public SpinnerListener(Settings settings) {
        this.settings = settings;
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        settings.setMineCount(++position);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) { }
}
