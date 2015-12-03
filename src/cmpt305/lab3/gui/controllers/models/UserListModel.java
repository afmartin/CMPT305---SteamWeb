package cmpt305.lab3.gui.controllers.models;

import cmpt305.lab3.structure.User;
import cmpt305.lab3.structure.listener.UserDataSetListener;
import java.util.ArrayList;
import java.util.List;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javax.swing.AbstractListModel;
import javax.swing.SwingUtilities;

public class UserListModel extends AbstractListModel implements UserDataSetListener{
	private final ObservableList<User> USER_LIST;

	public UserListModel(){
		this.USER_LIST = FXCollections.observableArrayList(new ArrayList<>());
	}

	@Override
	public int getSize(){
		return USER_LIST.size();
	}

	@Override
	public Object getElementAt(int index){
		return USER_LIST.get(index).getName();
	}

	public void addElement(final User u){
		SwingUtilities.invokeLater(() -> {
			if(!USER_LIST.contains(u)){
				USER_LIST.add(u);
			}
			int index = USER_LIST.indexOf(u);
			this.fireIntervalAdded(UserListModel.this, index, index);
		});
	}

	public void removeElement(final User u){
		SwingUtilities.invokeLater(() -> {
			int index = USER_LIST.indexOf(u);
			USER_LIST.remove(u);
			this.fireIntervalRemoved(UserListModel.this, index, index);
		});
	}

	public void removeElementAt(int index){
		if(index != -1){
			removeElement(USER_LIST.get(index));
		}
	}

	public void clear(){
		SwingUtilities.invokeLater(() -> {
			USER_LIST.clear();
			this.fireIntervalRemoved(UserListModel.this, 0, 0);
		});
	}

	public void addListener(ListChangeListener l){
		USER_LIST.addListener(l);
	}

	public void removeListener(ListChangeListener l){
		USER_LIST.removeListener(l);
	}

	public List<User> getList(){
		return new ArrayList<>(USER_LIST);
	}

	@Override
	public void addUser(User u){
		addElement(u);
	}

	@Override
	public void removeUser(User u){
		removeElement(u);
	}

	public void moveDown(int index){
		if(index < USER_LIST.size() - 1){
			User above = USER_LIST.get(index);
			User below = USER_LIST.get(index + 1);
			USER_LIST.set(index, below);
			USER_LIST.set(index + 1, above);
			this.fireContentsChanged(this, index, index + 1);
		}
	}

	public void moveUp(int index){
		if(index > 0){
			User above = USER_LIST.get(index - 1);
			User below = USER_LIST.get(index);
			USER_LIST.set(index - 1, below);
			USER_LIST.set(index, above);
			this.fireContentsChanged(this, index - 1, index);
		}
	}
}
