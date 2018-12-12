package logicbeanzs.com.CheapRidedDrivernz;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.graphics.drawable.AnimatedVectorDrawableCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.transition.TransitionInflater;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import com.demo.basegooglemap.R;

import logicbeanzs.com.CheapRidedDrivernz.Utils.CustomModel;
import logicbeanzs.com.CheapRidedDrivernz.Utils.CustomPickUp;
import logicbeanzs.com.CheapRidedDrivernz.Utils.CustomProgress;
import logicbeanzs.com.CheapRidedDrivernz.Utils.MapUtils;
import logicbeanzs.com.CheapRidedDrivernz.Utils.NoInternetDialog;
import logicbeanzs.com.CheapRidedDrivernz.Utils.Place;
import logicbeanzs.com.CheapRidedDrivernz.Utils.Placelistener;
import logicbeanzs.com.CheapRidedDrivernz.Utils.ProgressListener;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.List;

public class SearchActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener,
        PlaceAutocompleteAdapter.PlaceAutoCompleteInterface, OnMapReadyCallback,LocationListener,Placelistener, ProgressListener {
    private RecyclerView mRecyclerView;
    PlaceAutocompleteAdapter mAdapter;
    private static final LatLngBounds BOUNDS_INDIA = new LatLngBounds(
            new LatLng(6.4626999, 68.1097),
            new LatLng(35.513327, 97.39535869999999));
    EditText mSearchEdittext;
    private Location mLastLoc;
    LocationRequest mLocationRequest;
    private static final long INTERVAL = 1000 * 5; //1 minute
    private static final long FASTEST_INTERVAL = 1000 * 3;
//    ImageView mClear;
    private static final int PLACE_PICKER_REQUEST = 1000;
    private GoogleApiClient mClient;
    private GoogleMap googleMap;
    LatLng loc;
    private Marker carMarker;
    ImageView pin;
    FrameLayout frame;
    Button done;
    Place placeCust;
    String srcAdd = "";
    Button cuurent;
    ImageView iv_line;
    @Override
    public void onPlace(Place place) {
        this.placeCust = place;
        if (cuurent!=null)
        {
            cuurent.setText(placeCust.getSrcAddress());
        }
    }
    private AnimatedVectorDrawableCompat avd;
    private void startAnimation(boolean isStart)
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (isStart) {
                repeatAnimation();
            }
            else
            {
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        iv_line.removeCallbacks(action);
                        avd.stop();
                        iv_line.setVisibility(View.GONE);
                        iv_line.invalidate();
                    }
                },800);
//
            }
        }
    }
    private Runnable action = new Runnable() {
        @Override
        public void run() {
            repeatAnimation();
        }
    };
    Handler handler=new Handler();
    private void repeatAnimation() {
        if (avd!=null) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    //your code
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            iv_line.setVisibility(View.VISIBLE);
                            avd.start();
                            iv_line.postDelayed(action, 1000);
                            iv_line.invalidate();
                        }
                    });
                }
            }).start();

        }
    }
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
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(Color.TRANSPARENT);
            getWindow().setSharedElementEnterTransition(TransitionInflater.from(this)
                    .inflateTransition(R.transition.changebounds));
        }
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            loc = new LatLng(bundle.getDouble("lat"),bundle.getDouble("lon"));
        }

        mClient = new GoogleApiClient
                .Builder(this)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .addApi(LocationServices.API)
                .build();
        setContentView(R.layout.activity_search);
        noInternetDialog = new NoInternetDialog.Builder(this).build();
        noInternetDialog.setCancelable(false);
        CustomPickUp.getInstance().setListener(this);
        CustomProgress.getInstance().setListener(this);
        findViewById(R.id.close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBack();
            }
        });
        cuurent = findViewById(R.id.cuurent);
        iv_line = findViewById(R.id.iv_line);
        cuurent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SearchActivity.this, PickLocation.class);
                intent.putExtra("lat", loc.latitude);
                intent.putExtra("lon",loc.longitude);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    cuurent.setTransitionName("current");
                    ActivityOptionsCompat optionsCompat = ActivityOptionsCompat
                            .makeSceneTransitionAnimation(SearchActivity.this, cuurent, cuurent.getTransitionName());
                    startActivity(intent, optionsCompat.toBundle());
                } else
                    startActivity(intent);
            }
        });
        Animation animation = AnimationUtils.loadAnimation(this,android.R.anim.slide_in_left);
        findViewById(R.id.close).startAnimation(animation);
        pin = findViewById(R.id.pin);
        frame = findViewById(R.id.frame);
        done = findViewById(R.id.done);
        mSearchEdittext = findViewById(R.id.mSearchText);
        mSearchEdittext.setEnabled(false);
        mRecyclerView = findViewById(R.id.list_search);
        mRecyclerView.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(llm);
        avd = AnimatedVectorDrawableCompat.create(this, R.drawable.avd_line);
        iv_line.setBackground(avd);
        iv_line.setVisibility(View.GONE);
