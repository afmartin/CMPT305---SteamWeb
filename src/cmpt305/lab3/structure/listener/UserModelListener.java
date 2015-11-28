/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cmpt305.lab3.structure.listener;

import javafx.collections.ListChangeListener;

/**
 *
 * @author alex
 */
public interface UserModelListener extends ListChangeListener{
	@Override
	public default void onChanged(ListChangeListener.Change change){
		userModelChanged();
	}

	public void userModelChanged();
}
