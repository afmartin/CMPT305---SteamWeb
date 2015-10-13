package cmpt305.lab3.gui;

import cmpt305.lab3.exceptions.APIEmptyResponse;
import cmpt305.lab3.stucture.User;
import darrylbu.util.MenuScroller;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.UIManager;

public class AddMenu extends JPopupMenu{
    private static final JMenuItem addUser = new JMenuItem("Add User");
    private static final JMenuItem removeUser = new JMenuItem("Remove User");
    private static final Comparator<JMenuItem> JMENUSORT = new Comparator<JMenuItem>(){
	@Override
	public int compare(JMenuItem arg0, JMenuItem arg1) {
	    return arg0.getText().compareTo(arg1.getText());
	}
    };
    private final Map<JMenuItem, Long> friends = new HashMap();
    private final List<UserListener> listeners = new ArrayList();
    private int x, y;

    private final MenuScroller scroller;

    private final ActionListener listener = new ActionListener() {
	@Override
	public void actionPerformed(ActionEvent e) {
	    JMenuItem i = (JMenuItem)e.getSource();
	    for(UserListener l : listeners)
		try{
		    if(i.equals(addUser)){
			throw new UnsupportedOperationException("Not supported yet (Prompt user for ID).");
		    }
		    l.addUser(User.getUser(friends.get(i)), new Point(x, y));
		}catch(APIEmptyResponse ex){}
	}
    };

    public AddMenu(){
	super();
	scroller = new MenuScroller(this);
	scroller.setScrollCount(9);
	scroller.setTopFixedCount(1);
	add(addUser);
    }

    public void setFriends(User u){
	List<JMenuItem> flist = new ArrayList();
	flist.addAll(friends.keySet());
	Collections.sort(flist, JMENUSORT);

	for(Component c : flist)
	    remove(c);

	friends.clear();
	remove(removeUser);

	if(u == null){
	    scroller.setTopFixedCount(1);
	    return;
	}
	
	scroller.setTopFixedCount(2);
	add(removeUser);
	for(User f : u.getFriends().keySet()){
	    JMenuItem item = new JMenuItem(f.getName());
	    item.addActionListener(listener);
	    friends.put(item, f.getId());
	    add(item);
	}

    }
    @Override
    public void show(Component invoker, int x, int y){
	super.show(invoker, x, y);
	this.x = x;
	this.y = y;
    }
    public void removeFriends(){ setFriends(null); }
    public void addUserListener(UserListener a){ listeners.add(a); }
    public void removeUserListener(UserListener a){ listeners.remove(a); }
}