//        mClear = findViewById(R.id.clear);
//        mClear.setOnClickListener(this);
        final AutocompleteFilter typeFilter = new AutocompleteFilter.Builder()
//                .setTypeFilter(AutocompleteFilter.TYPE_FILTER_CITIES)
//                .setTypeFilter(AutocompleteFilter.TYPE_FILTER_ADDRESS)
                .setCountry("IN")
                .build();
        mAdapter = new PlaceAutocompleteAdapter(this,
                mClient, BOUNDS_INDIA, typeFilter);
        mAdapter.setProgress(iv_line);
        MapUtils.slideUp(this, mRecyclerView, new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                mRecyclerView.setAdapter(mAdapter);
                mSearchEdittext.setEnabled(true);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        mSearchEdittext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mRecyclerView.getVisibility() == View.GONE)
                {
                    MapUtils.slideUp(SearchActivity.this, mRecyclerView, new Animation.AnimationListener() {
                        @Override
                        public void onAnimationStart(Animation animation) {
                            MapUtils.hideKeyboard(SearchActivity.this);
                            mSearchEdittext.setText(null);
                            mSearchEdittext.setInputType(InputType.TYPE_CLASS_TEXT);
                            mRecyclerView.clearAnimation();
                            mRecyclerView.setVisibility(View.VISIBLE);
                            frame.setVisibility(View.GONE);
                        }

                        @Override
                        public void onAnimationEnd(Animation animation) {

                        }

                        @Override
                        public void onAnimationRepeat(Animation animation) {

                        }
                    });
                }
                else
                {
                    mSearchEdittext.setFocusable(true);
                    mSearchEdittext.setCursorVisible(true);
                    mSearchEdittext.requestFocus();
                    mSearchEdittext.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                            if (inputMethodManager != null) {
                                inputMethodManager.showSoftInput(mSearchEdittext, InputMethodManager.SHOW_IMPLICIT);
                            }
                            mSearchEdittext.setFocusable(true);
                            mSearchEdittext.setCursorVisible(true);
                            mSearchEdittext.requestFocus();
                        }
                    }, 200);
                }
            }
        });
        LatLng ladw = new LatLng(loc.latitude,loc.longitude);
        try {
            Geocoder selected_place_geocoder = new Geocoder(SearchActivity.this);
            List<Address> address;
            address = selected_place_geocoder.getFromLocation(ladw.latitude, ladw.longitude, 1);
            Address location = address.get(0);
            if (location != null) {
                if (location.getLocality() != null) {
                    srcAdd = location.getAddressLine(0) + "," + location.getLocality() + "," + location.getCountryName();
                } else {
                    srcAdd = location.getAddressLine(0) + "," + location.getCountryName();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        mSearchEdittext.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (count > 0) {
                    if (mAdapter != null) {
                        mRecyclerView.setAdapter(mAdapter);
                    }
                } else {
                    if (mAdapter != null) {
                        mAdapter.clearList();
                        mAdapter.notifyDataSetChanged();
                        mRecyclerView.setAdapter(mAdapter);

                    }
                }
                if (!s.toString().equals("") && mClient.isConnected()) {
                    mAdapter.getFilter().filter(s.toString());
                } else if (!mClient.isConnected()) {
//                    Toast.makeText(getApplicationContext(), Constants.API_NOT_CONNECTED, Toast.LENGTH_SHORT).show();
                    Log.e("connection", "NOT CONNECTED");
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }

    private void onBack() {
        MapUtils.SlideToAbove(mRecyclerView, new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                MapUtils.hideKeyboard(SearchActivity.this);
            }

            @Override
            public void onAnimationEnd(Animation anim) {
                mRecyclerView.clearAnimation();
                mRecyclerView.setVisibility(View.GONE);
                Animation animation = AnimationUtils.loadAnimation(SearchActivity.this,R.anim.slide_out_left);
                animation.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        findViewById(R.id.close).setVisibility(View.GONE);
                        SearchActivity.super.onBackPressed();
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });
                findViewById(R.id.close).startAnimation(animation);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(INTERVAL);
        mLocationRequest.setFastestInterval(FASTEST_INTERVAL);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            if (mClient.isConnected()) {
                LocationServices.FusedLocationApi.requestLocationUpdates(mClient, mLocationRequest, this);
            }

        }
    }
    @Override
    protected void onStart() {
        super.onStart();
        mClient.connect();
    }

    @Override
    protected void onStop() {
        mClient.disconnect();
        super.onStop();
    }
    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
    @Override
    public void onPlaceClick(ArrayList<PlaceAutocompleteAdapter.PlaceAutocomplete> mResultList, int position) {
       onProgressChanged(false);
        if(mResultList!=null){
            try {
                final String placeId = String.valueOf(mResultList.get(position).placeId);
                PendingResult<PlaceBuffer> placeResult = Places.GeoDataApi
                        .getPlaceById(mClient, placeId);
                placeResult.setResultCallback(new ResultCallback<PlaceBuffer>() {
                    @Override
                    public void onResult(@NonNull PlaceBuffer places) {
                        if(places.getCount()==1){
                            if (placeCust!=null)
                            {
                                Place place = new Place(new LatLng(placeCust.srcLat, placeCust.srcLon), new LatLng(places.get(0).getLatLng().latitude,
                                        places.get(0).getLatLng().longitude));
                                place.setSrcAddress(placeCust.getSrcAddress());
                                place.setDestAddress(places.get(0).getAddress());
                                CustomModel.getInstance().changeState(place);
                            }
                            else {
                                Place place = new Place(new LatLng(loc.latitude, loc.longitude), new LatLng(places.get(0).getLatLng().latitude,
                                        places.get(0).getLatLng().longitude));
                                place.setSrcAddress(srcAdd);
                                place.setDestAddress(places.get(0).getAddress());
                                CustomModel.getInstance().changeState(place);
                            }
                            mSearchEdittext.setText(places.get(0).getAddress());
                            onBack();
                        }else {
                            Toast.makeText(getApplicationContext(),"something went wrong",Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
            catch (Exception e){
                e.printStackTrace();
            }

        }
        else
        {
            MapUtils.hideKeyboard(SearchActivity.this);
            MapUtils.SlideToAbove(mRecyclerView, new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    mRecyclerView.clearAnimation();
                    mRecyclerView.setVisibility(View.GONE);
                    frame.setVisibility(View.VISIBLE);
                    mSearchEdittext.setInputType(InputType.TYPE_NULL);
                    final SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                            .findFragmentById(R.id.map);
                    mapFragment.getMapAsync(SearchActivity.this);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
        }
    }

    @Override
    public void onMapReady(final GoogleMap googleMap) {
        this.googleMap = googleMap;
        this.googleMap.getUiSettings().setCompassEnabled(false);
        this.googleMap.getUiSettings().setMapToolbarEnabled(false);
        try {
            boolean success = googleMap.setMapStyle(
                    MapStyleOptions.loadRawResourceStyle(
                            this, R.raw.custom_map));
        } catch (Resources.NotFoundException e) {
            Log.e("error", "Can't find style. Error: ", e);
        }
        createLocationRequest();
        googleMap.setOnCameraMoveListener(new GoogleMap.OnCameraMoveListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onCameraMove() {
               done.setEnabled(false);
               done.setText("Fetching...");
            }
        });
        googleMap.setOnCameraIdleListener(new GoogleMap.OnCameraIdleListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onCameraIdle() {
                LatLng ladw = googleMap.getCameraPosition().target;
                try {
                    Geocoder selected_place_geocoder = new Geocoder(SearchActivity.this);
                    List<Address> address;
                    address = selected_place_geocoder.getFromLocation(ladw.latitude, ladw.longitude, 1);
                    Address location = address.get(0);
                    if (location != null) {
                        if (location.getLocality() != null) {
                            String awq = location.getAddressLine(0) + "," + location.getLocality() + "," + location.getCountryName();
                            mSearchEdittext.setText(awq);
                        } else {
                            String gst = location.getAddressLine(0) + "," + location.getCountryName();
                            mSearchEdittext.setText(gst);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                done.setText("Done");
                done.setEnabled(true);
            }
        });
        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (done.getText().toString().equalsIgnoreCase("Done"))
                {
                    if (placeCust!=null)
                    {
                        Place place = new Place(new LatLng(placeCust.srcLat, placeCust.srcLon), googleMap.getCameraPosition().target);
                        place.setSrcAddress(placeCust.getSrcAddress());
                        place.setDestAddress(mSearchEdittext.getText().toString());
                        CustomModel.getInstance().changeState(place);
                    }
                    else {
                        Place place = new Place(new LatLng(loc.latitude, loc.longitude), googleMap.getCameraPosition().target);
                        place.setSrcAddress(srcAdd);
                        place.setDestAddress(mSearchEdittext.getText().toString());
                        CustomModel.getInstance().changeState(place);
                    }
                    onBack();
                }
                else {
                    Toast.makeText(getApplicationContext(),"Wait for Location to be fetched.",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public void onLocationChanged(Location location) {
        mLastLoc = location;
        if (carMarker == null) {
            carMarker = googleMap.addMarker(new MarkerOptions().position(new LatLng(location.getLatitude(), location.getLongitude())).
                    flat(true).icon(BitmapDescriptorFactory.fromResource(R.mipmap.source)));
            googleMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
                @Override
                public void onMapLoaded() {
                    googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(carMarker.getPosition(), 18), new GoogleMap.CancelableCallback() {
                        @Override
                        public void onFinish() {
                            pin.setVisibility(View.VISIBLE);
                            done.setVisibility(View.VISIBLE);
                        }

                        @Override
                        public void onCancel() {
                            pin.setVisibility(View.VISIBLE);
                            done.setVisibility(View.VISIBLE);
                        }
                    });
                }
            });
        }
        else
        {
            carMarker.setPosition(new LatLng(location.getLatitude(),location.getLongitude()));
        }
    }


    @Override
    public void onProgressChanged(boolean state) {
        startAnimation(state);
    }
}
