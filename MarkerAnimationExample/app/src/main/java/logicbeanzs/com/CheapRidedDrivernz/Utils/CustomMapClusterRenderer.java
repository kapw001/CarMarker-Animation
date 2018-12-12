package logicbeanzs.com.CheapRidedDrivernz.Utils;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.View;

import com.demo.basegooglemap.R;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterItem;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;
import com.google.maps.android.ui.IconGenerator;

import logicbeanzs.com.CheapRidedDrivernz.MapsActivity;

public class CustomMapClusterRenderer <T extends ClusterItem> extends DefaultClusterRenderer<T> {
    private IconGenerator mClusterIconGenerator;

    public CustomMapClusterRenderer(Context context, GoogleMap map, ClusterManager<T> clusterManager) {
        super(context, map, clusterManager);
        mClusterIconGenerator = new IconGenerator(
                context);
        Activity activity = (Activity) context;
        View multiProfile = activity.getLayoutInflater().inflate(
                R.layout.custom_marker_infowindow_left, null);
        mClusterIconGenerator.setContentView(multiProfile);
    }

    @Override
    protected void onBeforeClusterRendered(Cluster<T> cluster,
                                           MarkerOptions markerOptions) {
        try {
            mClusterIconGenerator.setBackground(null);
            Bitmap icon = mClusterIconGenerator.makeIcon(String.valueOf(cluster
                    .getSize()));
            markerOptions.icon(BitmapDescriptorFactory.fromBitmap(icon));
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("get_item_list_nir", "error 13.1 : " + e.toString());
        }
        Log.e("get_item_list_nir", "CallMap onBeforeClusterRendered 14");
    }

    @Override
    protected void onBeforeClusterItemRendered(T item,
                                               MarkerOptions markerOptions) {
        markerOptions.icon(BitmapDescriptorFactory.fromResource(R.mipmap.new_car_small));
        super.onBeforeClusterItemRendered(item, markerOptions);
    }
}
