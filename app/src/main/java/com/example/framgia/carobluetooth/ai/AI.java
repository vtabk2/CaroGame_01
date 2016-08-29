package com.example.framgia.carobluetooth.ai;

import android.graphics.Point;

import com.example.framgia.carobluetooth.data.Constants;
import com.example.framgia.carobluetooth.data.enums.BoardCellState;
import com.example.framgia.carobluetooth.data.model.ItemCaro;
import com.example.framgia.carobluetooth.utility.AiUtils;

/**
 * Created by framgia on 29/08/2016.
 */
public class AI implements Constants {
    private ItemCaro[][] mItemCaros;
    private Point mBestCell = new Point();
    private int mMinCol, mMaxCol, mMinRow, mMaxRow;
    private int[][] mValues;
    private final static int[] SCORE_DEFENSE = {0, 1, 9, 85, 769};
    private final static int[] SCORE_ATTACK = {0, 4, 28, 256, 2308};

    public AI() {
        mItemCaros = new ItemCaro[ROW][COL];
        for (int row = 0; row < ROW; row++) {
            for (int col = 0; col < COL; col++) {
                mItemCaros[row][col] = new ItemCaro(row, col, BoardCellState.EMPTY);
            }
        }
        mValues = new int[ROW][COL];
        resetValues();
        mBestCell.set(-1, -1);
    }

    public String checkDiagonalMain(int row, int col) {
        StringBuilder s = new StringBuilder("");
        int rowIndex = row;
        int colIndex = col;
        int count = 0;
        while (rowIndex < ROW - 1 && colIndex < ROW - 1 && count < 5) {
            count++;
            rowIndex++;
            colIndex++;
        }
        int limit = count + 6;
        count = 0;
        while (rowIndex > 0 && colIndex > 0 && count < limit) {
            s.append(getFlags(rowIndex, colIndex));
            count++;
            rowIndex--;
            colIndex--;
        }
        return s.toString();
    }

    public String checkDiagonalExtra(int row, int col) {
        int count = 0;
        int rowIndex = row;
        int colIndex = col;
        StringBuilder s = new StringBuilder("");
        while (rowIndex > 0 && colIndex < ROW - 1 && count < 5) {
            count++;
            rowIndex--;
            colIndex++;
        }
        int limit = count + 6;
        count = 0;
        while (rowIndex < ROW - 1 && colIndex > 0 && count < limit) {
            s.append(getFlags(rowIndex, colIndex));
            count++;
            rowIndex++;
            colIndex--;
        }
        return s.toString();
    }

    public String checkHorizontal(int row, int col) {
        int count = 0;
        int colIndex = col;
        StringBuilder s = new StringBuilder("");
        while (colIndex < COL - 1 && count < 5) {
            count++;
            colIndex++;
        }
        int limit = count + 6;
        count = 0;
        while (colIndex > 0 && count < limit) {
            s.append(getFlags(row, colIndex));
            count++;
            colIndex--;
        }
        return s.toString();
    }

    public String checkVertical(int row, int col) {
        int count = 0;
        int rowIndex = row;
        StringBuilder s = new StringBuilder("");
        while (rowIndex < ROW - 1 && count < 5) {
            count++;
            rowIndex++;
        }
        int limit = count + 6;
        count = 0;
        while (rowIndex > 0 && count < limit) {
            s.append(getFlags(rowIndex, col));
            count++;
            rowIndex--;
        }
        return s.toString();
    }

    public void setBoard(ItemCaro[][] itemCaros) {
        mItemCaros = itemCaros;
        updateMinMaxRowCol();
    }

    private int findMinCol() {
        int minCol = COL;
        for (int row = 0; row < ROW; row++) {
            for (int col = 0; col < COL; col++) {
                if (getBoardCellState(row, col) != BoardCellState.EMPTY.getValues() && col < minCol)
                    minCol = col;
            }
        }
        return minCol;
    }

