package com.alamsz.inc.expensetracker.utility;

import java.util.ResourceBundle;

public class InputValidationMessage {
		private static final String EMPTY_STRING = "";

		public String emptyText(String value, String fieldName){
			
			if(value == null || value.equals(EMPTY_STRING)){
				return "";
			}
			return null;
		}
}
