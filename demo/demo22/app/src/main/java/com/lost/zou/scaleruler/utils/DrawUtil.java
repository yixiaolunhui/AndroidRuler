package com.lost.zou.scaleruler.utils;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Build;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.Window;
import android.view.WindowManager;

import java.io.ByteArrayOutputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * 绘制工具类
 *
 * @author luopeihuan
 */
public class DrawUtil {
    public static float sDensity = 1.0f;
    public static int sDensityDpi;
    public static int sWidthPixels;
    public static int sHeightPixels;
    public static float sFontDensity;
    public static int sTouchSlop = 15; // 点击的最大识别距离，超过即认为是移动

    public static int sStatusHeight; // 平板中底边的状态栏高度
    private static Class sClass = null;
    private static Method sMethodForWidth = null;
    private static Method sMethodForHeight = null;
    public static int sTopStatusHeight;

    public static int sNavBarLocation;
    private static int sRealWidthPixels;
    private static int sRealHeightPixels;
    private static int sNavBarWidth; // 虚拟键宽度
    private static int sNavBarHeight; // 虚拟键高度
    public static final int NAVBAR_LOCATION_RIGHT = 1;
    public static final int NAVBAR_LOCATION_BOTTOM = 2;

    // 在某些机子上存在不同的density值，所以增加两个虚拟值
    public static float sVirtualDensity = -1;
    public static float sVirtualDensityDpi = -1;

    public static int sStatusBar;



    /**
     * dip/dp转像素
     * <p/>
     * dip或 dp大小
     *
     * @return 像素值
     */
    public static int dip2px(float dipVlue) {
        return (int) (dipVlue * sDensity + 0.5f);
    }

    /**
     * 像素转dip/dp
     *
     * @param pxValue 像素大小
     * @return dip值
     */
    public static int px2dip(float pxValue) {
        final float scale = sDensity;
        return (int) (pxValue / scale + 0.5f);
    }

    /**
     * sp 转 px
     *
     * @param spValue sp大小
     * @return 像素值
     */
    public static int sp2px(float spValue) {
        final float scale = sDensity;
        return (int) (scale * spValue);
    }

    /**
     * px转sp
     *
     * @param pxValue 像素大小
     * @return sp值
     */
    public static int px2sp(float pxValue) {
        final float scale = sDensity;
        return (int) (pxValue / scale);
    }

    public static void resetDensity(Context context) {
        if (context != null && null != context.getResources()) {
            DisplayMetrics metrics = context.getResources().getDisplayMetrics();
            sDensity = metrics.density;
            sFontDensity = metrics.scaledDensity;
            sWidthPixels = metrics.widthPixels;
            sHeightPixels = metrics.heightPixels;
            sDensityDpi = metrics.densityDpi;
            if (Machine.isTablet(context)) {
                sStatusHeight = getTabletScreenHeight(context) - sHeightPixels;
            }
            try {
                final ViewConfiguration configuration = ViewConfiguration.get(context);
                if (null != configuration) {
                    sTouchSlop = configuration.getScaledTouchSlop();
                }
                getStatusBarHeight(context);
            } catch (Error e) {
                Log.i("DrawUtil", "resetDensity has error" + e.getMessage());
            }
            sStatusBar = getStatusBarHeight(context);

        }
        resetNavBarHeight(context);
    }

    private static void resetNavBarHeight(Context context) {
        if (context != null) {
            WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
            Display display = wm.getDefaultDisplay();
            try {
                if (sClass == null) {
                    sClass = Class.forName("android.view.Display");
                }
                Point realSize = new Point();
                Method method = sClass.getMethod("getRealSize", Point.class);
                method.invoke(display, realSize);
                sRealWidthPixels = realSize.x;
                sRealHeightPixels = realSize.y;
                sNavBarWidth = realSize.x - sWidthPixels;
                sNavBarHeight = realSize.y - sHeightPixels;
            } catch (Exception e) {
                sRealWidthPixels = sWidthPixels;
                sRealHeightPixels = sHeightPixels;
                sNavBarHeight = 0;
            }
        }
        sNavBarLocation = getNavBarLocation();
    }

    public static int getTabletScreenWidth(Context context) {
        int width = 0;
        if (context != null) {
            try {
                WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
                Display display = wm.getDefaultDisplay();
                if (sClass == null) {
                    sClass = Class.forName("android.view.Display");
                }
                if (sMethodForWidth == null) {
                    sMethodForWidth = sClass.getMethod("getRealWidth");
                }
                width = (Integer) sMethodForWidth.invoke(display);
            } catch (Exception e) {
            }
        }

        // Rect rect= new Rect();
        // ((Activity)
        // context).getWindow().getDecorView().getWindowVisibleDisplayFrame(rect);
        // int statusbarHeight = height - rect.bottom;
        if (width == 0) {
            width = sWidthPixels;
        }

        return width;
    }

    public static int getTabletScreenHeight(Context context) {
        int height = 0;
        if (context != null) {
            WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
            Display display = wm.getDefaultDisplay();
            try {
                if (sClass == null) {
                    sClass = Class.forName("android.view.Display");
                }
                if (sMethodForHeight == null) {
                    sMethodForHeight = sClass.getMethod("getRealHeight");
                }
                height = (Integer) sMethodForHeight.invoke(display);
            } catch (Exception e) {
            }
        }

        // Rect rect= new Rect();
        // ((Activity)
        // context).getWindow().getDecorView().getWindowVisibleDisplayFrame(rect);
        // int statusbarHeight = height - rect.bottom;
        if (height == 0) {
            height = sHeightPixels;
        }

        return height;
    }

