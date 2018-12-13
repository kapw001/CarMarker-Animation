# CarMarker-Animation
[![](https://jitpack.io/v/tintinscorpion/CarMarker-Animation.svg)](https://jitpack.io/#tintinscorpion/CarMarker-Animation)
<br>
Smooth marker animation on google map along with proper turns and camera bearing. 
<br><br>
# Demo
<img src="https://github.com/tintinscorpion/CarMarker-Animation/blob/master/routeanim.gif"/>

<br><br>

## Steps:
<UL>
<LI>Pass the Marker to animate, googlemap, Latlng of current position of the marker, Latlng of curent position of the user, duration of the animation & CameraUpdate of googlemap.
<pre>
<code>
<LI>CarMoveAnim.startcarAnimation(marker,googleMap, startposition,endposition,duration,cameraupdate);
</LI>
<br><br>
Here marker,googlemap,startposition refers to the position of marker,end position refers to the position of the user 
or wherever the marker needs to be placed. 
These four fields are mandatory.
</code>
</pre>
</LI>
 <br><br>
 <LI>
  <ul>Note:
   <code>
    If you are animating car onLocationChanged() then,
   Ideal location request for car animation should be as below. Greater than the interval mentioned will give
   more good results but less than this may hamper the animation.

    mLocationRequest = new LocationRequest();
    mLocationRequest.setInterval(1000 * 5);
    mLocationRequest.setFastestInterval(1000 * 3);
     mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
   </code>
 </LI>


## Dependency
<pre>
<LI>App Level:
implementation 'com.github.tintinscorpion:CarMarker-Animation:1.1'
</LI>
<br>
<LI>Project Level:
maven { url 'https://jitpack.io' }
</LI>
</pre>


 

# Developers
<UL>
<LI><a href="https://github.com/tintinscorpion">Pritam Dasgupta</a>
</UL>
