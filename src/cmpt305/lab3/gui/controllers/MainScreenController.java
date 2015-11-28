package cmpt305.lab3.gui.controllers;

import cmpt305.lab3.gui.views.MainScreenView;
import cmpt305.lab3.structure.Genre;
import cmpt305.lab3.structure.User;
import cmpt305.lab3.structure.listener.GenreDataSetListener;
import cmpt305.lab3.structure.listener.UserDataSetListener;
import cmpt305.lab3.structure.listener.UserModelListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javax.swing.AbstractListModel;
import javax.swing.SwingUtilities;

public class MainScreenController implements GenreDataSetListener, UserDataSetListener, UserModelListener{
	private final MainScreenView VIEW;
	private final SettingsController SETTINGS;
	private final LogController LOG;
	private final GetUserController GET_USER_CONTROLLER = new GetUserController();
	private final GenreListModel GENRES = new GenreListModel();
	private final UserListModel USERS = new UserListModel();

	private final CompareGraphController GRAPH_CONTROLLER;

	@Override
	public void addGenre(Genre g){
		GENRES.addElement(g.toString());
	}

	@Override
	public void removeGenre(Genre g){
		GENRES.removeElement(g.toString());
	}

	@Override
	public void addUser(User u){
		USERS.addElement(u);
	}

	@Override
	public void removeUser(User u){
		USERS.removeElement(u);
	}

	@Override
	public void userModelChanged(){
		GRAPH_CONTROLLER.updateUsers(USERS.getList());
	}

	/*
	 NOTE: invokeLater is used to ensure thread safety when adding or removing
	 data from the GenreListModel.
	 */
	private class GenreListModel extends AbstractListModel{
		private final List<String> GENRE_LIST;

		@Override
		public int getSize(){
			return GENRE_LIST.size();
		}

		@Override
		public String getElementAt(int i){
			return GENRE_LIST.get(i);
		}

		public void addElement(final String str){
			SwingUtilities.invokeLater(() -> {
				GENRE_LIST.add(str);
				int index = GENRE_LIST.indexOf(str);
				fireIntervalAdded(GenreListModel.this, index, index);
				sort();
			});
		}

		public void removeElement(final String str){
			SwingUtilities.invokeLater(() -> {
				int index = GENRE_LIST.indexOf(str);
				GENRE_LIST.remove(str);
				fireIntervalRemoved(GenreListModel.this, index, index);
				sort();
			});

		}

		public void sort(){
			Collections.sort(GENRE_LIST);
			fireContentsChanged(this, 0, GENRE_LIST.size() - 1);
		}

		public GenreListModel(){
			super();
			GENRE_LIST = new ArrayList();
		}
	}

	private class UserListModel extends AbstractListModel{
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
				USER_LIST.add(u);
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
	}

	private void updateGraphGenreList(List<Object> selections){
		GRAPH_CONTROLLER.updateGenres(selections);
	}

	private void showAddUser(){
		GET_USER_CONTROLLER.toggle();
	}

	public MainScreenController(){
		User.addListener(this);
		Genre.addListener(this);
		USERS.addListener(this);

		for(Genre g : Genre.getKnown()){
			GENRES.addElement(g.toString());
		}

		VIEW = new MainScreenView();
		VIEW.pack();
		VIEW.setGenreModel(GENRES);
		VIEW.setUserModel(USERS);
		VIEW.setVisible(true);

		SETTINGS = new SettingsController();
		LOG = new LogController();

		VIEW.addClearButtonListener(ae -> USERS.clear());
		VIEW.addUserButtonListener(ae -> showAddUser());
		VIEW.addGenreListSelectionListener(se -> updateGraphGenreList(VIEW.getGenresSelected()));
		VIEW.addRemoveButtonListener(ae -> USERS.removeElementAt(VIEW.getUserListSelectionIndex()));

		GRAPH_CONTROLLER = new CompareGraphController(null);
		VIEW.setGraphPanel(GRAPH_CONTROLLER.getView());
		VIEW.setSettingsPanel(SETTINGS.getView());
		VIEW.setLogPanel(LOG.getView());
	}
}
