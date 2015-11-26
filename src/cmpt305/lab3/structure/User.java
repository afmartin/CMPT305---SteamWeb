package cmpt305.lab3.structure;

import cmpt305.lab3.api.API;
import cmpt305.lab3.api.API.Reqs;
import cmpt305.lab3.exceptions.APIEmptyResponse;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import javafx.collections.FXCollections;
import javafx.collections.MapChangeListener;
import javafx.collections.ObservableMap;
import org.json.JSONArray;
import org.json.JSONObject;

public class User{
	//STATIC
	protected static final ObservableMap<Long, User> ALL_USERS = FXCollections.observableMap(new HashMap<>());
	protected static final Map<String, Long> VANITY_MAP = new HashMap<>();

	public static User getUser(long steamid) throws APIEmptyResponse{
		if(ALL_USERS.containsKey(steamid)){
			return ALL_USERS.get(steamid);
		}

		return getUsers(steamid).get(0);
	}

	public static User getUser(String vanityURL) throws APIEmptyResponse{
		if(VANITY_MAP.containsKey(vanityURL)){
			return ALL_USERS.get(VANITY_MAP.get(vanityURL));
		}

		return getUsers(resolveVanity(vanityURL)).get(0);
	}

	public static void addListener(MapChangeListener l){
		ALL_USERS.addListener(l);
	}

	public static void removeListener(MapChangeListener l){
		ALL_USERS.removeListener(l);
	}

