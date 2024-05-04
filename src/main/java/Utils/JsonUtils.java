package Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;
import java.net.URL;
import java.nio.charset.Charset;

public class JsonUtils {

    private JsonUtils() {
        super();
    }

    /**
     * Legge tutto il contenuto da un Reader e lo restituisce come stringa.
     * @param rd Il Reader da cui leggere.
     * @return Il contenuto letto come stringa.
     * @throws IOException Se si verifica un errore di I/O.
     */
    public static String readAll(Reader rd) throws IOException {
        StringBuilder sb = new StringBuilder();
        int cp;
        while ((cp = rd.read()) != -1) {
            sb.append((char) cp);
        }
        return sb.toString();
    }
    /**
     * Legge un JSONArray da un URL.
     * @param url L'URL da cui leggere il JSON.
     * @return Un JSONArray contenente i dati letti.
     * @throws IOException Se si verifica un errore di I/O.
     * @throws JSONException Se si verifica un errore durante il parsing del JSON.
     */
    public static JSONArray readJsonArrayFromUrl(String url) throws IOException, JSONException {
        InputStream is = new URL(url).openStream();
        try {
            BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
            String jsonText = readAll(rd);
            return new JSONArray(jsonText);
        } finally {
            is.close();
        }
    }
    /**
     * Legge un JSONObject da un URL.
     * @param url L'URL da cui leggere il JSON.
     * @return Un JSONObject contenente i dati letti.
     * @throws IOException Se si verifica un errore di I/O.
     * @throws JSONException Se si verifica un errore durante il parsing del JSON.
     */
    public static JSONObject readJsonFromUrl(String url) throws IOException, JSONException {
        InputStream is = new URL(url).openStream();
        try {
            BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
            String jsonText = readAll(rd);
            return new JSONObject(jsonText);
        } finally {
            is.close();
        }
    }
}
