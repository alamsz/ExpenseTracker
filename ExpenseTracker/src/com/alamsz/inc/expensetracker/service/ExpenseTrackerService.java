package com.alamsz.inc.expensetracker.service;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import android.content.Context;

import com.alamsz.inc.expensetracker.TransactionAmountWrapper;
import com.alamsz.inc.expensetracker.dao.DatabaseHandler;
import com.alamsz.inc.expensetracker.dao.ExpenseTracker;
import com.alamsz.inc.expensetracker.dao.ExpenseTrackerDAO;
import com.alamsz.inc.expensetracker.dao.PayRecPayment;
import com.alamsz.inc.expensetracker.dao.PayRecMaster;
import com.alamsz.inc.expensetracker.utility.CSVFile;
import com.alamsz.inc.expensetracker.utility.CSVFileGenerator;
import com.alamsz.inc.expensetracker.utility.TransactionListWrapper;

public class ExpenseTrackerService {
	private ExpenseTrackerDAO daoExpTracker;

	public ExpenseTrackerService(DatabaseHandler dbHandler) {
		daoExpTracker = new ExpenseTrackerDAO(dbHandler);
		daoExpTracker.open();
	}

	public Map<Integer, TransactionAmountWrapper> getWeeklyTransactionSummary(
			int week, int month, int year, String fundType) {
		return daoExpTracker.getWeeklyTransactionSummary(week, month, year, fundType);
	}

	public Map<Integer, TransactionAmountWrapper> getMonthlyTransactionSummary(
			int month, int year, String fundType) {
		return daoExpTracker.getMonthlyTransactionSummary(month, year, fundType);
	}

	public Map<Integer, TransactionAmountWrapper> getYearlyTransactionSummary(
			int year, String fundType) {
		return daoExpTracker.getYearlyTransactionSummary(year, fundType);
	}

	public TransactionListWrapper getTotalTransactionAndprepareCSVList(
			List<ExpenseTracker> list) {
		List<List<String>> expenseTrackerList = new ArrayList<List<String>>();
		int totalExpense = 0;
		int totalIncome = 0;
		for (Iterator iterator = list.iterator(); iterator.hasNext();) {
			ExpenseTracker expenseTracker = (ExpenseTracker) iterator.next();
			if (expenseTracker.getType().equals(ExpenseTracker.TYPE_DEBET)) {
				totalIncome = totalIncome + expenseTracker.getAmount();
			} else {
				totalExpense = totalExpense + expenseTracker.getAmount();
			}
			List<String> listPenampung = ExpenseTracker
					.convertFinanceHelperToList(expenseTracker);
			expenseTrackerList.add(listPenampung);
		}
		return new TransactionListWrapper(totalExpense, totalIncome,
				expenseTrackerList);
	}

	public boolean saveResultFile(String fileName, Context context,
			List<List<String>> transactionHistoryList) {

		CSVFileGenerator generator = new CSVFileGenerator(fileName);
		CSVFile csvFile = new CSVFile(transactionHistoryList, ExpenseTracker.getFinanceHelperHeader(context), null);
		List<CSVFile> csvFileList = new ArrayList<CSVFile>();
		csvFileList.add(csvFile);
		return generator.generateCSVFile(csvFileList);
	}

	public List<ExpenseTracker> getListPerPeriod(String dateFrom,
			String dateTo, String category, String transType, String transCat) {

		return daoExpTracker.getListPerPeriod(dateFrom, dateTo, category,
				transType, transCat);
	}

	public void deleteFinanceHelper(ExpenseTracker finHelp) {
		daoExpTracker.deleteFinanceHelper(finHelp);

	}

	public ExpenseTracker addFinanceHelper(ExpenseTracker financeHelper) {
		// TODO Auto-generated method stub
		return daoExpTracker.addFinanceHelper(financeHelper);
	}

	public String getBalancePerCategory(String category) {
		// TODO Auto-generated method stub
		return daoExpTracker.getBalancePerCategory(category);
	}
	
	public String getBalanceOtherThanCash() {
		// TODO Auto-generated method stub
		return daoExpTracker.getBalanceOtherThanCash();
	}
	
	public int calculateExpensePerCategory(int month, int year, String category){
		return daoExpTracker.calculateExpensePerCategory(category,null, month, year);
	}

	public int calculateExpensePerCategory(int month, int year, String category, String fundSource){
		return daoExpTracker.calculateExpensePerCategory(category,fundSource, month, year);
	}
	public List<String[]> getDetailExpense(int date, int week, int month,
			int year, String fundType) {
		String transType = "K";
		return daoExpTracker.getDetailCategoryBasedOnType(date, week, month, year, transType, fundType);
	}
	public List<String[]> getDetailIncome(int date, int week, int month,
			int year, String fundType) {
		String transType = "D";
		return daoExpTracker.getDetailCategoryBasedOnType(date, week, month, year, transType, fundType);
	}
	public PayRecMaster findByPKPayRecMaster(long id){
		return daoExpTracker.findByPKPayRecMaster(id);
	}
	
	public List<PayRecMaster> getAllPayRecMasterByType(String type){
		return daoExpTracker.getPayRecMasterByType(type);
	}
	
	public boolean deletePayRecMaster(PayRecMaster payRecMaster){
		return daoExpTracker.deletePayRecMaster(payRecMaster);
	}
	
	public boolean deletePayRecMaster(long id){
		PayRecMaster payrec = new PayRecMaster();
		payrec.setId(id);
		return daoExpTracker.deletePayRecMaster(payrec);
		
	}
	
	public PayRecMaster addPayRecMasterData(PayRecMaster payRecMaster){
		return daoExpTracker.addPayRecRecord(payRecMaster);
	}
	
	public PayRecMaster modifyPayRecMasterData(PayRecMaster payRecMaster){
		return daoExpTracker.modifyPayRecRecord(payRecMaster);
	}
	
	
	public PayRecPayment findByPKPayRecDetail(long id){
		return daoExpTracker.findByPKPayRecPayment(id);
	}
	
	public List<PayRecPayment> getAllPayRecDetailByIDMaster(long id){
		return daoExpTracker.getPayRecPaymentByMasterId(id);
	}
	
	public boolean deletePayRecPayment(PayRecPayment PayRecPayment){
		return daoExpTracker.deletePayRecPayment(PayRecPayment);
	}
	
	public ExpenseTracker findByPKTransaction(long id){
		return daoExpTracker.findByPKTransaction(id);
	}
	public boolean deletePayRecDetail(PayRecPayment payRecPayment){
		
		return daoExpTracker.deletePayRecPayment(payRecPayment);
		
	}
	
	public PayRecPayment addPayRecDetailData(PayRecPayment payRecPayment){
		return daoExpTracker.addPayRecPaymentRecord(payRecPayment);
	}
	
	/*public PayRecPayment modifyPayRecDetailData(PayRecPayment payRecPayment){
		return daoExpTracker.modifyPayRecDetailRecord(payRecPayment);
	}
*/

	public void close() {
		daoExpTracker.close();

	}

	public String getBalance() {
		return daoExpTracker.getBalance();
	}
}
