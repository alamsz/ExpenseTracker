package com.alamsz.inc.expensetracker;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.chart.AbstractChart;
import org.achartengine.chart.BarChart.Type;
import org.achartengine.model.CategorySeries;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.renderer.DefaultRenderer;
import org.achartengine.renderer.SimpleSeriesRenderer;
import org.achartengine.renderer.XYMultipleSeriesRenderer;

import android.content.Context;
import android.graphics.Color;

import com.alamsz.inc.expensetracker.dao.ConfigurationExpTracker;
import com.alamsz.inc.expensetracker.utility.StaticVariables;

public class ExpenseTrackerChartView extends GraphicalView {
	public static final int COLOR_GREEN = Color.parseColor("#62c51a");
	public static final int COLOR_ORANGE = Color.parseColor("#ff6c0a");
	public static final int COLOR_BLUE = Color.parseColor("#23bae9");
	public static int[] colors = new int[] { Color.GREEN, Color.BLUE,
			Color.CYAN, COLOR_ORANGE, Color.GRAY, Color.MAGENTA, Color.RED,
			Color.WHITE, Color.YELLOW, Color.DKGRAY, Color.LTGRAY };
	public static int[] seriesName = new int[] { R.string.income,
			R.string.expense, R.string.balance };
	public static String legendTitle = "";

	/**
	 * Constructor that only calls the super method. It is not used to
	 * instantiate the object from outside of this class.
	 * 
	 * @param context
	 * @param arg1
	 */
	private ExpenseTrackerChartView(Context context, AbstractChart arg1) {
		super(context, arg1);
	}

	public static GraphicalView getMonthlyBarChartInstance(Context context,
			int numOfWeeks, String prefixWeek,
			Map<Integer, TransactionAmountWrapper> tawList) {
		return ChartFactory.getBarChartView(context,
				getExpenseTrackerDataset(context, tawList, numOfWeeks),
				getMonthlyReportRenderer(tawList, numOfWeeks, prefixWeek),
				Type.DEFAULT);
	}

	public static GraphicalView getYearlyBarChartInstance(Context context,
			int numOfWeeks, String[] seriesLabel,
			Map<Integer, TransactionAmountWrapper> tawList) {
		return ChartFactory.getBarChartView(context,
				getExpenseTrackerDataset(context, tawList, numOfWeeks),
				getYearlyReportRenderer(tawList, numOfWeeks, seriesLabel),
				Type.DEFAULT);
	}

	public static GraphicalView getWeeklyBarChartInstance(Context context,
			int numOfDays, int startDate,
			Map<Integer, TransactionAmountWrapper> tawList, String[] labelSeries) {
		return ChartFactory
				.getBarChartView(
						context,
						getWeeklyBarChartDataset(context, tawList, numOfDays,
								startDate),
						getWeeklyBarChartRenderer(tawList, numOfDays,
								startDate, labelSeries), Type.DEFAULT);
	}

	public static List<Object> getNewPieChartInstance(Context context,
			List<String[]> tawList, String legendTitle) {
		List<Object> listOfObject = new ArrayList<Object>();
		CategorySeries expenseTrackerPieDataset = getExpenseTrackerPieDataset(
				context, tawList);
		DefaultRenderer pieRenderer = getPieRenderer(context, tawList,
				legendTitle);
		GraphicalView graphView = ChartFactory.getPieChartView(context,
				expenseTrackerPieDataset, pieRenderer);
		listOfObject.add(graphView);
		listOfObject.add(expenseTrackerPieDataset);
		listOfObject.add(pieRenderer);
		return listOfObject;
	}

	private static CategorySeries getExpenseTrackerPieDataset(Context context,
			List<String[]> tawList) {

		CategorySeries catSeriesExpense = new CategorySeries("transaction");
		for (int i = 0; i < tawList.size(); i++) {
			String[] expensesCat = tawList.get(i);
			ConfigurationExpTracker confExp = StaticVariables.mapOfExpenseCatBasedOnTableCode
					.get(expensesCat[1]);
			if (confExp != null) {
				catSeriesExpense.add(confExp.getLocDesc(),
						Double.parseDouble(expensesCat[0]));
			}

		}

		return catSeriesExpense;
	}

