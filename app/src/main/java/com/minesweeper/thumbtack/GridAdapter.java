package com.minesweeper.thumbtack;

import android.app.Activity;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Typeface;
import android.util.TypedValue;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/*
 * Basic adapter to create populate the cells with data ... mines, numbers, etc.
 */
public class GridAdapter extends BaseAdapter {
    public static final int TOTAL_CELL_COUNT = 64;
    public static final int TOTAL_MINE_COUNT = 10;
    public static final int TOTAL_ROW_COUNT = 8;
    public static final int TOTAL_COLUMN_COUNT = 8;
    private static final int TEXT_CELL_SIZE = 18;
    private final TextView flagCountView;
    public int flagCount;

    // Colors
    private final int CELL_COLOR;
    private final int PRESSED_CELL_COLOR;
    private final int NUMBER_1_COLOR;
    private final int NUMBER_2_COLOR;
    private final int NUMBER_3_COLOR;
    private final int NUMBER_4_COLOR;

    private Activity activity;
    private List<Cell> cells;
    private boolean cheated;

    public GridAdapter(Activity activity, TextView flagCountView) {
        this.activity = activity;
        this.flagCountView = flagCountView;
        
        cells = new ArrayList<Cell>(TOTAL_CELL_COUNT);
        randomizeBoard();
        setBoardState();
        Resources resources = activity.getResources();
        CELL_COLOR = resources.getColor(R.color.CELL_COLOR);
        PRESSED_CELL_COLOR = resources.getColor(R.color.PRESSED_CELL_COLOR);
        NUMBER_1_COLOR = resources.getColor(R.color.NUMBER_1_COLOR);
        NUMBER_2_COLOR = resources.getColor(R.color.NUMBER_2_COLOR);
        NUMBER_3_COLOR = resources.getColor(R.color.NUMBER_3_COLOR);
        NUMBER_4_COLOR = resources.getColor(R.color.NUMBER_4_COLOR);
        flagCount = TOTAL_MINE_COUNT;
    }

    /*
     * Set which cells are mines using the first 10 items then shuffle the list
     * using Collections - this way ensures that there will always be 10 mines
     * since there won't be duplicate random slots
     */
    private void randomizeBoard() {
        for (int i = 0 ; i < TOTAL_CELL_COUNT ; i++) {
            Cell c = new Cell();
            if (i < TOTAL_MINE_COUNT) {
                c.isMine = true;
            }
            cells.add(c);
        }

        Collections.shuffle(cells);
    }

    /*
     * Iterate over each item on the board and figure out how many adjacent mines it has.
     * We are doing a lot of work up front here to find out the number of adjacent
     * mines to any given cell, but it would otherwise need to be calculated dynamically
     * (most likely when a board item was clicked)
     */
    private void setBoardState() {
        for (int i = 0 ; i < TOTAL_CELL_COUNT ; i++) {
            Cell c = cells.get(i);
            c.adjacentCellPositions = findAdjacentCells(i);
            c.adjacentMineCount = countMines(c.adjacentCellPositions);
        }
    }

    /*
     * Takes a position in the adapter and returns the indices (as Integers) of
     * the cells which are adjacent to said position.  Effectively provides a virtual
     * map between cell positions on the board and indices into the "cells" list.
     *
     * @param position the position in the list of cells to find adjacent mines for
     * @returns List<Integer> a list of Integer indices into the "cells" list
     */
    private List<Integer> findAdjacentCells(int position) {
        List<Integer> adjacentCellPositions = new ArrayList<Integer>();
        int row = position / GridAdapter.TOTAL_ROW_COUNT;
        int column = position % GridAdapter.TOTAL_COLUMN_COUNT;

        for (int k = row - 1 ; k <= row + 1; k++) {
            if (k < 0 || k > GridAdapter.TOTAL_ROW_COUNT) { continue; }
            for (int j = column - 1 ; j <= column + 1 ; j++) {
                // Skip out of bounds columns
                if (j < 0 || j >= GridAdapter.TOTAL_COLUMN_COUNT) { continue; }
                int adjacentPosition = k * GridAdapter.TOTAL_ROW_COUNT + j;
                if (adjacentPosition >= 0
                 && adjacentPosition < (GridAdapter.TOTAL_ROW_COUNT * GridAdapter.TOTAL_COLUMN_COUNT)) {
                    adjacentCellPositions.add(adjacentPosition);
                }
            }
        }
        return adjacentCellPositions;
    }

