package logicbeanzs.com.CheapRidedDrivernz.Utils;

import android.widget.ImageView;

import org.json.JSONObject;

public interface CarListener {
    public void onCarSelect(JSONObject ob,int position,ImageView img);
}
