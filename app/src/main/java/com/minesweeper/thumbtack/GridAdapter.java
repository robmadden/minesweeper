package com.minesweeper.thumbtack;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
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
    public static final int TOTAL_MINE_COUNT = 3;
    public static final int TOTAL_ROW_COUNT = 8;
    public static final int TOTAL_COLUMN_COUNT = 8;
    private final TextView flagCountView;
    public int flagCount;
    private final int TRANSPARENT_GREEN;

    private Context context;
    private List<Cell> cells;
    private boolean cheated;

    public GridAdapter(Context context, TextView flagCountView) {
        this.context = context;
        this.flagCountView = flagCountView;
        
        cells = new ArrayList<Cell>(TOTAL_CELL_COUNT);
        randomizeBoard();
        setBoardState();
        TRANSPARENT_GREEN = context.getResources().getColor(R.color.TRANSPARENT_GREEN);
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
                if (j < 0 || j > GridAdapter.TOTAL_COLUMN_COUNT) { continue; }
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
            view = new TextView(context);
            view.setLayoutParams(new GridView.LayoutParams(80, 80));
            view.setPadding(4, 4, 4, 4);
            view.setBackgroundColor(Color.BLACK);
        } else {
            view = (TextView) convertView;
        }

        Cell currentCell = cells.get(position);

        if (flagCountView != null) {
            flagCountView.setText(String.valueOf(flagCount));
        }

        if (cheated) {
            if (currentCell.isMine) {
                view.setBackgroundColor(Color.RED);
            } else if (currentCell.adjacentMineCount == 0) {
                view.setBackgroundColor(TRANSPARENT_GREEN);
            } else if (currentCell.adjacentMineCount > 0) {
                view.setBackgroundColor(TRANSPARENT_GREEN);
                view.setTypeface(null, Typeface.BOLD);
                view.setTextAlignment(TextView.TEXT_ALIGNMENT_TEXT_END);
                view.setText(String.valueOf(currentCell.adjacentMineCount));
            }
        } else {
            if (currentCell.isFlipped) {
                if (currentCell.isMine) {
                    view.setBackgroundColor(Color.RED);
                } else if (currentCell.adjacentMineCount == 0) {
                    view.setBackgroundColor(TRANSPARENT_GREEN);
                } else {
                    view.setBackgroundColor(TRANSPARENT_GREEN);
                    view.setTextAlignment(TextView.TEXT_ALIGNMENT_TEXT_END);
                    view.setTypeface(null, Typeface.BOLD);
                    view.setText(String.valueOf(currentCell.adjacentMineCount));
                }
            } else if (currentCell.isFlagged) {
                view.setBackgroundColor(Color.YELLOW);
            } else {
                view.setBackgroundColor(Color.LTGRAY);
            }
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