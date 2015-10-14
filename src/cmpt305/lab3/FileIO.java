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
import org.json.JSONArray;
import org.json.JSONObject;

public class FileIO {
    public static void save(Map<Long, Game> ALL_GAMES, List<Long> IGNORED_GAMES){
	List<Genre> l = new ArrayList();
	JSONArray games = new JSONArray();
	for(Game g : ALL_GAMES.values()){
	    JSONArray gen = new JSONArray();
	    for(Genre g1 : g.genres){
		gen.put(g1.id);
		if(!l.contains(g1)) l.add(g1);
	    }

	    JSONObject o = new JSONObject();
	    o.put("name", g.name);
	    o.put("appid", g.appid);
	    o.put("genres", gen);
	    games.put(o);
	}
	JSONArray genres = new JSONArray();
	for(Genre g : l){
	    JSONObject o = new JSONObject();
	    o.put("id", g.id);
	    o.put("name", g.name);
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

	save(json, 0);
    }

    private static void save(JSONObject json, int tryCount){
        try {
	    File f = new File(Settings.GAME_DATABASE + "~");
	    if(f.exists()) f.delete();
	    f.createNewFile();
	    try(PrintWriter out = new PrintWriter(f)){
		out.print(json.toString());
	    }
            File f0 = new File(Settings.GAME_DATABASE);
            if(f0.exists()) f0.delete();
            f.renameTo(f0);
	}catch(IOException ex){
            if(tryCount < Settings.MAX_RETRIES)
                save(json, ++tryCount);
            else
                Logger.getLogger(main.class.getName()).log(Level.SEVERE, null, ex);
	}
    }

    public static void load(){
        StringBuilder games = new StringBuilder();
        try(BufferedReader data = new BufferedReader(new FileReader(Settings.GAME_DATABASE))){
            while(data.ready())
                games.append(data.readLine());
        }catch(FileNotFoundException ex){
            return; //No file.
        }catch (IOException ex) {
            Logger.getLogger(Game.class.getName()).log(Level.SEVERE, null, ex);
	    return;
        }
	if(games.length() == 0) return;

        JSONObject json = new JSONObject(games.toString());

	if(json.has("genres"))
	    for(Object o : json.getJSONArray("genres")){
		JSONObject j = (JSONObject)o;
		Genre.getGenre(j.getLong("id"), j.getString("name"));
	    }

	if(json.has("games"))
	    for(Object o : json.getJSONArray("games")){
		JSONObject j = (JSONObject)o;
		final List<Genre> genres = new ArrayList();
		for(Object o2 : j.getJSONArray("genres")){
		    genres.add(Genre.getGenre((int)o2));
		}
		Game.getGame(j.getLong("appid"), j.getString("name"), genres.toArray(new Genre[genres.size()]));
	    }

	if(json.has("ignoredGames"))
	    for(Object o : json.getJSONArray("ignoredGames")){
		Game.addIgnoredGame((int)o);
	    }
    }
}
