package cmpt305.lab3.gui.controllers;

import cmpt305.lab3.Settings;
import cmpt305.lab3.exceptions.APIEmptyResponse;
import cmpt305.lab3.gui.views.GetUserView;
import cmpt305.lab3.main;
import cmpt305.lab3.structure.User;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class GetUserController{
	private final GetUserView VIEW = new GetUserView();
	private final AddButtonListener ADD_LISTENER = new AddButtonListener();

	private class AddButtonListener implements ActionListener{
		@Override
		public void actionPerformed(ActionEvent ae){
			addUser(VIEW.getInput());
		}
	}

	public void addUser(String input){
		User user = null;
		try{
			user = User.getUser(input);
		}catch(APIEmptyResponse ex){
			try{
				long id = Long.parseLong(input);
				user = User.getUser(id);
			}catch(APIEmptyResponse | NumberFormatException ex1){
				notValidUsername();
				return;
			}
		}

		if(user == null){
			notValidUsername();
			return;
		}

		VIEW.clearUsername();
		VIEW.dispose();

		final User CONFIRMED_USER = user;
		new Thread(() -> {
			System.out.println(CONFIRMED_USER);
			CONFIRMED_USER.getGames();
			main.printGameTime(CONFIRMED_USER);
		}).start();

	}

	public void notValidUsername(){
		System.err.println("Could not add user.");
		VIEW.invalidInput();
	}

	public void toggle(){
		if(!VIEW.isDisplayable()){
			if(Settings.getApiKey().equals("")){
				VIEW.noApiKey();
				VIEW.dispose();
			}else{
				VIEW.pack();
				VIEW.setVisible(true);
			}
		}else{
			VIEW.dispose();
		}
	}

	public GetUserController(){
		VIEW.addButtonListener(ADD_LISTENER);
		VIEW.dispose();
	}
}
