package cmpt305.lab3.stucture;

import cmpt305.lab3.api.API;
import cmpt305.lab3.api.API.Reqs;
import cmpt305.lab3.exceptions.APIEmptyResponse;
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

	return (User)Generator.UserGenerator.generate(steamid).get(0);
    }

    public static User getUser(String vanityURL) throws APIEmptyResponse{
        if(VANITY_MAP.containsKey(vanityURL))
            return ALL_USERS.get(VANITY_MAP.get(vanityURL));

	return (User)Generator.UserGenerator.generate(resolveVanity(vanityURL)).get(0);
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

    private final Map<Reqs, String> reqData = new HashMap();

    protected User(long steamid, String personaname, String profileurl, String avatarURL32, String avatarURL64, String avatarURL184){
        this.steamid = steamid;
        this.personaname = personaname;
        this.profileurl = profileurl;
        this.avatarURL32 = avatarURL32;
        this.avatarURL64 = avatarURL64;
        this.avatarURL184 = avatarURL184;

        reqData.put(Reqs.steamid, Long.toString(steamid));

        ALL_USERS.put(steamid, this);
    }

    public Map<Game, Long> getGames(){
        if(games != null) return games;
	games = new HashMap();

        JSONArray json;
        try {
	    //If user only has 1 game, will it still be an array?
            json = API.GetOwnedGames.getData(reqData).getJSONArray("games");
        } catch (APIEmptyResponse ex) {
            return games;
        }

        Map<Long, Long> process = new HashMap();

        for(Object o : json){
            JSONObject o1 = (JSONObject)o;
            process.put(o1.getLong("appid"), o1.getLong("playtime_forever"));
        }
        List<Game> curGames = null;
        try{
            curGames = Generator.GameGenerator.generate(process.keySet().toArray(new Long[process.keySet().size()]));
        }catch(APIEmptyResponse ex){}

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
        List<User> users = null;
        try{
            users = Generator.UserGenerator.generate(process.keySet().toArray(new Long[process.keySet().size()]));
        }catch(APIEmptyResponse ex){}

        if(users == null) return friends;

        for(User u : users)
            friends.put(u, process.get(u.steamid));

        return friends;
    }

    public long getGameTime(final Genre... genres){
	if(games == null) getGames();
	Filter<Game> filter;
	if(genres == null || genres.length == 0)
	    filter = Filter.ANY;
	else
	    filter = new Filter<Game>(){
	    @Override
	    public boolean accept(Game t) {
		for(Genre g : genres)
		    for(Genre g2 : t.genres)
			if(g.equals(g2)) return true;
		return false;
	    }
	};
	long time = 0;
	for(Game g : games.keySet())
	    if(filter.accept(g)) time += games.get(g);
	return time;
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
