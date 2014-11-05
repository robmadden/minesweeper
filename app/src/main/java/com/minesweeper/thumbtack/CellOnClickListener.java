package com.minesweeper.thumbtack;

import android.app.AlertDialog;
import android.graphics.Color;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;

/*
 * Contains logic to handle click events on cells
 */
public class CellOnClickListener implements OnItemClickListener {
    private GridAdapter adapter;
    private AlertDialog.Builder alertBuilder;

    public CellOnClickListener(GridAdapter adapter, AlertDialog.Builder alertBuilder) {
        this.adapter = adapter;
        this.alertBuilder = alertBuilder;
    }

    public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
        Cell c = (Cell) adapter.getItem(position);
        ImageView i = (ImageView) v;

        if (c.isMine) {
            i.setBackgroundColor(Color.RED);
            alertBuilder.show();
        } else {
            i.setBackgroundColor(Color.GREEN);
        }
    }
}
