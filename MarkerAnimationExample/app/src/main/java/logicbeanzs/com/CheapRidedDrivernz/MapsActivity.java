package logicbeanzs.com.CheapRidedDrivernz;
import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Point;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v7.widget.CardView;
import android.transition.ChangeBounds;
import android.transition.Explode;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.demo.basegooglemap.R;

import logicbeanzs.com.CheapRidedDrivernz.Utils.CarListener;
import logicbeanzs.com.CheapRidedDrivernz.Utils.CommonUiUtils;
import logicbeanzs.com.CheapRidedDrivernz.Utils.CustomCarSelect;
import logicbeanzs.com.CheapRidedDrivernz.Utils.CustomDirection;
import logicbeanzs.com.CheapRidedDrivernz.Utils.CustomMapClusterRenderer;
import logicbeanzs.com.CheapRidedDrivernz.Utils.CustomModel;
import logicbeanzs.com.CheapRidedDrivernz.Utils.DirectionListener;
import logicbeanzs.com.CheapRidedDrivernz.Utils.DirectionUtils;
import logicbeanzs.com.CheapRidedDrivernz.Utils.DirectionsJSONParser;
import logicbeanzs.com.CheapRidedDrivernz.Utils.MapAnimator;
import logicbeanzs.com.CheapRidedDrivernz.Utils.MapUtils;
import logicbeanzs.com.CheapRidedDrivernz.Utils.MyItem;
import logicbeanzs.com.CheapRidedDrivernz.Utils.NoInternetDialog;
import logicbeanzs.com.CheapRidedDrivernz.Utils.ParallaxPageTransformer;
import logicbeanzs.com.CheapRidedDrivernz.Utils.Place;
import logicbeanzs.com.CheapRidedDrivernz.Utils.Placelistener;
import logicbeanzs.com.CheapRidedDrivernz.Utils.UltraPagerAdapter;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.Projection;
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
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.ui.IconGenerator;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;
import com.tmall.ultraviewpager.UltraViewPager;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static logicbeanzs.com.CheapRidedDrivernz.Utils.MapUtils.getScreenHeight;
import static logicbeanzs.com.CheapRidedDrivernz.Utils.MapUtils.getScreenWidth;


public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, LocationListener ,
        Placelistener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,DirectionListener,
        GoogleMap.OnCameraIdleListener,CarListener {

    private GoogleMap googleMap;
    private static final long DELAY = 4500;
    private static final long ANIMATION_TIME_PER_ROUTE = 3000;
    String polyLine = "q`epCakwfP_@EMvBEv@iSmBq@GeGg@}C]mBS{@KTiDRyCiBS";
    private PolylineOptions polylineOptions;
    private Polyline greyPolyLine;
    private SupportMapFragment mapFragment;
    private Handler handler;
    private Marker carMarker,desMarker,carMarker2,carMarker3,desMarker2,desMarker3;
    private int index;
    private int next;
    private LatLng startPosition;
    private LatLng endPosition;
    private float v;
    Button button2;
    List<LatLng> polyLineList;
    List<String> instruct;
    private double lat, lng;
    // banani
    double latitude = 23.7877649;
    double longitude = 90.4007049;
    private String TAG = "HomeActivity";
    GoogleApiClient mGoogleApiClient;
    // Give your Server URL here >> where you get car location update
    public static final String URL_DRIVER_LOCATION_ON_RIDE = "*******";
    private boolean isFirstPosition = true;
    private Double startLatitude;
    private Double startLongitude;
    private Location mLastLoc;
    LocationRequest mLocationRequest;
    private static final long INTERVAL = 1000 * 5; //1 minute
    private static final long FASTEST_INTERVAL = 1000 * 3;
    JSONObject ob1, ob2, ob3, ob4, ob5, ob6, ob7, ob8, ob9, ob10;
    CardView destCard;
    ImageButton back;
    Button dest;
    FloatingActionButton fab;
    SlidingUpPanelLayout sliding_layout;
    Button fetch;
    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(INTERVAL);
        mLocationRequest.setFastestInterval(FASTEST_INTERVAL);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }
    UltraViewPager mRecyclerView;
    ViewGroup.MarginLayoutParams marginLayoutParams;
    LinearLayout detLin;
    ImageView img;
    TextView txt;
    NoInternetDialog noInternetDialog;
    @Override
    protected void onDestroy() {
        super.onDestroy();
        noInternetDialog.onDestroy();
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
        }
        setContentView(R.layout.activity_maps);
        noInternetDialog = new NoInternetDialog.Builder(this).build();
        noInternetDialog.setCancelable(false);
        fab = findViewById(R.id.fab);
        fab.hide();
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               Intent intent = new Intent(MapsActivity.this,RouteActivity.class);
               intent.putExtra("data", (Serializable) polyLineList);
               intent.putStringArrayListExtra("inst", (ArrayList<String>) instruct);
               intent.putExtra("dir",util);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    back.setTransitionName("back");
                    ActivityOptionsCompat bundle = ActivityOptionsCompat.makeSceneTransitionAnimation(MapsActivity.this,back,back.getTransitionName());
                    startActivity(intent,bundle.toBundle());
                }
                else {
                    startActivity(intent);
                }
            }
        });
        CustomModel.getInstance().setListener(this);
        CustomDirection.getInstance().setListener(this);
        CustomCarSelect.getInstance().setListener(this);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
         mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
        handler = new Handler();
        dest = findViewById(R.id.destBtn);
        destCard = findViewById(R.id.destCard);
        detLin = findViewById(R.id.detLin);
        img = findViewById(R.id.img);
        txt = findViewById(R.id.txt);
        fetch = findViewById(R.id.fetch);
        sliding_layout = findViewById(R.id.sliding_layout);
        sliding_layout.setPanelHeight(0);
