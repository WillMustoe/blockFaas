package blockchain;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.Calendar;

import javax.swing.JFrame;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.ui.RefineryUtilities;

public class MarketGraph extends JFrame {

	private static final long serialVersionUID = 338372249488843316L;
	private static final String Series_Key = "Values";
	private final XYSeriesCollection model;
	private final XYSeries series;
	private final long startTime;

	public MarketGraph(String applicationTitle, String chartTitle) {
		super(applicationTitle);
		model = new XYSeriesCollection();
		series = new XYSeries(Series_Key);
		model.addSeries(series);
		JFreeChart lineChart = ChartFactory.createXYLineChart(chartTitle, "Time (s)", "Price per million function calls (£)",
				model, PlotOrientation.VERTICAL, true, true, false);

		ChartPanel chartPanel = new ChartPanel(lineChart);
		chartPanel.setPreferredSize(new java.awt.Dimension(560, 367));
		setContentPane(chartPanel);
		Calendar calendar = Calendar.getInstance();
		this.startTime = calendar.getTimeInMillis();
		}

	public void display() {
		this.pack();
		RefineryUtilities.centerFrameOnScreen(this);
		this.setVisible(true);
	}
	
	public void addValue(double value) {
		Calendar calendar = Calendar.getInstance();
		long timeInMillis = calendar.getTimeInMillis() - startTime;
		series.add((float)timeInMillis/1000f, value);
	}
	
	void writeOut() {
		try (Writer writer = new BufferedWriter(new OutputStreamWriter(
	              new FileOutputStream("C:\\Users\\willm\\Documents\\GitHub\\output.dat"), "utf-8"))) {
			
			series.getItems().forEach(s ->{
				 try {
					writer.write(s.toString() + "\n");
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			});
	} catch (UnsupportedEncodingException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	} catch (FileNotFoundException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	}
	
	

}