    public static boolean isPad() {
        if (sDensity >= 1.5 || sDensity <= 0) {
            return false;
        }
        if (sWidthPixels < sHeightPixels) {
            if (sWidthPixels > 480 && sHeightPixels > 800) {
                return true;
            }
        } else {
            if (sWidthPixels > 800 && sHeightPixels > 480) {
                return true;
            }
        }
        return false;
    }

    public static int getStatusBarHeight(Context context) {
        Class<?> c = null;
        Object obj = null;
        Field field = null;
        int x = 0;
        int top = 0;
        try {
            c = Class.forName("com.android.internal.R$dimen");
            obj = c.newInstance();
            field = c.getField("status_bar_height");
            x = Integer.parseInt(field.get(obj).toString());
            top = context.getResources().getDimensionPixelSize(x);
            sTopStatusHeight = top;
        } catch (Exception e1) {
            e1.printStackTrace();
        }
        return top;
    }

    public static int getRealWidth() {
        if (Machine.s_IS_SDK_ABOVE_KITKAT) {
            return sRealWidthPixels;
        }
        return sWidthPixels;
    }

    public static int getRealHeight() {
        if (Machine.s_IS_SDK_ABOVE_KITKAT) {
            return sRealHeightPixels;
        }
        return sHeightPixels;
    }


    /**
     * 虚拟键在下面时
     *
     * @return
     */
    public static int getNavBarHeight() {
        if (Machine.s_IS_SDK_ABOVE_KITKAT) {
            return sNavBarHeight;
        }
        return 0;
    }

    /**
     * 横屏，虚拟键在右边时
     *
     * @return
     */
    public static int getNavBarWidth() {
        if (Machine.s_IS_SDK_ABOVE_KITKAT) {
            return sNavBarWidth;
        }
        return 0;
    }

    public static int getNavBarLocation() {
        if (sRealWidthPixels > sWidthPixels) {
            return NAVBAR_LOCATION_RIGHT;
        }
        return NAVBAR_LOCATION_BOTTOM;
    }

    /**
     * 底部状态栏高度
     */
    public static int getNavigationBarHeight(Context context) {
        if (Build.VERSION.SDK_INT >= 19) {
            Resources resources = context.getResources();
            int resourceId = resources.getIdentifier("navigation_bar_height", "dimen", "android");
            int height = resources.getDimensionPixelSize(resourceId);
            return height;
        } else {
            return 0;
        }
    }

    /**
     * 裁剪成圆形图片
     *
     * @param bitmap 原图
     */
    public static Bitmap getRoundedCornerBitmap(Bitmap bitmap) {
        if (bitmap != null) {
            Bitmap moutBitmap = Bitmap.createBitmap(bitmap.getWidth(),
                    bitmap.getHeight(), Bitmap.Config.ARGB_8888);
            Canvas mcanvas = new Canvas(moutBitmap);
            final int mcolor = 0xff424242;
            final Paint mpaint = new Paint();
            final Rect mrect = new Rect(0, 0, bitmap.getWidth(),
                    bitmap.getHeight());
            final RectF mrectF = new RectF(mrect);
            final float mroundPX = bitmap.getWidth() / 2;
            mpaint.setAntiAlias(true);
            mcanvas.drawARGB(0, 0, 0, 0);
            mpaint.setColor(mcolor);
            mcanvas.drawRoundRect(mrectF, mroundPX, mroundPX, mpaint);
            mpaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
            mcanvas.drawBitmap(bitmap, mrect, mrect, mpaint);
            return moutBitmap;
        } else {
            return null;
        }

    }

    public static byte[] Bitmap2Bytes(Bitmap bm) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.PNG, 100, baos);
        return baos.toByteArray();
    }

    //使用Bitmap加Matrix来缩放
    public static Bitmap resizeImage(Bitmap bitmap, int w, int h) {
        Bitmap BitmapOrg = bitmap;
        int width = BitmapOrg.getWidth();
        int height = BitmapOrg.getHeight();
        int newWidth = w;
        int newHeight = h;

        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;

        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);
        // if you want to rotate the Bitmap
        // matrix.postRotate(45);
        Bitmap resizedBitmap = Bitmap.createBitmap(BitmapOrg, 0, 0, width,
                height, matrix, true);
        return resizedBitmap;
    }

    public static void setTranslucentStatusBar(Window window) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
//                    | WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
//                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
//            window.setNavigationBarColor(Color.TRANSPARENT);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
//            window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        }
    }

    public static void setNoTranslucentStatusBar(Window window) {
        Log.i("zou", "DrawUtil setNoTranslucentStatusBar");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Log.i("zou", "DrawUtil setNoTranslucentStatusBar1111");
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.BLACK);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Log.i("zou", "DrawUtil setNoTranslucentStatusBar2222");
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

        }
    }

    /**
     * 跑步中使用截图
     * @param view
     * @return
     */
    public static Bitmap convertViewToBitmap(View view){
        view.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED), View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());
        view.buildDrawingCache();
        Bitmap bitmap = view.getDrawingCache();
        view.setDrawingCacheEnabled(false);
        return bitmap;
    }
}
