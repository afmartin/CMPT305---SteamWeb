package cmpt305.lab3.stucture;

import java.util.ArrayList;
import java.util.List;

public class Genre {
    public static final List<Genre> KNOWN = new ArrayList();
    public final String name;
    public final long id;
    public Genre(long id, String name){
	this.name = name;
	this.id = id;
	if(!KNOWN.contains(this))
	    KNOWN.add(this);
    }
    public Genre(long id){
	this.id = id;
	if(KNOWN.contains(this))
	    this.name = KNOWN.get(KNOWN.indexOf(this)).name;
	else
	    this.name = "Unknown";//Change this later. In fact, maybe remove this whole thing (It'll only be used for testing)
    }

    @Override
    public int hashCode() {
	int hash = 5;
	hash = 43 * hash + (int) (this.id ^ (this.id >>> 32));
	return hash;
    }

    @Override
    public boolean equals(Object obj) {
	if (obj == null) {
	    return false;
	}
	if (getClass() != obj.getClass()) {
	    return false;
	}
	final Genre other = (Genre) obj;
	return this.id == other.id;
    }

    @Override
    public String toString() {
	return name;
    }
}
