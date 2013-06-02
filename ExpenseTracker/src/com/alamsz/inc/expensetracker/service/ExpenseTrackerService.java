package com.alamsz.inc.expensetracker.service;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import android.content.Context;

import com.alamsz.inc.expensetracker.dao.ExpenseTracker;
import com.alamsz.inc.expensetracker.utility.CSVFileGenerator;
import com.alamsz.inc.expensetracker.utility.TransactionListWrapper;

public class ExpenseTrackerService {
	public TransactionListWrapper getTotalTransactionAndprepareCSVList(List<ExpenseTracker> list) {
		List<List<String>> expenseTrackerList = new ArrayList<List<String>>();
		int totalExpense = 0;
		int totalIncome = 0;
		for (Iterator iterator = list.iterator(); iterator.hasNext();) {
			ExpenseTracker expenseTracker = (ExpenseTracker) iterator.next();
			if(expenseTracker.getType().equals(ExpenseTracker.TYPE_DEBET)){
				totalIncome = totalIncome + expenseTracker.getAmount();
			}else{
				totalExpense = totalExpense + expenseTracker.getAmount();
			}
			List<String> listPenampung = ExpenseTracker
					.convertFinanceHelperToList(expenseTracker);
			expenseTrackerList.add(listPenampung);
		}
		return new TransactionListWrapper(totalExpense, totalIncome, expenseTrackerList);
	}
	
	public boolean saveResultFile(String fileName,Context context, List<List<String>> transactionHistoryList) {

		CSVFileGenerator generator = new CSVFileGenerator(
				transactionHistoryList,
				ExpenseTracker.getFinanceHelperHeader(context),
				fileName);
		return generator.generateCSVFile();
	}
}
