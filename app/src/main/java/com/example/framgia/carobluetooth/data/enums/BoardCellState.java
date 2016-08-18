package com.example.framgia.carobluetooth.data.enums;

/**
 * Created by framgia on 17/08/2016.
 */
public enum BoardCellState {
    EMPTY(0),
    PLAYER_X(1),
    PLAYER_O(2);
    private int mValues;

    BoardCellState(int values) {
        mValues = values;
    }

    public int getValues() {
        return mValues;
    }
}
