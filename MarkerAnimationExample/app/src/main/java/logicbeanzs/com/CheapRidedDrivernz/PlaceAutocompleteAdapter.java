package logicbeanzs.com.CheapRidedDrivernz;
import com.demo.basegooglemap.R;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.AutocompletePrediction;
import com.google.android.gms.location.places.AutocompletePredictionBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.model.LatLngBounds;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.graphics.drawable.Animatable2;
import android.graphics.drawable.AnimatedVectorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.graphics.drawable.AnimatedVectorDrawableCompat;
import android.support.v7.widget.RecyclerView;
import android.text.style.CharacterStyle;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.TimeUnit;

import logicbeanzs.com.CheapRidedDrivernz.Utils.CustomProgress;

/**
 * Adapter that handles Autocomplete requests from the Places Geo Data API.
 * {@link AutocompletePrediction} results from the API are frozen and stored directly in this
 * adapter. (See {@link AutocompletePrediction#freeze()}.)
 * <p>
 * Note that this adapter requires a valid {@link com.google.android.gms.common.api.GoogleApiClient}.
 * The API client must be maintained in the encapsulating Activity, including all lifecycle and
 * connection states. The API client must be connected with the {@link Places#GEO_DATA_API} API.
 */
public class PlaceAutocompleteAdapter extends RecyclerView.Adapter<PlaceAutocompleteAdapter.PlaceViewHolder> implements Filterable {
    private ImageView iv_line;
    public void setProgress(ImageView iv_line) {
        this.iv_line = iv_line;
    }


    public interface PlaceAutoCompleteInterface {
        public void onPlaceClick(ArrayList<PlaceAutocomplete> mResultList, int position);
    }

    Context mContext;
    PlaceAutoCompleteInterface mListener;
    private static final String TAG = "PlaceAutocompleteAdapter";
    private static final CharacterStyle STYLE_BOLD = new StyleSpan(Typeface.BOLD);
    private ArrayList<PlaceAutocomplete> mResultList;
    private GoogleApiClient mGoogleApiClient;
    private LatLngBounds mBounds;
    private AutocompleteFilter mPlaceFilter;


    public PlaceAutocompleteAdapter(Context context, GoogleApiClient googleApiClient,
                                    LatLngBounds bounds, AutocompleteFilter filter) {
        this.mContext = context;
        mGoogleApiClient = googleApiClient;
        mBounds = bounds;
        mPlaceFilter = filter;
        this.mListener = (PlaceAutoCompleteInterface) mContext;
    }

    /*
    Clear List items
     */
    public void clearList() {
        if (mResultList != null && mResultList.size() > 0) {
            mResultList.clear();
            mResultList = null;
        }
    }