//        sliding_layout.setAnchorPoint((float)getScreenHeight()/3); // slide up 50% then stop
        sliding_layout.setPanelState(SlidingUpPanelLayout.PanelState.HIDDEN);
        MapUtils.createTopDownAnimation(this,destCard,null);
        back = findViewById(R.id.back);
        mRecyclerView = findViewById(R.id.recycle);
        marginLayoutParams =
                (ViewGroup.MarginLayoutParams) mRecyclerView.getLayoutParams();
        mRecyclerView.setScrollMode(UltraViewPager.ScrollMode.HORIZONTAL);
        marginLayoutParams.height = getScreenHeight()/3;
        ((FrameLayout.LayoutParams) fab.getLayoutParams()).bottomMargin = (int) (getScreenHeight()/2.7);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (googleMap!=null)
                {
                    googleMap.clear();
                    polyLineList = null;
                    instruct = null;
                }
//                googleMap.setPadding(0,0,0,0);
//                googleMap.setPadding(0, 40, 0, 40);
                sliding_layout.setPanelHeight(0);
                sliding_layout.setPanelState(SlidingUpPanelLayout.PanelState.HIDDEN);
                fab.hide();
                MapUtils.createBottomUpAnimation(MapsActivity.this,back,backtop);
                position = new CameraPosition.Builder()
                        .target(new LatLng(mLastLoc.getLatitude(), mLastLoc.getLongitude()))
                        .zoom(16f)
                        .build();

                CameraUpdate update = CameraUpdateFactory
                        .newCameraPosition
                                (position);
                googleMap.animateCamera(update);
                if (carMarker != null) {
                    carMarker.remove();
                }
                carMarker = googleMap.addMarker(new MarkerOptions().position(new LatLng(mLastLoc.getLatitude(), mLastLoc.getLongitude())).
                            flat(true).icon(BitmapDescriptorFactory.fromResource(R.mipmap.source)));
            }
        });
        createLocationRequest();
        dest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MapsActivity.this, SearchActivity.class);
                intent.putExtra("lat", mLastLoc.getLatitude());
                intent.putExtra("lon",mLastLoc.getLongitude());
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                    ActivityOptionsCompat optionsCompat = ActivityOptionsCompat
                            .makeSceneTransitionAnimation(MapsActivity.this, destCard, destCard.getTransitionName());
                    startActivity(intent, optionsCompat.toBundle());
                } else
                    startActivity(intent);
            }
        });
        FrameLayout frameLayout = findViewById(R.id.sliderLin);
        marginLayoutParams =
                (ViewGroup.MarginLayoutParams) frameLayout.getLayoutParams();
        marginLayoutParams.height = getScreenHeight()/2;
        frameLayout.setLayoutParams(marginLayoutParams);
    }

    Place placeGet;
    SlidingUpPanelLayout.PanelState state = SlidingUpPanelLayout.PanelState.COLLAPSED;
    @Override
    public void onPlace(Place place) {
        this.placeGet = place;
        MapUtils.createBottomUpAnimation(this,destCard,bottomup);
        sliding_layout.setPanelHeight((int) (getScreenHeight()/2.5));
        marginLayoutParams =
                (ViewGroup.MarginLayoutParams) mRecyclerView.getLayoutParams();
        marginLayoutParams.height = (int) (getScreenHeight()/3.5);
        mRecyclerView.setLayoutParams(marginLayoutParams);
        PagerAdapter adapter = new UltraPagerAdapter(false,mRecyclerView);
        mRecyclerView.setAdapter(adapter);
        mRecyclerView.setInfiniteLoop(false);
        mRecyclerView.setPageTransformer(false,new ParallaxPageTransformer(this));
        sliding_layout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
        detLin.getLayoutParams().height = getScreenHeight()/2;
        sliding_layout.setDragView(findViewById(R.id.sliderLin));
        fetch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (googleMap!=null)
                {
                    googleMap.clear();
                    polyLineList = null;
                    instruct = null;
                }
//                googleMap.setPadding(0,0,0,0);
//                googleMap.setPadding(0, 40, 0, 40);
                sliding_layout.setPanelHeight(0);
                sliding_layout.setPanelState(SlidingUpPanelLayout.PanelState.HIDDEN);
                fab.hide();
                MapUtils.createBottomUpAnimation(MapsActivity.this,back,backtop);
                position = new CameraPosition.Builder()
                        .target(new LatLng(mLastLoc.getLatitude(), mLastLoc.getLongitude()))
                        .zoom(16f)
                        .build();

                CameraUpdate update = CameraUpdateFactory
                        .newCameraPosition
                                (position);
                googleMap.animateCamera(update);
                if (carMarker != null) {
                    carMarker.remove();
                }
                carMarker = googleMap.addMarker(new MarkerOptions().position(new LatLng(mLastLoc.getLatitude(), mLastLoc.getLongitude())).
                        flat(true).icon(BitmapDescriptorFactory.fromResource(R.mipmap.source)));
                CommonUiUtils.showDial(MapsActivity.this);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        CommonUiUtils.disDial();
                    }
                },10000);
            }
        });
        sliding_layout.addPanelSlideListener(new SlidingUpPanelLayout.PanelSlideListener() {
            @Override
            public void onPanelSlide(View panel, float slideOffset) {
                Log.i(TAG, "onPanelSlide, offset " + slideOffset);
                mRecyclerView.setAlpha(1 - slideOffset);
                fetch.setAlpha(1 - slideOffset);
                detLin.setAlpha(slideOffset);
                fab.hide();
            }

            @Override
            public void onPanelStateChanged(View panel, SlidingUpPanelLayout.PanelState previousState, SlidingUpPanelLayout.PanelState newState) {
                Log.i(TAG, "onPanelStateChanged " + newState);
                if(newState.equals(SlidingUpPanelLayout.PanelState.EXPANDED)){
                    if (polyLineList!=null && polyLineList.size()>0) {
                        ((FrameLayout.LayoutParams) fab.getLayoutParams()).bottomMargin = (int) (getScreenHeight() / 2.18);
                        fab.show();
                    }
                    mRecyclerView.setAlpha(0f);
                    fetch.setAlpha(0f);
                    detLin.setAlpha(1f);
                }
                else if(newState.equals(SlidingUpPanelLayout.PanelState.COLLAPSED)){
                    if (polyLineList!=null && polyLineList.size()>0) {
                        ((FrameLayout.LayoutParams) fab.getLayoutParams()).bottomMargin = (int) (getScreenHeight() / 2.7);
                        fab.show();
                    }
                    mRecyclerView.setAlpha(1f);
                    fetch.setAlpha(1f);
                    detLin.setAlpha(0f);
                }
                else if(newState.equals(SlidingUpPanelLayout.PanelState.HIDDEN))
                {
                    fab.hide();
                }
            }
        });

        sliding_layout.setFadeOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mRecyclerView.setAlpha(1f);
                fetch.setAlpha(1f);
                detLin.setAlpha(0f);
                sliding_layout.setPanelState (SlidingUpPanelLayout.PanelState.COLLAPSED);
            }
        });

        String url = getDirectionsUrl(place.getSrcLatLng(), place.getDesrLatLng());
        DownloadTask downloadTask = new DownloadTask();
        downloadTask.execute(url);
    }

    @Override
    public void onCarSelect(JSONObject ob, int position,ImageView imgs) {
        try {
            img.setImageResource(ob.getInt("img"));
            txt.setText(ob.getString("name"));
            if (position == 1000)
            {
                sliding_layout.setPanelState(SlidingUpPanelLayout.PanelState.EXPANDED);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    DirectionUtils util;
    @Override
    public void onDirectionsData(DirectionUtils utils) {
      this.util = utils;
    }

   Animation.AnimationListener bottomup = new Animation.AnimationListener() {
       @Override
       public void onAnimationStart(Animation animation) {
           dest.setEnabled(false);
           destCard.setVisibility(View.GONE);
       }

       @Override
       public void onAnimationEnd(Animation animation) {
           destCard.setVisibility(View.GONE);
           destCard.clearAnimation();
           MapUtils.createTopDownAnimation(MapsActivity.this,back,uptop);
       }

       @Override
       public void onAnimationRepeat(Animation animation) {

       }
   };
    Animation.AnimationListener uptop = new Animation.AnimationListener() {
        @Override
        public void onAnimationStart(Animation animation) {
            destCard.setVisibility(View.GONE);
            back.setVisibility(View.VISIBLE);
        }

        @Override
        public void onAnimationEnd(Animation animation) {
//            googleMap.setPadding(0,0,0,0);
//            googleMap.setPadding(0, 90, 0, getScreenHeight()/3);
        }

        @Override
        public void onAnimationRepeat(Animation animation) {

        }
    };
    Animation.AnimationListener backtop = new Animation.AnimationListener() {
        @Override
        public void onAnimationStart(Animation animation) {

        }

        @Override
        public void onAnimationEnd(Animation animation) {
            back.setVisibility(View.GONE);
            MapUtils.createTopDownAnimation(MapsActivity.this,destCard,backdest);
        }

        @Override
        public void onAnimationRepeat(Animation animation) {

        }
    };
    Animation.AnimationListener backdest = new Animation.AnimationListener() {
        @Override
        public void onAnimationStart(Animation animation) {
            dest.setEnabled(true);
            destCard.setVisibility(View.VISIBLE);
        }

        @Override
        public void onAnimationEnd(Animation animation) {

        }

        @Override
        public void onAnimationRepeat(Animation animation) {

        }
    };
    @Override
    public void onResume() {
        super.onResume();
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            if (mGoogleApiClient!=null && mGoogleApiClient.isConnected()) {
                LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
            }

        }
    }
    @Override
    public void onConnected(Bundle bundle) {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(INTERVAL);
        mLocationRequest.setFastestInterval(FASTEST_INTERVAL);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            if (mGoogleApiClient.isConnected()) {
                LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
            }

        }

    }
    @Override
    public void onBackPressed()
    {
        if (sliding_layout!=null && sliding_layout.getPanelState()== SlidingUpPanelLayout.PanelState.EXPANDED)
        {
            mRecyclerView.setAlpha(1f);
            fetch.setAlpha(1f);
            detLin.setAlpha(0f);
            sliding_layout.setPanelState (SlidingUpPanelLayout.PanelState.COLLAPSED);
        }
        else
        {
            super.onBackPressed();
        }
    }
    @Override
    public void onConnectionSuspended(int i) {

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
    protected void onStart() {
        super.onStart();
       buildGoogleApiClient();
    }

    @Override
    protected void onStop() {
        if (mGoogleApiClient!=null && mGoogleApiClient.isConnected())
        {
            mGoogleApiClient.disconnect();
        }
        super.onStop();
    }



    private void makeLocationPermissionRequest() {
        ActivityCompat.requestPermissions(this,
                new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 1);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case 1: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                        if (mGoogleApiClient.isConnected()) {
                            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
                        }
                        googleMap.setOnCameraIdleListener(this);
                    }
                } else {
                    makeLocationPermissionRequest();
                }
            }
        }
    }


    private String getDirectionsUrl(LatLng origin,LatLng dest) {
        String str_origin = "origin="+origin.latitude+","+origin.longitude;
        String str_dest = "destination="+dest.latitude+","+dest.longitude;
        String sensor = "sensor=false";
        String parameters = str_origin+"&"+str_dest+"&key="+getString(R.string.google_maps_key);
        String output = "json";
        String url = "https://maps.googleapis.com/maps/api/directions/"+output+"?"+parameters;
        Log.e("Route UrL", url);
        return url;
    }
    private String downloadUrl(String strUrl) throws IOException {
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try{
            URL url = new URL(strUrl);
            // Creating an http connection to communicate with url
            urlConnection = (HttpURLConnection) url.openConnection();
            // Connecting to url
            urlConnection.connect();
            // Reading data from url
            iStream = urlConnection.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));
            StringBuilder sb  = new StringBuilder();
            String line = "";
            while( ( line = br.readLine())  != null){
                sb.append(line);
            }
            data = sb.toString();
            br.close();
        }catch(Exception e){
            Log.d("Exceptiondownloading", e.toString());
        }finally{
            if (iStream != null) {
                iStream.close();
            }
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }
        return data;
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }




    @SuppressLint("StaticFieldLeak")
    private class DownloadTask extends AsyncTask<String, Void, String> {
        // Downloading data in non-ui thread
        @Override
        protected String doInBackground(String... url) {
            // For storing data from web service
            String data = "";
            try{
                // Fetching the data from web service
                data = downloadUrl(url[0]);
            }catch(Exception e){
                Log.d("Background Task",e.toString());
            }
            return data;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            ParserTask parserTask = new ParserTask();
            // Invokes the thread for parsing the JSON data
            parserTask.execute(result);
        }
    }
    @SuppressLint("StaticFieldLeak")
    private class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String,String>>> > {

        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {

            JSONObject jObject;
            List<List<HashMap<String, String>>> routes = null;

            try{
                jObject = new JSONObject(jsonData[0]);
                DirectionsJSONParser parser = new DirectionsJSONParser();
                Log.e("JsonObject", jObject.toString());
                routes = parser.parse(jObject);

            }catch(Exception e){
                e.printStackTrace();
            }
            return routes;
        }

        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> result) {
            if (result!=null && result.size()>0) {
                polyLineList = new ArrayList<>();
                instruct = new ArrayList<>();
                for (int i = 0; i < result.size(); i++) {
                    List<HashMap<String, String>> path = result.get(i);
                    for (int j = 0; j < path.size(); j++) {
                        HashMap<String, String> point = path.get(j);
                        double lat = Double.parseDouble(point.get("lat"));
                        double lng = Double.parseDouble(point.get("lng"));
                        LatLng position = new LatLng(lat, lng);
                        polyLineList.add(position);
                        instruct.add(point.get("instructions"));
                    }
                }
                staticPolyLine();
            }
        }
    }
    void staticPolyLine() {
        googleMap.clear();
        Log.e("Size", String.valueOf(polyLineList.get(0)));
        final int MAP_BOUND_PADDING = 180;
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        for (LatLng latLng:polyLineList)
        {
            builder.include(latLng);
        }
        final LatLngBounds bounds = builder.build();
//        googleMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
//            @Override
//            public void onMapLoaded() {
                CameraUpdate mCameraUpdate = CameraUpdateFactory.newLatLngBounds(bounds, MAP_BOUND_PADDING);
                googleMap.animateCamera(mCameraUpdate);
//            }
//        });

        MapAnimator.getInstance().animateRoute(googleMap, polyLineList);
        fab.show();
        startCarAnimation(polyLineList.get(0).latitude,polyLineList.get(0).longitude);
    }
    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;
        this.googleMap.getUiSettings().setCompassEnabled(false);
        this.googleMap.getUiSettings().setMapToolbarEnabled(false);