	public static List<User> getUsers(Long... ids) throws APIEmptyResponse{
		if(ids == null || ids.length == 0){
			return null;
		}

		Map<API.Reqs, String> reqs = new HashMap();
		reqs.put(API.Reqs.steamids, Arrays.toString(ids).replaceAll(" |\\[|\\]", ""));

		JSONArray json;
		try{
			json = API.GetPlayerSummaries.getData(reqs).getJSONArray("players");
		}catch(APIEmptyResponse ex){
			json = new JSONArray();
		}

		List ret = new ArrayList();

		for(Object o : json){
			JSONObject o1 = (JSONObject) o;
			long steamid = Long.parseLong(o1.getString("steamid"));
			if(ALL_USERS.containsKey(steamid)){
				ret.add(ALL_USERS.get(steamid));
			}else{
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
		if(ret.isEmpty()){
			throw (new APIEmptyResponse());
		}
		return ret;
	}

	private static long resolveVanity(String vanityURL) throws APIEmptyResponse{
		if(VANITY_MAP.containsKey(vanityURL)){
			return VANITY_MAP.get(vanityURL);
		}

		Map<Reqs, String> reqs = new HashMap();
		reqs.put(Reqs.vanityurl, vanityURL);

		JSONObject info = API.ResolveVanityURL.getData(reqs);
		if(!info.has("steamid")){
			throw new APIEmptyResponse();
		}
		long steamid = Long.parseLong(info.getString("steamid"));
		VANITY_MAP.put(vanityURL, steamid);

		return steamid;
	}

	//NON-Static
	private final long steamid;
	private final String personaname, profileurl, avatarURL32, avatarURL64, avatarURL184, vanity;
	//Friend, Friend_Since
	private Set<User> friends = null;
	//Game, Playtime
	private Map<Game, Long> games = null;
	private final Map<User, Pair<Double, Map<Double, Genre>>> userCompatabilityMap = new HashMap();
	private long totalGameTime = -1;

	private final Map<Reqs, String> reqData = new HashMap();

	private User(long steamid, String personaname, String profileurl, String avatarURL32, String avatarURL64, String avatarURL184){
		this.steamid = steamid;
		this.personaname = personaname;
		this.profileurl = profileurl;
		this.avatarURL32 = avatarURL32;
		this.avatarURL64 = avatarURL64;
		this.avatarURL184 = avatarURL184;
		this.vanity = this.profileurl.substring(29, this.profileurl.length() - 1);

		reqData.put(Reqs.steamid, Long.toString(steamid));
	}

	public String getVanity(){
		return this.vanity;
	}

	public Map<Game, Long> getGames(){
		if(games != null){
			return new HashMap(games);
		}
		games = new HashMap();

		JSONArray json;
		try{
			//If user only has 1 game, will it still be an array?
			JSONObject o = API.GetOwnedGames.getData(reqData);
			if(!o.has("games")){
				throw new APIEmptyResponse();
			}
			json = o.getJSONArray("games");
		}catch(APIEmptyResponse ex){
			return new HashMap(games);
		}

		Map<Long, Long> process = new HashMap();

		for(Object o : json){
			JSONObject o1 = (JSONObject) o;
			process.put(o1.getLong("appid"), o1.getLong("playtime_forever"));
		}
		List<Game> curGames;
		curGames = Game.getGames(process.keySet().toArray(new Long[process.keySet().size()]));

		if(curGames == null){
			return new HashMap(games);
		}

		curGames.stream().forEach((g) -> {
			games.put(g, process.get(g.appid));
		});

		return new HashMap(games);
	}

	public Set<User> getFriends(){
		if(friends != null){
			return new HashSet(friends);
		}
		friends = new HashSet();

		JSONArray json;
		try{
			json = API.GetFriendList.getData(reqData).getJSONArray("friends");
		}catch(APIEmptyResponse ex){
			return new HashSet(friends);
		}

		Set<Long> process = new HashSet();

		for(Object o : json){
			JSONObject o1 = (JSONObject) o;
			process.add(Long.parseLong(o1.getString("steamid")));
		}
		List<User> users;
		try{
			users = getUsers(process.toArray(new Long[process.size()]));
		}catch(APIEmptyResponse ex){
			return new HashSet(friends);
		}

		users.stream().forEach((u) -> friends.add(u));

		return new HashSet(friends);
	}

	public Pair<Double, Map<Double, Genre>> getCompatabilityWith(User u){
		if(userCompatabilityMap.containsKey(u)){
			return userCompatabilityMap.get(u);
		}

		Map<Double, Genre> map = new TreeMap(Collections.reverseOrder());

		double totalScore = Genre.getKnown().stream().mapToDouble(g -> {
			double t1 = getGameRatio(g),
					t2 = u.getGameRatio(g);
			if(t1 >= .000001f && t2 >= .000001f){
				double score = 2 * (t1 * t2) / (t1 + t2);
				map.put(score, g);
				return score;
			}
			return 0;
		}).sum();

		Pair p = new Pair(totalScore, map);
		userCompatabilityMap.put(u, p);
		u.userCompatabilityMap.put(this, p);

		return p;
	}

	public long getGameTime(){
		return getGameTime(null);
	}

	public long getGameTime(final Genre genre){
		if(games == null){
			getGames();
		}
		Filter<Game> filter;
		if(genre == null){
			if(totalGameTime != -1){
				return totalGameTime;
			}else{
				filter = Filter.ANY;
			}
		}else{
			filter = (Game t) -> {
				for(Genre g2 : t.genres){
					if(genre.equals(g2)){
						return true;
					}
				}
				return false;
			};
		}
		long time = 0;
		time = games.keySet().stream()
				.filter((g) -> (filter.accept(g)))
				.map((g) -> filter.equals(Filter.ANY) ? g.genres.length * games.get(g) : games.get(g))
				.reduce(time, (accumulator, _item) -> accumulator + _item);

		if(filter == Filter.ANY){
			totalGameTime = time;
		}
		return time;
	}

	public double getGameRatio(){
		return getGameRatio(null);
	}

	public double getGameRatio(final Genre genre){
		if(games == null){
			getGames();
		}
		if(totalGameTime == -1){
			getGameTime();
		}
		if(genre == null){
			return 1;
		}
		return (double) getGameTime(genre) / totalGameTime;
	}

	public String getName(){
		return personaname;
	}

	public long getId(){
		return steamid;
	}

	@Override
	public int hashCode(){
		return 43 * 7 + (int) (this.steamid ^ (this.steamid >>> 32));
	}

	@Override
	public boolean equals(Object obj){
		if(obj == null || getClass() != obj.getClass()){
			return false;
		}
		return this.steamid == ((User) obj).steamid;
	}

	@Override
	public String toString(){
		return String.format("%s: %d", personaname, steamid);
	}
}