    private int findMaxCol() {
        int maxCol = 0;
        for (int row = 0; row < ROW; row++) {
            for (int col = 0; col < COL; col++) {
                if (getBoardCellState(row, col) != BoardCellState.EMPTY.getValues() && col > maxCol)
                    maxCol = col;
            }
        }
        return maxCol;
    }

    private int findMinRow() {
        int minRow = ROW;
        for (int row = 0; row < ROW; row++) {
            for (int col = 0; col < COL; col++) {
                if (getBoardCellState(row, col) != BoardCellState.EMPTY.getValues() && row < minRow)
                    minRow = row;
            }
        }
        return minRow;
    }

    private int findMaxRow() {
        int maxRow = 0;
        for (int row = 0; row < ROW; row++) {
            for (int col = 0; col < COL; col++) {
                if (getBoardCellState(row, col) != BoardCellState.EMPTY.getValues() && row > maxRow)
                    maxRow = row;
            }
        }
        return maxRow;
    }

    protected int getBoardCellState(int row, int col) {
        return mItemCaros[row][col].getBoardCellState().getValues();
    }

    protected String getFlags(int row, int col) {
        switch (mItemCaros[row][col].getBoardCellState()) {
            case HUMAN:
                return "X";
            case MACHINE:
                return "O";
            case EMPTY:
                return "_";
        }
        return "_";
    }

    private void resetValues() {
        for (int row = 0; row < ROW; row++) {
            for (int col = 0; col < COL; col++) {
                mValues[row][col] = 0;
            }
        }
    }

    public void evaluateBoard(BoardCellState boardCellState) {
        resetValues();
        for (int row = 0; row < ROW - 4; row++) {
            for (int col = 0; col < COL; col++) {
                checkVertical(row, col, boardCellState);
            }
        }
        for (int row = 0; row < ROW; row++) {
            for (int col = 0; col < COL - 4; col++) {
                checkHorizontal(row, col, boardCellState);
            }
        }
        for (int row = 0; row < ROW - 4; row++) {
            for (int col = 0; col < COL - 4; col++) {
                checkDiagonalDown(row, col, boardCellState);
            }
        }
        for (int row = 4; row < ROW; row++) {
            for (int col = 0; col < COL - 4; col++) {
                checkDiagonalUp(row, col, boardCellState);
            }
        }
    }

    public Point minMax() {
        int max = 0;
        evaluateBoard(BoardCellState.MACHINE);
        for (int row = mMinRow; row <= mMaxRow; row++) {
            for (int col = mMinCol; col <= mMaxCol; col++) {
                if (mValues[row][col] > max) {
                    mBestCell.x = row;
                    mBestCell.y = col;
                    max = mValues[row][col];
                }
            }
        }
        return mBestCell;
    }

    private void updateMinMaxRowCol() {
        mMinCol = findMinCol();
        mMinCol = mMinCol > 0 ? mMinCol - 1 : 0;
        mMaxCol = findMaxCol();
        mMaxCol = mMaxCol < COL - 1 ? mMaxCol + 1 : COL - 1;
        mMinRow = findMinRow();
        mMinRow = mMinRow > 0 ? mMinRow - 1 : 0;
        mMaxRow = findMaxRow();
        mMaxRow = mMaxRow < ROW - 1 ? mMaxRow + 1 : ROW - 1;
        if (mMaxRow == ROW - 1 || mMaxCol == COL - 1 || mMinRow == 0 || mMinCol == 0) {
            mMinRow = 0;
            mMaxRow = mMaxRow + 1 >= ROW - 1 ? ROW - 1 : mMaxRow + 1;
            mMinCol = 0;
            mMaxCol = mMaxCol + 1 >= COL - 1 ? COL - 1 : mMaxCol + 1;
        }
    }

    private boolean check(int minRow, int minCol, int maxRow, int maxCol,
                          BoardCellState boardCellState) {
        return (AiUtils.isInside(minRow, minCol) && AiUtils.isInside(maxRow, maxCol) &&
            getBoardCellState(minRow, minCol) == boardCellState.getValues() &&
            getBoardCellState(maxRow, maxCol) == boardCellState.getValues());
    }

