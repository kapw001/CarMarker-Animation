# CarMarker-Animation
Smooth marker animation on google map along with proper turns and camera bearing. 

<br><br>

## Steps:
<UL>
<LI>Pass the Marker to animate, googlemap, Latlng of current position of the marker, Latlng of curent position of the user, duration of the animation & CameraUpdate of googlemap.
<pre>
<code>
CarMoveAnim.startcarAnimation(marker,googleMap, startposition,endposition,duration,cameraupdate);

Here marker,googlemap,startposition refers to the position of marker,end position refers to the position of the user 
or wherever the marker needs to be placed. 
These four fields are mandatory.
</code>
</pre>
</LI>



## Dependency
<pre>
<LI>App Level:
implementation 'com.github.tintinscorpion:CarMarker-Animation:1.0'
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
