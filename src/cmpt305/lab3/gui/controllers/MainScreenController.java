package cmpt305.lab3.gui.controllers;

import cmpt305.lab3.gui.controllers.models.GenreListModel;
import cmpt305.lab3.gui.controllers.models.UserListModel;
import cmpt305.lab3.gui.views.MainScreenView;
import cmpt305.lab3.structure.Genre;
import java.util.List;

public class MainScreenController{
	private final MainScreenView VIEW;

	private final GenreListModel GENRES = new GenreListModel();
	private final UserListModel USERS = new UserListModel();

	private final CompareGraphController GRAPH_CONTROLLER = new CompareGraphController(null);
	private final GetUserController GET_USER_CONTROLLER = new GetUserController();
	private final SettingsController SETTINGS;
	private final LogController LOG;

	private void updateGraphGenreList(List<Object> selections){
		GRAPH_CONTROLLER.updateGenres(selections);
	}

	private void showAddUser(){
		GET_USER_CONTROLLER.toggle();
	}

	private void setUserDirectionButtons(){
		VIEW.setUpButtonActive((VIEW.getUserListSelectionIndex() > 0));
		VIEW.setDownButtonActive((VIEW.getUserListSelectionIndex() < USERS.getSize() - 1));
	}

	public MainScreenController(){
		USERS.addListener(GRAPH_CONTROLLER);
		GET_USER_CONTROLLER.addListener(USERS);
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

		setUserDirectionButtons();

		VIEW.addUpButtonListener(ae -> {
			USERS.moveUp(VIEW.getUserListSelectionIndex());
			VIEW.setUserListSelectionIndex(VIEW.getUserListSelectionIndex() - 1);
			setUserDirectionButtons();
		});
		VIEW.addDownButtonListener(ae -> {
			USERS.moveDown(VIEW.getUserListSelectionIndex());
			VIEW.setUserListSelectionIndex(VIEW.getUserListSelectionIndex() + 1);
			setUserDirectionButtons();
		});

		VIEW.addUserListSelectionListener(l -> setUserDirectionButtons());
		VIEW.setGraphPanel(GRAPH_CONTROLLER.getView());
		VIEW.setSettingsPanel(SETTINGS.getView());
		VIEW.setLogPanel(LOG.getView());
	}
}
