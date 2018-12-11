package com.logicbeanzs.carrouteanimation;

import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;

import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

public class CarMoveAnim {

//    Ideal location request for car animation. Greater than this interval will give
// more good results but less than this may hamper the animation.

//    mLocationRequest = new LocationRequest();
//        mLocationRequest.setInterval(1000 * 5);
//        mLocationRequest.setFastestInterval(1000 * 3);
//        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

    public static void startcarAnimation(final Marker carMarker, final GoogleMap googleMap, final LatLng startPosition,
                                         final LatLng endPosition, int duration, final CameraUpdate cameraUpdateFactory) {
        ValueAnimator valueAnimator = ValueAnimator.ofFloat(0, 1);
        if (duration == 0 || duration<3000)
        {
            duration = 3000;
        }
        valueAnimator.setDuration(duration);
        final LatLngInterpolatorNew latLngInterpolator = new LatLngInterpolatorNew.LinearFixed();
        valueAnimator.setInterpolator(new LinearInterpolator());
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                float v = valueAnimator.getAnimatedFraction();
                double lng = v * endPosition.longitude + (1 - v)
                        * startPosition.longitude;
                double lat = v * endPosition.latitude + (1 - v)
                        * startPosition.latitude;
                LatLng newPos = latLngInterpolator.interpolate(v, startPosition, endPosition);
                carMarker.setPosition(newPos);
                carMarker.setAnchor(0.5f, 0.5f);
                carMarker.setRotation((float) bearingBetweenLocations(startPosition, endPosition));
                if (cameraUpdateFactory != null) {
                    googleMap.animateCamera(cameraUpdateFactory);
                } else {
                    googleMap.animateCamera(CameraUpdateFactory
                            .newCameraPosition
                                    (new CameraPosition.Builder()
                                            .target(newPos)
                                            .bearing((float) bearingBetweenLocations(startPosition, endPosition))
                                            .zoom(18f)
                                            .build()));
                }
            }
        });
        valueAnimator.start();
    }

    private static double bearingBetweenLocations(LatLng latLng1,LatLng latLng2) {

        double PI = 3.14159;
        double lat1 = latLng1.latitude * PI / 180;
        double long1 = latLng1.longitude * PI / 180;
        double lat2 = latLng2.latitude * PI / 180;
        double long2 = latLng2.longitude * PI / 180;
        double dLon = (long2 - long1);
        double y = Math.sin(dLon) * Math.cos(lat2);
        double x = Math.cos(lat1) * Math.sin(lat2) - Math.sin(lat1)
                * Math.cos(lat2) * Math.cos(dLon);

        double brng = Math.atan2(y, x);

        brng = Math.toDegrees(brng);
        brng = (brng + 360) % 360;

        return brng;
    }
    public interface LatLngInterpolatorNew {
        LatLng interpolate(float fraction, LatLng a, LatLng b);
        class LinearFixed implements LatLngInterpolatorNew {
            @Override
            public LatLng interpolate(float fraction, LatLng a, LatLng b) {
                double lat = (b.latitude - a.latitude) * fraction + a.latitude;
                double lngDelta = b.longitude - a.longitude;
                // Take the shortest path across the 180th meridian.
                if (Math.abs(lngDelta) > 180) {
                    lngDelta -= Math.signum(lngDelta) * 360;
                }
                double lng = lngDelta * fraction + a.longitude;
                return new LatLng(lat, lng);
            }
        }
    }
}
