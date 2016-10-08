package com.lost.zou.scaleruler.utils;

import android.graphics.Paint;
import android.text.TextPaint;

/**
 * Created by chenjingmian
 */
public class TextUtil {

    /**
     * @return 返回指定笔和指定字符串的长度
     */
    public static float getFontlength(TextPaint paint, String str) {
        return paint.measureText(str);
    }

    /**
     * @return 返回指定笔的文字高度
     */
    public static float getFontHeight(Paint paint) {
        Paint.FontMetrics fm = paint.getFontMetrics();
        return fm.descent - fm.ascent;
    }

    public static String getNoString(int no) {
        String result;
        if (no > 19) {
            result = tranNoString(no / 10) + "十" + tranNoString(no % 10);
        } else if (no > 9) {
            result = "十" + tranNoString(no % 10);
        } else {
            result = tranNoString(no);
        }
        return result;
    }

    public static String tranNoString(int no) {
        switch (no) {
            case 1: return "一";
            case 2: return "二";
            case 3: return "三";
            case 4: return "四";
            case 5: return "五";
            case 6: return "六";
            case 7: return "七";
            case 8: return "八";
            case 9: return "九";
        }
        return "";
    }
}