	/**
	 * Creates the renderer for the pie chart and sets the basic color scheme
	 * and hides labels and legend.
	 * 
	 * @return a renderer for the pie chart
	 */
	private static DefaultRenderer getPieRenderer(Context context,
			List<String[]> expCatList, String legendTitles) {

		DefaultRenderer defaultRenderer = new DefaultRenderer();
		legendTitle = legendTitles;
		for (int i = 0; i < expCatList.size(); i++) {
			String[] expensesCat = expCatList.get(i);
			ConfigurationExpTracker confExp = StaticVariables.mapOfExpenseCatBasedOnTableCode
					.get(expensesCat[1]);
			if (confExp != null) {
				SimpleSeriesRenderer simpleRenderer = new SimpleSeriesRenderer();
				int r = Character.getNumericValue(confExp.getTableCode()
						.charAt(0)) * 5 + 50;
				int g = Character.getNumericValue(confExp.getTableCode()
						.charAt(1)) * 3 + 50;
				int b = Character.getNumericValue(confExp.getTableCode()
						.charAt(2)) * 2 + 50;

				simpleRenderer.setColor(colors[i]);

				defaultRenderer.addSeriesRenderer(simpleRenderer);
			}

		}

		defaultRenderer.setShowLabels(true);
		defaultRenderer.setShowLegend(true);
		defaultRenderer.setChartTitle(legendTitle);
		defaultRenderer.setClickEnabled(true);
		return defaultRenderer;
	}

	public static XYMultipleSeriesRenderer getMonthlyReportRenderer(
			Map<Integer, TransactionAmountWrapper> tawList, int numOfSeries,
			String prefix) {
		XYMultipleSeriesRenderer renderer = new XYMultipleSeriesRenderer();
		renderer.setAxisTitleTextSize(10);
		renderer.setChartTitleTextSize(20);
		renderer.setLabelsTextSize(10);
		renderer.setLegendTextSize(15);
		renderer.setBarSpacing(5);
		renderer.setBarWidth(25);
		renderer.setMargins(new int[] { 20, 20, 20, 20 });

		SimpleSeriesRenderer simpleRenderer = new SimpleSeriesRenderer();
		simpleRenderer.setColor(colors[0]);
		simpleRenderer.setDisplayChartValues(true);

		renderer.addSeriesRenderer(simpleRenderer);
		simpleRenderer = new SimpleSeriesRenderer();
		simpleRenderer.setColor(colors[6]);
		simpleRenderer.setDisplayChartValues(true);
		renderer.addSeriesRenderer(simpleRenderer);
		renderer.setXLabels(0);

		for (int i = 0; i <= numOfSeries; i++) {
			renderer.addXTextLabel(i, prefix + " " + String.valueOf(i));
		}
		renderer.setXAxisMax(numOfSeries + 1);
		renderer.setShowGridX(false);
		renderer.setClickEnabled(true);
		renderer.setDisplayValues(true);
		return renderer;

	}

	public static XYMultipleSeriesRenderer getYearlyReportRenderer(
			Map<Integer, TransactionAmountWrapper> tawList, int numOfSeries,
			String[] label) {
		XYMultipleSeriesRenderer renderer = new XYMultipleSeriesRenderer();
		renderer.setAxisTitleTextSize(10);
		renderer.setChartTitleTextSize(20);
		renderer.setLabelsTextSize(10);
		renderer.setLegendTextSize(15);
		renderer.setBarSpacing(5);
		renderer.setBarWidth(25);
		renderer.setMargins(new int[] { 20, 20, 20, 20 });

		SimpleSeriesRenderer simpleRenderer = new SimpleSeriesRenderer();
		simpleRenderer.setColor(colors[0]);
		simpleRenderer.setDisplayChartValues(true);

		renderer.addSeriesRenderer(simpleRenderer);
		simpleRenderer = new SimpleSeriesRenderer();
		simpleRenderer.setColor(colors[6]);
		simpleRenderer.setDisplayChartValues(true);
		renderer.addSeriesRenderer(simpleRenderer);
		renderer.setXLabels(0);
		renderer.addXTextLabel(0, "");
		for (int i = 1; i <= numOfSeries; i++) {
			renderer.addXTextLabel(i, label[i - 1].substring(0, 3));
		}
		renderer.setXAxisMax(numOfSeries + 1);
		renderer.setShowGridX(false);
		renderer.setClickEnabled(true);
		renderer.setDisplayValues(true);
		return renderer;

	}