    private void checkVertical(int row, int col, BoardCellState boardCellState) {
        int numberHuman = 0;
        int numberComputer = 0;
        for (int rowDelta = 0; rowDelta < 5; rowDelta++) {
            int values = getBoardCellState(row + rowDelta, col);
            if (values == BoardCellState.HUMAN.getValues()) numberHuman++;
            else if (values == BoardCellState.MACHINE.getValues()) numberComputer++;
        }
        if (numberComputer * numberHuman == 0 && numberComputer != numberHuman) {
            for (int rowDelta = 0; rowDelta < 5; rowDelta++) {
                int rowIndex = row + rowDelta;
                if (getBoardCellState(rowIndex, col) == BoardCellState.EMPTY.getValues()) {
                    if (numberComputer == 0) {
                        mValues[rowIndex][col] += boardCellState == BoardCellState.MACHINE ?
                            SCORE_DEFENSE[numberHuman] : SCORE_ATTACK[numberHuman];
                        if (check(row - 1, col, row + 5, col, BoardCellState.MACHINE)) {
                            mValues[rowIndex][col] = 0;
                        }
                    }
                    if (numberHuman == 0) {
                        mValues[rowIndex][col] += boardCellState == BoardCellState.HUMAN ?
                            SCORE_DEFENSE[numberComputer] : SCORE_ATTACK[numberComputer];
                        if (check(row - 1, col, row + 5, col, BoardCellState.HUMAN)) {
                            mValues[rowIndex][col] = 0;
                        }
                    }
                    if (numberComputer == 4 || numberHuman == 4) mValues[rowIndex][col] *= 2;
                }
            }
        }
    }

    private void checkDiagonalDown(int row, int col, BoardCellState boardCellState) {
        int numberHuman = 0;
        int numberComputer = 0;
        for (int i = 0; i < 5; i++) {
            int values = getBoardCellState(row + i, col + i);
            if (values == BoardCellState.HUMAN.getValues()) numberHuman++;
            else if (values == BoardCellState.MACHINE.getValues()) numberComputer++;
        }
        if (numberComputer * numberHuman == 0 && numberHuman != numberComputer) {
            for (int i = 0; i < 5; i++) {
                int rowIndex = row + i;
                int colIndex = col + i;
                if (getBoardCellState(rowIndex, colIndex) == BoardCellState.EMPTY.getValues()) {
                    if (numberComputer == 0) {
                        mValues[rowIndex][colIndex] += boardCellState == BoardCellState.MACHINE ?
                            SCORE_DEFENSE[numberHuman] : SCORE_ATTACK[numberHuman];
                        if (AiUtils.isInside(row - 1, col - 1) &&
                            AiUtils.isInside(row + 5, col + 5) &&
                            getBoardCellState(row - 1, col - 1) ==
                                BoardCellState.MACHINE.getValues() &&
                            getBoardCellState(row + 5, col + 5) ==
                                BoardCellState.MACHINE.getValues()) {
                            mValues[rowIndex][colIndex] = 0;
                        }
                    }
                    if (numberHuman == 0) {
                        mValues[rowIndex][colIndex] += boardCellState == BoardCellState.HUMAN ?
                            SCORE_DEFENSE[numberComputer] : SCORE_ATTACK[numberComputer];
                        if (AiUtils.isInside(row - 1, col - 1) &&
                            AiUtils.isInside(row + 5, col + 5) &&
                            getBoardCellState(row - 1, col - 1) ==
                                BoardCellState.HUMAN.getValues() &&
                            getBoardCellState(row + 5, col + 5) ==
                                BoardCellState.HUMAN.getValues()) {
                            mValues[rowIndex][colIndex] = 0;
                        }
                    }
                    if (numberComputer == 4 || numberHuman == 4) mValues[rowIndex][colIndex] *= 2;
                }
            }
        }
    }

