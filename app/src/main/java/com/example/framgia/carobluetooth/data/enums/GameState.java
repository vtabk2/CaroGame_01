package com.example.framgia.carobluetooth.data.enums;

/**
 * Created by framgia on 17/08/2016.
 */
public enum GameState {
    NONE(0),
    PLAYER_X_WIN(1),
    PLAYER_X_LOSE(2);
    private int mValues;

    GameState(int values) {
        mValues = values;
    }

    public int getValues() {
        return mValues;
    }
}