    /*
     * Helper method to take a "cell" on the board and count how many mines are adjacent to it
     *
     * @param List<Integer> positions a list of indices into "cells" which denote a sublist
     * @returns int the number of mines in the sublist
     */
    private int countMines(List<Integer> positions) {
        int numAdjacentMines = 0;
        for (Integer i : positions) {
            Cell adjacentCell = getItem(i);
            if (adjacentCell != null) {
                if (adjacentCell.isMine) {
                    numAdjacentMines++;
                }
            }
        }
        return numAdjacentMines;
    }

    public List<Cell> getCells() {
        return this.cells;
    }

    public int getCount() {
        return cells.size();
    }

    public Cell getItem(int position) {
        int size = cells.size();
        if (cells != null && position <= size && position >=0) {
            return cells.get(position);
        } else {
            return null;
        }
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        TextView view;

        if (convertView == null) {
            view = new TextView(activity);

            Display display = activity.getWindowManager().getDefaultDisplay();
            Point size = new Point();
            display.getSize(size);
            TypedValue tv = new TypedValue();

            boolean inPortraitMode = false;
            if (activity.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
                inPortraitMode = true;
            }

            int actionBarHeight = 70;
            if (activity.getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true)) {
                actionBarHeight = TypedValue.complexToDimensionPixelSize(tv.data, activity.getResources().getDisplayMetrics());
            }

            int width = 0;
            if (inPortraitMode) {
                size.y -= actionBarHeight;

                if (size.x < size.y) {
                    width = (int) ((double) (size.x * .95));
                } else {
                    width = (int) ((double) (size.y * .95));
                }
            } else {
                size.y -= actionBarHeight;
                width = (int) ((double) (size.y * .95));
            }

            int cellWidth = width / TOTAL_COLUMN_COUNT;
            int cellHeight = width / TOTAL_ROW_COUNT;
            view.setLayoutParams(new GridView.LayoutParams(cellWidth, cellHeight));
            view.setBackgroundColor(Color.BLACK);
        } else {
            view = (TextView) convertView;
        }

        Cell currentCell = cells.get(position);

        if (flagCountView != null) {
            flagCountView.setText(String.valueOf(flagCount));
        }

        if (currentCell.isFlipped || cheated) {
            if (currentCell.isMine) {
                view.setBackgroundColor(PRESSED_CELL_COLOR);
                view.setBackgroundResource(R.drawable.mine);
            } else if (currentCell.adjacentMineCount == 0) {
                view.setBackgroundColor(PRESSED_CELL_COLOR);
            } else {
                view.setBackgroundColor(PRESSED_CELL_COLOR);
                view.setPadding(5, 0, 0, 0);
                view.setTypeface(null, Typeface.BOLD);

                if (currentCell.adjacentMineCount == 1) {
                    view.setTextColor(NUMBER_1_COLOR);
                } else if (currentCell.adjacentMineCount == 2) {
                    view.setTextColor(NUMBER_2_COLOR);
                } else if (currentCell.adjacentMineCount == 3) {
                    view.setTextColor(NUMBER_3_COLOR);
                } else if (currentCell.adjacentMineCount == 4) {
                    view.setTextColor(NUMBER_4_COLOR);
                } else {
                    view.setTextColor(Color.MAGENTA);
                }
                view.setTextSize(TEXT_CELL_SIZE);
                view.setText(String.valueOf(currentCell.adjacentMineCount));
            }
        } else if (currentCell.isFlagged) {
            view.setBackgroundColor(Color.YELLOW);
        } else {
            view.setBackgroundColor(CELL_COLOR);
        }

        return view;
    }

    public void cheat() {
        cheated = true;
        notifyDataSetChanged();
    }

    public boolean isInCheatMode() {
        return cheated;
    }
}