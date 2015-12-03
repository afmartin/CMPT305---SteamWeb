package cmpt305.lab3.gui.controllers;

import cmpt305.lab3.gui.controllers.models.GenreListModel;
import cmpt305.lab3.gui.controllers.models.UserListModel;
import cmpt305.lab3.gui.views.MainScreenView;
import cmpt305.lab3.structure.Genre;
import cmpt305.lab3.structure.User;
import cmpt305.lab3.structure.listener.UserModelListener;
import java.util.List;

public class MainScreenController implements UserModelListener{
	private final MainScreenView VIEW;
	private final SettingsController SETTINGS;
	private final LogController LOG;
	private final GetUserController GET_USER_CONTROLLER = new GetUserController();
	private final GenreListModel GENRES = new GenreListModel();
	private final UserListModel USERS = new UserListModel();

	private final CompareGraphController GRAPH_CONTROLLER;

	@Override
	public void userModelChanged(){
		GRAPH_CONTROLLER.updateUsers(USERS.getList());
	}

	private void updateGraphGenreList(List<Object> selections){
		GRAPH_CONTROLLER.updateGenres(selections);
	}

	private void showAddUser(){
		GET_USER_CONTROLLER.toggle();
	}

	public MainScreenController(){
		USERS.addListener(this);
		User.addListener(USERS);
		Genre.addListener(GENRES);

		Genre.getKnown().stream().forEach((g) -> GENRES.addElement(g.toString()));

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
