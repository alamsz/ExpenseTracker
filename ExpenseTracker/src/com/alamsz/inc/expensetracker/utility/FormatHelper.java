package com.alamsz.inc.expensetracker.utility;

import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Currency;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import android.content.Context;
import android.util.Log;
import android.widget.EditText;

import com.alamsz.inc.expensetracker.dao.ConfigurationExpTracker;
import com.alamsz.inc.expensetracker.dao.DatabaseHandler;

public final class FormatHelper {
	private static final String ZERO = "0";
	private static final int BIGGEST_NUM_IN_ONE_DIGIT = 9;
	private static final String DD_MM_YYYY = "dd-MM-yyyy";
	private static int year;
	private static int month;
	private static int day;
	public static Currency cur = Currency.getInstance("IDR");
	public static Date stringToDate(String inputDateStr) {
		SimpleDateFormat formatDate = new SimpleDateFormat(DD_MM_YYYY);
		Date inputDate = null;
		try {
			inputDate = formatDate.parse(inputDateStr);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//Log.d(getClass().getName(), inputDateStr);
		//Log.d(getClass().getName(), inputDate.toGMTString());
		return inputDate;
	}

	public static long formatDateToLong(Date inputDate) {
		
		long timeInMilis = inputDate.getTime();
		//Date test = new Date(timeInMilis);
		
		//Log.d(getClass().getName(), String.valueOf(timeInMilis));
		//Log.d(getClass().getName(), test.toGMTString());
		return timeInMilis;
	}
	
	public static DatabaseHandler getDBHandler(DatabaseHandler dbHandler, Context context){
		if(dbHandler == null){
			dbHandler = new DatabaseHandler(context);
		}
		return dbHandler;
	}

	public static long formatDateToLong(String inputDateStr) {
		SimpleDateFormat sdf = new SimpleDateFormat(DD_MM_YYYY);
		
		long timeInMilis;
		try {
			timeInMilis = sdf.parse(inputDateStr).getTime();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return -1;
		}
		
		return timeInMilis;
	}
	
	// display current date
	public static void setCurrentDateOnView(EditText dateField) {

		final Calendar c = Calendar.getInstance();
		year = c.get(Calendar.YEAR);
		month = c.get(Calendar.MONTH);
		day = c.get(Calendar.DAY_OF_MONTH);
		String formattedMonth = FormatHelper.formatTwoDigitsMonth(month);
		String formattedDay = FormatHelper.formatTwoDigitsDay(day);
		if(dateField != null)
		dateField.setText(new StringBuilder().append(formattedDay).append("-")
				.append(formattedMonth).append("-").append(year).append(" "));

	}
	
	

	
	public static String formatDateForDisplay(long inputDateLong){
		SimpleDateFormat dateFormatter = new SimpleDateFormat(DD_MM_YYYY);
		return dateFormatter.format(new Date(inputDateLong));
		
	}
	
	public static String formatDateForDisplay(Date dateInput){
		SimpleDateFormat dateFormatter = new SimpleDateFormat(DD_MM_YYYY);
		return dateFormatter.format(dateInput);
		
	}
	
	public static String getBalanceInCurrency(String balance){
		int balanceInt = 0;
		if(balance != null)
			balanceInt = Integer.parseInt(balance);
		return getBalanceInCurrency(balanceInt);
	}
	
	public static String getBalanceInCurrency(int balanceInt){
		Locale curLocale = StaticVariables.currencyLocale;
		//somehow the locale always set to in_
		if(curLocale.toString().equals("in_ID")){
			curLocale = Locale.ITALY;
		} else{
			curLocale = Locale.US;
		}
		NumberFormat nf = NumberFormat.getCurrencyInstance(curLocale);
		Log.d("curLocale",curLocale.toString() + " "+StaticVariables.currencyLocale);
		nf.setCurrency(Currency.getInstance(StaticVariables.currencyLocale));
		
		return nf.format(balanceInt);
	}
	
	public static String formatTwoDigitsMonth(int month){
		return month + 1 > BIGGEST_NUM_IN_ONE_DIGIT ? String.valueOf(month + 1) : ZERO
			+ String.valueOf(month + 1);
	}
	
	public static String formatTwoDigitsDay(int day){
		return day > BIGGEST_NUM_IN_ONE_DIGIT ? String.valueOf(day) : ZERO
			+ String.valueOf(day);
	}
	
	public static String getSystemDate(){
		Date sysDate = new Date();
		SimpleDateFormat dateFormatter = new SimpleDateFormat(DD_MM_YYYY);
		return dateFormatter.format(sysDate);
	}
	
	
	public static List<Date> getWeekStartAndEndDateFromDate(int date, int month, int year){
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.MONTH, month);
		cal.set(Calendar.DATE, date);
		cal.set(Calendar.YEAR, year);
		//cal.set(Calendar.DAY_OF_WEEK, cal.getFirstDayOfWeek());
		return getWeekStartDateAndEndDate(cal);//System.out.println( + " -> "
			//	+ df.format(last.getTime()));
	}
	
