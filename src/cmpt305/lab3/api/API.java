package cmpt305.lab3.api;

import cmpt305.lab3.exceptions.APIEmptyResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;
import java.util.Random;
import org.json.JSONObject;

public enum API {
    //JSON {"friends":[{"steamid":id, "friend_since":time}...]}
    GetFriendList(Inter.ISteamUser, Version.v1, API.BASE, API.KEY, Reqs.steamid),
    GetOwnedGames(Inter.IPlayerService, Version.v1, API.BASE, API.KEY, Reqs.steamid),
    GetSchemaForGame(Inter.ISteamUserStats, Version.v2, API.BASE, API.KEY, Reqs.appid),
    //JSON {"steamid":id, "success":[01]}
    ResolveVanityURL(Inter.ISteamUser, Version.v1, API.BASE, API.KEY, Reqs.vanityurl),
    //JSON {"players":[]}
    GetPlayerSummaries(Inter.ISteamUser, Version.v2, API.BASE, API.KEY, Reqs.steamids),
    appdetails(null, null, API.STORE_BASE, null, Reqs.appids, Reqs.filters);

    private static enum Inter {ISteamUser, ISteamNews, ISteamUserStats, IPlayerService};
    private static enum Version {v1, v2};
    public static enum Reqs {steamid, steamids, appid, appids, filters, vanityurl};

    private static JSONObject crawlWeb(String strURL) throws APIEmptyResponse{
	if(strURL == null) return null;
        int tries = 0;
        //Static retry number... Sloppy.
        while(tries < 3){
            try {
                URL url = new URL(strURL);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                
                conn.setRequestMethod("GET");
                conn.setRequestProperty("http.agent", "Steam Analyzer " + (new Random()).nextInt(Integer.MAX_VALUE));
                
                StringBuilder str = new StringBuilder();

                try (BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
                    String line;
                    while ((line = reader.readLine()) != null)
                        str.append(line);
                }

                JSONObject out = new JSONObject(str.toString());

                while(out.length() == 1){
                    Object o = out.get(out.keys().next());
                    if(!(o instanceof JSONObject)) break;
                    out = (JSONObject)o;
                }

                return out;
            } catch (IOException ex) {
                ++tries;
            }
        }
        throw new APIEmptyResponse();
    }

    private static final String
	    BASE = "http://api.steampowered.com",
	    STORE_BASE = "http://store.steampowered.com/api",
	    KEY = Key.KEY;

    private final Inter inter;
    private final Version version;
    private final Reqs[] reqs;
    private final StringBuilder URL = new StringBuilder();

    private API(Inter i, Version v, String base, String key, Reqs... r){
	this.inter = i;
	this.version = v;
	this.reqs = r;
	for(Object s : new Object[]{base, inter == null ? null : inter, toString(), version == null ? null : version}){
	    if(s == null) continue;
	    this.URL.append(s.toString());
	    this.URL.append("/");
	}
	if(key != null){
	    this.URL.append("?key=");
	    this.URL.append(KEY);
	}else{
            this.URL.append("?");
        }
    }

    public Reqs[] getRequirements(){
	return reqs;
    }

    public JSONObject getData(Map<Reqs, String> map) throws APIEmptyResponse{
	if(map == null) return null;
	StringBuilder url = new StringBuilder(URL);
	for(Reqs r : reqs){
	    if(map.get(r) == null) continue;
	    url.append(String.format("&%s=%s", r, map.get(r)));
	}

	return crawlWeb(url.toString());
    }
}
