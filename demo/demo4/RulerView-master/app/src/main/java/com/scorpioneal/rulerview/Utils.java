package com.scorpioneal.rulerview;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.DisplayMetrics;

/**
 * Created by ScorpioNeal on 15/7/24.
 */
public class Utils {

    public static float convertDpToPixel(Context context, float dp) {
        Resources res = context.getResources();
        DisplayMetrics metrics = res.getDisplayMetrics();
        float px = dp * (metrics.densityDpi / 160f);
        return px;
    }

    public static float convertSpToPixel(Context context, float spValue) {
        Resources res = context.getResources();
        DisplayMetrics metrics = res.getDisplayMetrics();
        float fontScale = metrics.scaledDensity;
        return spValue * fontScale + 0.5f;
    }

    public static int calcTextWidth(Paint paint, String demoText) {
        return (int) paint.measureText(demoText);
    }

    public static int calcTextHeight(Paint paint, String demoText) {

        Rect r = new Rect();
        paint.getTextBounds(demoText, 0, demoText.length(), r);
        return r.height();
    }

}
