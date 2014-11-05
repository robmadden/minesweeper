package com.minesweeper.thumbtack;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/*
 * Basic adapter to create populate the cells with data ... mines, numbers, etc.
 */
public class GridAdapter extends BaseAdapter {
    public static final int TOTAL_CELL_COUNT = 64;
    public static final int TOTAL_MINE_COUNT = 10;
    private Context context;
    private List<Cell> mines;
    private boolean cheating;

    public GridAdapter(Context context) {
        this.context = context;
        mines = new ArrayList<Cell>(TOTAL_CELL_COUNT);

        // Set which cells are mines using the first 10 items then shuffle the list
        // using Collections - this way ensures that there will always be 10 mines
        // since there won't be duplicate random slots
        for (int i = 0 ; i < TOTAL_CELL_COUNT ; i++) {
            Cell c = new Cell();
            if (i < TOTAL_MINE_COUNT) {
                c.isMine = true;
            }
            mines.add(c);
        }

        Collections.shuffle(mines);
    }

    public int getCount() {
        return mines.size();
    }

    public Cell getItem(int position) {
        if (mines != null) {
            return mines.get(position);
        } else {
            return null;
        }
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView cell;
        if (convertView == null) {
            cell = new ImageView(context);
            cell.setLayoutParams(new GridView.LayoutParams(85, 85));
            cell.setPadding(8, 8, 8, 8);
        } else {
            cell = (ImageView) convertView;
        }
        Cell c = mines.get(position);

        if (cheating) {
            if (c.isMine) {
                cell.setBackgroundColor(Color.RED);
            } else {
                cell.setBackgroundColor(Color.GREEN);
            }
        } else {
            cell.setBackgroundColor(Color.GRAY);
        }
        return cell;
    }

    public void cheat() {
        cheating = true;
        notifyDataSetChanged();
    }
}