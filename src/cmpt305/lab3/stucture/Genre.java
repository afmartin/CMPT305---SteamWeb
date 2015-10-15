package cmpt305.lab3.stucture;

import java.util.HashMap;
import java.util.Map;

public class Genre{
	public static final Map<Long, Genre> KNOWN = new HashMap();

	public static Genre getGenre(long id, String name){
		if(KNOWN.containsKey(id)){
			return KNOWN.get(id);
		}
		Genre g = new Genre(id, name);
		KNOWN.put(id, g);
		return g;
	}

	public static Genre getGenre(long id){
		if(KNOWN.containsKey(id)){
			return KNOWN.get(id);
		}
	//Throw exception?
		//Request the genre from API somehow?
		return null;
	}

	public final String name;
	public final long id;

	private Genre(long id, String name){
		this.name = name;
		this.id = id;
	}

	@Override
	public int hashCode(){
		int hash = 5;
		hash = 43 * hash + (int) (this.id ^ (this.id >>> 32));
		return hash;
	}

	@Override
	public boolean equals(Object obj){
		if(obj == null){
			return false;
		}
		if(getClass() != obj.getClass()){
			return false;
		}
		final Genre other = (Genre) obj;
		return this.id == other.id;
	}

	@Override
	public String toString(){
		return name;
	}
}
