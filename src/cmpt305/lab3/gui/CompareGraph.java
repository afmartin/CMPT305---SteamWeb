/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cmpt305.lab3.gui;

import cmpt305.lab3.stucture.Genre;
import cmpt305.lab3.stucture.User;
import java.awt.Dimension;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.swing.JFrame;
import javax.swing.WindowConstants;
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
public class CompareGraph extends JFrame{
	private final ChartPanel chart;
	private final List<User> compare = new ArrayList<>();
	private User main;
	private final DefaultCategoryDataset barGraph, compareGraph;
	private final Set<Genre> selectedGennres;

	public void addUser(User u){
		if(u == null || compare.contains(u) || main.equals(u)){
			return;
		}
		compare.add(u);
		addData(u);
	}

	public void removeUser(User u){
		if(u == null || (!main.equals(u) && !compare.contains(u))){
			return;
		}
		User old = compare.remove(0);
		if(main.equals(u)){
			main = old;
			removeData(u, true);
		}else{
			removeData(u, false);
		}

	}

	private void addData(User u){
		Genre.getKnown().stream().forEach(g -> {
			if(selectedGennres.contains(g)){
				barGraph.addValue(u.getGameRatio(g), g.name, u.getName());
			}
		});
		addCompareData(u);
	}

	private void addCompareData(User u){
		compareGraph.addValue(u.getCompatabilityWith(main).getKey(), "Compatability", u.getName());
	}

	private void removeData(User u, boolean resetCompareData){
		final String uName = u.getName();
		if(resetCompareData){
			System.out.println(main);
			compareGraph.clear();
			addCompareData(main);
			compare.stream().forEach(this::addCompareData);
		}else{
			compareGraph.removeColumn(uName);
		}
		barGraph.removeColumn(uName);
	}

	public CompareGraph(User main, Set<Genre> genres, Set<User> users){
		this(main, genres, (User[]) users.toArray(new User[users.size()]));
	}

	public CompareGraph(User main, Set<Genre> genres, User... users){
		super("Comparison");
		{
			Dimension d = new Dimension(500, 500);
			this.setMinimumSize(d);
			this.setSize(d);
			this.setPreferredSize(d);
			this.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		}
		this.main = main;
		this.selectedGennres = genres;

		barGraph = new DefaultCategoryDataset();
		compareGraph = new DefaultCategoryDataset();

		JFreeChart t = ChartFactory.createBarChart("SteamWeb", "User", "Time Spent in Each Genre", barGraph);

		addData(main);

		if(users != null && users.length > 0){
			Arrays.asList(users).stream().filter(u -> !u.equals(main)).distinct().forEach(u -> {
				addData(u);
				compare.add(u);
			});
		}

		CategoryPlot plot = t.getCategoryPlot();
		plot.setDataset(1, compareGraph);
		plot.mapDatasetToRangeAxis(1, 1);
		plot.setRangeAxis(1, new NumberAxis("Compatability"));
		LineAndShapeRenderer rend = new LineAndShapeRenderer();
		rend.setBaseToolTipGenerator((cd, x, y) -> String.format("%s: %f", cd.getColumnKey(y), cd.getValue(x, y)));
		rend.setSeriesVisibleInLegend(0, false);
		plot.setRenderer(1, rend);

		chart = new ChartPanel(t);
		add(chart);
	}
}
