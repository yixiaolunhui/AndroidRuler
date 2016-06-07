# RulerView
Android RulerView

##介绍
类似尺子的控件，可以根据设定的值通过手势或手动设置来选择一个值。

##功能
*   支持手势: `springBack`、`fling`
*   支持OverScroll: `OverScroller`
*   自动对齐中心: `setAutoAlign(TRUE）`
*   对齐顶部或底部: `setGravity`使用`Gravity.Top`或`Gravity.Bottom`
*   平滑滚动： `smoothScrollToPosition`、`smoothScrollToValue`
*   动态获取值: 使用`OnScaleListener`监听值的变化

##基本使用
```xml
   <com.lw.widget.RulerView
        android:id="@+id/ruler_view"
        android:layout_width="match_parent"
        android:layout_height="50dp"
        android:gravity="top"
        app:begin="0"
        app:end="100"
        app:textColor="@android:color/holo_blue_dark"
        app:indicateColor="@android:color/holo_blue_dark"
        app:indicatePadding="10dp"
        app:indicateWidth="5dp"
        app:textSize="18sp"
    />
```

####应该设置一个固定高度，因为还没有实现`onMeasure`方法。

##效果图
![preview](preview.gif)
