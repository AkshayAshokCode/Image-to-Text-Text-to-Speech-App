package com.akshayAshokCode.textrecognition.data;

import com.akshayAshokCode.textrecognition.model.LanguageType;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class LanguageRepository {
    public List<LanguageType> getLanguageData() {

        LanguageType[] languageTypes = new LanguageType[30];
        languageTypes[0] = new LanguageType("American English", Locale.US);
        languageTypes[1] = new LanguageType("British English", Locale.UK);
        languageTypes[2] = new LanguageType("Chinese (Mandarian)", Locale.CHINA);
        languageTypes[3] = new LanguageType("Spanish", new Locale("es", "ES"));
        languageTypes[4] = new LanguageType("HIndi", new Locale("hi", "IN"));
        languageTypes[5] = new LanguageType("Arabic", new Locale("ar", "SA"));
        languageTypes[6] = new LanguageType("Portuguese", new Locale("pt", "BR"));
        languageTypes[7] = new LanguageType("Russian", new Locale("ru", "RU"));
        languageTypes[8] = new LanguageType("Japanese", Locale.JAPAN);
        languageTypes[9] = new LanguageType("German", Locale.GERMANY);
        languageTypes[10] = new LanguageType("Korean", Locale.KOREA);
        languageTypes[11] = new LanguageType("French", Locale.FRANCE);
        languageTypes[12] = new LanguageType("Persian (Farsi)", new Locale("fa", "IR"));
        languageTypes[13] = new LanguageType("Turkish", new Locale("tr", "TR"));
        languageTypes[14] = new LanguageType("Italian", Locale.ITALY);
        languageTypes[15] = new LanguageType("Bengali", new Locale("bn", "IN"));
        languageTypes[16] = new LanguageType("Telugu", new Locale("te", "IN"));
        languageTypes[17] = new LanguageType("Marathi", new Locale("mr", "IN"));
        languageTypes[18] = new LanguageType("Tamil", new Locale("ta", "IN"));
        languageTypes[19] = new LanguageType("Urdu", new Locale("ur", "PK"));
        languageTypes[20] = new LanguageType("Gujarati", new Locale("gu", "IN"));
        languageTypes[21] = new LanguageType("Kannada", new Locale("kn", "IN"));
        languageTypes[22] = new LanguageType("Malayalam", new Locale("ml", "IN"));
        languageTypes[23] = new LanguageType("Punjabi", new Locale("pa", "IN"));
        languageTypes[24] = new LanguageType("Polish", new Locale("pl", "PL"));
        languageTypes[25] = new LanguageType("Dutch", new Locale("nl", "NL"));
        languageTypes[26] = new LanguageType("Finnish", new Locale("fi", "FI"));
        languageTypes[27] = new LanguageType("Swedish", new Locale("sv", "SE"));
        languageTypes[28] = new LanguageType("Norwegian", new Locale("no", "NO"));
        languageTypes[29] = new LanguageType("Danish", new Locale("da", "DK"));


        return Arrays.asList(languageTypes.clone());
    }
}
