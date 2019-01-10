# CarMarker-Animation
[![](https://jitpack.io/v/tintinscorpion/CarMarker-Animation.svg)](https://jitpack.io/#tintinscorpion/CarMarker-Animation)
[![Android Arsenal](https://img.shields.io/badge/Android%20Arsenal-CarMarker--Animation-brightgreen.svg?style=flat)](https://android-arsenal.com/details/1/7423)
[![Platform](https://img.shields.io/badge/platform-android-green.svg)](http://developer.android.com/index.html)

<br>
<meta name="google-site-verification" content="9xX5qBXiwU0-eOti0o3ujCSFXmus9BTbz6Dw5FNmtm0" />
Smooth marker animation on google map along with proper turns and camera bearing. 
<br>

# Demo
<img src="https://github.com/tintinscorpion/CarMarker-Animation/blob/master/routeanim.gif?raw=true" width="250" height="400"/>

<br><br>

## Steps:

Pass the Marker to animate, googlemap, Latlng of current position of the marker, Latlng of curent position of the user, 
duration of the animation & Cancellable Callback interface of googlemap.

``` java
CarMoveAnim.startcarAnimation(marker,googleMap, startposition,endposition,duration,callback);
```
<br>
Here marker,googlemap,startposition refers to the position of marker,end position refers to the position of 
the user or wherever the marker needs to be placed. 
These four fields are mandatory.
<br><br>

Optional:
<br>

  duration refers to the animation time. By default it will take 3000 even if 0 is passed.
callback is the interface of Googlemap.CancellabeCallback(). It requires when the user wants to animate the next animation 
after the first has finished else just pass null.
<br>
For eg-

``` java
new GoogleMap.CancelableCallback() {
                @Override
                public void onFinish() {                
                }
                @Override
                public void onCancel() {               
                }
            });
```
Note:
    If you are animating car onLocationChanged() then,
   Ideal location request for car animation should be as below. Greater than the interval mentioned will give
   more good results but less than this may hamper the animation.
   ``` java
    mLocationRequest = new LocationRequest();
    mLocationRequest.setInterval(1000 * 5);
    mLocationRequest.setFastestInterval(1000 * 3);
    mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
   ``` 
 

## Dependency

```groovy
App Level:
implementation 'com.github.tintinscorpion:CarMarker-Animation:1.1'
```
```groovy
Project Level:
maven { url 'https://jitpack.io' }
```
 <br><br>

# Developers

<a href="https://github.com/tintinscorpion">Pritam Dasgupta</a>

## License

MIT License

Copyright (c) 2018 Pritam Dasgupta

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.

