package com.example.framgia.carobluetooth.data.enums;

/**
 * Created by framgia on 30/08/2016.
 */
public enum LevelGame {
    EASY(1),
    MEDIUM(2),
    HARD(3);
    private int mValues;

    LevelGame(int values) {
        mValues = values;
    }

    public int getValues() {
        return mValues;
    }
}
