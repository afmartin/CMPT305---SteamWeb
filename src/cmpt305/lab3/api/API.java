package cmpt305.lab3.api;

import com.json.parsers.JSONParser;
import com.json.parsers.JsonParserFactory;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public enum API {
    GetFriendList(Inter.ISteamUser, Version.v0001, Reqs.steamid);

    private static enum Inter {ISteamUser, ISteamNews, ISteamUserStats};
    private static enum Version {v0001, v0002};
    public static enum Reqs {steamid};

    private static final String
	    BASE = "http://api.steampowered.com",
	    KEY = Key.KEY;

    private final Inter inter;
    private final Version version;
    private final Reqs[] reqs;

    private static String getData(String strURL){
	if(strURL == null) return null;
	try {
	    URL url = new URL(strURL);
	    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
	    conn.setRequestMethod("GET");
	    StringBuilder out;

	    try (BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
		String line;
		out = new StringBuilder();
		while ((line = reader.readLine()) != null) {
		    out.append(line);
		}
	    }
	    return out.toString();
	} catch (IOException ex) {
	    Logger.getLogger(API.class.getName()).log(Level.SEVERE, null, ex);
	}
	return null;
    }

    private API(Inter i, Version v, Reqs... r){
	this.inter = i;
	this.version = v;
	this.reqs = r;
    }

    public Reqs[] getRequirements(){
	return reqs;
    }

    public Map getData(Map<Reqs, String> map){
	if(map == null) return null;

	StringBuilder url = new StringBuilder(String.format("%s/%s/%s/%s/?key=%s", BASE, inter, toString(), version, KEY));
	for(Reqs r : reqs){
	    if(map.get(r) == null) return null;
	    url.append(String.format("&%s=%s", r, map.get(r)));
	}

	JsonParserFactory factory=JsonParserFactory.getInstance();
	JSONParser parser = factory.newJsonParser();
	Map json = parser.parseJson(getData(url.toString()));

	return json;
    }
}
