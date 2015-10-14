package cmpt305.lab3.stucture;

import cmpt305.lab3.api.API;
import cmpt305.lab3.api.API.Reqs;
import cmpt305.lab3.exceptions.APIEmptyResponse;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.json.JSONArray;
import org.json.JSONObject;

public class User {
    public class Friendship{
        public final User friend;
        public final long friend_since;
        public Friendship(User friend, long friend_since){
            this.friend = friend;
            this.friend_since = friend_since;
        }
    }
    //STATIC
    protected static final Map<Long, User> ALL_USERS = new HashMap();
    protected static final Map<String, Long> VANITY_MAP = new HashMap();

    public static User getUser(long steamid) throws APIEmptyResponse{
        if(ALL_USERS.containsKey(steamid))
            return ALL_USERS.get(steamid);

	return getUsers(steamid).get(0);
    }

    public static User getUser(String vanityURL) throws APIEmptyResponse{
        if(VANITY_MAP.containsKey(vanityURL))
            return ALL_USERS.get(VANITY_MAP.get(vanityURL));

	return getUsers(resolveVanity(vanityURL)).get(0);
    }

    public static List<User> getUsers(Long... ids) throws APIEmptyResponse{
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
            else{
                User u = new User(
			steamid,
			o1.getString("personaname"),
			o1.getString("profileurl"),
			o1.getString("avatar"),
			o1.getString("avatarmedium"),
			o1.getString("avatarfull"));
		ret.add(u);
                ALL_USERS.put(steamid, u);
            }
	}
	if(ret.isEmpty()) throw (new APIEmptyResponse());
	return ret;
    }

    private static long resolveVanity(String vanityURL) throws APIEmptyResponse{
        if(VANITY_MAP.containsKey(vanityURL))
            return VANITY_MAP.get(vanityURL);

        Map<Reqs, String> reqs = new HashMap();
        reqs.put(Reqs.vanityurl, vanityURL);

        JSONObject info = API.ResolveVanityURL.getData(reqs);
        long steamid = Long.parseLong(info.getString("steamid"));

        VANITY_MAP.put(vanityURL, steamid);

        return steamid;
    }

    //NON-Static

    private final long steamid;
    private final String personaname, profileurl, avatarURL32, avatarURL64, avatarURL184;
    //Friend, Friend_Since
    private Map<User, Long> friends = null;
    //Game, Playtime
    private Map<Game, Long> games = null;
    private long totalGameTime = -1;

    private final Map<Reqs, String> reqData = new HashMap();

    private User(long steamid, String personaname, String profileurl, String avatarURL32, String avatarURL64, String avatarURL184){
        this.steamid = steamid;
        this.personaname = personaname;
        this.profileurl = profileurl;
        this.avatarURL32 = avatarURL32;
        this.avatarURL64 = avatarURL64;
        this.avatarURL184 = avatarURL184;

        reqData.put(Reqs.steamid, Long.toString(steamid));
    }

    public Map<Game, Long> getGames(){
        if(games != null) return games;
	games = new HashMap();

        JSONArray json;
        try {
	    //If user only has 1 game, will it still be an array?
            JSONObject o = API.GetOwnedGames.getData(reqData);
            if(!o.has("games"))
                throw new APIEmptyResponse();
	    json = o.getJSONArray("games");
        } catch (APIEmptyResponse ex) {
            return games;
        }

        Map<Long, Long> process = new HashMap();

        for(Object o : json){
            JSONObject o1 = (JSONObject)o;
            process.put(o1.getLong("appid"), o1.getLong("playtime_forever"));
        }
        List<Game> curGames = null;
        curGames = Game.getGames(process.keySet().toArray(new Long[process.keySet().size()]));

        if(curGames == null) return games;

        for(Game g : curGames)
            games.put(g, process.get(g.appid));

        return games;
    }

    public Map<User, Long> getFriends(){
        if(friends != null) return friends;
	friends = new HashMap();

        JSONArray json;
        try {
            json = API.GetFriendList.getData(reqData).getJSONArray("friends");
        }catch(APIEmptyResponse ex){
            return friends;
        }

        Map<Long, Long> process = new HashMap();

        for(Object o : json){
            JSONObject o1 = (JSONObject)o;
            process.put(Long.parseLong(o1.getString("steamid")), o1.getLong("friend_since"));
        }
        List<User> users;
        try{
            users = getUsers(process.keySet().toArray(new Long[process.keySet().size()]));
        }catch(APIEmptyResponse ex){
	    return friends;
	}

        for(User u : users)
            friends.put(u, process.get(u.steamid));

        return friends;
    }

    public long getGameTime(){
        return getGameTime(null);
    }

    public long getGameTime(final Genre genre){
	if(games == null) getGames();
	Filter<Game> filter;
	if(genre == null)
            if(totalGameTime != -1)
                return totalGameTime;
            else
                filter = Filter.ANY;
	else
	    filter = new Filter<Game>(){
	    @Override
	    public boolean accept(Game t) {
                for(Genre g2 : t.genres)
                    if(genre.equals(g2)) return true;
		return false;
	    }
	};
	long time = 0;
	for(Game g : games.keySet())
	    if(filter.accept(g)) time += games.get(g);
        if(filter == Filter.ANY)
            totalGameTime = time;
	return time;
    }

    public float getGameRatio(){
        return getGameRatio(null);
    }

    public float getGameRatio(final Genre genres){
        if(games == null) getGames();
        if(totalGameTime == -1) getGameTime();
        if(genres == null)
            return 1;
        return (float)getGameTime(genres) / totalGameTime;
    }

    public String getName(){
        return personaname;
    }

    public long getId(){
        return steamid;
    }

    @Override
    public int hashCode() {
        return 43 * 7 + (int) (this.steamid ^ (this.steamid >>> 32));
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || getClass() != obj.getClass())
            return false;
        return this.steamid == ((User) obj).steamid;
    }

    @Override
    public String toString(){
        return String.format("%s: %d", personaname, steamid);
    }
}
