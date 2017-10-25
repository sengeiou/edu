package com.ubt.alpha1e.data;

import java.util.Locale;

public class LanguageTools {
    public static Locale getLocale(String language) {
        switch (language) {
            case "CN":
                return Locale.SIMPLIFIED_CHINESE;
            case "US":
                return Locale.ENGLISH;
            case "TW":
                return Locale.TRADITIONAL_CHINESE;
            case "HK":
                return Locale.TRADITIONAL_CHINESE;
            case "ES":
                return new Locale("ES");
            case "DE":
                return Locale.GERMANY;
            case "IT":
                return new Locale("IT");
            case "JA":
                return Locale.JAPANESE;
            case "KO":
                return Locale.KOREAN;
            case "FR":
                return Locale.FRENCH;
            default:
                return null;
        }
    }
}
