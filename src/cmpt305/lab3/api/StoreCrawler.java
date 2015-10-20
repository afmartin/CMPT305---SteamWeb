package cmpt305.lab3.api;

import java.net.HttpURLConnection;
import cmpt305.lab3.exceptions.APIEmptyResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.json.JSONArray;

/**
 * StoreCrawler
 * 
 * A class consisting of static methods to retrieve user genres
 * of a game.  This is an optional feature and is set as an
 * option in the Settings.
 * 
 * If Steam changes its layout this class will not work and will
 * always return an APIEmptyResponse exception.
 * 
 * @author Alexander Martin
 */
public class StoreCrawler {
    private static final String BASE_URL = "http://store.steampowered.com/app/";
    
    /**
     * retrieveJSONString
     * 
     * Given that user genres are displayed by a javascript function that
     * is provided JSON array of genres, extract the JSON.
     * 
     * Ex:
     * InitAppTagModal( 392620,
     *   [{"tagid":492,"name":"Indie","count":21,"browseable":true},
     *   {"tagid":9,"name":"Strategy","count":21,"browseable":true}],
     *
     * 
     * @param response from HTTP get request
     * @return String of JSON 
     * @throws APIEmptyResponse if getting JSON fails (most likely no genres) 
     */
    private static String retrieveJSONString(String response) throws APIEmptyResponse {
        String processed = response.replaceAll("\n", "");
        Pattern pattern;
        pattern = Pattern.compile("InitAppTagModal\\(.*,.*(\\[\\{.+?\\}\\])");
        Matcher match = pattern.matcher(processed);
        
        if (match.find()) {
            return match.group(1);
        } else {
            throw new APIEmptyResponse();
        }
    }
    
    /**
     * retrievePage
     * 
     * Will send a GET request to the store front
     * to get the HTML of the page for the appid.
     * 
     * @param appid of game
     * @return String server response
     * @throws APIEmptyResponse when no valid page is returned
     */
    private static String retrievePage(long appid) throws APIEmptyResponse {
        try {
            URL url = new URL(BASE_URL + appid);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            BufferedReader stream = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            
            StringBuilder total = new StringBuilder();
            String line;
            while ((line = stream.readLine()) != null) {
                total.append(line);
            }
            return total.toString();
        } catch (IOException ex) {
            throw new APIEmptyResponse();
        }
    }
    
    /**
     * getUserGenres
     * 
     * Get user genres of a specified appid.  This is an optional feature
     * for a user as it depends on the storefront page layout
     * staying the same.  
     * 
     * @param appid of game to search
     * @return JSONArray of user genres
     * @throws APIEmptyResponse when no valid genres are found
     */
    public static JSONArray getUserGenres(long appid) throws APIEmptyResponse {
        String response = retrievePage(appid);
        JSONArray json;
        try {
            json = new JSONArray(retrieveJSONString(response));
        } catch (ClassCastException e) {
            // JSON isn't valid.
            throw new APIEmptyResponse();
        }
        return json;
    }   
}