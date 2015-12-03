/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cmpt305.lab3.gui.controllers;

import cmpt305.lab3.structure.Genre;
import cmpt305.lab3.structure.User;
import java.awt.Dimension;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import javafx.collections.ListChangeListener;
import javax.swing.JPanel;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.renderer.category.LineAndShapeRenderer;
import org.jfree.data.category.DefaultCategoryDataset;

/**
 *
 * @author MrMagaw <MrMagaw@gmail.com>
 */
public class CompareGraphController implements ListChangeListener<User>{
	private final ChartPanel VIEW;
	private final List<User> COMPARE = new ArrayList<>();
	private List<Genre> genre_list = new ArrayList<>();
	private User main;
	private final DefaultCategoryDataset BAR_GRAPH, COMPARE_GRAPH;

	public void addUser(User u){
		if(main == null){
			main = u;
			addData(u);
			return;
		}
		if(u == null || COMPARE.contains(u) || main.equals(u)){
			return;
		}
		COMPARE.add(u);
		addData(u);

	}

	public void removeUser(User u){
		if(u == null || (!main.equals(u) && !COMPARE.contains(u))){
			return;
		}
		if(main.equals(u)){
			main = COMPARE.remove(0);
			removeData(u, true);
		}else{
			COMPARE.remove(u);
			removeData(u, false);
		}

	}

	private void addData(User u){
		if(u == null){
			return;
		}
		genre_list.stream().forEach(g -> BAR_GRAPH.addValue(u.getGameRatio(g), g.name, u.getName()));
		setSize();
		addCompareData(u);
	}

	private void addCompareData(User u){
		if(u == null){
			return;
		}
		COMPARE_GRAPH.addValue(u.getCompatabilityWith(main).getKey(), "Compatability", u.getName());
	}

	private void setSize(){
		VIEW.setPreferredSize(new Dimension(VIEW.getMaximumDrawWidth(), VIEW.getMaximumDrawHeight()));
		VIEW.revalidate();
	}

	public void updateGenres(List<Object> genres){
		genre_list = new ArrayList<>();
		genres.stream().forEach((g) -> genre_list.add(Genre.getGenre((String) g)));
		BAR_GRAPH.clear();
		if(main != null){
			addData(main);
		}
		COMPARE.stream().forEach((u) -> {
			addData(u);
		});
	}

	private void removeData(User u, boolean resetCompareData){
		final String uName = u.getName();
		if(resetCompareData){
			System.out.println(main);
			COMPARE_GRAPH.clear();
			addCompareData(main);
			COMPARE.stream().forEach(this::addCompareData);
		}else{
			COMPARE_GRAPH.removeColumn(uName);
		}
		BAR_GRAPH.removeColumn(uName);
	}

	private void clearData(){
		BAR_GRAPH.clear();
		COMPARE_GRAPH.clear();
		main = null;
	}

	public void updateUsers(List<User> users){
		clearData();
		users.stream().forEach((u) -> addUser(u));
	}

	public JPanel getView(){
		return this.VIEW;
	}

	public CompareGraphController(User main, Set<User> users){
		this(main, (User[]) users.toArray(new User[users.size()]));
	}

	public CompareGraphController(User main, User... users){
		this.main = main;

		BAR_GRAPH = new DefaultCategoryDataset();
		COMPARE_GRAPH = new DefaultCategoryDataset();

		JFreeChart t = ChartFactory.createBarChart("SteamWeb", "User", "Time Spent in Each Genre", BAR_GRAPH);

		addData(main);

		if(users != null && users.length > 0){
			Arrays.asList(users).stream().filter(u -> !u.equals(main)).distinct().forEach(u -> {
				addData(u);
				COMPARE.add(u);
			});
		}

		CategoryPlot plot = t.getCategoryPlot();
		plot.setDataset(1, COMPARE_GRAPH);
		plot.mapDatasetToRangeAxis(1, 1);
		plot.setRangeAxis(1, new NumberAxis("Compatability"));
		LineAndShapeRenderer rend = new LineAndShapeRenderer();
		rend.setBaseToolTipGenerator((cd, x, y) -> String.format("%s: %f", cd.getColumnKey(y), cd.getValue(x, y)));
		rend.setSeriesVisibleInLegend(0, false);
		plot.setRenderer(1, rend);

		VIEW = new ChartPanel(t);
		setSize();
	}

	@Override
	public void onChanged(Change<? extends User> c){
		c.getList().stream().forEach((i) -> System.out.println(":: - " + i));
		updateUsers(new ArrayList(c.getList()));
	}
}
