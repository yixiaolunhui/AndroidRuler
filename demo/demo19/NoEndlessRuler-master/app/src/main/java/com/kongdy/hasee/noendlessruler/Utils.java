package com.kongdy.hasee.noendlessruler;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.WindowManager;

/**
 * @author kongdy
 * @date 2016-07-13 23:02
 * @TIME 23:02
 **/


public class Utils {
    public static int screenWidth_;
    public static int screenHeight_;

    public static void initScreenSize(Context context) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        screenWidth_ = displayMetrics.widthPixels;
        screenHeight_ = displayMetrics.heightPixels;
    }
}
