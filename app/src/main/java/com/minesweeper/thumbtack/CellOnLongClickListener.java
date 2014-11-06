package com.minesweeper.thumbtack;

import android.view.View;
import android.widget.AdapterView;

public class CellOnLongClickListener implements AdapterView.OnItemLongClickListener {
    private GridAdapter adapter;

    public CellOnLongClickListener(GridAdapter adapter) {
        this.adapter = adapter;
    }
    public boolean onItemLongClick (AdapterView<?> parent, View view, int position, long id) {
        Cell c = adapter.getItem(position);
        if (c != null) {
            c.isFlagged = !c.isFlagged;
        }
        adapter.notifyDataSetInvalidated();
        return true;
    }
}
