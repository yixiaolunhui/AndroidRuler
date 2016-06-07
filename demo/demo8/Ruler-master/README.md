# Ruler
a simple RulerView for android

#How to use
created by XML like this:
```JAVA
<com.seek.ruler.SimpleRulerView
        android:layout_marginTop="10dp"
        android:id="@+id/height_ruler"
        seek:minValue ="1.0"
        seek:maxValue ="2.0"
        seek:intervalValue="0.01"
        seek:retainLength ="2"
        seek:rulerColor="@color/green"
        seek:textColor ="@color/green"
        android:layout_width="match_parent"
        android:layout_height="60dp"
        />
```
 or created by code like this :
 ```JAVA
 SimpleRulerView s = new SimpleRulerView(this);
		s.setMaxValue(2.00f);
		s.setMinValue(1.00f);
		s.setIntervalValue(0.01F);
		s.setRetainLength(2);
		s.setSelectedValue(1.7f);
		s.setOnValueChangeListener(this);
		...
```		
![](https://github.com/pruas/Ruler/raw/master/test.png)	


