package com.minesweeper.thumbtack;

import android.app.AlertDialog;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

/*
 * Contains logic to handle click events on cells
 */
public class CellOnClickListener implements OnItemClickListener {
    private GridAdapter adapter;
    private AlertDialog.Builder alert;

    public CellOnClickListener(GridAdapter adapter, AlertDialog.Builder alert) {
        this.alert = alert;
        this.adapter = adapter;
    }

    public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
        if (adapter.isInCheatMode()) {
            // If they are cheating, ignore the click
            return;
        } else if (position < 0 || position > adapter.getCount()) {
            return;
        }

        Cell cell = adapter.getItem(position);

        if (cell.isMine) {
            cell.isFlipped = true;
            alert.show();
        } else if (cell.adjacentMineCount > 0) {
            cell.isFlipped = true;
        } else if (cell.adjacentMineCount == 0) {
            cell.isFlipped = true;
            for (Integer i : cell.adjacentCellPositions) {
                Cell adjacentCell = adapter.getItem(i);
                if (!adjacentCell.isFlipped && adjacentCell.adjacentMineCount == 0) {
                    onItemClick(parent, v, i, i);
                }
            }
        }
        adapter.notifyDataSetChanged();
    }
}
