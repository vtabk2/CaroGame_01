package com.example.framgia.carobluetooth.data.enums;

import java.io.Serializable;

/**
 * Created by framgia on 17/08/2016.
 */
public enum BoardCellState implements Serializable {
    EMPTY(0),
    PLAYER_X(4),
    PLAYER_O(3),
    HUMAN(2),
    MACHINE(1);
    private int mValues;

    BoardCellState(int values) {
        mValues = values;
    }

    public int getValues() {
        return mValues;
    }
}
