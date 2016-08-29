package com.example.framgia.carobluetooth.utility;

import com.example.framgia.carobluetooth.data.Constants;

/**
 * Created by framgia on 30/08/2016.
 */
public class AiUtils implements Constants {
    public static boolean isInside(int row, int col) {
        return row >= 0 && row < ROW && col >= 0 && col < COL;
    }
}
