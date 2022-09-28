package mo.ed.amit.dayeleven.mygoogleapis;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.HashMap;

public class SessionManagement {
    private static final String KEY_USERNAME = "USERNAME";
    public static SharedPreferences pref;
    public static SharedPreferences.Editor editor;
    public static Context _context;
    int PRIVATE_MODE = 0;
    public static final String PREFS_Logger = "MapFile";

    public SessionManagement(Context context) {
        this._context = context;
        pref = _context.getSharedPreferences(PREFS_Logger, PRIVATE_MODE);
        editor = pref.edit();
    }

    public void setUsername(String username){
        editor.putString(KEY_USERNAME, username);
        editor.commit();
    }

    public void logout(){
        editor.clear();
        editor.commit();
    }

    public String getUserName() {
        return pref.getString(KEY_USERNAME, null);
    }
}