//        this.googleMap.setTrafficEnabled(true);
        try {
            // Customise the styling of the base map using a JSON object defined
            // in a raw resource file.
            boolean success = googleMap.setMapStyle(
                    MapStyleOptions.loadRawResourceStyle(
                            this, R.raw.custom_map));
        } catch (Resources.NotFoundException e) {
            Log.e("error", "Can't find style. Error: ", e);
        }
        googleMap.setPadding(0, 40, 0, 40);
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int locationPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
            int locationPermission2 = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION);
            if (locationPermission != PackageManager.PERMISSION_GRANTED && locationPermission2 != PackageManager.PERMISSION_GRANTED) {
                makeLocationPermissionRequest();
            } else {
                if (mGoogleApiClient.isConnected()) {
                    LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
                }
                googleMap.setOnCameraIdleListener(this);
            }
        }
//        setUpClusterer();
    }
    CameraPosition position;
    @Override
    public void onLocationChanged(final Location location) {
        Log.e("------>","Chnaged.");
        if (polyLineList == null || polyLineList.size() ==0) {
            fab.hide();
            if (mLastLoc == null) {
                position = new CameraPosition.Builder()
                        .target(new LatLng(location.getLatitude(), location.getLongitude()))
                        .zoom(14f)
                        .build();
                CameraUpdate update = CameraUpdateFactory
                        .newCameraPosition
                                (position);
                googleMap.animateCamera(update);
                if (carMarker == null) {
                    carMarker = googleMap.addMarker(new MarkerOptions().position(new LatLng(location.getLatitude(), location.getLongitude())).
                            flat(true).icon(BitmapDescriptorFactory.fromResource(R.mipmap.source)));
                }
            }
            else {
                MapUtils.animateMarker(location,carMarker);
                googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude()
                        ,location.getLongitude()),14f));
            }
        }
        mLastLoc = location;
    }
    private ClusterManager<MyItem> mClusterManager;
    private void setUpClusterer() {
        mClusterManager = new ClusterManager<MyItem>(this, googleMap);
        googleMap.setOnCameraIdleListener(mClusterManager);
        googleMap.setOnMarkerClickListener(mClusterManager);
        mClusterManager.setAnimation(false);
        mClusterManager.setRenderer(new CustomMapClusterRenderer<>(this, googleMap, mClusterManager));
    }
    TextView tv_timing_right;
    private void startCarAnimation(Double latitude, Double longitude) {
        startCarAnimations(latitude,longitude);
//        LatLng latLng = new LatLng(latitude, longitude);
//        if (carMarker!=null)
//        {
//            carMarker.remove();
//        }
//        index = -1;
//        next = 1;
//        IconGenerator iconFactory_left = new IconGenerator(this);
//        iconFactory_left.setBackground(null);
//        View view_left = View.inflate(this, R.layout.custom_marker_infowindow_right, null);
//        iconFactory_left.setContentView(view_left);
//        LinearLayout ll_marker_left = view_left.findViewById(R.id.ll_marker_right);
//        TextView tv_timing_left = view_left.findViewById(R.id.tv_timing_right);
//        tv_timing_left.setText("5"+"\n"+"mins");
//        TextView tv_address_left = view_left.findViewById(R.id.tv_address_right);
//        tv_address_left.setText(placeGet.getSrcAddress());
//        int width_ll_left = ll_marker_left.getLayoutParams().width;
//        ImageView img = view_left.findViewById(R.id.iv_marker_right);
//        img.setImageResource(R.mipmap.source);
//        ll_marker_left.setPadding(240, 0, 0, 0);
//        carMarker = googleMap.addMarker(new MarkerOptions().position(latLng)
//                .anchor(0.5f, 0.5f)
//                .icon(BitmapDescriptorFactory.fromBitmap(iconFactory_left.makeIcon("current"))));
//
//        IconGenerator iconFactory_lef2 = new IconGenerator(this);
//        iconFactory_lef2.setBackground(null);
//        View view_lef2 = View.inflate(this, R.layout.custom_marker_infowindow_left, null);
//        iconFactory_lef2.setContentView(view_lef2);
//        LinearLayout ll_marker_lef2 = view_lef2.findViewById(R.id.ll_marker_right);
//        TextView tv_timing_lef2 = view_lef2.findViewById(R.id.tv_timing_right);
//        tv_timing_lef2.setText("5"+"\n"+"mins");
//        TextView tv_address_lef2 = view_lef2.findViewById(R.id.tv_address_right);
//        tv_address_lef2.setText(placeGet.getSrcAddress());
//        int width_ll_lef2 = ll_marker_lef2.getLayoutParams().width;
//        ll_marker_lef2.setPadding(0, 0, 220, 0);
//        carMarker2 = googleMap.addMarker(new MarkerOptions().position(latLng)
//                .anchor(0.5f, 0.5f)
//                .icon(BitmapDescriptorFactory.fromBitmap(iconFactory_lef2.makeIcon("current2"))));
//        carMarker2.setVisible(false);
//
//        IconGenerator iconFactory_right = new IconGenerator(this);
//        iconFactory_right.setBackground(null);
//        View view_right = View.inflate(this, R.layout.custom_marker_infowindow_right, null);
//        iconFactory_right.setContentView(view_right);
//        tv_timing_right = view_right.findViewById(R.id.tv_timing_right);
//        if (util!=null) {
//            tv_timing_right.setText(util.getDuration());
//        }
//        TextView tv_address_right = view_right.findViewById(R.id.tv_address_right);
//        tv_address_right.setText(placeGet.getDestAddress());
//        LinearLayout ll_marker_right = view_right.findViewById(R.id.ll_marker_right);
//        int width_ll_right = ll_marker_right.getLayoutParams().width;
//        ll_marker_right.setPadding(200, 0, 0, 0);
//        desMarker = googleMap.addMarker(new MarkerOptions().position(polyLineList.get(polyLineList.size()-1))
//                .anchor(0.5f, 0.5f)
//                .icon(BitmapDescriptorFactory.fromBitmap(iconFactory_right.makeIcon("destination"))));
//
//        IconGenerator iconFactory_righ2 = new IconGenerator(this);
//        iconFactory_righ2.setBackground(null);
//        View view_righ2 = View.inflate(this, R.layout.custom_marker_infowindow_left, null);
//        iconFactory_righ2.setContentView(view_righ2);
//        tv_timing_right = view_righ2.findViewById(R.id.tv_timing_right);
//        if (util!=null) {
//            tv_timing_right.setText(util.getDuration());
//        }
//        TextView tv_address_righ2 = view_righ2.findViewById(R.id.tv_address_right);
//        tv_address_righ2.setText(placeGet.getDestAddress());
//        LinearLayout ll_marker_righ2 = view_righ2.findViewById(R.id.ll_marker_right);
//        int width_ll_righ2 = ll_marker_righ2.getLayoutParams().width;
//        ImageView iv_marker_right = view_righ2.findViewById(R.id.iv_marker_right);
//        iv_marker_right.setImageResource(R.mipmap.destination);
//        ll_marker_righ2.setPadding(0, 0, 180, 0);
//        desMarker2 = googleMap.addMarker(new MarkerOptions().position(polyLineList.get(polyLineList.size()-1))
//                .anchor(0.5f, 0.5f)
//                .icon(BitmapDescriptorFactory.fromBitmap(iconFactory_righ2.makeIcon("destination2"))));
//        desMarker2.setVisible(false);
    }
    private void startCarAnimations(Double latitude, Double longitude) {
        LatLng latLng = new LatLng(latitude, longitude);
        if (carMarker != null) {
            carMarker.remove();
        }
        index = -1;
        next = 1;
        carMarker = googleMap.addMarker(new MarkerOptions().position(latLng)
                .anchor(0.5f, 0.5f)
                .icon(BitmapDescriptorFactory.fromResource(R.mipmap.source)));
        desMarker = googleMap.addMarker(new MarkerOptions().position(polyLineList.get(polyLineList.size()-1))
                .anchor(0.5f, 0.5f)
                .icon(BitmapDescriptorFactory.fromResource(R.mipmap.destination)));

        IconGenerator iconFactory_left = new IconGenerator(this);
        iconFactory_left.setBackground(null);
        View view_left = View.inflate(this, R.layout.custom_marker_infowindow_right, null);
        iconFactory_left.setContentView(view_left);
        LinearLayout ll_marker_left = view_left.findViewById(R.id.ll_marker_right);
        TextView tv_timing_left = view_left.findViewById(R.id.tv_timing_right);
        tv_timing_left.setText("5 mins");
        TextView tv_address_left = view_left.findViewById(R.id.tv_address_right);
        tv_address_left.setText(placeGet.getSrcAddress());
        ll_marker_left.setPadding((int) (getScreenWidth()/1.9), 0, 0, 0);
        carMarker2 = googleMap.addMarker(new MarkerOptions().position(latLng)
                .anchor(0.5f, 0.5f)
                .icon(BitmapDescriptorFactory.fromBitmap(iconFactory_left.makeIcon("current"))));

        IconGenerator iconFactory_lef2 = new IconGenerator(this);
        iconFactory_lef2.setBackground(null);
        View view_lef2 = View.inflate(this, R.layout.custom_marker_infowindow_right, null);
        iconFactory_lef2.setContentView(view_lef2);
        LinearLayout ll_marker_lef2 = view_lef2.findViewById(R.id.ll_marker_right);
        TextView tv_timing_lef2 = view_lef2.findViewById(R.id.tv_timing_right);
        tv_timing_lef2.setText("5 mins");
        TextView tv_address_lef2 = view_lef2.findViewById(R.id.tv_address_right);
        tv_address_lef2.setText(placeGet.getSrcAddress());
        ll_marker_lef2.setPadding(0, 0, (int) (getScreenWidth()/1.9), 0);
        carMarker3 = googleMap.addMarker(new MarkerOptions().position(latLng)
                .anchor(0.5f, 0.5f)
                .icon(BitmapDescriptorFactory.fromBitmap(iconFactory_lef2.makeIcon("current2"))));
        carMarker3.setVisible(false);


        IconGenerator iconFactory_right = new IconGenerator(this);
        iconFactory_right.setBackground(null);
        View view_right = View.inflate(this, R.layout.custom_marker_infowindow_right, null);
        iconFactory_right.setContentView(view_right);
        tv_timing_right = view_right.findViewById(R.id.tv_timing_right);
        if (util!=null) {
            tv_timing_right.setText(util.getDuration());
        }
        TextView tv_address_right = view_right.findViewById(R.id.tv_address_right);
        tv_address_right.setText(placeGet.getDestAddress());
        LinearLayout ll_marker_right = view_right.findViewById(R.id.ll_marker_right);
        ll_marker_right.setPadding((int) (getScreenWidth()/1.9), 0, 0, 0);
        desMarker2 = googleMap.addMarker(new MarkerOptions().position(polyLineList.get(polyLineList.size()-1))
                .anchor(0.5f, 0.5f)
                .icon(BitmapDescriptorFactory.fromBitmap(iconFactory_right.makeIcon("destination"))));

        IconGenerator iconFactory_righ2 = new IconGenerator(this);
        iconFactory_righ2.setBackground(null);
        View view_righ2 = View.inflate(this, R.layout.custom_marker_infowindow_right, null);
        iconFactory_righ2.setContentView(view_righ2);
        tv_timing_right = view_righ2.findViewById(R.id.tv_timing_right);
        if (util!=null) {
            tv_timing_right.setText(util.getDuration());
        }
        TextView tv_address_righ2 = view_righ2.findViewById(R.id.tv_address_right);
        tv_address_righ2.setText(placeGet.getDestAddress());
        LinearLayout ll_marker_righ2 = view_righ2.findViewById(R.id.ll_marker_right);
        ll_marker_righ2.setPadding(0, 0, (int) (getScreenWidth()/1.9), 0);
        desMarker3 = googleMap.addMarker(new MarkerOptions().position(polyLineList.get(polyLineList.size()-1))
                .anchor(0.5f, 0.5f)
                .icon(BitmapDescriptorFactory.fromBitmap(iconFactory_righ2.makeIcon("destination2"))));
        desMarker3.setVisible(false);
    }

    public void onMarkerClick(Marker marker) {
        float container_height = getResources().getDimension(R.dimen.DIP_300);
        Projection projection = googleMap.getProjection();
        Point markerScreenPosition = projection.toScreenLocation(marker.getPosition());
        Point pointHalfScreenAbove = new Point(markerScreenPosition.x,(int) (markerScreenPosition.y - (container_height / 2)));
        LatLng aboveMarkerLatLng = projection.fromScreenLocation(pointHalfScreenAbove);
    }

    @Override
    public void onCameraIdle() {
        Projection projection = googleMap.getProjection();
        LatLng markerLocation = null;
        LatLng dropMarkerLocation=null;
        try {
            if (carMarker != null) {
                markerLocation = carMarker.getPosition();
            } else {
                markerLocation = new LatLng(0.0f, 0.0f);
            }
            if (desMarker != null) {
                dropMarkerLocation = desMarker.getPosition();
            } else {
                dropMarkerLocation = new LatLng(0.0f, 0.0f);
            }
            Point screenPosition = projection.toScreenLocation(markerLocation);
            Point screenPositiondrop = projection.toScreenLocation(dropMarkerLocation);

            final View mapview = mapFragment.getView();
            float maxX = 0;
            if (mapview != null) {
                maxX = mapview.getMeasuredWidth();
            }
            if (mapview != null) {
                float maxY = mapview.getMeasuredHeight();
            }
            float x = 0.0f;
            float y = 0.0f;


//help of marker postion change infowindow position
            if (carMarker2 != null) {

                if (screenPosition.x> 100 && screenPosition.x > maxX/2) {
                    //infowindow leftside move
                    Log.e("Side","CarLeft");
                    carMarker3.setVisible(true);
                    carMarker2.setVisible(false);
                } else {
                    //infowindow rightside move
                    carMarker2.setVisible(true);
                    carMarker3.setVisible(false);
                    Log.e("Side","CarRight");
                }
            }
            if (desMarker != null) {
                if (screenPositiondrop.x >100 && screenPositiondrop.x > maxX/2) {
                 desMarker3.setVisible(true);
                 desMarker2.setVisible(false);
                } else {
                     desMarker2.setVisible(true);
                    desMarker3.setVisible(false);
                    Log.e("Side","DesRight");
                }
            }

        }catch (Exception e){
            e.printStackTrace();
        }
    }
    @Override
    protected void onPause() {
        super.onPause();
//        stopRepeatingTask();
    }


    private HashMap<Marker, Integer> mHashMap = new HashMap<>();
    private ArrayList<LatLng> myList = new ArrayList<>();

    private void getDriverLocationUpdate() {

    }

    Runnable mStatusChecker = new Runnable() {
        @Override
        public void run() {
            try {
                getDriverLocationUpdate();
            } catch (Exception e) {
                Log.e(TAG, e.getMessage());
            }
            handler.postDelayed(mStatusChecker, DELAY);
        }
    };

    void startGettingOnlineDataFromCar() {
        setDrivers();
    }

    private void setDrivers() {
        myList.clear();
        mHashMap.clear();
        handler.post(mStatusChecker);
    }

}