	public static XYMultipleSeriesRenderer getWeeklyBarChartRenderer(
			Map<Integer, TransactionAmountWrapper> tawList, int numOfDays,
			int startDate, String[] labelSeries) {
		XYMultipleSeriesRenderer renderer = new XYMultipleSeriesRenderer();
		renderer.setAxisTitleTextSize(10);
		renderer.setChartTitleTextSize(20);
		renderer.setLabelsTextSize(10);
		renderer.setLegendTextSize(15);
		renderer.setBarWidth(25);
		renderer.setBarSpacing(5);
		renderer.setXLabelsPadding(20);
		renderer.setXLabelsAngle(-30);
		renderer.setMargins(new int[] { 20, 20, 20, 20 });

		// for (int i = 0; i < colors.length; i++) {

		SimpleSeriesRenderer simpleRenderer = new SimpleSeriesRenderer();
		simpleRenderer.setColor(colors[0]);
		simpleRenderer.setDisplayChartValues(true);

		renderer.addSeriesRenderer(simpleRenderer);
		simpleRenderer = new SimpleSeriesRenderer();
		simpleRenderer.setColor(colors[6]);
		simpleRenderer.setDisplayChartValues(true);
		renderer.addSeriesRenderer(simpleRenderer);
		renderer.setXLabels(0);

		// }
		for (int i = 0; i <= numOfDays; i++) {
			renderer.addXTextLabel(i+1, labelSeries[i]);
		}
		renderer.setXAxisMax(numOfDays);
		renderer.setXAxisMin(0);
		renderer.setShowGridX(false);
		renderer.setClickEnabled(true);
		renderer.setDisplayValues(true);
		return renderer;

	}

	public static XYMultipleSeriesDataset getExpenseTrackerDataset(
			Context context, Map<Integer, TransactionAmountWrapper> tawList,
			int numOfSeries) {
		XYMultipleSeriesDataset dataset = new XYMultipleSeriesDataset();
		CategorySeries catSeriesExpense = new CategorySeries(
				context.getString(seriesName[1]));
		CategorySeries catSeriesIncome = new CategorySeries(
				context.getString(seriesName[0]));

		for (int i = 0; i < numOfSeries; i++) {
			TransactionAmountWrapper taw = tawList.get(i + 1);
			if (taw != null) {
				catSeriesExpense.add(taw.getExpense());
				catSeriesIncome.add(taw.getIncome());
			} else {
				catSeriesExpense.add(0);
				catSeriesIncome.add(0);
			}

		}
		dataset.addSeries(catSeriesIncome.toXYSeries());
		dataset.addSeries(catSeriesExpense.toXYSeries());
		return dataset;
	}

	public static XYMultipleSeriesDataset getWeeklyBarChartDataset(
			Context context, Map<Integer, TransactionAmountWrapper> tawList,
			int numOfDays, int startDate) {
		XYMultipleSeriesDataset dataset = new XYMultipleSeriesDataset();
		CategorySeries catSeriesExpense = new CategorySeries(
				context.getString(seriesName[1]));
		CategorySeries catSeriesIncome = new CategorySeries(
				context.getString(seriesName[0]));

		for (int i =0; i <= numOfDays; i++) {
			TransactionAmountWrapper taw = tawList.get(startDate + i);
			if (taw != null) {
				catSeriesExpense.add(taw.getExpense());
				catSeriesIncome.add(taw.getIncome());
			} else {
				catSeriesExpense.add(0);
				catSeriesIncome.add(0);
			}

		}
		dataset.addSeries(catSeriesIncome.toXYSeries());
		dataset.addSeries(catSeriesExpense.toXYSeries());
		return dataset;
	}

	public void setChartSettings(XYMultipleSeriesRenderer renderer,
			String title, String xTitle, String yTitle, double xMin,
			double xMax, double yMin, double yMax, int axesColor,
			int labelsColor) {
		renderer.setChartTitle(title);
		renderer.setXTitle(xTitle);
		renderer.setYTitle(yTitle);
		renderer.setXAxisMin(xMin);
		renderer.setXAxisMax(xMax);
		renderer.setYAxisMin(yMin);
		renderer.setYAxisMax(yMax);
		// renderer.setAxesColor(axesColor);
		// renderer.setLabelsColor(labelsColor);
	}

}
