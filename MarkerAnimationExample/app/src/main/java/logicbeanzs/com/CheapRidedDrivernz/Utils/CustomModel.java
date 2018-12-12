package logicbeanzs.com.CheapRidedDrivernz.Utils;

public class CustomModel {
    private static CustomModel mInstance;
    private Placelistener mListener;
    private CustomModel() {}

    public static CustomModel getInstance() {
        if(mInstance == null) {
            mInstance = new CustomModel();
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
