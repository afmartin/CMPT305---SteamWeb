package cmpt305.lab3.gui.controllers;

import cmpt305.lab3.gui.views.LogView;
import com.wordpress.tips4java.MessageConsole;
import java.awt.Color;
import javax.swing.JTextPane;

public class LogController{
	private final LogView VIEW;

	public LogController(){
		VIEW = new LogView();
		JTextPane text = VIEW.getTextComponent();
		MessageConsole mc = new MessageConsole(text);
		mc.redirectOut();
		mc.redirectErr(Color.red, null);

		VIEW.setVisible(true);
		System.out.println("Enabled Log");

	}

	public void toggle(){
		if(!VIEW.isDisplayable()){
			VIEW.pack();
			VIEW.setVisible(true);
		}else{
			VIEW.dispose();
		}
	}
}
