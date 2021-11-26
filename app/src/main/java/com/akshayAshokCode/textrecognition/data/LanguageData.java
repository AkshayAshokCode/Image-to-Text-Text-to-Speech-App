package com.akshayAshokCode.textrecognition.data;

import com.akshayAshokCode.textrecognition.model.LanguageType;

import java.util.Locale;
//[deu_DEU, eng_USA, rus_RUS, fra_FRA, ita_ITA, eng_GBR, spa_ESP]
public class LanguageData {
    public LanguageType[] getLanguageData(){
        LanguageType[] languageTypes=new LanguageType[7];
        languageTypes[0]=new LanguageType("British English", new Locale("eng_GBR"));
        languageTypes[1]=new LanguageType("American English",Locale.US);
        languageTypes[2]=new LanguageType("French",new Locale("fra_FRA"));
        languageTypes[3]=new LanguageType("Russian",new Locale("rus_RUS"));
        languageTypes[4]=new LanguageType("German",new Locale("deu_DEU"));
        languageTypes[5]=new LanguageType("Italian",new Locale("ita_ITA"));
        languageTypes[6]=new LanguageType("Spanish",new Locale("spa_ESP"));
        return languageTypes;
    }
}
