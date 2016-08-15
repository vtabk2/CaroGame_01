package com.example.framgia.carobluetooth.data.model;

import com.example.framgia.carobluetooth.data.enums.GameState;
import com.example.framgia.carobluetooth.data.enums.Navigation;
import com.example.framgia.carobluetooth.data.enums.TurnGame;

/**
 * Created by framgia on 17/08/2016.
 */
public class GameData {
    private ItemCaro mItemCaro;
    private GameState mGamestate;
    private TurnGame mTurnGame;
    private Navigation mNavigation;

    public GameData(ItemCaro itemCaro, GameState gamestate, TurnGame turnGame,
                    Navigation navigation) {
        mItemCaro = itemCaro;
        mGamestate = gamestate;
        mTurnGame = turnGame;
        mNavigation = navigation;
    }

    public GameState getGamestate() {
        return mGamestate;
    }

    public ItemCaro getItemCaro() {
        return mItemCaro;
    }

    public TurnGame getTurnGame() {
        return mTurnGame;
    }
}
