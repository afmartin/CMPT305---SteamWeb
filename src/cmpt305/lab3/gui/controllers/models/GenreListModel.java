package cmpt305.lab3.gui.controllers.models;

import cmpt305.lab3.structure.Genre;
import cmpt305.lab3.structure.listener.GenreDataSetListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;
import javax.swing.AbstractListModel;
import javax.swing.SwingUtilities;

public class GenreListModel extends AbstractListModel implements GenreDataSetListener{
	private final List<String> GENRE_LIST;
	private final List<String> DISPLAY_LIST;
	private Predicate<String> filter;

	@Override
	public int getSize(){
		return DISPLAY_LIST.size();
	}

	@Override
	public String getElementAt(int i){
		return DISPLAY_LIST.get(i);
	}

	public void addElement(final String str){
		SwingUtilities.invokeLater(() -> {
			GENRE_LIST.add(str);
			if(filter.test(str)){
				DISPLAY_LIST.add(str);
				int index = GENRE_LIST.indexOf(str);
				fireIntervalAdded(GenreListModel.this, index, index);
				sort();
			}
		});
	}

	public void removeElement(final String str){
		SwingUtilities.invokeLater(() -> {
			GENRE_LIST.remove(str);
			int index = DISPLAY_LIST.indexOf(str);
			if(index != -1){
				DISPLAY_LIST.remove(str);
				fireIntervalRemoved(GenreListModel.this, index, index);
				sort();
			}
		});
	}

	public int getIndexOf(String s){
		return DISPLAY_LIST.indexOf(s);
	}

	private void refresh(){
		SwingUtilities.invokeLater(() -> {
			int size = getSize();
			DISPLAY_LIST.clear();
			GENRE_LIST.stream().filter(filter).forEach(DISPLAY_LIST::add);
			sort();
			this.fireContentsChanged(GenreListModel.this, 0, size - 1);
		});
	}

	public void setFilter(Predicate<String> p){
		filter = p;
		refresh();
	}

	private void sort(){
		Collections.sort(DISPLAY_LIST);
		fireContentsChanged(this, 0, DISPLAY_LIST.size() - 1);
	}

	public GenreListModel(){
		super();
		GENRE_LIST = new ArrayList<>();
		DISPLAY_LIST = new ArrayList<>();
		filter = (s) -> true;
	}

	@Override
	public void addGenre(Genre g){
		addElement(g.toString());
	}

	@Override
	public void removeGenre(Genre g){
		removeElement(g.toString());
	}
}
