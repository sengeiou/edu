package com.ant.country;

import java.util.List;
import android.content.Context;
import android.util.Log;

import com.ant.chosecountrynum.R;

public class CountryTool {

	public static String getContryNameByCode(String code, Context mContext) {
		List<CountrySortModel> mAllCountryList;

		String[] countryList = mContext.getResources().getStringArray(
				R.array.country_code_list_en);

		Log.d("CountryTool","mContext = " +  mContext.getResources().getConfiguration().locale.getCountry());

		if (mContext.getResources().getConfiguration().locale.getCountry()
				.equals("CN") || mContext.getResources().getConfiguration().locale.getCountry()
				.equals("TW")) {
			countryList = mContext.getResources().getStringArray(
					R.array.country_code_list_ch);
		}

		for (int i = 0, length = countryList.length; i < length; i++) {
			String[] country = countryList[i].split("\\*");

			String countryName = country[0];
			String countryNumber = country[1];

			if (code.equals(countryNumber.substring(1))) {
				return countryName;
			}

		}

		return "";

	}
}
