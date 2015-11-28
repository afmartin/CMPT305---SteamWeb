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
			int value = VIEW.showConfirmDialog(WARNING, options);
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
			String key = VIEW.getAppId();
			if(key != null){
				Settings.setApiKey(key);
			}
			quit();
		}
	}

	private final SettingsView VIEW;

	public void quit(){
		VIEW.dispose();
	}

	public void toggle(){
		VIEW.setVisible(!VIEW.isVisible());
	}

	private void clearCache(){
		Game.clear();
	}

	public SettingsController(){
		SETTINGS_BUTTON_LISTENER = new SettingsButtonListener();
		LOG_BUTTON_LISTENER = new LogButtonListener();
		CONFIRM_BUTTON_LISTENER = new ConfirmButtonListener();

		VIEW = new SettingsView();
		VIEW.addSettingButtonListener(this.SETTINGS_BUTTON_LISTENER);
		VIEW.addLogButtonListener(this.LOG_BUTTON_LISTENER);
		VIEW.addConfirmButtonListener(this.CONFIRM_BUTTON_LISTENER);
		VIEW.setAppId(Settings.getApiKey());
		VIEW.pack();
		VIEW.setVisible(false);
	}
}
