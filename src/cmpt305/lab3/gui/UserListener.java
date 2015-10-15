package cmpt305.lab3.gui;

import cmpt305.lab3.stucture.User;
import java.awt.Point;

public interface UserListener{
	public void addUser(User u, Point p);

	public void removeUser(User u);
}
