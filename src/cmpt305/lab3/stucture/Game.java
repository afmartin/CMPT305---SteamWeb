package cmpt305.lab3.stucture;

import cmpt305.lab3.Settings;
import cmpt305.lab3.main;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

public class Game {
    protected static final Map<Long, Game> ALL_GAMES = new HashMap();

    public static void save(){
	List<Genre> l = new ArrayList();
	JSONArray j = new JSONArray();
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
	    j.put(o);
	}
	JSONArray j2 = new JSONArray();
	for(Genre g : l){
	    JSONObject o = new JSONObject();
	    o.put("id", g.id);
	    o.put("name", g.name);
	    j2.put(o);
	}
	JSONObject json = new JSONObject();
	json.put("genres", j2);
	json.put("games", j);


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
        }
        JSONObject json = new JSONObject(games.toString());
	for(Object o : json.getJSONArray("genres")){
	    JSONObject j = (JSONObject)o;
            new Genre(j.getLong("id"), j.getString("name"));
	}
	for(Object o : json.getJSONArray("games")){
	    JSONObject j = (JSONObject)o;
            final List<Genre> genres = new ArrayList();
	    for(Object o2 : j.getJSONArray("genres")){
                genres.add(new Genre((int)o2));
	    }
	    new Game(j.getLong("appid"), j.getString("name"), genres.toArray(new Genre[genres.size()]));
	}
    }

    public final long appid;
    public final String name;
    public final Genre[] genres;

    protected Game(long appid, String name, Genre... genres){
	this.appid = appid;
	this.name = name;
	this.genres = genres;

        ALL_GAMES.put(appid, this);
    }

    public boolean hasGenre(Genre g){
	for(Genre g1 : genres)
	    if(g1.equals(g)) return true;
	return false;
    }

    @Override
    public String toString(){
	return String.format("%s (%d)", name, appid);
    }
}
