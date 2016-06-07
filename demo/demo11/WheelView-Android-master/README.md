# WheelView-Android
Selector with wheel view, applicable to selecting money or other short length values.


Use with Gradle:
---

```
dependencies {
  compile 'com.lantouzi.wheelview:library:1.1.2'
}
```

Screenshot of Demo:
---
![Demo](https://raw.githubusercontent.com/lantouzi/WheelView-Android/master/preview/demo.png)

Usage
---
### Style the view in xml:

* **lwvHighlightColor** highlight color for selected item and the cursor.
* **lwvMarkColor** color of mark on normal status.
* **lwvMarkTextColor** color of mark text on normal status.
* **lwvIntervalFactor** factor for calculate interval using text width.(larger means sparser)
* **lwvMarkRatio** ratio that decides how short is the short mark than the long mark.
* **lwvCursorSize** size(width) of the cursor upside.
* **lwvMarkTextSize** text size of mark text on normal status.
* **lwvCenterMarkTextSize** text size of the center mark text (on selected status)
* **lwvAdditionalCenterMark** additional text used for unit of the center mark.

### Listener

```
public interface OnWheelItemSelectedListener {
	// Called each time when the center index changed.
	void onWheelItemChanged(WheelView wheelView, int position);

	// Called only when the center index selected and wheel never moving to others.
	void onWheelItemSelected(WheelView wheelView, int position);
}
```


### Limit scope of selection.
*(Added in 1.1.1)*

* **setMinSelectableIndex/setMaxSelectableIndex** limit min/max index whitch is selectable in code.

Check out the demo project for more information.

License
---

	Licensed under the Apache License, Version 2.0 (the "License");
	you may not use this file except in compliance with the License.
	You may obtain a copy of the License at

	   http://www.apache.org/licenses/LICENSE-2.0

	Unless required by applicable law or agreed to in writing, software
	distributed under the License is distributed on an "AS IS" BASIS,
	WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
	See the License for the specific language governing permissions and
	limitations under the License.

