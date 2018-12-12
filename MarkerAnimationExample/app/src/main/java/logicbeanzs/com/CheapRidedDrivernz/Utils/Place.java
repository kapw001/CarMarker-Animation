package logicbeanzs.com.CheapRidedDrivernz.Utils;

import com.google.android.gms.maps.model.LatLng;

public class Place {
    public Place()
    {}
    public Place(LatLng src,LatLng des)
    {
        this.srcLat = src.latitude;
        this.destLat = des.latitude;
        this.srcLon = src.longitude;
        this.destLon = des.longitude;
    }
    public double srcLat;

    public double getSrcLat() {
        return srcLat;
    }

    public void setSrcLat(double srcLat) {
        this.srcLat = srcLat;
    }

    public double getSrcLon() {
        return srcLon;
    }

    public void setSrcLon(double srcLon) {
        this.srcLon = srcLon;
    }

    public double getDestLat() {
        return destLat;
    }

    public void setDestLat(double destLat) {
        this.destLat = destLat;
    }

    public double getDestLon() {
        return destLon;
    }

    public void setDestLon(double destLon) {
        this.destLon = destLon;
    }

    public LatLng getSrcLatLng(){return new LatLng(srcLat,srcLon);}

    public LatLng getDesrLatLng(){return new LatLng(destLat,destLon);}

    public double srcLon;
    public double destLat;
    public double destLon;

    public CharSequence getSrcAddress() {
        return srcaddress;
    }

    public void setSrcAddress(CharSequence address) {
        this.srcaddress = address;
    }

    public CharSequence srcaddress;

    public CharSequence getDestAddress() {
        return destaddress;
    }

    public void setDestAddress(CharSequence destaddress) {
        this.destaddress = destaddress;
    }

    public CharSequence destaddress;
}
