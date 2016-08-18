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
    int WIN_LOSE_DEFAULT = 0;
    int INCREASE_DEFAULT = 1;
    int MARGIN = 8;
    int PADDING = 12;
    int CELL_SIZE = 80;
    int COL = 500;
    int ROW = 500;
    int DEFAULT_INTENT_VALUE = -1;
}