    private void checkDiagonalUp(int row, int col, BoardCellState boardCellState) {
        int numberHuman = 0;
        int numberComputer = 0;
        for (int i = 0; i < 5; i++) {
            int values = getBoardCellState(row - i, col + i);
            if (values == BoardCellState.HUMAN.getValues()) numberHuman++;
            else if (values == BoardCellState.MACHINE.getValues()) numberComputer++;
        }
        if (numberComputer * numberHuman == 0 && numberComputer != numberHuman) {
            for (int i = 0; i < 5; i++) {
                int rowIndex = row - i;
                int colIndex = col + i;
                if (getBoardCellState(rowIndex, colIndex) == BoardCellState.EMPTY.getValues()) {
                    if (numberComputer == 0) {
                        mValues[rowIndex][colIndex] += boardCellState == BoardCellState.MACHINE ?
                            SCORE_DEFENSE[numberHuman] : SCORE_ATTACK[numberHuman];
                        if (AiUtils.isInside(row + 1, col - 1) &&
                            AiUtils.isInside(row - 5, col + 5) &&
                            getBoardCellState(row + 1, col - 1) ==
                                BoardCellState.MACHINE.getValues() &&
                            getBoardCellState(row - 5, col + 5) ==
                                BoardCellState.MACHINE.getValues()) {
                            mValues[rowIndex][colIndex] = 0;
                        }
                    }
                    if (numberHuman == 0) {
                        mValues[rowIndex][colIndex] += boardCellState == BoardCellState.HUMAN ?
                            SCORE_DEFENSE[numberComputer] : SCORE_ATTACK[numberComputer];
                        if (AiUtils.isInside(row + 1, col - 1) &&
                            AiUtils.isInside(row - 5, col + 5) &&
                            getBoardCellState(row + 1, col - 1) ==
                                BoardCellState.HUMAN.getValues() &&
                            getBoardCellState(row - 5, col + 5) ==
                                BoardCellState.HUMAN.getValues()) {
                            mValues[rowIndex][colIndex] = 0;
                        }
                    }
                    if (numberComputer == 4 || numberHuman == 4) mValues[rowIndex][colIndex] *= 2;
                }
            }
        }
    }

    private void checkHorizontal(int row, int col, BoardCellState boardCellState) {
        int numberHuman = 0;
        int numberComputer = 0;
        for (int colDelta = 0; colDelta < 5; colDelta++) {
            int values = getBoardCellState(row, col + colDelta);
            if (values == BoardCellState.HUMAN.getValues()) numberHuman++;
            else if (values == BoardCellState.MACHINE.getValues()) numberComputer++;
        }
        if (numberHuman * numberComputer == 0 && numberHuman != numberComputer) {
            for (int colDelta = 0; colDelta < 5; colDelta++) {
                int colIndex = col + colDelta;
                if (getBoardCellState(row, colIndex) == BoardCellState.EMPTY.getValues()) {
                    if (numberComputer == 0) {
                        mValues[row][colIndex] += boardCellState == BoardCellState.MACHINE ?
                            SCORE_DEFENSE[numberHuman] : SCORE_ATTACK[numberHuman];
                        if (AiUtils.isInside(row, col - 1) && AiUtils.isInside(row, col + 5) &&
                            getBoardCellState(row, col - 1) == BoardCellState.MACHINE.getValues() &&
                            getBoardCellState(row, col + 5) == BoardCellState.MACHINE.getValues()) {
                            mValues[row][colIndex] = 0;
                        }
                    }
                    if (numberHuman == 0) {
                        mValues[row][colIndex] += boardCellState == BoardCellState.HUMAN ?
                            SCORE_DEFENSE[numberComputer] : SCORE_ATTACK[numberComputer];
                        if (AiUtils.isInside(row, col - 1) && AiUtils.isInside(row, col + 5) &&
                            getBoardCellState(row, col - 1) == BoardCellState.HUMAN.getValues() &&
                            getBoardCellState(row, col + 5) == BoardCellState.HUMAN.getValues()) {
                            mValues[row][colIndex] = 0;
                        }
                    }
                    if (numberComputer == 4 || numberHuman == 4) mValues[row][colIndex] *= 2;
                }
            }
        }
    }
}

