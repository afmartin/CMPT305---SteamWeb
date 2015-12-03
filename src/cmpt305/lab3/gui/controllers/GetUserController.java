package cmpt305.lab3.gui.controllers;

import cmpt305.lab3.Settings;
import cmpt305.lab3.exceptions.APIEmptyResponse;
import cmpt305.lab3.gui.views.GetUserView;
import cmpt305.lab3.main;
import cmpt305.lab3.structure.User;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import javax.swing.SwingUtilities;

public class GetUserController{
	private final GetUserView VIEW = new GetUserView();
	private final EnterKeyListener KEY_LISTENER = new EnterKeyListener();

	private class EnterKeyListener implements KeyListener{

		@Override
		public void keyTyped(KeyEvent e){
		}

		@Override
		public void keyPressed(KeyEvent e){
			int key = e.getKeyCode();
			if(key == KeyEvent.VK_ENTER){
				initAdd();
			}
		}

		@Override
		public void keyReleased(KeyEvent e){
		}
	}

	public void initAdd(){
		if(VIEW.getInput().length() > 0){
			VIEW.disableButton();
			addUser(VIEW.getInput());
		}
	}

	public void addUser(String input){
		SwingUtilities.invokeLater(new Thread(() -> {
			User user;
			try{
				user = User.getUser(input);
			}catch(APIEmptyResponse ex){
				try{
					long id = Long.parseLong(input);
					user = User.getUser(id);
				}catch(APIEmptyResponse | NumberFormatException ex1){
					user = null;
				}
			}

			if(user == null){
				notValidUsername();
				VIEW.enableButton();
			}else{
				VIEW.clearUsername();
				VIEW.enableButton();
				VIEW.dispose();
				System.out.println(user);
				final User USER = user;
				new Thread(() -> USER.getGames()).start();
			}
		}));

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
				SwingUtilities.invokeLater(() -> {
					VIEW.pack();
					VIEW.setVisible(true);
				});
			}
		}else{
			VIEW.dispose();
		}
	}

	public GetUserController(){
		VIEW.addButtonListener((ae) -> initAdd());
		VIEW.addTextListener(KEY_LISTENER);
		VIEW.dispose();
	}
}
