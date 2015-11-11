package cmpt305.lab3.stucture;

import java.util.Set;
import java.util.HashSet;
import java.util.Objects;
import javafx.collections.FXCollections;
import javafx.collections.ObservableSet;
import javafx.collections.SetChangeListener;

public class Genre{
	private static final ObservableSet<Genre> KNOWN = FXCollections.observableSet(new HashSet<>());

	public static Genre getGenre(String name){
		Genre g = new Genre(name);

		if(KNOWN.contains(g)){
			return g;
		}
		KNOWN.add(g);
		return g;
	}

	public static Set<Genre> getKnown(){
		return new HashSet(KNOWN);
	}

	public static void addListener(SetChangeListener l){
		KNOWN.addListener(l);
	}

	public static void removeListener(SetChangeListener l){
		KNOWN.removeListener(l);
	}

	protected static void clear(){
		KNOWN.clear();
	}

	public final String name;

	private Genre(String name){
		this.name = name;
	}

	@Override
	public int hashCode(){
		int hash = 3;
		hash = 83 * hash + Objects.hashCode(this.name);
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
		if(!Objects.equals(this.name, other.name)){
			return false;
		}
		return true;
	}

	@Override
	public String toString(){
		return name;
	}
}
