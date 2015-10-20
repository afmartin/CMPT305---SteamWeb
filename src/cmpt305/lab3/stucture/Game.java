package cmpt305.lab3.stucture;

import cmpt305.lab3.FileIO;
import cmpt305.lab3.Settings;
import cmpt305.lab3.api.API;
import cmpt305.lab3.api.StoreCrawler;
import cmpt305.lab3.exceptions.APIEmptyResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.json.JSONArray;
import org.json.JSONObject;

public class Game{
	private static final Map<Long, Game> ALL_GAMES = new HashMap();
	private static final List<Long> IGNORED_GAMES = new ArrayList();

	public static Game getGame(long appid){
		if(ALL_GAMES.containsKey(appid)){
			return ALL_GAMES.get(appid);
		}
		if(IGNORED_GAMES.contains(appid)){
			return null;
		}

		Map<API.Reqs, String> reqs = new HashMap();
		reqs.put(API.Reqs.appids, Long.toString(appid));

		JSONObject json;
		try{
			json = API.appdetails.getData(reqs);
			if(json.has("success") && !json.getBoolean("success")){
				throw new APIEmptyResponse();
			}
			json = json.getJSONObject("data");
		}catch(APIEmptyResponse ex){
			if(Settings.VERBOSE){
				System.err.println("Failed to get game: " + appid);
			}
			Game.IGNORED_GAMES.add(appid);
			return null;
		}

		List<Genre> genres = new ArrayList();
		boolean hasGenres = true;
		if(!json.has("genres")){
			hasGenres = false;
		}else{
			for(Object o : json.getJSONArray("genres")){
				JSONObject o1 = (JSONObject) o;
				genres.add(Genre.getGenre(o1.getString("description")));
			}
		}

		try{
			JSONArray user_genres = StoreCrawler.getUserGenres(appid);
			for(Object o : user_genres){
				JSONObject entry = (JSONObject) o;
				Genre g = Genre.getGenre(entry.getString("name"));
				if(!genres.contains(g)){
					genres.add(g);
				}
			}
		}catch(APIEmptyResponse ex){
			if(!hasGenres){
				if(Settings.VERBOSE){
					System.err.printf("Game has no genres: %s (%d)\n", json.getString("name"), appid);
				}
				Game.IGNORED_GAMES.add(appid);
				return null;
			}
		}
		return getGame(appid, json.getString("name"), genres.toArray(new Genre[genres.size()]));
	}

	public static Game getGame(long appid, String name, Genre... genres){
		if(ALL_GAMES.containsKey(appid)){
			return ALL_GAMES.get(appid);
		}
		Game g = new Game(appid, name, genres);
		ALL_GAMES.put(appid, g);
		return g;
	}

	public static List<Game> getGames(Long... appids){
		if(appids == null || appids.length == 0){
			return null;
		}
		List<Game> ret = new ArrayList();

		int preSizeA = ALL_GAMES.size();
		int preSizeI = IGNORED_GAMES.size();

		for(long id : appids){
			Game g = getGame(id);
			if(g != null){
				ret.add(g);
			}
		}

		if(preSizeA != ALL_GAMES.size() || preSizeI != IGNORED_GAMES.size()){
			FileIO.save(ALL_GAMES, IGNORED_GAMES);
		}

		return ret;
	}

	public static void addIgnoredGame(long appid){
		IGNORED_GAMES.add(appid);
	}

	public static int getIgnoredCount(){
		return IGNORED_GAMES.size();
	}

	public static int getGameCount(){
		return ALL_GAMES.size();
	}

	public final long appid;
	public final String name;
	public final Genre[] genres;

	private Game(long appid, String name, Genre... genres){
		this.appid = appid;
		this.name = name;
		this.genres = genres;
	}

	public boolean hasGenre(Genre g){
		for(Genre g1 : genres){
			if(g1.equals(g)){
				return true;
			}
		}
		return false;
	}

	@Override
	public String toString(){
		return String.format("%s (%d)", name, appid);
	}
}
