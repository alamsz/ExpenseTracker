package com.alamsz.inc.expensetracker.utility;

import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Currency;
import java.util.Date;
import java.util.Locale;

import android.widget.EditText;

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
	
	// display current date
	public static void setCurrentDateOnView(EditText dateField) {

		final Calendar c = Calendar.getInstance();
		year = c.get(Calendar.YEAR);
		month = c.get(Calendar.MONTH);
		day = c.get(Calendar.DAY_OF_MONTH);
		String formattedMonth = FormatHelper.formatTwoDigitsMonth(month);
		String formattedDay = FormatHelper.formatTwoDigitsDay(day);
		dateField.setText(new StringBuilder().append(formattedDay).append("-")
				.append(formattedMonth).append("-").append(year).append(" "));

	}

	
	public static String formatDateForDisplay(long inputDateLong){
		SimpleDateFormat dateFormatter = new SimpleDateFormat(DD_MM_YYYY);
		return dateFormatter.format(new Date(inputDateLong));
		
	}
	
	public static String getBalanceInCurrency(String balance){
		int balanceInt = 0;
		if(balance != null)
			balanceInt = Integer.parseInt(balance);
		return getBalanceInCurrency(balanceInt);
	}
	
	public static String getBalanceInCurrency(int balanceInt){
		NumberFormat nf = NumberFormat.getCurrencyInstance(Locale.JAPAN);
		nf.setCurrency(cur);
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
}
