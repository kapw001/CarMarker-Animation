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
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Created by sudip_j0hgrea on 6/19/2017.
 */

public final class ImprovedDateTypeAdapter extends TypeAdapter<Date> {

    public static final TypeAdapterFactory FACTORY = new TypeAdapterFactory() {

        public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> typeToken) {

            @SuppressWarnings("unchecked")
            TypeAdapter<T> typeAdapter = (TypeAdapter<T>) ((typeToken.getRawType() == Date.class) ? new ImprovedDateTypeAdapter()
                    : null);
            return typeAdapter;
        }
    };
    private final DateFormat enUsFormat;
    private final DateFormat localFormat;
    private final DateFormat iso8601Format;

    public ImprovedDateTypeAdapter() {
        this.enUsFormat = DateFormat.getDateTimeInstance(2, 2, Locale.US);

        this.localFormat = DateFormat.getDateTimeInstance(2, 2);

        this.iso8601Format = buildIso8601Format();
    }

    private static DateFormat buildIso8601Format() {
        DateFormat iso8601Format = new SimpleDateFormat(
                "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US);
        iso8601Format.setTimeZone(TimeZone.getTimeZone("UTC"));
        return iso8601Format;
    }

    public Date read(JsonReader in) throws IOException {
        if (in.peek() == JsonToken.NULL) {
            in.nextNull();
            return null;
        }
        return deserializeToDate(in.nextString());
    }

    private synchronized Date deserializeToDate(String json) {
        try {

            return new Date(Long.parseLong(json));
        } catch (Exception e) {

            try {

                return this.localFormat.parse(json);
            } catch (ParseException e1) {

                try {

                    return this.enUsFormat.parse(json);
                } catch (ParseException e2) {

                    try {

                        return this.iso8601Format.parse(json);
                    } catch (ParseException e3) {

                        throw new JsonSyntaxException(json, e2);
                    }
                }
            }
        }
    }

    public synchronized void write(JsonWriter out, Date value)
            throws IOException {
        if (value == null) {
            out.nullValue();
            return;
        }
        String dateFormatAsString = this.iso8601Format.format(value);
        out.value(dateFormatAsString);
    }
}
