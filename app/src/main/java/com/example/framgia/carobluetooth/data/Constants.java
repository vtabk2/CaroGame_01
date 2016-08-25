package com.example.framgia.carobluetooth.data;

/**
 * Created by framgia on 10/08/2016.
 */
public interface Constants {
    int STROKE_WIDTH = 2;
    String INTENT_DEVICE_ADDRESS = "device_address";
    int TIME_SHOW_VISIBILITY = 300;
    int STATE_NONE = 0;
    int STATE_LISTEN = 1;
    int STATE_CONNECTING = 2;
    int STATE_CONNECTED = 3;
    int MESSAGE_STATE_CHANGE = 1;
    int MESSAGE_READ = 2;
    int MESSAGE_WRITE = 3;
    int MESSAGE_DEVICE_NAME = 4;
    int MESSAGE_TOAST = 5;
    String DEVICE_NAME = "device_name";
    String TOAST = "toast";
    String SHARED_PREFERENCES = "caro game bluetooth";
    String WIN = "win";
    String LOSE = "lose";
    String WIN_HUMAN = "win human";
    String LOSE_HUMAN = "lose man";
    int WIN_LOSE_DEFAULT = 0;
    int INCREASE_DEFAULT = 1;
    int MARGIN = 8;
    int PADDING = 12;
    int CELL_SIZE_DP = 35;
    int COL = 50;
    int ROW = 50;
    int DEFAULT_INTENT_VALUE = -1;
    int DEFAULT_MIN_ROW_COL = 100;
    int DEFAULT_MAX_ROW_COL = -100;
    int WIN_COUNT = 5;
    int DEFAULT_COUNT = 0;
    int[] DELTA_ROW = {1, 1, 1, 0};
    int[] DELTA_COL = {-1, 0, 1, 1};
    int LENGTH = 4;
    int BEST_SCORE = 10000;
    int[] ATTACK_SCORE = {-1, 0, 2, 8, 5000, 10000};
    int[] DEFENSE_SCORE = {-1, 0, 2, 6, 800, 7000};
    int[] BONUS = {150, 30};
    int NUMBER_BONUS = 2;
    int OUT_OF_DISTANCE = 2;
}
