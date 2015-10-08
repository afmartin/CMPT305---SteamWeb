package cmpt305.lab3.stucture;

import java.util.HashMap;
import java.util.Map;

public class Game {
    protected static final Map<Long, Game> ALL_GAMES = new HashMap();
    
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
