package cmpt305.lab3.gui.controllers;

import cmpt305.lab3.exceptions.APIEmptyResponse;
import cmpt305.lab3.gui.views.GetUserView;
import cmpt305.lab3.main;
import cmpt305.lab3.stucture.User;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class GetUserController{
	private final GetUserView VIEW = new GetUserView();
	private final AddButtonListener ADD_LISTENER = new AddButtonListener();

	private class AddButtonListener implements ActionListener{
		@Override
		public void actionPerformed(ActionEvent ae){
			addUsername(VIEW.getUsername());
		}
	}

	public void addUsername(String username){
		User user = null;
		try{
			user = User.getUser(username);
		}catch(APIEmptyResponse ex){
			notValidUsername();
			return;
		}

		if(user == null){
			notValidUsername();
			return;
		}
		VIEW.clearUsername();
		VIEW.dispose();

		final User CONFIRMED_USER = user;

		new Thread(){
			@Override
			public void run(){
				System.out.println(CONFIRMED_USER);
				CONFIRMED_USER.getGames();
				main.printGameTime(CONFIRMED_USER);
			}
		}.start();

	}

	public void notValidUsername(){
		VIEW.setInfoText();
	}

	public void toggle(){
		if(!VIEW.isDisplayable()){
			VIEW.pack();
			VIEW.setVisible(true);
		}else{
			VIEW.dispose();
		}
	}

	public GetUserController(){
		VIEW.addButtonListener(ADD_LISTENER);
		VIEW.dispose();
	}
}