	public static String[] getWeeksDatesFromDate(Date now){
		String[] dates = new String[7];
		Calendar cal = Calendar.getInstance();
		cal.setTime(now);
		//cal.add(Calendar.DATE, -1);
		for(int i=0;i <7;i++){
			dates[i] = formatDateForDisplay(cal.getTime());
			cal.add(Calendar.DATE, 1);
		}
		return dates;
	}
	
	public static List<Date> getWeekStartAndEndDateFromDate(Date now){
		Calendar cal = Calendar.getInstance();
		cal.setTime(now);
		return getWeekStartDateAndEndDate(cal);
	}
	
	public static List<Date> getWeekStartAndEndDateFromWeekInAMonth(int week, int month, int year){
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.MONTH, month);
		cal.set(Calendar.WEEK_OF_MONTH, week);
		cal.set(Calendar.YEAR, year);
		//cal.set(Calendar.DAY_OF_WEEK, cal.getFirstDayOfWeek());
		return getWeekStartDateAndEndDate(cal);//System.out.println( + " -> "
			//	+ df.format(last.getTime()));
	}
	
	
	public static List<Date> getWeekStartAndEndDateFromWeekInAYear(int week, int year){
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.WEEK_OF_YEAR, week);
		cal.set(Calendar.YEAR, year);
		//cal.set(Calendar.DAY_OF_WEEK, cal.getFirstDayOfWeek());
		return getWeekStartDateAndEndDate(cal);//System.out.println( + " -> "
			//	+ df.format(last.getTime()));
	}

	private static List<Date> getWeekStartDateAndEndDate(Calendar cal) {
		Calendar first = (Calendar) cal.clone();
		while (first.get(Calendar.DAY_OF_WEEK) != Calendar.MONDAY) {
			first.add(Calendar.DATE, -1);
	    }
		//first.setFirstDayOfWeek(Calendar.MONDAY);
		//first.add(Calendar.DAY_OF_WEEK,
		//	first.getFirstDayOfWeek()-first.get(Calendar.DAY_OF_WEEK));

		// and add six days to the end date
		Calendar last = (Calendar) first.clone();
		last.add(Calendar.DAY_OF_YEAR, 6);

		// print the result
	
		List<Date> listDateRange = new ArrayList<Date>();
		listDateRange.add(first.getTime());
		listDateRange.add(last.getTime());
		return listDateRange;
	}
	
	public static List<Integer> getWeekNoMonthAndYearFromDate(Date now){
		Calendar first = Calendar.getInstance();
		first.setTime(now);
		Calendar clone = (Calendar) first.clone();
		while (clone.get(Calendar.DAY_OF_WEEK) != Calendar.MONDAY) {
			clone.add(Calendar.DATE, -1);
	    }
		List<Integer> arrayofWeek = new ArrayList<Integer>();
		arrayofWeek.add(clone.get(Calendar.WEEK_OF_MONTH));
		arrayofWeek.add(clone.get(Calendar.WEEK_OF_YEAR));
		arrayofWeek.add(clone.get(Calendar.MONTH)+1);
		arrayofWeek.add(clone.get(Calendar.YEAR));
		return arrayofWeek;
	}
	
	public static ConfigurationExpTracker initConfig(String tableType) {
		ConfigurationExpTracker configIncCat = new ConfigurationExpTracker();
		configIncCat.setTableType(tableType);
		configIncCat.setTableCode("");
		configIncCat.setLocDesc("");
		return configIncCat;
	}
}
