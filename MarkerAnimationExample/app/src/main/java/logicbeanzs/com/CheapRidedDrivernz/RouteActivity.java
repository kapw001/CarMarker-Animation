package logicbeanzs.com.CheapRidedDrivernz;

import android.Manifest;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Color;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.transition.TransitionInflater;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.demo.basegooglemap.R;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.maps.model.SquareCap;
import com.google.maps.android.ui.IconGenerator;
import com.logicbeanzs.carrouteanimation.CarMoveAnim;

import java.util.ArrayList;
import java.util.List;

import logicbeanzs.com.CheapRidedDrivernz.Utils.ApiClient;
import logicbeanzs.com.CheapRidedDrivernz.Utils.DirectionUtils;
import logicbeanzs.com.CheapRidedDrivernz.Utils.MapAnimator;
import logicbeanzs.com.CheapRidedDrivernz.Utils.MapUtils;
import logicbeanzs.com.CheapRidedDrivernz.Utils.Placelistener;

import static com.google.android.gms.maps.model.JointType.ROUND;
import static logicbeanzs.com.CheapRidedDrivernz.Utils.MapUtils.getBearing;

public class RouteActivity extends FragmentActivity implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {
    GoogleApiClient mGoogleApiClient;
    List<LatLng> polyLineList;
    ImageButton back;
    private Location mLastLoc;
    LocationRequest mLocationRequest;
    private static final long INTERVAL = 1000 * 5; //1 minute
    private static final long FASTEST_INTERVAL = 1000 * 3;
    private PolylineOptions polylineOptions;
    private Polyline greyPolyLine;
    private SupportMapFragment mapFragment;
    private Handler handler;
    private Marker carMarker,desMarker;
    private int index;
    private int next;
    private LatLng startPosition;
    private LatLng endPosition;
    GoogleMap googleMap;
    private float v;
    private double lat, lng;
    List<String>ins;
    DirectionUtils utils;
    LinearLayout detLin;
    String htmlins = null;
    TextView direct,st_add,end_add,distance,duration;
    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(INTERVAL);
        mLocationRequest.setFastestInterval(FASTEST_INTERVAL);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(Color.TRANSPARENT);
            getWindow().setSharedElementEnterTransition(TransitionInflater.from(this)
                    .inflateTransition(R.transition.changebounds));
        }
        setContentView(R.layout.activity_route);
        Bundle bundle = getIntent().getExtras();
        if(bundle!=null)
        {
            if (bundle.containsKey("data")) {
                polyLineList = (ArrayList<LatLng>) getIntent().getSerializableExtra("data");
            }
            if (bundle.containsKey("inst"))
            {
                ins = bundle.getStringArrayList("inst");
            }
            if (bundle.containsKey("dir"))
            {
                utils = (DirectionUtils)getIntent().getSerializableExtra("dir");
            }
        }
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        detLin = findViewById(R.id.detLin);
        direct = findViewById(R.id.direct);
        direct.setSelected(true);
        st_add = findViewById(R.id.st_add);
        st_add.setSelected(true);
        st_add.setText(utils.getStAddress());
        end_add = findViewById(R.id.end_add);
        end_add.setSelected(true);
        end_add.setText(utils.getEndAddress());
        distance = findViewById(R.id.distance);
        distance.setText("Distance: "+utils.getDistance());
        duration = findViewById(R.id.duration);
        duration.setText("Estimated Time: "+utils.getDuration());
        handler = new Handler();
        back = findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              RouteActivity.super.onBackPressed();
            }
        });
    }
    @Override
    protected void onStart() {
        super.onStart();
        buildGoogleApiClient();
    }
    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }
    @Override
    protected void onStop() {
        if (mGoogleApiClient!=null && mGoogleApiClient.isConnected())
        {
            mGoogleApiClient.disconnect();
        }
        stopRepeatingTask();
        super.onStop();
    }
    @Override
    public void onConnected(@Nullable Bundle bundle) {
        createLocationRequest();
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;
        this.googleMap.getUiSettings().setCompassEnabled(false);
        this.googleMap.getUiSettings().setMapToolbarEnabled(false);
        try {
            // Customise the styling of the base map using a JSON object defined
            // in a raw resource file.
            boolean success = googleMap.setMapStyle(
                    MapStyleOptions.loadRawResourceStyle(
                            this, R.raw.custom_map));
        } catch (Resources.NotFoundException e) {
            Log.e("error", "Can't find style. Error: ", e);
        }
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int locationPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
            if (locationPermission == PackageManager.PERMISSION_GRANTED) {
                if (polyLineList!=null && polyLineList.size()>0)
                {
                    staticPolyLine();
                }
            }
        }

    }
    void staticPolyLine() {
        googleMap.clear();
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        for (LatLng latLng:polyLineList)
        {
            builder.include(latLng);
        }
        final LatLngBounds bounds = builder.build();
        googleMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
            @Override
            public void onMapLoaded() {
                CameraUpdate mCameraUpdate = CameraUpdateFactory.newLatLngBounds(bounds, 20);
                googleMap.animateCamera(mCameraUpdate);
            }
        });
        polylineOptions = new PolylineOptions();
        polylineOptions.color(Color.BLACK);
        polylineOptions.width(8);
        polylineOptions.startCap(new SquareCap());
        polylineOptions.endCap(new SquareCap());
        polylineOptions.jointType(ROUND);
        polylineOptions.addAll(polyLineList);
        greyPolyLine = googleMap.addPolyline(polylineOptions);
        startCarAnimation(polyLineList.get(0).latitude,polyLineList.get(0).longitude);
    }
    private void startCarAnimation(Double latitude, Double longitude) {
        LatLng latLng = new LatLng(latitude, longitude);
        if (carMarker!=null)
        {
            carMarker.remove();
        }
        index = -1;
        next = 1;
        carMarker = googleMap.addMarker(new MarkerOptions().position(latLng).
                flat(true).icon(BitmapDescriptorFactory.fromResource(R.mipmap.new_car_small)));
        desMarker = googleMap.addMarker(new MarkerOptions().position(polyLineList.get(polyLineList.size()-1))
                .anchor(0.5f, 0.5f).icon(BitmapDescriptorFactory.fromResource(R.mipmap.destination)));
        MapUtils.createBottomUpAnimation(this, detLin, new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                detLin.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animation animation) {

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        handler.postDelayed(pol, 3000);
    }
    Runnable pol = new Runnable() {
        @SuppressLint("SetTextI18n")
        @Override
        public void run() {
            if (index < (polyLineList.size() - 1)) {
                index++;
                next = index + 1;
            } else {
                index = -1;
                next = 1;
                direct.setText("Destination Reached.");
                stopRepeatingTask();
                return;
            }

            if (index < (polyLineList.size() - 1)) {
                startPosition = carMarker.getPosition();
                endPosition = polyLineList.get(next);
            }
            if (htmlins!=null) {
                if (!htmlins.equalsIgnoreCase(ins.get(index)))
                direct.setText(htmlins);
            }
            else
            {
                direct.setText(ins.get(index));
            }

            CarMoveAnim.startcarAnimation(carMarker,googleMap, startPosition,endPosition,3000,new GoogleMap.CancelableCallback() {
                @Override
                public void onFinish() {
                    handler.postDelayed(pol,600);
                }

                @Override
                public void onCancel() {
                    handler.postDelayed(pol,600);
                }
            });
            htmlins = ins.get(index);
        }
    };

    private void stopRepeatingTask() {
        if (pol != null) {
            handler.removeCallbacks(pol);
        }
    }


}
