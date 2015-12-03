package cmpt305.lab3.gui.controllers;

import cmpt305.lab3.Settings;
import cmpt305.lab3.exceptions.APIEmptyResponse;
import cmpt305.lab3.gui.views.GetUserView;
import cmpt305.lab3.structure.User;
import cmpt305.lab3.structure.listener.UserDataSetListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.HashSet;
import java.util.Set;
import javax.swing.SwingUtilities;

public class GetUserController{
	private final GetUserView VIEW = new GetUserView();
	private final EnterKeyListener KEY_LISTENER = new EnterKeyListener();
	private final Set<UserDataSetListener> listeners = new HashSet();

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

	public void addListener(UserDataSetListener u){
		listeners.add(u);
	}

	public void removeListener(UserDataSetListener u){
		listeners.remove(u);
	}

	public void initAdd(){
		if(VIEW.getInput().length() > 0){
			VIEW.disableButton();
			addUser(VIEW.getInput());
		}
	}

	private void notify(User u){
		listeners.stream().forEach((UserDataSetListener l) -> l.addUser(u));
	}

	public void addUser(String input){
		new Thread(() -> {
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
			final User USER = user;
			SwingUtilities.invokeLater(() -> {
				if(USER == null){
					notValidUsername();
					VIEW.enableButton();
				}else{
					VIEW.clearUsername();
					VIEW.enableButton();
					VIEW.dispose();
					System.out.println(USER);
					notify(USER);
					new Thread(() -> USER.getGames()).start();
				}
			});
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
