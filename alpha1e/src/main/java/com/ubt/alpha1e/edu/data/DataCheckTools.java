package com.ubt.alpha1e.edu.data;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DataCheckTools {
	public static boolean isPhoneNum(String num) {
		long a = -1;
		try {
			a = Long.parseLong(num);
		} catch (Exception e) {

			a = -1;
		}
		if (a == -1)
			return false;
		else
			return true;
	}

	public static boolean isEmail(String email) {
		if (email.contains("@") && email.contains(".")
				&& email.split("@").length == 2
				&& email.lastIndexOf("@") < email.lastIndexOf(".")
				&& email.lastIndexOf(".") < email.length() - 1)
			return true;
		else
			return false;
	}

	public static Boolean isCorrectPswFormat(String str) {
		String str2 = "^[a-zA-Z0-9_]+$";
		Pattern p = Pattern.compile(str2);
		Matcher m = p.matcher(str);
		return m.matches();
	}
}
