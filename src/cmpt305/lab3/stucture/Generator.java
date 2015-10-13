package cmpt305.lab3.stucture;

import cmpt305.lab3.api.API;
import cmpt305.lab3.exceptions.APIEmptyResponse;
import static cmpt305.lab3.stucture.User.ALL_USERS;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.json.JSONArray;
import org.json.JSONObject;

public interface Generator<T> {
    public static final Generator<User> UserGenerator = new Generator(){
	@Override
	public List<User> generate(Long... ids) throws APIEmptyResponse{
	    if(ids == null || ids.length == 0) return null;

	    Map<API.Reqs, String> reqs = new HashMap();
	    reqs.put(API.Reqs.steamids, Arrays.toString(ids).replaceAll(" |\\[|\\]", ""));

	    JSONArray json;
	    try {
		json = API.GetPlayerSummaries.getData(reqs).getJSONArray("players");
	    } catch (APIEmptyResponse ex) {
		json = new JSONArray();
	    }

	    List ret = new ArrayList();

	    for(Object o : json){
		JSONObject o1 = (JSONObject)o;
		long steamid = Long.parseLong(o1.getString("steamid"));
		if(ALL_USERS.containsKey(steamid))
		    ret.add(ALL_USERS.get(steamid));
		else
		    ret.add(new User(
			    steamid,
			    o1.getString("personaname"),
			    o1.getString("profileurl"),
			    o1.getString("avatar"),
			    o1.getString("avatarmedium"),
			    o1.getString("avatarfull")
		    ));
	    }
	    if(ret.isEmpty()) throw (new APIEmptyResponse());
	    return ret;
	}
    };
    public static final Generator<Game> GameGenerator = new Generator(){
	@Override
	public List<Game> generate(Long... ids) throws APIEmptyResponse {
	    if(ids == null || ids.length == 0) return null;
	    //Issues with multiple appids...
	    //Name filter doesn't work D:
	    //Need to fix later.
	    //Map<API.Reqs, String> reqs = new HashMap();
	    //reqs.put(API.Reqs.filters, "genres,name");

	    List<Game> ret = new ArrayList();

            boolean save = false;

	    for(long id : ids){
		if(Game.ALL_GAMES.containsKey(id)){
		    ret.add(Game.ALL_GAMES.get(id));
		    continue;
		}
		Map<API.Reqs, String> reqs = new HashMap();
		reqs.put(API.Reqs.appids, Long.toString(id));

		JSONObject json;
		try {
		    json = API.appdetails.getData(reqs);
		    if(json.has("success") && !json.getBoolean("success"))
			throw new APIEmptyResponse();
		    json = json.getJSONObject("data");
		}catch(APIEmptyResponse ex){
		    System.err.println("Failed to get game: " + id);
		    continue;
		}

		List<Genre> genres = new ArrayList();
                if(!json.getString("type").equals("game"))
                    continue;
                //If a game doesn't have a genre, we don't care about it...
                if(!json.has("genres"))
                    continue;
		for(Object o : json.getJSONArray("genres")){
		    JSONObject o1 = (JSONObject)o;
		    genres.add(new Genre(Integer.parseInt(o1.getString("id")), o1.getString("description")));
		}

                save = true;
		ret.add(new Game(id, json.getString("name"), genres.toArray(new Genre[genres.size()])));
	    }
	    if(ret.isEmpty()) throw (new APIEmptyResponse());
            if(save) Game.save();
	    return ret;
	}
    };
    public List<T> generate(Long... ids) throws APIEmptyResponse;
}
