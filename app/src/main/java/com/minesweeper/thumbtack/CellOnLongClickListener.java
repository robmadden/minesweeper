package com.minesweeper.thumbtack;

import android.view.View;
import android.widget.AdapterView;

public class CellOnLongClickListener implements AdapterView.OnItemLongClickListener {
    private GridAdapter adapter;

    public CellOnLongClickListener(GridAdapter adapter) {
        this.adapter = adapter;
    }
    public boolean onItemLongClick (AdapterView<?> parent, View view, int position, long id) {
        if (adapter.isInCheatMode()) {
            return true;
        }

        if (adapter.flagCount == 0 && !adapter.getItem(position).isFlagged) {
            return true;
        }

        Cell c = adapter.getItem(position);
        if (c != null) {
            c.isFlagged = !c.isFlagged;
        }

        if (c.isFlagged) {
            adapter.flagCount--;
        } else {
            adapter.flagCount++;
        }

        adapter.notifyDataSetInvalidated();
        return true;
    }
}
