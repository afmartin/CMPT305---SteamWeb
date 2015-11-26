package cmpt305.lab3.gui.controllers;

import cmpt305.lab3.Settings;
import cmpt305.lab3.structure.Game;
import cmpt305.lab3.gui.views.SettingsView;
import cmpt305.lab3.structure.Genre;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class SettingsController{

	private final SettingsButtonListener SETTINGS_BUTTON_LISTENER;
	private final LogButtonListener LOG_BUTTON_LISTENER;
	private final ConfirmButtonListener CONFIRM_BUTTON_LISTENER;

	private LogController log;

	private class SettingsButtonListener implements ActionListener{
		@Override
		public void actionPerformed(ActionEvent ae){
			final String WARNING = "Are you sure you want to reset the cache?  It will take a long time "
					+ "to collect data from scratch!";
			Object[] options = {"Yes", "No"};
			int value = view.showConfirmDialog(WARNING, options);
			if(value == 0){
				clearCache();
				System.out.println("Cleared the cache.");
			}
		}
	}

	private class LogButtonListener implements ActionListener{
		@Override
		public void actionPerformed(ActionEvent ae){
			if(log == null){
				log = new LogController();
			}else{
				log.toggle();
			}
		}
	}

	private class ConfirmButtonListener implements ActionListener{
		@Override
		public void actionPerformed(ActionEvent ae){
			String key = view.getAppId();
			if(key != null){
				Settings.setApiKey(key);
			}
			quit();
		}
	}

	private final SettingsView view;

	public void quit(){
		view.dispose();
	}

	public void toggle(){
		view.setVisible(!view.isVisible());
	}

	private void clearCache(){
		Game.clear();
	}

	public SettingsController(){
		SETTINGS_BUTTON_LISTENER = new SettingsButtonListener();
		LOG_BUTTON_LISTENER = new LogButtonListener();
		CONFIRM_BUTTON_LISTENER = new ConfirmButtonListener();

		view = new SettingsView();
		view.addSettingButtonListener(this.SETTINGS_BUTTON_LISTENER);
		view.addLogButtonListener(this.LOG_BUTTON_LISTENER);
		view.addConfirmButtonListener(this.CONFIRM_BUTTON_LISTENER);
		view.setAppId(Settings.getApiKey());
		view.pack();
		view.setVisible(false);
	}
}