    /**
     * Sets the bounds for all subsequent queries.
     */
    public void setBounds(LatLngBounds bounds) {
        mBounds = bounds;
    }


    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults results = new FilterResults();
                // Skip the autocomplete query if no constraints are given.
                if (constraint != null) {
                    CustomProgress.getInstance().changeState(true);
                    // Query the autocomplete API for the (constraint) search string.
                    mResultList = getAutocomplete(constraint);
                    if (mResultList != null) {
                        // The API successfully returned results.
                        results.values = mResultList;
                        results.count = mResultList.size();
                    }
                }
                return results;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                if (results != null && results.count > 0) {
                    // The API returned at least one result, update the data.  mResultList = (ArrayList<AutocompletePrediction>) results.values;
                    mResultList = (ArrayList<PlaceAutocomplete>) results.values;
                    notifyDataSetChanged();
                } else {
                    // The API did not return any results, invalidate the data set.
                    if (mResultList != null) {
                        mResultList.clear();
                        mResultList = null;
                    }
                    notifyItemRangeChanged(0,0);
                }
                CustomProgress.getInstance().changeState(false);
            }
        };
    }

    private ArrayList<PlaceAutocomplete> getAutocomplete(CharSequence constraint) {
        if (mGoogleApiClient.isConnected()) {
            Log.i("", "Starting autocomplete query for: " + constraint);

            PendingResult<AutocompletePredictionBuffer> results =
                    Places.GeoDataApi
                            .getAutocompletePredictions(mGoogleApiClient, constraint.toString(),
                                    mBounds, mPlaceFilter);

            // This method should have been called off the main UI thread. Block and wait for at most 60s
            // for a result from the API.
            AutocompletePredictionBuffer autocompletePredictions = results
                    .await(60, TimeUnit.SECONDS);

            // Confirm that the query completed successfully, otherwise return null
            final Status status = autocompletePredictions.getStatus();
            if (!status.isSuccess()) {
//                Toast.makeText(mContext, "Error contacting API: " + status.toString(),
//                        Toast.LENGTH_SHORT).show();
                Log.e("", "Error getting autocomplete prediction API call: " + status.toString());
                autocompletePredictions.release();
                return null;
            }

            Log.i("", "Query completed. Received " + autocompletePredictions.getCount()
                    + " predictions.");
            Iterator<AutocompletePrediction> iterator = autocompletePredictions.iterator();
            ArrayList<PlaceAutocomplete> resultList = new ArrayList<>(autocompletePredictions.getCount());
            while (iterator.hasNext()) {
                AutocompletePrediction prediction = iterator.next();
                // Get the details of this prediction and copy it into a new PlaceAutocomplete object.
                resultList.add(new PlaceAutocomplete(prediction.getPlaceId(),
                        prediction.getFullText(STYLE_BOLD),prediction.getSecondaryText(STYLE_BOLD)));
            }
            autocompletePredictions.release();
            return resultList;
        }
        Log.e("", "Google API client is not connected for autocomplete query.");
        return null;
    }

    @NonNull
    @Override
    public PlaceViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        switch (viewType) {
            case 0:
                View v1 = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.setloc,  viewGroup,false);
                return new PlaceViewHolder(v1);
            case 1:
                View v0 = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.expandlistitem, viewGroup,false);
                return new PlaceViewHolder(v0);
            default:
                View v3 = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.setloc,  viewGroup,false);
                return new PlaceViewHolder(v3);
        }
    }


    @Override
    public void onBindViewHolder(@NonNull PlaceViewHolder mPredictionHolder, @SuppressLint("RecyclerView") final int i) {
        switch (getItemViewType(i)) {
            case 0:
                mPredictionHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mListener.onPlaceClick(null,i);
                    }
                });
                break;
            case 1:
                if (mResultList!=null && mResultList.size()>0) {
                    mPredictionHolder.mParentLayout.setText(mResultList.get(i).description);
                    mPredictionHolder.mAddress.setText(mResultList.get(i).second);
                }
                mPredictionHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mListener.onPlaceClick(mResultList, i);
                    }
                });
                break;
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (mResultList!=null && mResultList.get(position).placeId!=null)
            return 1;
        else
            return 0;
    }

    @Override
    public int getItemCount() {
        if (mResultList != null)
            return mResultList.size();
        else
            return 1;
    }

    public PlaceAutocomplete getItem(int position) {
        return mResultList.get(position);
    }

    /*
    View Holder For Trip History
     */
    class PlaceViewHolder extends RecyclerView.ViewHolder {
        //        CardView mCardView;
        TextView mParentLayout;
        TextView mAddress;

        PlaceViewHolder(View itemView) {
            super(itemView);
            mParentLayout = itemView.findViewById(android.R.id.text1);
            mAddress = itemView.findViewById(android.R.id.text2);
        }

    }

    /**
     * Holder for Places Geo Data Autocomplete API results.
     */
    public class PlaceAutocomplete {

        public CharSequence placeId;
        public CharSequence description="";
        public CharSequence second="";
        PlaceAutocomplete(CharSequence placeId, CharSequence description, CharSequence secondaryText) {
            this.placeId = placeId;
            this.description = description;
            this.second = secondaryText;
        }

        @Override
        public String toString() {
            return description.toString();
        }
    }
}





