package cmpt305.lab3;

import cmpt305.lab3.stucture.Game;
import cmpt305.lab3.stucture.Genre;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Set;
import org.json.JSONArray;
import org.json.JSONObject;

public class FileIO{

	private static final String SETTINGS_FILE = "settings.json";

	public static void saveSettings(){
		JSONObject settings = new JSONObject();

		settings.put("max retries", Settings.getMaxRetries());
		settings.put("max timeout ms", Settings.getMaxTimeoutMs());
		settings.put("retry time ms", Settings.getRetryTimeMs());
		settings.put("game database", Settings.getGameDatabase());
		settings.put("verbose", Settings.isVerbose());
		settings.put("key", Settings.getApiKey());

		save(settings, SETTINGS_FILE, 0);
	}

	public static void saveGenres(Map<Long, Game> ALL_GAMES, List<Long> IGNORED_GAMES){
		int counter = 0;
		Map<Genre, Integer> l = new HashMap();
		JSONArray games = new JSONArray();
		for(Game g : ALL_GAMES.values()){
			JSONArray gen = new JSONArray();
			for(Genre g1 : g.genres){
				if(!l.containsKey(g1)){
					l.put(g1, counter++);
				}
				gen.put(l.get(g1));
			}

			JSONObject o = new JSONObject();
			o.put("name", g.name);
			o.put("appid", g.appid);
			o.put("genres", gen);
			games.put(o);
		}
		JSONArray genres = new JSONArray();
		for(Entry<Genre, Integer> e : l.entrySet()){
			JSONObject o = new JSONObject();
			o.put("id", e.getValue());
			o.put("name", e.getKey().name);
			genres.put(o);
		}
		JSONArray ignoredGames = new JSONArray();
		for(long appid : IGNORED_GAMES){
			ignoredGames.put(appid);
		}

		JSONObject json = new JSONObject();
		json.put("genres", genres);
		json.put("games", games);
		json.put("ignoredGames", ignoredGames);

		save(json, Settings.getGameDatabase(), 0);
	}

	private static void save(JSONObject json, String filename, int tryCount){
		try{
			File f = new File(filename + "~");
			if(f.exists()){
				f.delete();
			}
			f.createNewFile();
			try(PrintWriter out = new PrintWriter(f)){
				out.print(json.toString());
			}
			File f0 = new File(filename);
			if(f0.exists()){
				f0.delete();
			}
			f.renameTo(f0);
		}catch(IOException ex){
			if(tryCount < Settings.getMaxRetries()){
				save(json, filename, ++tryCount);
			}else{
				Logger.getLogger(main.class.getName()).log(Level.SEVERE, null, ex);
			}
		}
	}

	public static String load(String filename){
		StringBuilder json = new StringBuilder();
		try(BufferedReader data = new BufferedReader(new FileReader(filename))){
			while(data.ready()){
				json.append(data.readLine());
			}
		}catch(FileNotFoundException ex){
			return null; //No file.
		}catch(IOException ex){
			Logger.getLogger(Game.class.getName()).log(Level.SEVERE, null, ex);
			return null;
		}
		if(json.length() == 0){
			return null;
		}

		return json.toString();
	}

	public static void loadGames(){
		String games = load(Settings.getGameDatabase());
		if(games == null){
			return;
		}
		JSONObject json = new JSONObject(games);
		Map<Integer, Genre> genreMap = new HashMap();

		if(json.has("genres")){
			for(Object o : json.getJSONArray("genres")){
				JSONObject j = (JSONObject) o;
				Genre g = Genre.getGenre(j.getString("name"));
				genreMap.put(j.getInt("id"), g);
			}
		}

		if(json.has("games")){
			for(Object o : json.getJSONArray("games")){
				JSONObject j = (JSONObject) o;
				final List<Genre> genres = new ArrayList();
				for(Object o2 : j.getJSONArray("genres")){
					genres.add(genreMap.get((int) o2));
				}
				Game.getGame(j.getLong("appid"), j.getString("name"), genres.toArray(new Genre[genres.size()]));
			}
		}

		if(json.has("ignoredGames")){
			for(Object o : json.getJSONArray("ignoredGames")){
				Game.addIgnoredGame((int) o);
			}
		}
	}

	public static void loadSettings(){
		String settings = load(SETTINGS_FILE);
		if(settings == null){
			return;
		}
		JSONObject json = new JSONObject(settings);

		Settings.setMaxRetries(json.getInt("max retries"));
		Settings.setMaxTimeoutMs(json.getInt("max timeout ms"));
		Settings.setRetryTimeMs(json.getLong("retry time ms"));
		Settings.setGameDatabase(json.getString("game database"));
		Settings.setVerbose(json.getBoolean("verbose"));
		Settings.setApiKey(json.getString("key"));
	}
}
