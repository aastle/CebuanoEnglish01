package com.aastle.cebuanoenglish01;

/**
 * Created by prometheus on 11/22/13.
 */
public class englishcebuano {

    private String english;
    private String cebuano;

    public englishcebuano(String english,String cebuano) {
        this.english = english;
        this.cebuano = cebuano;
    }

    public static englishcebuano getInstance(String english,String cebuano){
        return new englishcebuano(english,cebuano);
    }

    public void setEnglish(String english){
        this.english = english;
    }

    public void setCebuano(String cebuano) {
        this.cebuano = cebuano;
    }

    public String getEnglish(){
        return this.english;
    }

    public String getCebuano(){
        return this.cebuano;
    }
}
