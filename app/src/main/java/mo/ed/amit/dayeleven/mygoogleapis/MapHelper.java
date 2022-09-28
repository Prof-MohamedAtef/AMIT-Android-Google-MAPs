package mo.ed.amit.dayeleven.mygoogleapis;

import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.provider.Settings;

import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;

public class MapHelper {
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

}
