package logicbeanzs.com.CheapRidedDrivernz.Utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.demo.basegooglemap.R;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class CarAdapter extends RecyclerView.Adapter<CarAdapter.ViewHolder> {

    private List<String> itemcollection = new ArrayList<String>();
    private Context context;
    Double distance = null;
    boolean sel = false;
    private int  row_index = -1;
    private JSONObject sub;
    String autime,sligtime,ridetime,runrime,renttime,outtime;
    LatLng auL,slL,rideL,runL,rentL,outL;
    int colorId;
    private CarAdapter.interaction interaction ;
    CarAdapter(Context context, CarAdapter.interaction interact) {
        this.context = context;
        this.interaction = interact;
    }
    public void setData(String cal)
    {
        itemcollection.add(cal);
        notifyDataSetChanged();
    }

    public void clear()
    {itemcollection.clear();
        notifyDataSetChanged();}

    private String getItem(int position) {
        // TODO Auto-generated method stub
        return itemcollection.get(position);
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()), parent);
    }


    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, @SuppressLint("RecyclerView") final int position) {
        DisplayMetrics displaymetrics = new DisplayMetrics();
        ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        if (getItemCount() == 2)
        {
            int devicewidth = displaymetrics.widthPixels / getItemCount();
            int deviceheight = displaymetrics.heightPixels / getItemCount();
            holder.link.getLayoutParams().width = devicewidth;
            holder.picture.getLayoutParams().width = (int) (recyclerView.getWidth()/4.2);
            holder.picture.getLayoutParams().height= (int) (recyclerView.getWidth()/4.2);
            if (position == 0)
            {
                RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) holder.lin.getLayoutParams();
                lp.addRule(RelativeLayout.ALIGN_PARENT_END);
                holder.lin.setLayoutParams(lp);
                holder.lin.setPadding(0,0,22,0);
            }
            else
            {
                RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) holder.lin.getLayoutParams();
                lp.addRule(RelativeLayout.ALIGN_PARENT_START);
                holder.lin.setLayoutParams(lp);
                holder.lin.setPadding(22,0,0,0);
            }
        }
        else {
            int devicewidth = displaymetrics.widthPixels / getItemCount();
            int deviceheight = displaymetrics.heightPixels / 4;
            holder.lin.getLayoutParams().width = devicewidth;
            holder.picture.getLayoutParams().width = (int) (recyclerView.getWidth()/4.2);
            holder.picture.getLayoutParams().height= (int) (recyclerView.getWidth()/4.2);
        }
        try {
            final JSONObject ob = new JSONObject(getItem(position));
            holder.name.setText(ob.getString("name"));
            holder.picture.setImageResource(ob.getInt("img"));
            holder.picture.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (row_index != position) {
                        row_index = position;
                        if (interaction != null) {
                            interaction.onCarSelect(ob, position,holder.picture);
                        }
                    }
                    else
                    {
                        if (interaction != null) {
                            interaction.onCarSelect(ob, 1000,holder.picture);
                        }
                    }
                }
            });
            if (ob.has("select") && ob.getInt("select") ==1)
            {
                ob.remove("select");
                if (interaction!=null)
                {
                    interaction.onCarSelect(ob,position,holder.picture);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    @Override
    public int getItemCount() {
        return itemcollection.size();
    }

    private RecyclerView recyclerView;
    public void setParams(RecyclerView layoutParams) {
        this.recyclerView = layoutParams;
    }

     class ViewHolder extends RecyclerView.ViewHolder {
        ImageView picture;
        TextView name;
        LinearLayout lin;
        RelativeLayout link;
         ViewHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.types_items, parent, false));
           picture = itemView.findViewById(R.id.img);
           name = itemView.findViewById(R.id.txt);
           lin = itemView.findViewById(R.id.lin);
           link = itemView.findViewById(R.id.link);
        }
    }
    public interface interaction {
        void onCarSelect(JSONObject ob,int position,ImageView img);
    }
}
