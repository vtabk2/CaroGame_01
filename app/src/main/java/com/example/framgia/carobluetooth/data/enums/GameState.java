package com.example.framgia.carobluetooth.data.enums;

import java.io.Serializable;

/**
 * Created by framgia on 17/08/2016.
 */
public enum GameState implements Serializable{
    NONE(0),
    PLAYER_X_WIN(1),
    PLAYER_X_LOSE(2),
    PLAYING(3);
    private int mValues;

    GameState(int values) {
        mValues = values;
    }

    public int getValues() {
        return mValues;
    }
}
