/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cmpt305.lab3.structure.listener;

import cmpt305.lab3.structure.Genre;
import javafx.collections.SetChangeListener;

public interface GenreDataSetListener extends SetChangeListener{
	@Override
	public default void onChanged(SetChangeListener.Change change){
		if(change.wasAdded()){
			addGenre((Genre) change.getElementAdded());
		}else{
			removeGenre((Genre) change.getElementRemoved());
		}
	}

	public void addGenre(Genre g);

	public void removeGenre(Genre g);
}
