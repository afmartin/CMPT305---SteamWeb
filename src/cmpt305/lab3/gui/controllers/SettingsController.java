package cmpt305.lab3.gui.controllers;

import cmpt305.lab3.Settings;
import cmpt305.lab3.Settings.Avatar;
import cmpt305.lab3.structure.Game;
import cmpt305.lab3.gui.views.SettingsView;
import java.awt.Desktop;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JPanel;

public class SettingsController{

	private final ResetCacheButtonListener RESET_CACHE_BUTTON_LISTENER;
	private final ConfirmButtonListener CONFIRM_BUTTON_LISTENER;
	private final GetApiKeyButtonListener GET_API_KEY_BUTTON_LISTENER;
	private final SettingsView VIEW;
	private final DefaultComboBoxModel AVATAR_MODEL;

	private final String STEAM_API_URL = "https://steamcommunity.com/dev";

	private class ResetCacheButtonListener implements ActionListener{
		@Override
		public void actionPerformed(ActionEvent ae){
			final String WARNING = "Are you sure you want to reset the cache?  It will take a long time "
					+ "to collect data from scratch!";
			Object[] options = {"Yes", "No"};
			int value = VIEW.showConfirmDialog(WARNING, options);
			if(value == 0){
				clearCache();
				System.out.println("Cleared the cache.");
			}
		}
	}

	private class ConfirmButtonListener implements ActionListener{
		@Override
		public void actionPerformed(ActionEvent ae){
			String key = VIEW.getAppId();
			if(key != null){
				Settings.setApiKey(key);
				Settings.setAvatar(VIEW.getAvatarSelection());
			}
		}
	}

	private class GetApiKeyButtonListener implements ActionListener{
		@Override
		public void actionPerformed(ActionEvent ae){
			if(Desktop.isDesktopSupported()){
				try{
					Desktop.getDesktop().browse(new URI(STEAM_API_URL));
				}catch(IOException | URISyntaxException ex){
					VIEW.displayUrlBox(STEAM_API_URL);
				}
			}else{
				VIEW.displayUrlBox(STEAM_API_URL);
			}
		}
	}

	private void clearCache(){
		Game.clear();
	}

	public JPanel getView(){
		return VIEW;
	}

	public SettingsController(){
		RESET_CACHE_BUTTON_LISTENER = new ResetCacheButtonListener();
		CONFIRM_BUTTON_LISTENER = new ConfirmButtonListener();
		GET_API_KEY_BUTTON_LISTENER = new GetApiKeyButtonListener();
		AVATAR_MODEL = new DefaultComboBoxModel(Avatar.values());

		VIEW = new SettingsView();
		VIEW.addConfirmButtonListener(this.CONFIRM_BUTTON_LISTENER);
		VIEW.addResetCacheButtonListener(this.RESET_CACHE_BUTTON_LISTENER);
		VIEW.addGetApiKeyButtonListener(this.GET_API_KEY_BUTTON_LISTENER);
		VIEW.setAppId(Settings.getApiKey());
		VIEW.setAvatarModel(AVATAR_MODEL, Settings.getAvatar());
	}
}
