package blockchain;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;

import javax.swing.JFrame;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RefineryUtilities;

public class MarketGraph extends JFrame {

	private static final long serialVersionUID = 338372249488843316L;
	private static final String Series_Key = "Values";
	private final XYSeriesCollection model;
	private final XYSeries series;

	public MarketGraph(String applicationTitle, String chartTitle) {
		super(applicationTitle);
		model = new XYSeriesCollection();
		series = new XYSeries(Series_Key);
		model.addSeries(series);
		JFreeChart lineChart = ChartFactory.createXYLineChart(chartTitle, "Time", "Price per million function calls",
				model, PlotOrientation.VERTICAL, true, true, false);

		ChartPanel chartPanel = new ChartPanel(lineChart);
		chartPanel.setPreferredSize(new java.awt.Dimension(560, 367));
		setContentPane(chartPanel);
	}

	public void display() {
		this.pack();
		RefineryUtilities.centerFrameOnScreen(this);
		this.setVisible(true);
	}
	
	public void addValue(double value) {
		Calendar calendar = Calendar.getInstance();
		float timeInHours = calendar.get(Calendar.HOUR_OF_DAY) + calendar.get(Calendar.MINUTE)/ (float) 60  + calendar.get(Calendar.SECOND) / (float) 3600 ;
		
		series.add(timeInHours, value);
	}
	
	

}
