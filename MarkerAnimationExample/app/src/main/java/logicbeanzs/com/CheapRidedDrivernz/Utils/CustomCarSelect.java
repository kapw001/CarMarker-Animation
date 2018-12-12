package logicbeanzs.com.CheapRidedDrivernz.Utils;

import android.widget.ImageView;

import org.json.JSONObject;

public class CustomCarSelect {
    private static CustomCarSelect mInstance;
    private CarListener mListener;
    private CustomCarSelect() {}

    public static CustomCarSelect getInstance() {
        if(mInstance == null) {
            mInstance = new CustomCarSelect();
        }
        return mInstance;
    }

    public void setListener(CarListener listener) {
        mListener = listener;
    }

    public void changeState(JSONObject ob, int position,ImageView img) {
        if(mListener != null) {
            notifyStateChange(ob,position,img);
        }
    }

    private void notifyStateChange(JSONObject ob, int position, ImageView img) {
        mListener.onCarSelect(ob,position,img);
    }
}
