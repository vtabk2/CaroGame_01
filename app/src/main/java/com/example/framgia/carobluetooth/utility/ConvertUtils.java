package com.example.framgia.carobluetooth.utility;

import android.content.res.Resources;
import android.util.DisplayMetrics;

/**
 * Created by framgia on 24/08/2016.
 */
public class ConvertUtils {
    public static int convertDpToPx(int dp) {
        return Math.round(
            dp * Resources.getSystem().getDisplayMetrics().densityDpi /
                DisplayMetrics.DENSITY_DEFAULT);
    }
}
