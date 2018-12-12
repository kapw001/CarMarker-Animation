package logicbeanzs.com.CheapRidedDrivernz.Utils;

public class CustomPickUp {
    private static CustomPickUp mInstance;
    private Placelistener mListener;
    private CustomPickUp() {}

    public static CustomPickUp getInstance() {
        if(mInstance == null) {
            mInstance = new CustomPickUp();
        }
        return mInstance;
    }

    public void setListener(Placelistener listener) {
        mListener = listener;
    }

    public void changeState(Place place) {
        if(mListener != null) {
            notifyStateChange(place);
        }
    }

    private void notifyStateChange(Place place) {
        mListener.onPlace(place);
    }
}
