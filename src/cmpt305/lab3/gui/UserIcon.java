/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cmpt305.lab3.gui;

import cmpt305.lab3.stucture.User;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 *
 * @author MrMagaw <MrMagaw@gmail.com>
 */
public class UserIcon extends JLabel{
	private final User user;

	UserIcon(User u){
		super(u.getName());
		user = u;
		setBackground(Color.red);
	}

	public User getUser(){
		return user;
	}
}
