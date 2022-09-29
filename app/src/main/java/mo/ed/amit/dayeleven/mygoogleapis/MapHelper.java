package mo.ed.amit.dayeleven.mygoogleapis;

import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.provider.Settings;
import android.util.Log;

import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MapHelper {
    private static OnServiceUnavailable serviceListener;
    private static List<Address> addresses;
    private static String errorMessage;
    private static Address address;
    private static String AddressText;

    public static void statusCheck(LocationManager manager, Context mContext) {
        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            mContext.startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
        }
    }

    public static CameraPosition returnCameraPosition(String lat, String lng, float v) {
        return CameraPosition.builder()
                .target(returnLatLong(lat, lng))
                .zoom(v)
                .bearing(0)
                .tilt(45)
                .build();
    }

    public static LatLng returnLatLong(String lat, String lng) {
        return new LatLng(Double.parseDouble(lat), Double.parseDouble(lng));
    }

    public static String filterThreeSyllable(String s) {
        String left=null;
        if (s!=null){
            int index = 0;
            String str=s;

            for (int i=0; i<3; i++) {
                index = str.indexOf(',', index);
            }
            if (index==-1){
                for (int i=0; i<3; i++) {
                    index = str.indexOf('،', index);
                }
            }
            String right;
            if (str.length()>0){
                if (index>0){
                    left = str.substring(0, index);
                    right= str.substring(index + 1);
                }
            }
            if (str.length()<30){
                left=str;
            }

        }
        return left;
    }

    public static String getStreetName(Context context, Location location, OnServiceUnavailable onServiceUnavailable, boolean internet){
        serviceListener=onServiceUnavailable;
        try {
            // Using getFromLocation() returns an array of Addresses for the area immediately
            // surrounding the given latitude and longitude. The results are a best guess and are
            // not guaranteed to be accurate.
            if (location != null) {
                Geocoder geocoder = new Geocoder(context, Locale.getDefault());
                if (geocoder!=null) {
                    addresses = geocoder.getFromLocation(
                            location.getLatitude(),
                            location.getLongitude(),
                            // In this sample, we get just a single address.
                            1);
                }else {
                    return context.getString(R.string.internet_disapled);
                }
            }
        } catch (IOException ioException) {
            // Catch network or other I/O problems.
            errorMessage = context.getString(R.string.internet_disapled);
            Log.e("TAG", errorMessage, ioException);
            if (addresses!=null&&addresses.size()>0){
                return address.getAddressLine(0);
            }else {
                if (internet){
                    serviceListener.ServiceUnavailable(context.getString(R.string.internet_disapled));
                    return context.getString(R.string.internet_disapled);
                }else{
                    serviceListener.ServiceUnavailable(context.getString(R.string.internet_disapled));
                    return context.getString(R.string.internet_disapled);
                }
            }
        } catch (IllegalArgumentException illegalArgumentException) {
            // Catch invalid latitude or longitude values.
            errorMessage = context.getString(R.string.internet_disapled);
            Log.e("TAG", errorMessage + ". " +
                    "Latitude = " + location.getLatitude() +
                    ", Longitude = " + location.getLongitude(), illegalArgumentException);
            return context.getString(R.string.internet_disapled);
        }
        if (addresses == null || addresses.size() == 0) {
            if (errorMessage!=null){
                if (errorMessage.isEmpty()) {
                    errorMessage = context.getString(R.string.internet_disapled);
                    Log.e("TAG", errorMessage);
                    return context.getString(R.string.internet_disapled);
                }
            }
        } else {
            try{
                if (addresses.get(0)!=null){
                    address = addresses.get(0);
                    ArrayList<String> addressFragments = new ArrayList<String>();
                    for (int i = 0; i < address.getMaxAddressLineIndex(); i++) {
                        addressFragments.add(address.getAddressLine(i));
                    }
                    AddressText=address.getAddressLine(0);
                    return AddressText;
                }
            }catch (Exception e){
                e.printStackTrace();
                return context.getString(R.string.internet_disapled);
            }
        }
        if (address!=null){
            if (address.getAddressLine(0).length()>0){
                return address.getAddressLine(0);
            }
            return address.getAddressLine(0);
        } else return context.getString(R.string.internet_disapled);
    }

    public interface OnServiceUnavailable{
        public void ServiceUnavailable(String msg);
    }

}
