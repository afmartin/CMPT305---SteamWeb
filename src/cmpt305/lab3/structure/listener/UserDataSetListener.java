package cmpt305.lab3.structure.listener;

import cmpt305.lab3.structure.User;
import javafx.collections.MapChangeListener;

public interface UserDataSetListener extends MapChangeListener{
	@Override
	public default void onChanged(MapChangeListener.Change change){
		if(change.wasAdded()){
			addUser((User) change.getValueAdded());
		}else{
			removeUser((User) change.getValueRemoved());
		}
	}

	public void addUser(User u);

	public void removeUser(User u);
}
