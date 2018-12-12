package logicbeanzs.com.CheapRidedDrivernz.Utils;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.sql.Time;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Created by sudip_j0hgrea on 6/19/2017.
 */

public class ImprovedTimeTypeAdapter extends TypeAdapter<Time> {

    public static final TypeAdapterFactory FACTORY = new TypeAdapterFactory() {

        public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> typeToken) {

            @SuppressWarnings("unchecked")
            TypeAdapter<T> typeAdapter = (TypeAdapter<T>) ((typeToken.getRawType() == Time.class) ? new ImprovedDateTypeAdapter()
                    : null);
            return typeAdapter;
        }
    };
    private final DateFormat enUsFormat;
    private final DateFormat localFormat;
    private final DateFormat iso8601Format;

    public ImprovedTimeTypeAdapter() {
        this.enUsFormat = DateFormat.getTimeInstance(2, Locale.US);

        this.localFormat = DateFormat.getTimeInstance(2);

        this.iso8601Format = buildIso8601Format();
    }

    private static DateFormat buildIso8601Format() {
        DateFormat iso8601Format = new SimpleDateFormat(
                "HH:mm:ss", Locale.US);
        iso8601Format.setTimeZone(TimeZone.getTimeZone("UTC"));
        return iso8601Format;
    }

    public Time read(JsonReader in) throws IOException {
        if (in.peek() == JsonToken.NULL) {
            in.nextNull();
            return null;
        }
        return deserializeToDate(in.nextString());
    }

    private synchronized Time deserializeToDate(String json) {
        try {

            return new Time(Long.parseLong(json));
        } catch (Exception e) {

            try {

                long ms = this.localFormat.parse(json).getTime();
                return new Time(ms);
            } catch (ParseException e1) {

                try {

                    long ms = this.iso8601Format.parse(json).getTime();
                    return new Time(ms);
                } catch (ParseException e2) {

                    try {

                        long ms = this.enUsFormat.parse(json).getTime();
                        return new Time(ms);
                    } catch (ParseException e3) {

                        throw new JsonSyntaxException(json, e2);
                    }
                }
            }
        }
    }

    public synchronized void write(JsonWriter out, Time value)
            throws IOException {
        if (value == null) {
            out.nullValue();
            return;
        }
        String dateFormatAsString = this.iso8601Format.format(value);
        out.value(dateFormatAsString);
    }
}
