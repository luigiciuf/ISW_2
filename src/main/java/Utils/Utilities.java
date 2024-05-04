package Utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.nio.charset.Charset;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.ArrayList;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import model.Instance;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class Utilities {
    private Utilities() {
        super();
    }

    /**
     * Verifica se una stringa contiene un'altra stringa con un esatto matching di parola.
     * @param source La stringa sorgente in cui cercare.
     * @param subItem La stringa da cercare.
     * @return true se la stringa `subItem` è trovata nella `source`, altrimenti false.
     */
    public static boolean IsContain(String source, String subItem){
        // Analizing exact matching for strings
        String pattern = "\\b"+subItem+"\\b";
        Pattern p=Pattern.compile(pattern);
        Matcher m=p.matcher(source);
        return m.find();
    }
    /**
     * Converte un numero intero in una data nel formato "yyyyMMdd".
     * @param d L'intero da convertire in data.
     * @return La data corrispondente all'intero dato.
     * @throws ParseException Se il formato della data non è valido.
     */
    public static Date IntToDate(int d) throws ParseException {
        SimpleDateFormat originalFormat = new SimpleDateFormat("yyyyMMdd");
        return originalFormat.parse(String.valueOf(d));
    }

    /**
     * Clona una lista di istanze (Instance) e restituisce una nuova lista.
     * @param list La lista da clonare.
     * @return Una nuova lista con copie degli elementi originali.
     */
    public static List<Instance> clone(List<Instance> list){
        List<Instance> clonedList = new ArrayList<>();
        for(Instance c : list) {
            clonedList.add(new Instance(c));
        }
        return clonedList;
    }
}
