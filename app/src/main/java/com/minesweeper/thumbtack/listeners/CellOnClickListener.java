package com.minesweeper.thumbtack.listeners;

import android.app.AlertDialog;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

import com.minesweeper.thumbtack.models.Cell;
import com.minesweeper.thumbtack.GridAdapter;
import com.minesweeper.thumbtack.activities.MainActivity;

/*
 * Contains logic to handle click events on cells
 */
public class CellOnClickListener implements OnItemClickListener {
    private GridAdapter adapter;
    private AlertDialog.Builder alert;
    private MainActivity activity;

    public CellOnClickListener(GridAdapter adapter, AlertDialog.Builder alert, MainActivity activity) {
        this.alert = alert;
        this.adapter = adapter;
        this.activity = activity;
    }

    public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
        if (adapter.isInCheatMode()) {
            // If they are cheating, ignore the click
            return;
        } else if (position < 0 || position > adapter.getCount()) {
            return;
        }

        // If the game hasn't started, start the timer
        if (!activity.gameHasStarted) {
            activity.startTimer();
        }

        Cell cell = adapter.getItem(position);
        // If for whatever reason this cell was flagged
        if (cell.isFlagged) {
            return;
        }

        cell.isFlipped = true;

        if (cell.isMine) {
            alert.show();
        } else if (cell.adjacentMineCount == 0) {
            for (Integer i : cell.adjacentCellPositions) {
                Cell adjacentCell = adapter.getItem(i);
                if (!adjacentCell.isFlipped && adjacentCell.adjacentMineCount >= 0) {
                    onItemClick(parent, v, i, i);
                }
            }
        }
        adapter.notifyDataSetChanged();
    }
}
