package logicbeanzs.com.CheapRidedDrivernz.Utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.lang.reflect.Modifier;
import java.sql.Time;
import java.util.Date;

/**
 * Created by sudip_j0hgrea on 10/12/2017.
 */

public class ApiClient {
    private static Gson gson;
    private final static Object lock = new Object();
    public static Gson getGson() {
        if (gson == null) {
            synchronized (lock) {
                if (gson == null) {
                    GsonBuilder builder = new GsonBuilder();
                    builder.registerTypeAdapter(Time.class, new ImprovedTimeTypeAdapter());
                    builder.registerTypeAdapter(Date.class, new ImprovedDateTypeAdapter());
                    builder.excludeFieldsWithModifiers(Modifier.FINAL, Modifier.TRANSIENT, Modifier.STATIC);
                    builder.serializeNulls();
                    builder.excludeFieldsWithoutExposeAnnotation();
                    gson = builder.create();
                }
            }
        }
        return gson;
    }
}
