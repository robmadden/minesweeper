package com.minesweeper.thumbtack;

import java.util.List;

/*
 * Building block class that encapsulates the field/logic for one particular cell
 */
public class Cell {
    public boolean isMine;
    public boolean isFlipped;
    public boolean isFlagged;
    public List<Integer> adjacentCellPositions;
    public Integer adjacentMineCount;
}
