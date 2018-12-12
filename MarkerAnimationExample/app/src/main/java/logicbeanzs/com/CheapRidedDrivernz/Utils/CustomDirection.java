package logicbeanzs.com.CheapRidedDrivernz.Utils;

public class CustomDirection {
    private static CustomDirection mInstance;
    private DirectionListener mListener;
    private CustomDirection() {}

    public static CustomDirection getInstance() {
        if(mInstance == null) {
            mInstance = new CustomDirection();
        }
        return mInstance;
    }

    public void setListener(DirectionListener listener) {
        mListener = listener;
    }

    public void changeState(DirectionUtils place) {
        if(mListener != null) {
            notifyStateChange(place);
        }
    }

    private void notifyStateChange(DirectionUtils place) {
        mListener.onDirectionsData(place);
    }
}
