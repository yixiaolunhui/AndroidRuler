#博客: http://blog.csdn.net/zbjdsbj/article/details/51180011
ScaleRuler
身高、体重横向滚动刻度尺选择器

更新：
1、增加体重支持小数点的选择方式的自定义View；
2、增加一个倾斜的TextView的自定义View实现；
3、增加一些辅助工具类

使用：

更新：

```html
<com.lost.zou.scaleruler.view.DecimalScaleRulerView
            android:id="@+id/ruler_weight"
            android:layout_width="match_parent"
            android:layout_height="58dp"
            android:layout_alignParentBottom="true" />
            
            
```

```html
mWeightRulerView.setParam(DrawUtil.dip2px(10), DrawUtil.dip2px(32), DrawUtil.dip2px(24),
                DrawUtil.dip2px(14), DrawUtil.dip2px(9), DrawUtil.dip2px(12));
        mWeightRulerView.initViewParam(mWeight, 20.0f, 200.0f, 1);
        mWeightRulerView.setValueChangeListener(new DecimalScaleRulerView.OnValueChangeListener() {
            @Override
            public void onValueChange(float value) {
                mWeightValueTwo.setText(value + "kg");
                mWeight = value;
            }
        });
```

```html
<com.lost.zou.scaleruler.view.SlantedTextView
            android:layout_width="48dp"
            android:layout_height="48dp"
            app:slantedBackgroundColor="@color/colorPrimary"
            app:slantedLength="28dp"
            app:slantedMode="left"
            app:slantedText="体重"
            app:slantedTextColor="#ffffff"
            app:slantedTextSize="14sp" />
```

```html
<com.lost.zou.scaleruler.view.ScaleRulerView
        android:id="@+id/scaleWheelView_height"
        android:layout_width="match_parent"
        android:layout_height="45dp"
        android:layout_marginTop="36dp"
        android:background="@android:color/white"
        android:paddingLeft="30dp"
        android:paddingRight="30dp" />
```

效果图如下：
![image](https://github.com/ZBJDSBJ/ScaleRuler/blob/master/app/src/main/res/raw/scaleruler5.jpg)


![image](https://github.com/ZBJDSBJ/ScaleRuler/blob/master/app/src/main/res/raw/scaleruler1.jpg)

![image](https://github.com/ZBJDSBJ/ScaleRuler/blob/master/app/src/main/res/raw/scaleruler2.jpg)

![image](https://github.com/ZBJDSBJ/ScaleRuler/blob/master/app/src/main/res/raw/scaleruler3.jpg)

![image](https://github.com/ZBJDSBJ/ScaleRuler/blob/master/app/src/main/res/raw/scaleruler4.jpg)





